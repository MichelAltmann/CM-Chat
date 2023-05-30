const express = require("express");
const cors = require("cors");
const http = require("http");
const socketIO = require("socket.io");
const PORT = 8081;
const Message = require("./Message");
const bodyParser = require("body-parser");

const con = require("./connection");
const { parse } = require("path");

const app = express();
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());
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

  const sql = "SELECT * FROM user WHERE username = ? and password = ?";

  con.query(sql, [username, password], (error, data) => {
    if (error)
      return res
        .status(401)
        .json({ success: false, message: "Invalid username or password" });

    if (data.length > 0) {
      return res.json(data[0]);
    } else {
      return res
        .status(401)
        .json({ success: false, message: "Invalid username or password" });
    }
  });
});

app.put("/signup", (req, res) => {
  const user = req.body;
  const checkSql = "SELECT * FROM user WHERE email = ?";
  con.query(checkSql, user.email, (error, data) => {
    if (error)
      return res.status(500).json({ message: "Internal server error." });
    if (data.length > 0) {
      return res.status(400).json({ message: "Email already in use" });
    } else {
      const usernameSql = "SELECT * FROM user WHERE username = ?";
      con.query(usernameSql, user.username, (error, data) => {
        if (error)
          return res.status(500).json({ message: "Internal server error." });
        if (data.length > 0) {
          return res.status(400).json({ message: "Username already in use" });
        } else {
          const sql =
            "INSERT INTO user(id, email, password, username, birthday, profileImage, backgroundImage, bio, createdDate, deleted, isSuspended)" +
            "VALUES(?,?,?,?,?,?,?,?,?,?,?)";
          con.query(
            sql,
            [
              user.id,
              user.email,
              user.password,
              user.username,
              parseDateToSQL(user.birthday),
              user.profileImage,
              user.backgroundImage,
              user.bio,
              parseDateToSQL(user.createdDate),
              user.deleted,
              user.isSuspended,
            ],
            (error, data) => {
              if (error) return res.send(error);
              res.status(200).json({ message: "Signup Successful" });
            }
          );
        }
      });
    }
  });
});

function parseDateToSQL(dateString) {
  const months = {
    Jan: "01",
    Feb: "02",
    Mar: "03",
    Apr: "04",
    May: "05",
    Jun: "06",
    Jul: "07",
    Aug: "08",
    Sep: "09",
    Oct: "10",
    Nov: "11",
    Dec: "12",
  };

  const dateObj = new Date(dateString);
  const month = months[dateObj.toLocaleString("en", { month: "short" })];
  const day = String(dateObj.getDate()).padStart(2, "0");
  const year = dateObj.getFullYear();

  return `${year}-${month}-${day}`;
}

app.get("/friends/:id", (req, res) => {
  const id = parseInt(req.params.id);
  const sql = `SELECT u.*
  FROM user u
  INNER JOIN friend f ON (u.id = f.friendId AND f.userId = ${id})
     OR (u.id = f.userId AND f.friendId = ${id})
  WHERE u.id != ${id} and u.deleted = 0 and u.isSuspended = 0;`;

  con.query(sql, (error, data) => {
    if (error) return res.status(500).json({ message: "Invalid id." });

    const users = data.reduce((acc, row) => {
      if (!acc[row.id]) {
        acc[row.id] = {
          id: row.id,
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
