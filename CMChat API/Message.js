class Message {
  constructor(receiverId, text, senderId, image, status) {
    (this.receiverId = receiverId),
      (this.text = text),
      (this.senderId = senderId),
      (this.image = image),
      (this.status = status);
  }
}

module.exports = Message;
