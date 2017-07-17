package org.cuber.call.utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import org.cuber.call.caller.proxy.CallerProxyVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by cuber on 2017/7/10.
 */
public class VertxUtils {

    private static Logger log = LoggerFactory.getLogger(VertxUtils.class);


    public static void createConsumer(Method method, Vertx vertx, Object springBean) {
        EventBus eventBus = vertx.eventBus();
        String address =  MethodUtils.getMethodSignature(method);
        eventBus.consumer(address, message -> {
            Buffer buffer = (Buffer)message.body();
            Kryo kryo = CallerProxyVerticle.getKryo();
            Input input = new Input(buffer.getBytes());
            Object[] objects = kryo.readObjectOrNull(input,Object[].class);
            long startTime = System.nanoTime();
            Output output = null;
            try {
                Object result = method.invoke(springBean, objects);
                output = new Output(1024,-1);
                kryo.writeObjectOrNull(output,result,method.getReturnType());
                output.flush();
                Buffer buffer1 = Buffer.buffer(output.toBytes());
                message.reply(buffer1);
            } catch (Exception e) {
                message.fail(1, e.getMessage());
            } finally {
                output.close();
                input.close();
                long endTime = System.nanoTime();
                log.info(" it's take [{}] ms", endTime - startTime);
            }
        });
    }
}
