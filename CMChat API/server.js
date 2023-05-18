const express = require("express");
const cors = require("cors");
const http = require("http");
const socketIO = require("socket.io");
const PORT = 8081;

const app = express();
const server = http.createServer(app);
const io = socketIO(server);

io.on("connection", (socket) => {
  console.log("A user connected!");

  socket.on("disconnect", () => {
    console.log("A client disconnected");
  });

  socket.on("newMessage", (message) => {
    console.log("New message received:", message);
  });
});

server.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
