package org.cuber.call.caller.proxy;

import com.alibaba.fastjson.JSON;
import io.vertx.core.AbstractVerticle;
import org.cuber.call.utils.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

/**
 * Created by cuber on 2017/7/12.
 */
public class CallerProxyVerticle extends AbstractVerticle implements InvocationHandler {
    private Logger log = LoggerFactory.getLogger(CallerProxyVerticle.class);

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodSignature = MethodUtils.getMethodSignature(method);
        String decodeMethodJson = MethodUtils.encoderJsonMethodParamValue(method, args);
        CompletableFuture<String> future = new CompletableFuture<>();
        vertx.eventBus().send(methodSignature, decodeMethodJson, reply -> {
            if (reply.succeeded()) {
                String address = reply.result().replyAddress();
                String replyJson = reply.result().body().toString();
                log.info("get result [{}] from [{}]", replyJson, address);
                future.complete(replyJson);
            } else {
                future.completeExceptionally(reply.cause());
            }
        });
        String replyJson = future.get();
        Object replyObj = JSON.parseObject(replyJson, method.getReturnType());
        return replyObj;
    }
}
