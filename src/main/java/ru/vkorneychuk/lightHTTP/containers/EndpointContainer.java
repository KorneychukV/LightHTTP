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

    private Map<String, EndpointMetaData> endpointsMap = new HashMap<>();

    public void addEndpoint(String path, EndpointMetaData endpointMetaData){
        endpointsMap.put(path, endpointMetaData);
    }

    public EndpointMetaData getEndpoint(String path){
        return endpointsMap.get(path);
    }
}
