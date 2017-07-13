package org.cuber.call.caller.beans;

import org.cuber.call.caller.define.CallerDefine;
import org.cuber.call.vertx.holder.AbstractVertxHolder;
import org.springframework.beans.factory.FactoryBean;

/**
 * Created by cuber on 2017/7/12.
 */
public class FactoryCaller<T> implements FactoryBean<T> {

    private Class<T> callerInterface;

    private AbstractVertxHolder abstractVertxHolder;

    private CallerDefine callerDefine;


    @Override
    public T getObject() throws Exception {
        return abstractVertxHolder.getProxyObj(callerInterface, callerDefine.getCallAppName(), callerDefine.getVersion());
    }

    @Override
    public Class<?> getObjectType() {
        return callerInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public Class<T> getCallerInterface() {
        return callerInterface;
    }

    public void setCallerInterface(Class<T> callerInterface) {
        this.callerInterface = callerInterface;
    }

    public AbstractVertxHolder getAbstractVertxHolder() {
        return abstractVertxHolder;
    }

    public void setAbstractVertxHolder(AbstractVertxHolder abstractVertxHolder) {
        this.abstractVertxHolder = abstractVertxHolder;
    }

    public CallerDefine getCallerDefine() {
        return callerDefine;
    }

    public void setCallerDefine(CallerDefine callerDefine) {
        this.callerDefine = callerDefine;
    }
}