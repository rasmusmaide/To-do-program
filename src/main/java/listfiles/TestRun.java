package listfiles;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class TestRun {
    private Connection conn;
    public static void main(String[] args) throws Exception {
        Class.forName("org.h2.Driver");
        File file = new File("todoBase");
        TestRun db = new TestRun(
                "jdbc:h2:tcp://localhost/" + file.getAbsolutePath());

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println();
            String str = scanner.nextLine();

            if (str.equals("exit")) {
                System.out.println("sdasd");
                break;
            }
        }

        db.conn.close();
    }


    public TestRun(String dataBaseURL) throws SQLException {
        this.conn = DriverManager.getConnection(dataBaseURL);

    }


}

