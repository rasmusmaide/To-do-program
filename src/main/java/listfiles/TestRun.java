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
        /*TestRun db = new TestRun(
                "jdbc:h2:tcp://localhost/" + file.getAbsolutePath());
*/
        dataBaseCommands dbc = new dataBaseCommands("jdbc:h2:tcp://localhost/" + file.getAbsolutePath());

        Scanner scanner = new Scanner(System.in);

        System.out.println(dbc.getAllTasks());
        try {
            while (true) {
                System.out.println("Enter command: ");
                String str = scanner.nextLine();

                /*switch (str) {
                    case "exit":
                        System.out.println("Bye!");
                        break;
                    case "show list":
                        dbc.getAllTasks();
                    default:
                        System.out.println("Not a command!");
                }*/
                if (str.equals("exit")) {
                    System.out.println("Bye!");
                    break;
                } else if (str.equals("show list")) {
                    System.out.println(dbc.getAllTasks());

                } else if (str.equals("add")) {
                    System.out.println("enter task here");

                } else if (str.equals("delete")) {
                    System.out.println("enter task here");

                } else {
                    System.out.println("Not a command!");
                }



            }
        } finally {
            dbc.conn.close();
        }


    }


    public TestRun(String dataBaseURL) throws SQLException {
        this.conn = DriverManager.getConnection(dataBaseURL);

    }


}

