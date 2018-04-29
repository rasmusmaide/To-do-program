package listfiles.Connection;

import listfiles.Task;
import listfiles.Todo_list;
import listfiles.dataBaseCommands;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class Server {

    public static void main(String[] args) throws Exception {
        int server = 1337;

        try (ServerSocket serverSocket = new ServerSocket(server)) {
            System.out.println("now listening on: " + server);


            while (true) {
                Socket socket = serverSocket.accept();
                Echo echo = new Echo(socket);
                new Thread(echo).start();


                ////////////////////////////////
                org.h2.tools.Server h2Server = org.h2.tools.Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers").start();
                Class.forName("org.h2.Driver");
                dataBaseCommands dbc;


                dbc = new dataBaseCommands("jdbc:h2:tcp://localhost/~/todoBase");


                //dbc.removeTodo_s();//kui on todo_s tabeliga variant veel alles


                try {
                    dbc.newInitialize();//esmakordsel käivitamisel
                } catch (SQLException e) {
                    e.printStackTrace();
                }


                List<String> infoIn = echo.getInfo();


                try {
                    String command = infoIn.get(0);

                    switch (command) {
                        case "get lists": // {"get lists", task.getTaskID()}
                            String userIDstring = infoIn.get(1);
                            int userID = Integer.parseInt(userIDstring);

                            /*List<Todo_list> userLists = dbc.getAllUserLists(userID);

                            for (Todo_list todo_list : userLists) {
                                System.out.println(todo_list.toString());

                            }*/

                            // todo saada tagasi

                            break;
                        case "show list":
                            System.out.println(dbc.getAllTasks().toString());
                            break;
                        case "addtask":
                            System.out.println("Add task: ");

                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date currentdate = new Date();

                            String entrydate = df.format(currentdate);
                            String date = infoIn.get(1);
                            String head = infoIn.get(2);
                            String text = infoIn.get(3);


                            Task ntask = new Task(entrydate, date, head, text, false);
                            dbc.addTask(ntask);
                            System.out.println("added task: " + ntask.toString());
                            // TODO saada tagasi

                            break;
                        case "deletetask": // {"deletetask", task.getTaskID()}
                            //while (true) { // TODO miks while tsykkel?
                                try {
                                    String indexstring = infoIn.get(1);

                                    int index = Integer.parseInt(indexstring);
                                    dbc.deleteTask(index);

                                    System.out.println("Task deleted successfully.");
                                    break;
                                } catch (NumberFormatException e) {
                                    System.out.println("Not a valid index!");
                                }
                            //}
                            break;
                        case "done":
                            try {
                                String indexstring = infoIn.get(1);

                                int index = Integer.parseInt(indexstring);
                                dbc.markAsDone(index);

                                System.out.println("Task marked as done.");

                            } catch (NumberFormatException e) {
                                System.out.println("Not a valid index!");
                            }
                            break;
                        case "undone":
                            try {
                                String indexstring = infoIn.get(1);

                                int index = Integer.parseInt(indexstring);
                                dbc.markAsUnDone(index);

                                System.out.println("Task marked as undone.");

                            } catch (NumberFormatException e) {
                                System.out.println("Not a valid index!");
                            }
                            break;
                        case "descedit": // {"descedit", task.getTaskID(), fieldtext};
                            try {
                                String indexstring = infoIn.get(1);

                                int index = Integer.parseInt(indexstring);

                                String taskDescription = infoIn.get(2);
                                dbc.changeText(index, taskDescription);

                                System.out.println("Task description changed to: " + taskDescription);

                            } catch (NumberFormatException e) {
                                System.out.println("Not a valid index!");
                            }
                            break;

                        case "dateedit": // {"dateedit", task.getTaskID(), duedate};
                            try {
                                String indexstring = infoIn.get(1);

                                int index = Integer.parseInt(indexstring);

                                String taskDuedate = infoIn.get(2);
                                dbc.changeDueDate(index, taskDuedate);

                                System.out.println("Task duedate changed to: " + taskDuedate);

                            } catch (NumberFormatException e) {
                                System.out.println("Not a valid index!");
                            }
                            break;
                        case "renametask": // {"renametask", task.getTaskID(), fieldtext};
                            try {
                                String indexstring = infoIn.get(1);

                                int index = Integer.parseInt(indexstring);

                                String taskHeadline = infoIn.get(2);
                                dbc.changeHeadline(index, taskHeadline);

                                System.out.println("Task headline changed to: " + taskHeadline);

                            } catch (NumberFormatException e) {
                                System.out.println("Not a valid index!");
                            }
                            break;
                        case "renametodo": // {"renametodo", todo_list.getTodo_listID(), fieldtext};
                            try {
                                String indexstring = infoIn.get(1);

                                int index = Integer.parseInt(indexstring);

                                String todoDescription = infoIn.get(2);
                                //dbc.changeTodoDescription(index, todoDescription); // TODO sellise funktsiooniga käsku dbcs ei ole

                                System.out.println("Todolist name changed to: " + todoDescription);

                            } catch (NumberFormatException e) {
                                System.out.println("Not a valid index!");
                            }
                            break;
                        case "checkuser":
                            try {
                                String username = infoIn.get(1);

                                String password = infoIn.get(2);
                                //dbc.checkuser(username, password); // TODO sellise funktsiooniga käsku dbcs ei ole

                                System.out.println("User found: " + username + " " + password);

                                //int index = Integer.parseInt(indexstring); TODO saadab userID tagasi

                            } catch (NumberFormatException e) {
                                System.out.println("Not a valid index!");
                            }

                            break;
                        case "register":
                            try {
                                String username = infoIn.get(1);

                                String password = infoIn.get(2);
                                //dbc.register(username, password); // TODO sellise funktsiooniga käsku dbcs ei ole

                                System.out.println("User signed up: " + username + " " + password);

                                //int index = Integer.parseInt(indexstring); TODO saadab userID tagasi

                            } catch (NumberFormatException e) {
                                System.out.println("Not a valid index!");
                            }
                            break;
                        case "login":
                            try {
                                String username = infoIn.get(1);

                                String password = infoIn.get(2);
                                //dbc.login(username, password); // TODO sellise funktsiooniga käsku dbcs ei ole

                                System.out.println("User logged in: " + username + " " + password);

                                //int index = Integer.parseInt(indexstring); TODO saadab userID tagasi

                            } catch (NumberFormatException e) {
                                System.out.println("Not a valid index!");
                            }
                            break;
                        default:
                            System.out.println("Not a command!");

                            break;
                    }


                } finally {
                    dbc.conn.close();
                    h2Server.stop();
                }
                //////////////////////////////////
            }
        }

    }
}