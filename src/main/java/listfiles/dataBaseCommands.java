package listfiles;

// #goodreads --> https://www.tutorialspoint.com/h2_database/h2_database_create.htm

//Some code reminders, mainly for me
//---
//DROP TABLE TUTORIALS_TBL
//***
//CREATE TABLE tutorials_tbl (
//id INT NOT NULL,
//title VARCHAR(50) NOT NULL,
//author VARCHAR(20) NOT NULL,
//submission_date DATE,
//);
//***

import org.h2.tools.RunScript;

import javax.swing.text.html.ListView;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.FALSE;

public class dataBaseCommands { // TODO rename this shit

    public Connection conn;


    public dataBaseCommands(String dataBaseURL) throws Exception { //, String un, String pw
        conn = DriverManager.getConnection(dataBaseURL);//, un, pw);

    }

    public void initialize() throws Exception {
        ClassLoader classLoader = dataBaseCommands.class.getClassLoader();
        Reader reader = (new InputStreamReader(classLoader.getResourceAsStream("tableInitializer.txt"), "UTF-8"));
        RunScript.execute(conn, reader);
    }

    public void removeTodo_s() throws Exception {
        ClassLoader classLoader = dataBaseCommands.class.getClassLoader();
        Reader reader = (new InputStreamReader(classLoader.getResourceAsStream("dropOld.txt"), "UTF-8"));
        RunScript.execute(conn, reader);
    }

    public void newInitialize() throws Exception {
        ClassLoader classLoader = dataBaseCommands.class.getClassLoader();
        Reader reader = (new InputStreamReader(classLoader.getResourceAsStream("tasks.txt"), "UTF-8"));
        RunScript.execute(conn, reader);
        Reader reader2 = (new InputStreamReader(classLoader.getResourceAsStream("users.txt"), "UTF-8"));
        RunScript.execute(conn, reader2);
        Reader reader3 = (new InputStreamReader(classLoader.getResourceAsStream("description.txt"), "UTF-8"));
        RunScript.execute(conn, reader3);
    }


    public List<Task> getAllTasks(String todoID) throws SQLException {

        List<Task> allTasks = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement("SELECT * FROM tasks WHERE task_group = ?");
             ResultSet resultSet = statement.executeQuery()) {

            statement.setString(1, todoID);

            while (resultSet.next()) {

                Task oneTask = new Task(resultSet.getTimestamp("creation_date").toString(),
                        resultSet.getTimestamp("due_date").toString(),
                        resultSet.getString("headline"),
                        resultSet.getString("text"),
                        Boolean.parseBoolean(resultSet.getString("done")));
                oneTask.setTodo_listID(todoID);
                oneTask.setTaskID(resultSet.getString("id"));

                allTasks.add(oneTask);

            }
        }

        return allTasks;
    }

    public String addTask(Task task) throws SQLException {
        //takes the valeus from task as strings and add them to the sql execution statement
        String taskID = null;

        try (PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO TASKS(CREATION_DATE, DUE_DATE, HEADLINE, TEXT, DONE, task_group) VALUES ( ?, ?, ?, ?, ?, ?);",
                Statement.RETURN_GENERATED_KEYS))
        {
            statement.setString(1, task.getCreationDate());
            statement.setString(2, task.getDeadline());
            statement.setString(3, task.getHeadline());
            statement.setString(4, task.getDescription());
            statement.setBoolean(5, task.getDone());
            statement.setString(6, task.getTodo_listID());
            statement.executeUpdate();

            try (ResultSet insertedKey = statement.getGeneratedKeys()) {
                while (insertedKey.next()) { // kas siin tsüklit üldse on mul vaja?
                    int idString = insertedKey.getInt("id"); // long vist mõistlikum, aga ABs on hetkel int, hiljem muudab kui vaja
                    System.out.println("Inserted and returned task: " + taskID);
                    taskID = Integer.toString(idString); // todo eh...
                }
            }
        }

        return taskID;
    }

    public void deleteTask(int row) throws SQLException {
        /*Flask
        *   cur.execute("DELETE FROM users WHERE id = %s", [id])
            # Number reassesment
            cur.execute("SET @num := 0")
            cur.execute("UPDATE users SET id = @num := (@num+1)")
            cur.execute("ALTER TABLE users AUTO_INCREMENT = 1")*/

        // VERY MOST IMPORTANT: https://stackoverflow.com/questions/740358/reorder-reset-auto-increment-primary-key
        //(SET @count = 0
        // UPDATE `TASKS` SET `TASKS`.`id` = @count:= @count + 1;)


        try (PreparedStatement statement = conn.prepareStatement("DELETE FROM TASKS WHERE id = ?");
             PreparedStatement statement2 = conn.prepareStatement("SET @count = 0");
             PreparedStatement statement3 = conn.prepareStatement("UPDATE `TASKS` SET `TASKS`.`id` = @count:= @count + 1")) {


            statement.setString(1, Integer.toString(row));

            statement.executeUpdate();
            statement2.executeUpdate();
            statement3.executeUpdate();
        }
    }

    public void markAsDone(int row) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement("UPDATE `TASKS` SET done = 'TRUE' WHERE id = ?")) {
            statement.setString(1, Integer.toString(row));
            statement.executeUpdate();
        }
    }

    public void markAsUnDone(int row) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement("UPDATE `TASKS` SET done = 'FALSE' WHERE id = ?")) {
            statement.setString(1, Integer.toString(row));
            statement.executeLargeUpdate();
        }

    }

    public void changeText(int row, String newMessage) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement("UPDATE `TASKS` SET text = ? WHERE id = ?")) {
            statement.setString(1, newMessage);
            statement.setString(2, Integer.toString(row));
            statement.executeUpdate();
        }

    }

    public void changeHeadline(int row, String newHeadline) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement("UPDATE `TASKS` SET headline = ? WHERE id = ?")) {
            statement.setString(1, newHeadline);
            statement.setString(2, Integer.toString(row));
            statement.executeUpdate();
        }

    }

    public void changeCreationDate(int row, String newCreationDate) throws SQLException {  //not needed?
        try (PreparedStatement statement = conn.prepareStatement("UPDATE `TASKS` SET creation_date = ? WHERE id = ?")) {
            statement.setString(1, newCreationDate);
            statement.setString(2, Integer.toString(row));
            statement.executeUpdate();
        }

    }

    public void changeDueDate(int row, String newDueDate) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement("UPDATE `TASKS` SET due_date = ? WHERE id = ?")) {
            statement.setString(1, newDueDate);
            statement.setString(2, Integer.toString(row));
            statement.executeUpdate();

        }
    }

    public String newTodo(int userID) throws SQLException {
        String todoID = null;

        try (PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO DESCRIPTION(GROUP_NAME, OWNER_ID) VALUES (?, ?);",
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "New To-do list");
            statement.setString(2, Integer.toString(userID));
            statement.executeUpdate();

            try (ResultSet insertedKey = statement.getGeneratedKeys()) {
                while (insertedKey.next()) { // kas siin tsüklit üldse on mul vaja?
                    int idString = insertedKey.getInt("id"); // long vist mõistlikum, aga ABs on hetkel int, hiljem muudab kui vaja
                    System.out.println("Inserted and returned: " + todoID);
                    todoID = Integer.toString(idString); // todo eh...
                }
            }
        }

        return todoID;
    }

    public void changeTodoDescription(int index, String todoDescription) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement("UPDATE `DESCRIPTION` SET description = ? WHERE id = ?")) {
            statement.setString(1, todoDescription);
            statement.setString(2, Integer.toString(index));
            statement.executeUpdate();
        }
    }

    public boolean checkuserRegister(String username) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(
                "SELECT * FROM users WHERE username = ?")) {
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            int counter = 0;
            while (resultSet.next()) {
                String userID = resultSet.getString("id");
                counter += 1;
            }
            if (counter > 0) {
                return true;
            }
        }

        return false; // sellist userit ei ole
    }

    public boolean register(String username, String password) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO USERS(USERNAME, PASSWORD) VALUES (?, ?)")) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.executeUpdate();
        }

        return true; // uus user lisatud
    }

    public boolean checkuserLogin(String username, String password) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(
                "SELECT * FROM users WHERE username = ? AND password = ?")) {
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            int counter = 0;
            while (resultSet.next()) {
                String userID = resultSet.getString("id");
                counter += 1;
            }
            if (counter > 0) {
                return true;
            }
        }

        return false;
    }

    public String login(String username, String password) throws SQLException {
        String userID = "0";

        try (PreparedStatement statement = conn.prepareStatement(
                "SELECT id FROM users WHERE username = ? AND password = ?")) {
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            userID = resultSet.getString("id");

        }

        return userID; // tagastab indexi
    }

    public List<Todo_list> getAllUserLists(int userID) throws SQLException {
        List<Todo_list> allUserLists = new ArrayList<>();

        try (PreparedStatement statement = conn.prepareStatement(
                "SELECT * FROM description WHERE owner = ?")) {
            statement.setString(1, Integer.toString(userID));

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String todoDescription = resultSet.getString("description");
                String todoID = resultSet.getString("id");

                Todo_list todoFromDB = new Todo_list(new ArrayList<>(), todoDescription);
                todoFromDB.setTodo_listID(todoID);
                allUserLists.add(todoFromDB);
            }

        }

        for (Todo_list todo_list : allUserLists) {
            String todoID = todo_list.getTodo_listID();
            todo_list.setTasks(getAllTasks(todoID));
        }
        return allUserLists;
    }
}


//The code I have used so far...
//---
//Creating th table:
//CREATE TABLE TASKS (
//        id INT AUTO_INCREMENT PRIMARY KEY,
//        creation_date TIMESTAMP,
//        due_date TIMESTAMP,
//        headline VARCHAR(100),
//    text VARCHAR,
//    done VARCHAR(10),
//);

//Inserting values to table:
//INSERT INTO TASKS VALUES (1,'2005-01-12 08:02:00','2005-01-12 08:02:00','My note','some very random text', FALSE)

/**
 * IMPORTANT!
 * Advanced insert, where id is automatically placed
 * INSERT INTO TASKS(CREATION_DATE, DUE_DATE , HEADLINE, TEXT, DONE) VALUES ( '2005-01-12 08:02:00', '2005-01-12 08:02:00', 'jkl', 'asd', 'FALSE')
 */
