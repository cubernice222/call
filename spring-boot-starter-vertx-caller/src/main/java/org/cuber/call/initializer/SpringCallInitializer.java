package org.cuber.call.initializer;

import com.google.common.base.Strings;
import org.cuber.call.CallConstant;
import org.cuber.call.CallDefine;
import org.cuber.call.caller.define.CallerDefine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by cuber on 2017/7/12.
 */
public class SpringCallInitializer implements ApplicationContextInitializer{
    private  Logger log = LoggerFactory.getLogger(SpringCallInitializer.class);
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        ConfigurableEnvironment env = applicationContext.getEnvironment();
        String callYamlPathPattern = env.getProperty(CallConstant.CALLPATTERNCONFIG);
        if(Strings.isNullOrEmpty(callYamlPathPattern)) {
            callYamlPathPattern = CallConstant.CALLYAMLPATHPATTERN;
        }
        try{
            Resource[] resources = resourcePatternResolver.getResources(callYamlPathPattern);
            YamlPropertySourceLoader yamlPropertySourceLoader = new YamlPropertySourceLoader();
            List<CallerDefine> callers = new ArrayList<>();
            CallDefine callDefine = new CallDefine();
            Arrays.stream(resources).forEach(resource -> {
                try{
                    PropertySource propertySource = yamlPropertySourceLoader.load(CallConstant.CALLERPROPERTYSOURCENAME,resource,null);

                    if(propertySource != null){
                        String isCallee = (String)propertySource.getProperty(CallConstant.ISCALLEE);
                        String callAppName = (String) propertySource.getProperty(CallConstant.CALLAPPNAME);
                        if (Strings.isNullOrEmpty(callAppName)) {
                            callAppName = CallConstant.DEFAULTAPPNAME;
                        }
                        String callAppVersion = (String) propertySource.getProperty(CallConstant.CALLAPPVERSION);
                        if (Strings.isNullOrEmpty(callAppVersion)) {
                            callAppVersion = CallConstant.DEFAULTAPPVERSION;
                        }
                        if(!"Y".equals(isCallee)) {
                            String callerScanPackage = (String) propertySource.getProperty(CallConstant.CALLERSCANPACKAGE);
                            if (Strings.isNullOrEmpty(callerScanPackage)) {
                                callerScanPackage = "**." + callAppName + "." + CallConstant.CALLERPROPERTYSOURCENAME;
                            }
                            CallerDefine callerDefine = new CallerDefine();
                            callerDefine.setCallAppName(callAppName);
                            callerDefine.setVersion(callAppVersion);
                            callerDefine.setCallerScanPackage(callerScanPackage);
                            callers.add(callerDefine);
                        }else{
                            callDefine.setVersion(callAppVersion);
                            callDefine.setCallAppName(callAppName);
                        }
                    }
                }catch (Exception e){
                    log.error("ok read resource just err", e);
                }
            });
            List<CallerDefine> cleanList = callers.stream().filter(callerDefine -> !callerDefine.getCallAppName().equals(callDefine.getCallAppName())).collect(Collectors.toList());
            Map<String,Object> callerPropertySource = new HashMap<>();
            callerPropertySource.put(CallConstant.CALLERPROPERTYSOURCENAME,cleanList);
            callerPropertySource.put(CallConstant.CALLEEPROPERTYSOURCENAME,callDefine);
            MapPropertySource propertySource = new MapPropertySource(CallConstant.DEFAULTAPPNAME, callerPropertySource);
            env.getPropertySources().addLast(propertySource);
        }catch (Exception e){
            log.error("ok caller initializer just error", e);
        }
    }
}
