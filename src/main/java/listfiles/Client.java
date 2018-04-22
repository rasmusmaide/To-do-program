package listfiles;

import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;

public class Client {
    public static void main(String[] args) throws Exception {
        Class.forName("org.h2.Driver");
        Connection connection = DriverManager.getConnection("tcp://172.31.192.138:9092");
        // TODO: 4/22/2018 lahendada no suitable driver found exception 

    }
}
