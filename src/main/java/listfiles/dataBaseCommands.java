package listfiles;

import org.h2.tools.RunScript;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseCommands {

    public Connection conn;

    public DataBaseCommands(String dataBaseURL) throws Exception { //, String un, String pw
        conn = DriverManager.getConnection(dataBaseURL);//, un, pw);

    }

    public void initialize() throws Exception {
        ClassLoader classLoader = DataBaseCommands.class.getClassLoader();
        Reader reader = (new InputStreamReader(classLoader.getResourceAsStream("tableInitializer.sql"), "UTF-8"));
        RunScript.execute(conn, reader);
    }

    public void removeTodo_s() throws Exception {
        ClassLoader classLoader = DataBaseCommands.class.getClassLoader();
        Reader reader = (new InputStreamReader(classLoader.getResourceAsStream("dropOld.txt"), "UTF-8"));
        RunScript.execute(conn, reader);
    }

    public List<Task> getAllTasks(String todoID) throws SQLException {

        List<Task> allTasks = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement("SELECT * FROM tasks WHERE todo_id = ?")) {
            statement.setString(1, todoID);

            ResultSet resultSet = statement.executeQuery();


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
            resultSet.close();
        }

        return allTasks;
    }

    public String addTask(Task task) throws SQLException {
        //takes the valeus from task as strings and add them to the sql execution statement
        String taskID = null;

        try (PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO tasks(creation_date, due_date, headline, description, done, todo_id) VALUES ( ?, ?, ?, ?, ?, ?);",
                Statement.RETURN_GENERATED_KEYS)) {
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

        try (PreparedStatement statement = conn.prepareStatement("DELETE FROM TASKS WHERE id = ?");
             PreparedStatement statement2 = conn.prepareStatement("SET @count = 0");
             PreparedStatement statement3 = conn.prepareStatement("UPDATE `TASKS` SET `TASKS`.`id` = @count:= @count + 1")) {


            statement.setString(1, Integer.toString(row));

            statement.executeUpdate();
            statement2.executeUpdate();
            statement3.executeUpdate();
        }
    } // TODO transaction

    public void markAsDone(int row) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement("UPDATE tasks SET done = 'TRUE' WHERE id = ?")) {
            statement.setString(1, Integer.toString(row));
            statement.executeUpdate();
        }
    }

    public void markAsUnDone(int row) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement("UPDATE tasks SET done = 'FALSE' WHERE id = ?")) {
            statement.setString(1, Integer.toString(row));
            statement.executeLargeUpdate();
        }

    }

    public void changeText(int row, String newDescription) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement("UPDATE tasks SET description = ? WHERE id = ?")) {
            statement.setString(1, newDescription);
            statement.setString(2, Integer.toString(row));
            statement.executeUpdate();
        }

    }

    public void changeHeadline(int row, String newHeadline) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement("UPDATE tasks SET headline = ? WHERE id = ?")) {
            statement.setString(1, newHeadline);
            statement.setString(2, Integer.toString(row));
            statement.executeUpdate();
        }

    }

    public void changeDueDate(int row, String newDueDate) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement("UPDATE tasks SET due_date = ? WHERE id = ?")) {
            statement.setString(1, newDueDate);
            statement.setString(2, Integer.toString(row));
            statement.executeUpdate();

        }
    }

    public String newTodo(int userID) throws SQLException {
        String todoID = null;

        try (PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO todos(description, user_id) VALUES (?, ?);",
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
        try (PreparedStatement statement = conn.prepareStatement("UPDATE todos SET description = ? WHERE id = ?")) {
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
    } // TODO kontroll enne regamist

    public void register(String username, String password) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO USERS(username, password) VALUES (?, ?)")) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.executeUpdate();
        }

        //return true; // uus user lisatud
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
    } // TODO kontroll enne sisse logimist

    public String login(String username, String password) throws SQLException {
        String userID = "0";

        try (PreparedStatement statement = conn.prepareStatement(
                "SELECT * FROM users WHERE username = ? AND password = ?")) {
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int userIDInt = resultSet.getInt(1);
            userID = Integer.toString(userIDInt);
            System.out.println(userID);
            resultSet.close();

        }

        return userID; // tagastab indexi
    }

    public List<Todo_list> getAllUserLists(int userID) throws SQLException {
        List<Todo_list> allUserLists = new ArrayList<>();

        try (PreparedStatement statement = conn.prepareStatement(
                "SELECT * FROM todos WHERE user_id = ?")) {
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