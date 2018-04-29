package listfiles.Connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Echo implements Runnable {

    private Socket serverSocket;

    public Echo(Socket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public List<String> infoIn;

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

    public List<String> getInfo() {
        return infoIn;
    }

}