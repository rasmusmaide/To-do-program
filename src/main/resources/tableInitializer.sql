CREATE TABLE tasks (
        id INT AUTO_INCREMENT,
        creation_date TIMESTAMP,
        due_date TIMESTAMP,
        headline VARCHAR(100),
    text VARCHAR,
    done VARCHAR(10),
    owner INT,
    task_group INT
);

CREATE TABLE users (
        id INT AUTO_INCREMENT PRIMARY KEY,
        username VARCHAR(100),
        password VARCHAR(100)
);

CREATE TABLE description (
        id INT AUTO_INCREMENT,
        description VARCHAR(100),
        owner INT,
);
