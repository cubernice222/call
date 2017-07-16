package org.cuber.call.caller.proxy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import org.cuber.call.caller.annotation.Caller;
import org.cuber.call.utils.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cuber on 2017/7/12.
 */
public class CallerProxyVerticle extends AbstractVerticle implements InvocationHandler {
    private Logger log = LoggerFactory.getLogger(CallerProxyVerticle.class);

    private static ThreadLocal<Kryo> decodeKryos = new ThreadLocal<>();


    public static Kryo getKryo(){
        Kryo kryo = decodeKryos.get();
        if(kryo == null){
            kryo = new Kryo();
            kryo.setReferences(true);
            kryo.setRegistrationRequired(false);
            decodeKryos.set(kryo);
        }
        return kryo;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodSignature = MethodUtils.getMethodSignature(method);
        Kryo kryo = getKryo();
        Output output = new Output(1024,-1);
        Input input = null;
        Object replyObj = null;
        try{
            kryo.writeObjectOrNull(output,args,Object[].class);
            output.flush();
            Buffer bufferSend = Buffer.buffer(output.toBytes());
            String address =  methodSignature;
            CompletableFuture<Buffer> future = new CompletableFuture<>();
            vertx.eventBus().send(address, bufferSend, reply -> {
                if (reply.succeeded()) {
                    Buffer buffer = (Buffer) reply.result().body();
                    future.complete(buffer);
                } else {
                    future.completeExceptionally(reply.cause());
                }
            });
            Buffer buffer = future.get();
            input = new Input(buffer.getBytes());
            replyObj = kryo.readObjectOrNull(input, method.getReturnType());
        }catch (Exception e){
            throw  e;
        }finally {
            output.close();
            input.close();
        }
        return replyObj;
    }
}
