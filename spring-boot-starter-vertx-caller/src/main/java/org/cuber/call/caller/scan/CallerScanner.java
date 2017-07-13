package org.cuber.call.caller.scan;

import org.cuber.call.caller.beans.FactoryCaller;
import org.cuber.call.caller.define.CallerDefine;
import org.cuber.call.vertx.holder.AbstractVertxHolder;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import java.util.Set;

/**
 * Created by cuber on 2017/7/12.
 */
public class CallerScanner extends ClassPathBeanDefinitionScanner {
    private AbstractVertxHolder abstractVertxHolder;


    public AbstractVertxHolder getAbstractVertxHolder() {
        return abstractVertxHolder;
    }

    public void setAbstractVertxHolder(AbstractVertxHolder abstractVertxHolder) {
        this.abstractVertxHolder = abstractVertxHolder;
    }

    public CallerScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }


    public  void scan2Registry(CallerDefine callerDefine){
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(callerDefine.getCallerScanPackage());
        for (BeanDefinitionHolder beanDefinitionHolder:beanDefinitions
                ) {
            GenericBeanDefinition definition = (GenericBeanDefinition)beanDefinitionHolder.getBeanDefinition();

            definition.getPropertyValues().add("callerInterface", definition.getBeanClassName());

            definition.getPropertyValues().add("callerDefine", callerDefine);

            definition.setBeanClass(FactoryCaller.class);

            definition.getPropertyValues().add("abstractVertxHolder", abstractVertxHolder);
        }
    }


    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return (beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent());
    }
}
