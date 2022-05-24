package tech.flygo.demo;

import java.io.*;
import java.net.ServerSocket;

/**
 * @description: Socket编程HTTP服务
 * @author: flygo
 * @time: 2022/5/24 10:30
 */
public class RawHttpServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8001);

        while (true) {
            var socket = serverSocket.accept();
            System.out.println("A socket created");

            var iptStream = new DataInputStream(socket.getInputStream());
            var bfReader = new BufferedReader(new InputStreamReader(iptStream));

            var requestBuilder = new StringBuilder();

            String line = "";

            // ReadLine -> line end "\n"
            while (!(line = bfReader.readLine()).isBlank()) {
                requestBuilder.append(line + "\n");
            }

            var request = requestBuilder.toString();
            System.out.println(request);

            var bfWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bfWriter.write("HTTP/1.1 200 ok\n\nHello Word\n");
            bfWriter.flush();
            bfWriter.close();
        }
    }
}
