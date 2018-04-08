package listfiles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestRun {
    private Connection conn;
    public static void main(String[] args) throws Exception {
        Class.forName("org.h2.Driver");
        TestRun db = new TestRun(
                /** CHANGE THIS!!!
                 * AND RUN H2 FIRST */
                "jdbc:h2:tcp://localhost/C:\\Users\\Dell\\Desktop\\ \\To-do-program\\src\\todoBase");


        db.conn.close();
    }


    public TestRun(String dataBaseURL) throws SQLException {
        this.conn = DriverManager.getConnection(dataBaseURL);

    }


}

