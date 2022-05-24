package tech.flygo.demo.step4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @description: Socket编程NIO核心概念
 * @author: flygo
 * @time: 2022/5/24 17:54
 */
public class Step4Server {

  ServerSocketChannel ssc;

  public void listen(int port) throws IOException {
    ssc = ServerSocketChannel.open();
    ssc.bind(new InetSocketAddress(port));

    // Reactive / Reactor 设置为异步的
    ssc.configureBlocking(false);

    var selector = Selector.open();

    ssc.register(selector, ssc.validOps(), null);
    ByteBuffer buffer = ByteBuffer.allocate(1024 * 16);

    for (; ; ) {
      int numOfKeys = selector.select();

      Set selectedKeys = selector.selectedKeys();
      Iterator it = selectedKeys.iterator();

      while (it.hasNext()) {
        var key = (SelectionKey) it.next();
        if (key.isAcceptable()) {
          var channel = ssc.accept();
          if (channel == null) {
            continue;
          }

          // Kernel -> map(Buffer) -> Channel -> User(Buffer)
          channel.configureBlocking(false);
          channel.register(selector, SelectionKey.OP_READ);
        } else {
          var channel = (SocketChannel) key.channel();

          // _ _ _ _ _ _ _ _
          //           P(position)
          //           L
          buffer.clear();
          channel.read(buffer);
          String request = new String(buffer.array());
          // Logic ... 处理逻辑
          // 调用clear方法，把buffer游标的位置重置到起始位置
          buffer.clear();
          buffer.put("HTTP/1.1 200 ok\n\nHello NIO!!\n".getBytes());
          // H T T P ? / 1 ... ! _ _
          //                     P(L)
          //                   L
          // P
          // 调用flip方法,buffer的当前位置更改为buffer缓冲区的第一个位置。
          buffer.flip();
          channel.write(buffer);
          channel.close();
        }
      }
    }
  }

  public static void main(String[] args) throws IOException {
    var server = new Step4Server();
    server.listen(8001);
  }
}
