const express = require("express");
const cors = require("cors");
const http = require("http");
const socketIO = require("socket.io");
const PORT = 8081;
const Message = require("./Message");
const bodyParser = require("body-parser");
const Validation = require("./Validation");

const con = require("./connection");
const { parse } = require("path");

const validation = new Validation(con);

const app = express();
app.use(bodyParser.urlencoded({ limit: "10mb", extended: true }));
app.use(bodyParser.json({ limit: "10mb" }));
app.use(cors({ origin: "*" }));
const server = http.createServer(app);
const io = socketIO(server, {
  cors: {
    origin: "*", // Allow requests from any origin
    methods: ["GET", "POST"], // Allow only GET and POST requests
    allowedHeaders: ["Content-Type"], // Allow only specific headers
  },
});

io.on("connection", (socket) => {
  console.log("A user connected! " + socket.id);

  socket.on("disconnect", () => {
    console.log("A client disconnected");
  });

  socket.on("newMessage", (messageJson) => {
    const messageObj = JSON.parse(messageJson);
    const message = new Message(
      messageObj.receiverId,
      messageObj.text,
      messageObj.senderId,
      messageObj.image,
      "sent"
    );
    io.emit("newMessage" + message.receiverId, message);
    io.emit("newMessage" + message.senderId, message);
  });
});

app.post("/login", (req, res) => {
  const { username, password } = req.body;

  const sql = "SELECT * FROM user WHERE username = ? and password = ?;";

  con.query(sql, [username, password], (error, data) => {
    if (error)
      return res
        .status(401)
        .json({ success: false, message: "Invalid username or password" });

    if (data.length > 0) {
      row = data[0];

      const profileBuffer = row.profileImage;
      const backgroundBuffer = row.backgroundImage;
      var profileImage = null;

      if (profileBuffer != null) {
        profileImage = Array.from(Buffer.from(profileBuffer));
      }
      var backgroundImage = null;

      if (backgroundBuffer != null) {
        backgroundImage = Array.from(Buffer.from(backgroundBuffer));
      }

      const user = {
        id: row.id,
        nickname: row.nickname,
        username: row.username,
        birthday: row.birthday,
        profileImage: profileImage,
        backgroundImage: backgroundImage,
        bio: row.bio,
        createdDate: row.createdDate,
        deleted: row.deleted,
        isSuspended: row.isSuspended,
      };
      return res.json(user);
    } else {
      return res
        .status(401)
        .json({ success: false, message: "Invalid username or password" });
    }
  });
});

app.post("/edit", (req, res) => {
  var user = req.body;
  validation.checkUsername(user, (error, result) => {
    if (error) return res.status(500).json({ message: error });
    var sql =
      "UPDATE user SET nickname = ?, username = ?, birthday = ?, bio = ?";

    var userEdit = [
      user.nickname,
      user.username,
      Validation.parseDateToSQL(user.birthday),
      user.bio,
    ];

    var profileBuffer = null;
    var backgroundBuffer = null;

    if (user.profileImage != null) {
      profileBuffer = Buffer.from(user.profileImage);
      sql = sql.concat(", profileImage = ?");
      userEdit.push(profileBuffer);
    }
    if (user.backgroundImage != null) {
      backgroundBuffer = Buffer.from(user.backgroundImage);
      sql = sql.concat(", backgroundImage = ?");
      userEdit.push(backgroundBuffer);
    }

    sql = sql.concat("where id = ?;");
    userEdit.push(user.id);

    con.query(sql, userEdit, (error, data) => {
      if (error)
        return res.status(500).json({ message: "Internal server error." });
      const userSql = "SELECT * FROM user WHERE id = ?;";
      con.query(userSql, user.id, (error, data) => {
        if (error)
          return res.status(500).json({ message: "Internal server error." });
        if (data.length > 0) {
          row = data[0];

          const profileBuffer = row.profileImage;
          const backgroundBuffer = row.backgroundImage;
          var profileImage = null;

          if (profileBuffer != null) {
            profileImage = Array.from(Buffer.from(profileBuffer));
          }
          var backgroundImage = null;

          if (backgroundBuffer != null) {
            backgroundImage = Array.from(Buffer.from(backgroundBuffer));
          }

          const editedUser = {
            id: row.id,
            nickname: row.nickname,
            username: row.username,
            birthday: row.birthday,
            profileImage: profileImage,
            backgroundImage: backgroundImage,
            bio: row.bio,
          };
          return res.json(editedUser);
        }
      });
    });
  });
});

app.put("/signup", validation.signup);

app.post("/friend/request", (req, res) => {
  const senderId = req.query.id;
  const username = req.query.friendUsername;
  const getIdSql = "SELECT id FROM user WHERE username = ?;";
  con.query(getIdSql, username, (error, data) => {
    if (error)
      return res.status(500).json({ message: "Internal server error." });
    if (data.length === 0)
      return res
        .status(400)
        .json({ message: "No user found with that username." });
    const id = data[0].id;

    getFriendsInvites(senderId, id, (error, result) => {
      console.log(senderId + " " + id);
      if (error) return res.status(400).json({ message: error });
      if (result.length > 0) {
        if (result[0].status === 0) {
          return res
            .status(400)
            .json({ message: "User already has a pending invite." });
        } else if (result[0].status === 1) {
          return res
            .status(400)
            .json({ message: "You are friends with the user already." });
        }
      }
      const sql =
        "INSERT INTO friend(friendId, status, userId) VALUES (?,0,?);";
      con.query(sql, [id, senderId], (error, data) => {
        if (error) return res.status(500).json({ message: error });
        return res.json({ message: "Friend request sent." });
      });
    });
  });
});

function getFriendsInvites(senderId, friendId, callback) {
  var sql = `select * from friend f where (f.userId = ${senderId} AND f.friendId = ${friendId}) or (f.friendId = ${senderId} AND f.userId = ${friendId});`;

  con.query(sql, (error, data) => {
    if (error) return callback("Internal server error.", null);
    return callback(null, data);
  });
}

app.post("/friend/request/accept", (req, res) => {
  const senderId = req.query.friendId;
  const receiverId = req.query.id;
  const sql = "UPDATE friend SET status = ? WHERE userId = ? and friendId = ?";
  con.query(sql, [1, senderId, receiverId], (error, data) => {
    if (error)
      return res.status(500).json({ message: "Internal server error" });
    return res.json({ message: "Friend request accepted." });
  });
});

app.post("/friend/request/refuse", (req, res) => {
  const senderId = req.query.friendId;
  const receiverId = req.query.id;
  const sql = "DELETE FROM friend WHERE userId = ? and friendId = ?;";
  con.query(sql, [senderId, receiverId], (error, data) => {
    if (error)
      return res.status(500).json({ message: "Internal server error" });
    return res.json({ message: "Friend request refused." });
  });
});

function getFriends(id, status, callback) {
  var sql = `SELECT u.*, f.*
  FROM user u
  INNER JOIN friend f ON (u.id = f.friendId AND f.userId = ${id})
     OR (u.id = f.userId AND f.friendId = ${id})
  WHERE u.id != ${id} and u.deleted = 0 and u.isSuspended = 0`;

  if (status != null) {
    sql = sql.concat(` and f.status = ${status};`);
  }

  con.query(sql, (error, data) => {
    if (error) return callback("Invalid query.", null);
    const users = data.reduce((acc, row) => {
      if (!acc[row.id]) {
        if (id === row.userId) return acc;
        const profileBuffer = row.profileImage;
        const backgroundBuffer = row.backgroundImage;
        var profileImage = null;

        if (profileBuffer != null) {
          profileImage = Array.from(Buffer.from(profileBuffer));
        }
        var backgroundImage = null;

        if (backgroundBuffer != null) {
          backgroundImage = Array.from(Buffer.from(backgroundBuffer));
        }

        acc[row.id] = {
          id: row.id,
          nickname: row.nickname,
          username: row.username,
          birthday: row.birthday,
          profileImage: profileImage,
          backgroundImage: backgroundImage,
          bio: row.bio,
          createdDate: row.createdDate,
          status: row.status,
        };
      }
      return acc;
    }, {});

    const jsonResult = Object.values(users);
    return callback(null, jsonResult);
  });
}

app.get("/friends", (req, res) => {
  const id = parseInt(req.query.id);
  const status = parseInt(req.query.status);

  if (status > 0 && status > 2) {
    return res.status(400).json({ message: "Invalid status." });
  }

  getFriends(id, status, (error, result) => {
    if (error) return res.status(500).json({ message: error });
    return res.json(result);
  });
});

server.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
