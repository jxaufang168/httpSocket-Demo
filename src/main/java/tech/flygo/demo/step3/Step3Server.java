package tech.flygo.demo.step3;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @description: Socket编程HTTP服务的进一步抽象
 * @author: flygo
 * @time: 2022/5/24 16:46
 */
public class Step3Server {

  ServerSocket serverSocket;
  IHandlerInterface httpHandler;

  public Step3Server(IHandlerInterface httpHandler) {
    this.httpHandler = httpHandler;
  }

  public void listen(int port) throws IOException {
    serverSocket = new ServerSocket(port);
    while (true) {
      this.accept();
    }
  }

  private void accept() throws IOException {
    var socket = serverSocket.accept();
    new Thread(
            () -> {
              try {
                this.handler(socket);
              } catch (IOException e) {
                e.printStackTrace();
              }
            })
        .start();
  }

  private void handler(Socket socket) throws IOException {
    var request = new Request(socket);
    var response = new Response(socket);
    this.httpHandler.handler(request, response);
  }

  public static void main(String[] args) throws IOException {
    var server =
        new Step3Server(
            ((request, response) -> {
              System.out.println(request.getHeaders());
              response.send("<html><body><h1>Hello Word!</h1></body></html>");
            }));
    server.listen(8001);
  }
}
