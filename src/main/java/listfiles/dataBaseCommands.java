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

    Connection conn;



    public dataBaseCommands(String dataBaseURL) throws Exception {
        conn = DriverManager.getConnection(dataBaseURL);

    }

    public void initialize() throws Exception {
        Reader reader = new BufferedReader(new FileReader("tableInitializer.txt"));
        RunScript.execute(conn,reader);
    }

    public Todo_list getAllTasks() throws SQLException {


        List<Task> allTasks = new ArrayList<>();
        try (PreparedStatement context = conn.prepareStatement("select * from TODO_S");
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

    void addTask(Task task) throws SQLException {
        //takes the valeus from task as strings and add them to the sql execution statement

        try (PreparedStatement statement = conn.prepareStatement("INSERT INTO todo_s(CREATION_DATE, DUE_DATE , HEADLINE, TEXT, DONE) VALUES ( ?, ?, ?, ?, ?)")) {
            statement.setString(1, task.getCreationDate());
            statement.setString(2, task.getDeadline());
            statement.setString(3, task.getHeadline());
            statement.setString(4, task.getDescription());
            statement.setBoolean(5, task.getDone());
            statement.executeUpdate();
            statement.close();
        }
    }

    void deleteTask(int row) throws SQLException {
        /*Flask
        *   cur.execute("DELETE FROM users WHERE id = %s", [id])
            # Number reassesment
            cur.execute("SET @num := 0")
            cur.execute("UPDATE users SET id = @num := (@num+1)")
            cur.execute("ALTER TABLE users AUTO_INCREMENT = 1")*/

        // VERY MOST IMPORTANT: https://stackoverflow.com/questions/740358/reorder-reset-auto-increment-primary-key
        //(SET @count = 0
        // UPDATE `todo_s` SET `todo_s`.`id` = @count:= @count + 1;)


        try (PreparedStatement statement = conn.prepareStatement("DELETE FROM todo_s WHERE id = ?");
             PreparedStatement statement2 = conn.prepareStatement("SET @count = 0");
             PreparedStatement statement3 = conn.prepareStatement("UPDATE `todo_s` SET `todo_s`.`id` = @count:= @count + 1")) {


            statement.setString(1, Integer.toString(row));

            statement.executeUpdate();
            statement2.executeUpdate();
            statement3.executeUpdate();


            statement.close();
            statement2.close();
            statement3.close();


        }
    }

    void markAsDone(int row) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement("UPDATE `todo_s` SET done = 'TRUE' WHERE id = ?")) {


            statement.setString(1, Integer.toString(row));


            //statement.executeLargeUpdate();
            statement.executeUpdate();

            statement.close();
        }
    }


    void markAsUnDone(int row) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement("UPDATE `todo_s` SET done = 'FALSE' WHERE id = ?")) {


            statement.setString(1, Integer.toString(row));

            statement.executeLargeUpdate();

            statement.close();
        }

    }

    void changeText(int row, String newMessage) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement("UPDATE `todo_s` SET text = ? WHERE id = ?")) {
            statement.setString(1, newMessage);
            statement.setString(2, Integer.toString(row));

            statement.executeUpdate();

            statement.close();
        }

    }

    void changeHeadline(int row, String newHeadline) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement("UPDATE `todo_s` SET headline = ? WHERE id = ?")) {
            statement.setString(1, newHeadline);
            statement.setString(2, Integer.toString(row));

            statement.executeUpdate();

            statement.close();
        }

    }

    void changeCreationDate(int row, String newCreationDate) throws SQLException {  //not needed?
        try (PreparedStatement statement = conn.prepareStatement("UPDATE `todo_s` SET creation_date = ? WHERE id = ?")) {
            statement.setString(1, newCreationDate);
            statement.setString(2, Integer.toString(row));

            statement.executeUpdate();

            statement.close();
        }

    }


    void changeDueDate(int row, String newDueDate) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement("UPDATE `todo_s` SET due_date = ? WHERE id = ?")) {
            statement.setString(1, newDueDate);
            statement.setString(2, Integer.toString(row));

            statement.executeUpdate();

            statement.close();

        }
    }

}


//The code I have used so far...
//---
//Creating th table:
//CREATE TABLE ToDo_s (
//        id INT AUTO_INCREMENT PRIMARY KEY,
//        creation_date TIMESTAMP,
//        due_date TIMESTAMP,
//        headline VARCHAR(100),
//    text VARCHAR,
//    done VARCHAR(10),
//);

//Inserting values to table:
//INSERT INTO todo_s VALUES (1,'2005-01-12 08:02:00','2005-01-12 08:02:00','My note','some very random text', FALSE)

/**
 * IMPORTANT!
 * Advanced insert, where id is automatically placed
 * INSERT INTO todo_s(CREATION_DATE, DUE_DATE , HEADLINE, TEXT, DONE) VALUES ( '2005-01-12 08:02:00', '2005-01-12 08:02:00', 'jkl', 'asd', 'FALSE')
 */