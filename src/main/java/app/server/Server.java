package app.server;

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
                //dbc.removeTodos();
                dbc.initialize();//esmakordsel käivitamisel
            } catch (SQLException e) {
                System.out.println("tabel juba olemas");
            }
            dbc.showAbsolutelyAllTasks(); // testimiseks
            dbc.showAbsolutelyAllUsers(); // testimiseks
            try {
                while (true) {
                        ////////////////////////////////

                        socket = serverSocket.accept();

                        Echo echo = new Echo(socket, dbc);
                        Thread thread = new Thread(echo);
                        thread.start();

                        //////////////////////////////////
                }
            } finally {
                dbc.conn.close();
                h2Server.stop();
            }


        }

    }
}