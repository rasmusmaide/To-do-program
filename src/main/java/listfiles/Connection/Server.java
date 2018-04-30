package listfiles.Connection;

import listfiles.Task;
import listfiles.Todo_list;
import listfiles.dataBaseCommands;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Server {

    public static void main(String[] args) throws Exception {
        int portNumber = 1337;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("now listening on: " + portNumber);
            Socket socket;

            org.h2.tools.Server h2Server = org.h2.tools.Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers").start();
            Class.forName("org.h2.Driver");
            dataBaseCommands dbc;
            dbc = new dataBaseCommands("jdbc:h2:tcp://localhost/~/todoBase");
            try {
                dbc.newInitialize();//esmakordsel käivitamisel
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                while (true) {


                    try { // on vaja?

                        ////////////////////////////////

                        socket = serverSocket.accept();

                        Echo echo = new Echo(socket, dbc);
                        Thread thread = new Thread(echo);
                        thread.start();


                        //dbc.removeTodo_s();//kui on todo_s tabeliga variant veel alles
                        //List<String> infoIn = echo.getInfo();

                    } finally {

                    }
                    //////////////////////////////////
                }
            } finally {
                dbc.conn.close(); // TODO ilmselt siin probleem
                h2Server.stop();
            }



        }

    }
}