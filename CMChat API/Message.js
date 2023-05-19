class Message {
  constructor(text, senderId, image) {
    (this.text = text), (this.senderId = senderId), (this.image = image);
  }
}

module.exports = Message;
