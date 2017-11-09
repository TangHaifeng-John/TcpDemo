package candy.example.com.tcpdemo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by candy on 17-11-9.
 */

public class TestServer {

    public static void main(String args[]) {

        startConnect();

    }


    public static void startConnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerSocket ss = null;
                try {
                    ss = new ServerSocket(4900);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    while (true) {
                        Socket s = ss.accept();
                        System.out.println("a client connect!");
                        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                        dos.writeUTF("IPAddress = " + s.getInetAddress() + "    Port = " + s.getPort());
                        dos.close();
                        s.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
