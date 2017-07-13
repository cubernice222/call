package org.cuber.call.utils;

import com.alibaba.fastjson.JSON;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
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
        String address = MethodUtils.getMethodSignature(method);
        eventBus.consumer(address, message -> {
            String receiveJsonStr = (String) message.body();
            long startTime = System.currentTimeMillis();
            String addressLocal = message.address();
            log.info(" [{}] receive message [{}] from [{}]", address,receiveJsonStr, addressLocal);
            Object[] objects = MethodUtils.decoderMethodParamValue4Json(method, receiveJsonStr);
            try {
                Object result = method.invoke(springBean, objects);
                message.reply(JSON.toJSONString(result));
            } catch (Exception e) {
                message.fail(1, e.getMessage());
            } finally {
                long endTime = System.currentTimeMillis();
                log.info(" it's take [{}] ms", endTime - startTime);
            }
        });
    }
}
