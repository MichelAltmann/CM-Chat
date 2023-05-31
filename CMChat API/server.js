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

app.get("/friends", (req, res) => {
  const id = parseInt(req.query.id);
  const status = parseInt(req.query.status);
  const sql = `SELECT u.*
  FROM user u
  INNER JOIN friend f ON (u.id = f.friendId AND f.userId = ${id})
     OR (u.id = f.userId AND f.friendId = ${id})
  WHERE u.id != ${id} and u.deleted = 0 and u.isSuspended = 0 and f.status = ${status};`;

  con.query(sql, (error, data) => {
    if (error) return res.status(500).json({ message: "Invalid id." });

    const users = data.reduce((acc, row) => {
      if (!acc[row.id]) {
        acc[row.id] = {
          id: row.id,
          nickname: row.nickname,
          username: row.username,
          birthday: row.birthday,
          profileImage: row.profileImage,
          backgroundImage: row.backgroundImage,
          bio: row.bio,
          createdDate: row.createdDate,
        };
      }
      return acc;
    }, {});

    const jsonResult = Object.values(users);
    return res.json(jsonResult);
  });
});

server.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
