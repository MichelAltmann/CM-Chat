const express = require("express");
const cors = require("cors");
const http = require("http");
const socketIO = require("socket.io");
const PORT = 8081;
const Message = require("./Message");
const bodyParser = require("body-parser");
const Validation = require("./Validation");
const path = require("path");

const con = require("./connection");
const multer = require("multer");
const fs = require("fs");

const validation = new Validation(con);

const app = express();
app.use(bodyParser.urlencoded({ limit: "10mb", extended: true }));
app.use(bodyParser.json({ limit: "10mb" }));
app.use(cors({ origin: "*" }));
app.use((req, res, next) => {
  res.setHeader("Content-Type", "text/html; charset=utf-8");
  next();
});
const server = http.createServer(app);
const io = socketIO(server, {
  cors: {
    origin: "*", // Allow requests from any origin
    methods: ["GET", "POST"], // Allow only GET and POST requests
    allowedHeaders: ["Content-Type"], // Allow only specific headers
  },
});

// webSocket server for handling calls

const WebSocket = require("websocket").server;
const webSocket = new WebSocket({ httpServer: server });

const users = [];

webSocket.on("request", (req) => {
  const connection = req.accept();
  // console.log(connection);

  connection.on("message", (message) => {
    const data = JSON.parse(message.utf8Data);
    const user = findUser(data.name);

    switch (data.type) {
      case "store_user":
        if (user != null) {
          connection.send(
            JSON.stringify({
              type: "user already exists",
            })
          );
          return;
        }
        const newUser = {
          name: data.name,
          conn: con.connection,
        };
        users.push(newUser);
        break;
      case "start_call":
        let userToCall = findUser(data.target);

        if (userToCall) {
          connection.send(
            JSON.stringify({
              type: "call_response",
              data: "user is ready for call",
            })
          );
        } else {
          connection.send(
            JSON.stringify({
              type: "call_response",
              data: "user is not online",
            })
          );
        }

        break;

      case "create_offer":
        let userToReceiveOffer = findUser(data.target);

        if (userToReceiveOffer) {
          userToReceiveOffer.conn.send(
            JSON.stringify({
              type: "offer_received",
              name: data.name,
              data: data.data.sdp,
            })
          );
        }
        break;
      case "create_answer":
        let userToReceiveAnswer = fiondUser(data.target);
        if (userToReceiveAnswer) {
          userToReceiveAnswer.conn.send(
            JSON.stringify({
              type: "answer_received",
              name: data.name,
              data: data.data.sdp,
            })
          );
        }
        break;

      case "ice_candidate":
        let userToReceiveIceCandidate = findUser(data.target);
        if (userToReceiveIceCandidate) {
          userToReceiveIceCandidate.conn.send(
            JSON.stringify({
              type: "ice_candidate",
              name: data.name,
              data: {
                sdpMLineIndex: data.data.sdpMLineIndex,
                sdpMid: data.data.sdpMid,
                sdpCandidate: data.data.sdpCandidate,
              },
            })
          );
        }
        break;
    }
  });

  connection.on("close", () => {
    users.forEach((user) => {
      if (user.conn === connection) {
        users.splice(users.indexOf(user), 1);
      }
    });
  });
});

function findUser(name) {
  users.forEach((user) => {
    if (name === user.name) return user;
  });
}

app.use("/images", express.static(path.join("images")));

app.get("/image", (req, res) => {
  if (req.query.imageId == null) {
    return res.status(400).send({ message: "Bad request." });
  }
  const image = `${__dirname}/images/${req.query.imageId}`; // Replace with the actual path to your image
  res.sendFile(image);
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
      messageObj.senderName,
      messageObj.senderImage,
      messageObj.image,
      "sent"
    );
    io.emit("newMessage" + message.receiverId, message);
    io.emit("newMessage" + message.senderId, message);
  });
});

const storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, "images");
  },
  filename: function (req, file, cb) {
    const uniqueSuffix = Date.now() + "-" + Math.random() * 1e20;
    cb(null, file.originalname + "-" + uniqueSuffix + ".jpg");
  },
});

const upload = multer({ storage: storage });

app.post("/image", upload.single("image"), (req, res) => {
  const image = req.file;
  if (image == null) {
    return res.status(400).json({ message: "Bad request." });
  }

  const imageId = image.filename;

  return res.json({ imageId: imageId });
});

app.delete("/image", (req, res) => {
  const lastImageId = req.query.lastImageId;
  if (lastImageId == null) {
    return res.status(400).json({ message: "No image found." });
  }
  deleteImage(lastImageId, (error, result) => {
    if (error) {
      console.log(error);
      return res.status(400).json({ error });
    }

    return res.status(200).json({ message: "Image deleted successfully." });
  });
});

function deleteImage(imageId, callback) {
  const imagePath = "images/" + imageId;
  console.log(imagePath);
  fs.access(imagePath, fs.constants.F_OK, (error) => {
    if (error) {
      // File does not exist
      return callback("Image not found", null);
    }

    // Delete the file using Multer's removeFile method
    fs.unlink(imagePath, (error) => {
      if (error) {
        // Error occurred while deleting the file
        return callback("Internal server error", null);
      }

      return callback(null, "Image deleted successfully");
    });
  });
}

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

      const user = {
        id: row.id,
        nickname: row.nickname,
        username: row.username,
        birthday: row.birthday,
        profileImage: row.profileImage,
        backgroundImage: row.backgroundImage,
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

    if (user.profileImage != null) {
      sql = sql.concat(", profileImage = ?");
      userEdit.push(user.profileImage);
    }
    if (user.backgroundImage != null) {
      sql = sql.concat(", backgroundImage = ?");
      userEdit.push(user.backgroundImage);
    }

    sql = sql.concat("where id = ?;");
    userEdit.push(user.id);

    con.query(sql, userEdit, (error, data) => {
      if (error) {
        console.log(error);
        return res.status(500).json({ message: "Internal server error." });
      }
      const userSql = "SELECT * FROM user WHERE id = ?;";
      con.query(userSql, user.id, (error, data) => {
        if (error)
          return res.status(500).json({ message: "Internal server error." });
        if (data.length > 0) {
          row = data[0];

          const editedUser = {
            id: row.id,
            nickname: row.nickname,
            username: row.username,
            birthday: row.birthday,
            profileImage: row.profileImage,
            backgroundImage: row.backgroundImage,
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
        if (id === row.userId && row.status === 0) return acc;

        acc[row.id] = {
          id: row.id,
          nickname: row.nickname,
          username: row.username,
          birthday: row.birthday,
          profileImage: row.profileImage,
          backgroundImage: row.backgroundImage,
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

app.get("/", (req, res) => {
  console.log("a" + req.ip);
  return res.json("Salve fdp");
});

server.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
