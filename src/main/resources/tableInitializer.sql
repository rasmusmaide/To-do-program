CREATE TABLE tasks (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  creation_date TIMESTAMP,
  due_date      TIMESTAMP,
  headline      VARCHAR(100),
  description   VARCHAR,
  done          VARCHAR(10),
  user_id       INT,
  todo_id       INT
);

CREATE TABLE users (
  id       INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100),
  passwordKey VARCHAR(100),
  salt     VARCHAR(100)
);

CREATE TABLE todos (
  id          INT AUTO_INCREMENT,
  description VARCHAR(100),
  user_id     INT,
);

ALTER TABLE tasks
  ADD CONSTRAINT fk_tasks_owner_to_users FOREIGN KEY (user_id) REFERENCES users (id);