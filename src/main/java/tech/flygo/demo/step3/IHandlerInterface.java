package tech.flygo.demo.step3;

import java.io.IOException;

/**
 * @description: 处理接口类
 * @author: flygo
 * @time: 2022/5/24 16:27
 */
@FunctionalInterface
public interface IHandlerInterface {

  void handler(Request request, Response response) throws IOException;
}
