package tech.flygo.demo.step3;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;

/**
 * @description: Http响应类
 * @author: flygo
 * @time: 2022/5/24 16:29
 */
public class Response {

  private Socket socket;
  // 状态码
  private int status;

  static HashMap<Integer, String> codeMap;

  public Response(Socket socket) {
    this.socket = socket;
    if (codeMap == null) {
      codeMap = new HashMap<>();
      codeMap.put(200, "ok");
    }
  }

  public void send(String msg) throws IOException {
    var resp = "HTTP/1.1 " + this.status + " " + this.codeMap.get(this.status) + "\n";
    resp += "\n";
    resp += msg;
    this.sendRaw(resp);
  }

  public void sendRaw(String msg) throws IOException {
    var bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    bufferWriter.write(msg);
    bufferWriter.flush();
    bufferWriter.close();
  }
}
