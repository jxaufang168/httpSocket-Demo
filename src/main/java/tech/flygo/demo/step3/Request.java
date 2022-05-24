package tech.flygo.demo.step3;

import org.apache.commons.httpclient.HttpParser;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * @description: Http请求类
 * @author: flygo
 * @time: 2022/5/24 16:29
 */
public class Request {

  static Pattern methodRegex = Pattern.compile("GET|POST|DELETE|OPTIONS|TRACE|HEAD");

  private final String body;
  private final String method;
  private final HashMap<String, String> headers;

  public String getBody() {
    return body;
  }

  public String getMethod() {
    return method;
  }

  public HashMap<String, String> getHeaders() {
    return headers;
  }

  public Request(Socket socket) throws IOException {
    // DataInputStream -> primitives(Char, Float)
    // InputStream -> bytes
    var iptStream = new DataInputStream(socket.getInputStream());
    var bfReader = new BufferedReader(new InputStreamReader(iptStream));

    // GET /path HTTP/1.1
    var methodLine = HttpParser.readLine(iptStream, "UTF-8");
    var m = methodRegex.matcher(methodLine);
    m.find();
    var method = m.group();

    var headers = HttpParser.parseHeaders(iptStream, "UTF-8");
    var headMap = new HashMap<String, String>();
    for (var h : headers) {
      headMap.put(h.getName(), h.getValue());
    }

    var bufferReader = new BufferedReader(new InputStreamReader(iptStream));
    var body = new StringBuilder();
    char[] buffer = new char[1024];

    while (iptStream.available() > 0) {
      bufferReader.read(buffer);
      body.append(buffer);
    }

    this.body = body.toString();
    this.method = method;
    this.headers = headMap;
  }
}
