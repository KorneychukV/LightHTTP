package ru.vkorneychuk.lightHTTP.containers;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class EndpointContainer {

    private static EndpointContainer instance;

    public static EndpointContainer getInstance(){
        if (instance == null){
            instance = new EndpointContainer();
        }
        return instance;
    }

    private Map<String, Method> endpointsMap = new HashMap<>();

    public void addMethod(String path, Method method){
        endpointsMap.put(path, method);
    }

    public Method getMethod(String path){
        return endpointsMap.get(path);
    }
}
