package mypackage;

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

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.getBoolean;

public class dataBaseCommands {

    Connection conn;

    public static void main(String[] args) throws Exception {
        Class.forName("org.h2.Driver");
        dataBaseCommands db = new dataBaseCommands(
                /** CHANGE THIS!!!
                 * AND RUN H2 FIRST */
                "jdbc:h2:tcp://localhost/C:\\Users\\Karel\\Desktop\\To-do-program\\src\\todoBase");
        System.out.println(db.getAllTasks());
    }


    dataBaseCommands(String dataBaseURL) throws SQLException {
        conn = DriverManager.getConnection(dataBaseURL);
        dataBaseCommands[] dataBase = new dataBaseCommands[1];
        // Siin tuleks luua parameetrina antud URL-i põhjal Connection objekt ja salvestada see isendimuutujasse.
        // Draiveri laadimist ei pea siin enam tegema!
    }

    List<List<String>> getAllTasks() throws SQLException {


        List<List<String>> allTasks = new ArrayList<List<String>>();

        PreparedStatement context = conn.prepareStatement("select * from TODO_S");

        ResultSet rs = context.executeQuery();

        while (rs.next()) {
            List<String> oneTask = new ArrayList<String>();


            oneTask.add(Integer.toString(rs.getInt("id")));

            oneTask.add(rs.getTimestamp("creation_date").toString());

            oneTask.add(rs.getTimestamp("due_date").toString());

            oneTask.add(rs.getString("headline"));

            oneTask.add(rs.getString("text"));

            oneTask.add(Boolean.toString(getBoolean("headline")));

            allTasks.add(oneTask);

        }
        rs.close();

        context.close();

        return allTasks;
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
//    done BOOLEAN,
//);

//Inserting values to table:
//INSERT INTO todo_s VALUES (1,'2005-01-12 08:02:00','2005-01-12 08:02:00','My note','some very random text', FALSE)

/**
 * IMPORTANT!
 * Advanced insert, where id is automatically placed
 * INSERT INTO todo_s(CREATION_DATE, DUE_DATE , HEADLINE, TEXT, DONE) VALUES ( '2005-01-12 08:02:00', '2005-01-12 08:02:00', 'jkl', 'asd', 'FALSE')
 */