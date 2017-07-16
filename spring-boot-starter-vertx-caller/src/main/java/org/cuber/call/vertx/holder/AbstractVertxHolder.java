package org.cuber.call.vertx.holder;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.cuber.call.caller.proxy.CallerProxyVerticle;

import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by cuber on 2017/7/12.
 */
public abstract class AbstractVertxHolder {

    private ConcurrentHashMap<String,Vertx> havingVertxs = new ConcurrentHashMap<String, Vertx>();
    private ConcurrentHashMap<Vertx,CallerProxyVerticle> havingVerticeProxys = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Class,Object> havingProxyObj = new ConcurrentHashMap<>();
    private Lock lock  = new ReentrantLock();

    public Vertx getVertxByAppNameAndVersion(String appName, String version){
        String key = appName + "-" + version;
        if(!havingVertxs.contains(key)){
            try{
                lock.lock();
                /**
                 * 可以考虑这个类不和caller关联起来
                 */
                Vertx vertx = productOneVertx(key);
                DeploymentOptions deploymentOptions = new DeploymentOptions().setWorker(true);
                CallerProxyVerticle verticle = new CallerProxyVerticle();
                vertx.deployVerticle(verticle, deploymentOptions);
                havingVertxs.putIfAbsent(key,vertx);
                havingVerticeProxys.putIfAbsent(vertx, verticle);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }

        }
        return havingVertxs.get(key);
    }

    public <T> T getProxyObj(Class<T> interfaceClass,String appName,String version){
        if(!havingProxyObj.contains(interfaceClass)){
            Vertx vertx = getVertxByAppNameAndVersion(appName,version);
            CallerProxyVerticle verticle = havingVerticeProxys.get(vertx);
            T t = (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                    new Class[]{interfaceClass},
                    verticle);
            havingProxyObj.putIfAbsent(interfaceClass, t);
        }
        return (T)havingProxyObj.get(interfaceClass);

    }

    abstract Vertx productOneVertx(String key) throws Exception;
}
