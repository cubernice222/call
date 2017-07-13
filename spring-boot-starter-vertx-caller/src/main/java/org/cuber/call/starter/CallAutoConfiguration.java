package org.cuber.call.starter;

import org.cuber.call.callee.beans.CalleeFactory;
import org.cuber.call.caller.beans.CallerFactory;
import org.cuber.call.vertx.holder.AbstractVertxHolder;
import org.cuber.call.vertx.holder.ZookeeperVertxHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

/**
 * Created by cuber on 2017/7/10.
 */
@Configuration
@ConditionalOnClass(AbstractVertxHolder.class)
public class CallAutoConfiguration implements EnvironmentAware {
    private AbstractVertxHolder vertxHolder;
    private String zookeeperHosts;
    private String rootPath;

    @Override
    public void setEnvironment(Environment environment) {
        this.rootPath = environment.getProperty("call.zookeeper.rootPath");
        this.zookeeperHosts = environment.getProperty("call.zookeeper.hosts");
    }

    @Bean(name = "vertxHolder")
    public AbstractVertxHolder init(){
        ZookeeperVertxHolder vertxHolder = new ZookeeperVertxHolder();
        vertxHolder.setRootPath(rootPath);
        vertxHolder.setZookeeperHosts(zookeeperHosts);
        this.vertxHolder = vertxHolder;
        return vertxHolder;
    }

    @Bean
    @DependsOn({"vertxHolder"})
    public CallerFactory initVendorFactory(){
        CallerFactory callerFactory = new CallerFactory();
        callerFactory.setAbstractVertxHolder(this.vertxHolder);
        return callerFactory;
    }
    @Bean
    @DependsOn({"vertxHolder"})
    public CalleeFactory initProviderFactory(){
        CalleeFactory providerFactory = new CalleeFactory();
        return providerFactory;
    }
}
