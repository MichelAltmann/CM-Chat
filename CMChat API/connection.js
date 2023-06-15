const mysql = require("mysql");

const con = mysql.createConnection({
  host: "localhost",
  user: "root",
  password: "",
  database: "cmchatdb",
  charset: "utf8mb4",
  collation: "utf8mb4_unicode_ci",
});

module.exports = con;
