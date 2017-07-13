package org.cuber.call.caller.beans;

import com.google.common.base.Strings;
import org.cuber.call.CallConstant;
import org.cuber.call.caller.annotation.Caller;
import org.cuber.call.caller.define.CallerDefine;
import org.cuber.call.caller.scan.CallerScanner;
import org.cuber.call.vertx.holder.AbstractVertxHolder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.AnnotationTypeFilter;


import java.util.List;

/**
 * Created by cuber on 2017/7/12.
 */
public class CallerFactory implements BeanDefinitionRegistryPostProcessor,EnvironmentAware,ApplicationContextAware {
    private Environment environment;

    private ApplicationContext applicationContext;


    private AbstractVertxHolder abstractVertxHolder;


    public Environment getEnvironment() {
        return environment;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        CallerScanner vertServiceScanner =
                new CallerScanner(beanDefinitionRegistry);
        List<CallerDefine> callerDefines = environment.getProperty(CallConstant.CALLERPROPERTYSOURCENAME, List.class);
        vertServiceScanner.setResourceLoader(this.applicationContext);
        vertServiceScanner.setAbstractVertxHolder(abstractVertxHolder);
        vertServiceScanner.setBeanNameGenerator(null);
        vertServiceScanner.addIncludeFilter(new AnnotationTypeFilter(Caller.class));
        if (callerDefines != null && callerDefines.size() > 0) {
            callerDefines.forEach(callerDefine -> {
                vertServiceScanner.scan2Registry(callerDefine);
            });
        }
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public AbstractVertxHolder getAbstractVertxHolder() {
        return abstractVertxHolder;
    }

    public void setAbstractVertxHolder(AbstractVertxHolder abstractVertxHolder) {
        this.abstractVertxHolder = abstractVertxHolder;
    }
}