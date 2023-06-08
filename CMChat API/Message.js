class Message {
  constructor(
    receiverId,
    text,
    senderId,
    senderName,
    senderImage,
    image,
    status
  ) {
    (this.receiverId = receiverId),
      (this.senderName = senderName),
      (this.senderImage = senderImage),
      (this.text = text),
      (this.senderId = senderId),
      (this.image = image),
      (this.status = status);
  }
}

module.exports = Message;
