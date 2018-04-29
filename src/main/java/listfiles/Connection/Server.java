package listfiles.Connection;

import listfiles.Task;
import listfiles.dataBaseCommands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

class Echo implements Runnable {

    private Socket serverSocket;

    public Echo(Socket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static List infoIn;

    @Override
    public void run() {

        try {

            try (
                    DataInputStream in = new DataInputStream(serverSocket.getInputStream());
                    DataOutputStream out = new DataOutputStream(serverSocket.getOutputStream())) {
                System.out.println("client connected; waiting for a byte");


                int intIn = in.readInt();

                infoIn = new ArrayList<>();

                for (int i = 0; i < intIn; i++) {
                    String getInfoIn = in.readUTF();
                    infoIn.add(getInfoIn);
                }


                System.out.println("cleaned up");


            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List getInfo() {
        return infoIn;
    }

}

class Server {

    static int server = 1337;

    public static void main(String[] args) throws Exception {


        try (ServerSocket serverSocket = new ServerSocket(server)) {
            System.out.println("now listening on: " + server);


            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new Echo(socket)).start();


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



                List infoIn = Echo.getInfo();
                try {
                    if (infoIn.get(0).equals("get list")) {
                        System.out.println("yks");
                        System.out.println(dbc.getAllTasks().toString());


                    } else if (infoIn.get(0).equals("show list")) {
                        System.out.println(dbc.getAllTasks().toString());
                    } else if (infoIn.get(0).equals("addtask")) {   // format: 2005-01-12 08:02:00;juust;kapsas
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

                    } else if (infoIn.get(0).equals("deletetask")) {
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

                    } else if (infoIn.get(0).equals("done")) {

                        try {
                            String indexstring = (String) infoIn.get(1);

                            int index = Integer.parseInt(indexstring);
                            dbc.markAsDone(index);

                            System.out.println("Task done.");

                        } catch (NumberFormatException e) {
                            System.out.println("Not a valid index!");
                        }
                    } else { // TODO commands to be added: renametodo, descedit, dateedit, renametask, checkuser, register, login
                        System.out.println("Not a command!");

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