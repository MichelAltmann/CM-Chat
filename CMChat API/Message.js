class Message {
  constructor(text, senderId, image, status) {
    (this.text = text),
      (this.senderId = senderId),
      (this.image = image),
      (this.status = status);
  }
}

module.exports = Message;
