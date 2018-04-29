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

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.FALSE;

public class dataBaseCommands {

    public Connection conn;



    public dataBaseCommands(String dataBaseURL) throws Exception { //, String un, String pw
        conn = DriverManager.getConnection(dataBaseURL);//, un, pw);

    }

    public void initialize() throws Exception {
        ClassLoader classLoader = dataBaseCommands.class.getClassLoader();
        Reader reader = (new InputStreamReader(classLoader.getResourceAsStream("tableInitializer.txt"),"UTF-8"));
        RunScript.execute(conn,reader);
    }

    public void removeTodo_s() throws Exception {
        ClassLoader classLoader = dataBaseCommands.class.getClassLoader();
        Reader reader = (new InputStreamReader(classLoader.getResourceAsStream("dropOld.txt"),"UTF-8"));
        RunScript.execute(conn,reader);
    }

    public void newInitialize() throws Exception {
        ClassLoader classLoader = dataBaseCommands.class.getClassLoader();
        Reader reader = (new InputStreamReader(classLoader.getResourceAsStream("tasks.txt"),"UTF-8"));
        RunScript.execute(conn,reader);
        Reader reader2 = (new InputStreamReader(classLoader.getResourceAsStream("users.txt"),"UTF-8"));
        RunScript.execute(conn,reader2);
        Reader reader3 = (new InputStreamReader(classLoader.getResourceAsStream("description.txt"),"UTF-8"));
        RunScript.execute(conn,reader3);
    }


    public Todo_list getAllTasks() throws SQLException {


        List<Task> allTasks = new ArrayList<>();
        try (PreparedStatement context = conn.prepareStatement("select * from TASKS");
             ResultSet rs = context.executeQuery()) {


            while (rs.next()) {

                Task oneTask = new Task(rs.getTimestamp("creation_date").toString(),
                        rs.getTimestamp("due_date").toString(),
                        rs.getString("headline"),
                        rs.getString("text"),
                        Boolean.parseBoolean(rs.getString("done")));

                allTasks.add(oneTask);

            }
        }


        String desc = "Listname placeholder";
        Todo_list todo_list = new Todo_list(allTasks, desc); // juhuks kui tahaks returnida Todo_list isendit

        return todo_list; //allTasks;
    }

    public void addTask(Task task) throws SQLException {
        //takes the valeus from task as strings and add them to the sql execution statement

        try (PreparedStatement statement = conn.prepareStatement("INSERT INTO TASKS(CREATION_DATE, DUE_DATE , HEADLINE, TEXT, DONE) VALUES ( ?, ?, ?, ?, ?)")) {
            statement.setString(1, task.getCreationDate());
            statement.setString(2, task.getDeadline());
            statement.setString(3, task.getHeadline());
            statement.setString(4, task.getDescription());
            statement.setBoolean(5, task.getDone());
            statement.executeUpdate();
        }
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
