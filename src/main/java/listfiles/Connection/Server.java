package listfiles.Connection;

import listfiles.Task;
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



                try{
                    dbc.newInitialize();//esmakordsel käivitamisel
                }catch (SQLException e){
                    e.printStackTrace();
                }



                List<String> infoIn = echo.getInfo();


                try {
                    String command = infoIn.get(0);

                    switch (command) {
                        case "get list":
                            System.out.println("yks");
                            System.out.println(dbc.getAllTasks().toString());


                            break;
                        case "show list":
                            System.out.println(dbc.getAllTasks().toString());
                            break;
                        case "addtask":
                            System.out.println("Add task: ");


                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date currentdate = new Date();

                            String entrydate = df.format(currentdate);
                            String date = (String) infoIn.get(1);
                            String head = (String) infoIn.get(2);
                            String text = (String) infoIn.get(3);


                            Task ntask = new Task(entrydate, date, head, text, false);
                            dbc.addTask(ntask);
                            System.out.println("added task: " + ntask.toString());

                            break;
                        case "deletetask":
                            while (true) {
                                try {
                                    String indexstring = (String) infoIn.get(1);

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

                            try {
                                String indexstring = (String) infoIn.get(1);

                                int index = Integer.parseInt(indexstring);
                                dbc.markAsDone(index);

                                System.out.println("Task done.");

                            } catch (NumberFormatException e) {
                                System.out.println("Not a valid index!");
                            }
                            break;
                        default:  // TODO commands to be added: renametodo, descedit, dateedit, renametask, checkuser, register, login
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