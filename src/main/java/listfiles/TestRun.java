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
            label:
            while (true) {
                System.out.println("Enter command: ");
                String str = scanner.nextLine();

                switch (str) {
                    case "exit":
                        System.out.println("Bye!");
                        break label;
                    case "show list":
                        System.out.println(dbc.getAllTasks());
                        break;
                    case "add":   // 2005-01-12 08:03:00;2005-01-12 08:02:00;juust;kapsas;TRUE
                        System.out.println("Add task: "); // ("2005-01-12 08:03:00", "2005-01-12 08:02:00", "pealkiri", "tegevus", FALSE);
                        String[] tasktext = scanner.nextLine().split(";"); // ";" error potential
                        Task ntask = new Task(tasktext[0], tasktext[1], tasktext[2], tasktext[3], Boolean.parseBoolean(tasktext[4]));
                        dbc.addTask(ntask);
                        System.out.println("added task: " + ntask.toString());
                        //System.out.println(dbc.getAllTasks());
                        break;
                    case "delete":
                        System.out.println("Enter index: ");
                        int index = Integer.parseInt(scanner.nextLine());
                        dbc.deleteTask(index);
                        System.out.println("Task deleted successfully.");
                        break;
                    default:
                        System.out.println("Not a command!");
                        break;
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

