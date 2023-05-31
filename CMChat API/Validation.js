class Validation {
  constructor(con) {
    this.con = con;
    this.signup = this.signup.bind(this);
  }

  checkEmail(email, callback) {
    const checkSql = "SELECT * FROM user WHERE email = ?";
    this.con.query(checkSql, email, (error, data) => {
      if (error) return callback("Internal server error.", null);
      if (data.length > 0) {
        return callback("Email already in use", null);
      } else {
        return callback(null, "Email available");
      }
    });
  }

  checkUsername(user, callback) {
    const usernameSql = "SELECT * FROM user WHERE username = ?";
    this.con.query(usernameSql, user.username, (error, data) => {
      if (error) return callback("Internal server error.", null);
      if (data.length > 0 && data[0].id != user.id) {
        return callback("Username already in use", null);
      } else {
        return callback(null, "Username available");
      }
    });
  }

  signup(req, res) {
    const user = req.body;
    this.checkEmail(user.email, (error, result) => {
      if (error) return res.status(500).json({ message: error });
      this.checkUsername(user, (error, result) => {
        if (error) return res.status(500).json({ message: error });
        const sql =
          "INSERT INTO user(id, email, password,nickname, username, birthday, profileImage, backgroundImage, bio, createdDate, deleted, isSuspended)" +
          "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        this.con.query(
          sql,
          [
            user.id,
            user.email,
            user.password,
            user.username,
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
      });
    });
  }

  static parseDateToSQL(dateString) {
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
}

module.exports = Validation;
