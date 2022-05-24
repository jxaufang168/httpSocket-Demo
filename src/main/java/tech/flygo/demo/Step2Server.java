package tech.flygo.demo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Function;

/**
 * @description: Socket编程HTTP服务的多线程优化
 * @author: flygo
 * @time: 2022/5/24 14:47
 */
public class Step2Server {

  ServerSocket serverSocket;
  Function<String, String> handler;

  public Step2Server(Function<String, String> handler) {
    this.handler = handler;
  }

  public void listen(int port) throws IOException {
    serverSocket = new ServerSocket(port);
    while (true) {
      this.accept();
    }
  }

  private void accept() throws IOException {
    // Blocking... 线程阻塞
    var socket = serverSocket.accept();
    new Thread(
            () -> {
              this.handler(socket);
            })
        .start();
  }

  private void handler(Socket socket) {
    // Blocking...
    // Thread --> sleep --> Other Threads
    System.out.println("A socket created by Thread: " + Thread.currentThread().getId());

    try {
      var iptStream = new DataInputStream(socket.getInputStream());
      var bfReader = new BufferedReader(new InputStreamReader(iptStream));

      var requestBuilder = new StringBuilder();

      String line = "";

      // Readline -> line end '\n'
      while (true) {
        line = bfReader.readLine();
        if (line == null || line.isBlank()) {
          break;
        }
        requestBuilder.append(line + "\n");
      }

      var request = requestBuilder.toString();
      System.out.println(request);

      var bfWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      var response = this.handler.apply(request);

      bfWriter.write(response);
      bfWriter.flush();
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws IOException {
    var server =
        new Step1Server(
            req -> {
              try {
                Thread.sleep(10);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              return "HTTP/1.1 201 ok\n\nGood!\n";
            });
    server.listen(8001);
  }
}
