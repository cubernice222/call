package org.cuber.call.utils;

import com.alibaba.fastjson.JSON;
import org.cuber.call.annotation.Parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cuber on 2017/7/10.
 */
public class MethodUtils {
    private static ConcurrentHashMap<Method, Map<String,Class>> paramClassCache = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Method, Map<String, Annotation[]>> paramAnnoCache = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Method, Map<String, String>> methodSignatureMap = new ConcurrentHashMap<>();

    public static Map<String, Class> getMethodParam(Method method){
        if(!paramClassCache.containsKey(method)){
            Map<String, Class> map = new HashMap<>();
            Class[] types = method.getParameterTypes();
            if(types != null && types.length > 0){
                for (int i = 0; i < types.length ; i++) {
                    map.put(String.valueOf(i), types[i]);
                }
            }
            paramClassCache.putIfAbsent(method, map);
        }
        return paramClassCache.get(method);
    }
    public static Map<String, Annotation[]> getMethodParamAnnos(Method method){
        if(!paramAnnoCache.containsKey(method)){
            Map<String, Annotation[]> map = new HashMap<>();
            Annotation[][] annotations = method.getParameterAnnotations();
            if(annotations != null && annotations.length > 0){
                for (int i = 0; i < annotations.length ; i++) {
                    map.put(String.valueOf(i), annotations[i]);
                }
            }
            paramAnnoCache.putIfAbsent(method,map);
        }

        return paramAnnoCache.get(method);
    }

    public static Object[] decoderMethodParamValue4Json(Method method, String paramJson){
        Map<String, Class> paramTypeMap = getMethodParam(method);
        Map<String, String> signatureMap = getMethodSignatureMap(method);
        int paraLength = method.getParameterCount();
        List<Object> objects = new ArrayList<>();
        Map<String,String> paramMap = (Map<String,String>) JSON.parseObject(paramJson,Map.class);
        for(int i = 0; i < paraLength; i++){
            String key = String.valueOf(i);
            Class cls = paramTypeMap.get(key);
            objects.add(JSON.parseObject(paramMap.get(signatureMap.get(key)),cls));
        }
        return objects.toArray();
    }

    public static String encoderJsonMethodParamValue(Method method, Object[] paramValues){
        Map<String, String> signatureMap = getMethodSignatureMap(method);
        int paraLength = method.getParameterCount();
        Map<String, String> jsonMap = new HashMap<>();
        for(int i = 0; i < paraLength; i++){
            String key = String.valueOf(i);
            jsonMap.put(signatureMap.get(key),JSON.toJSONString(paramValues[i]));
        }
        return JSON.toJSONString(jsonMap);
    }
    public static Map<String,String> getMethodSignatureMap(Method method){
        if(!methodSignatureMap.contains(method)){
            Map<String,Class> map = getMethodParam(method);
            Map<String,Annotation[]> annosMap  = getMethodParamAnnos(method);
            Map<String,String> signatureMap = new HashMap<>();
            int paraLength = method.getParameterCount();
            for(int i = 0; i < paraLength; i++){
                String key = String.valueOf(i);
                String appendParamStr = map.get(key).getName();
                signatureMap.put(key,appendParamStr);
                Annotation[] annotations = annosMap.get(key);
                Arrays.stream(annotations).forEach(annotation -> {
                    if(annotation.getClass().equals(Parameter.class)){
                        Parameter param = (Parameter) annotation;
                        signatureMap.put(key,param.value());
                    }
                });

            }
            methodSignatureMap.put(method,signatureMap);
        }
        return methodSignatureMap.get(method);
    }

    public static String getMethodSignature(Method method){
        StringBuilder methodSignature = new StringBuilder(method.getName() + ":[");
        int paraLength = method.getParameterCount();
        Map<String,String> map = getMethodSignatureMap(method);
        for(int i = 0; i < paraLength; i++){
            methodSignature.append(map.get(String.valueOf(i))).append(",");
        }
        methodSignature.append("]");
        return methodSignature.toString();
    }

    public static void main(String[] args) {
        Method[] methods = MethodUtils.class.getDeclaredMethods();
        Arrays.stream(methods).forEach(method -> {
            System.out.println(method.getName());
        });
    }
}
