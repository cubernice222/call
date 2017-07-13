package org.cuber.call.callee.beans;


import com.google.common.base.Strings;
import io.vertx.core.Vertx;
import org.cuber.call.CallConstant;
import org.cuber.call.CallDefine;
import org.cuber.call.callee.annotation.Callee;
import org.cuber.call.utils.VertxUtils;
import org.cuber.call.vertx.holder.AbstractVertxHolder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Created by cuber on 2017/7/12.
 */
public class CalleeFactory implements BeanDefinitionRegistryPostProcessor,EnvironmentAware {
    private Environment environment;
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        Map<String,Object> callees = configurableListableBeanFactory.getBeansWithAnnotation(Callee.class);
        CallDefine callDefine = environment.getProperty(CallConstant.CALLEEPROPERTYSOURCENAME,CallDefine.class);
        if(callDefine != null && !Strings.isNullOrEmpty(callDefine.getCallAppName())){
            AbstractVertxHolder abstractClusteringVerHolder = configurableListableBeanFactory.getBean("vertxHolder",AbstractVertxHolder.class);
            callees.forEach((k,v)->{
                Class[] interfaces = v.getClass().getInterfaces();
                Optional<Class> interfaceFirst =  Arrays.stream(interfaces).filter(InterfaceClass->{
                    Callee callee = (Callee) InterfaceClass.getAnnotation(Callee.class);
                    return callee != null;
                }).findFirst();
                Class handlerClass = v.getClass();
                if(null != interfaceFirst && interfaceFirst.isPresent()){
                    handlerClass  = interfaceFirst.getClass();
                }
                Vertx vertx = abstractClusteringVerHolder.getVertxByAppNameAndVersion(callDefine.getCallAppName(),callDefine.getVersion());
                Method[] methods = handlerClass.getDeclaredMethods();
                Arrays.stream(methods).forEach(method->
                        VertxUtils.createConsumer(method,vertx,v));
            });
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}