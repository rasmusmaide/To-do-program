package listfiles.Connection;

import listfiles.DataBaseCommands;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;


public class Server {

    public static void main(String[] args) throws Exception {
        int portNumber = 1337;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("now listening on: " + portNumber);
            Socket socket;

            org.h2.tools.Server h2Server = org.h2.tools.Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers").start();
            Class.forName("org.h2.Driver");
            DataBaseCommands dbc;
            dbc = new DataBaseCommands("jdbc:h2:tcp://localhost/~/todoBase");
            try {
                //dbc.removeTodo_s();
                dbc.initialize();//esmakordsel käivitamisel
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
                dbc.conn.close();
                h2Server.stop();
            }


        }

    }
}