package listfiles;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class TestRun {
    //private Connection conn;

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
                        System.out.println(dbc.getAllTasks().toString());
                        break;
                    case "add":   // format: 2005-01-12 08:02:00;juust;kapsas
                        System.out.println("Add task: ");

                        String text = scanner.nextLine();
                        if (text.equals("cancel")) break;
                        String[] tasktext = text.split(";"); // ";" error potential

                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date currentdate = new Date();

                        String entrydate = df.format(currentdate);
                        String date = tasktext[0];
                        String head = tasktext[1];
                        String desc = tasktext[2];


                        Task ntask = new Task(entrydate, date, head, desc, false);
                        dbc.addTask(ntask);
                        System.out.println("added task: " + ntask.toString());

                        break;
                    case "delete":
                        while (true) {
                            System.out.println("Enter index: ");

                            try {
                                String indexstring = scanner.nextLine();
                                if (indexstring.equals("cancel")) break;

                                int index = Integer.parseInt(indexstring);
                                dbc.deleteTask(index);

                                System.out.println("Task deleted successfully.");
                                break;
                            } catch (NumberFormatException e) {
                                System.out.println("Not a valid index!");
                            }
                        }

                        break;
                    case "done":
                        System.out.println("Enter index: ");

                        try {
                            String indexstring = scanner.nextLine();
                            if (indexstring.equals("cancel")) break;

                            int index = Integer.parseInt(indexstring);
                            dbc.markAsDone(index);

                            System.out.println("Task done.");
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Not a valid index!");
                        }
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


    /*public TestRun(String dataBaseURL) throws SQLException {
        this.conn = DriverManager.getConnection(dataBaseURL);

    }*/


}

