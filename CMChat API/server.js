const express = require("express");
const cors = require("cors");
const http = require("http");
const socketIO = require("socket.io");
const PORT = 8081;
const Message = require("./Message");

const app = express();
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
    console.log("New message received:", messageObj.text);
    const message = new Message(messageObj.text, messageObj.senderId);
    io.emit("newMessage", message);
  });
});

server.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
