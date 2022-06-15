package ru.vkorneychuk.lightHTTP.containers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ConfigContainer {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static ConfigContainer instance;

    private Map<String, Object> configData;

    private int port = 8080;

    private ConfigContainer(){};

    public static ConfigContainer getInstance(){
        if (instance == null){
            instance = new ConfigContainer();
        }
        return instance;
    }

    public void setConfigData(Map<String, Object> configData){
        this.configData = configData;
    }

    public void saveDefaultParameters(){
        Map<String, Object> serverParameters = safeObjectMapConvert(configData.get("LightHTTPParameters"));

        this.port = (Integer) serverParameters.getOrDefault("port", this.port);

    }

    private Map<String, Object> safeObjectMapConvert(Object object){
        // TODO what's wrong?
        if(object instanceof HashMap) {
            return (HashMap<String, Object>) object;
        }
        return null;
    }

    public void setParameter(String parameterName, Object parameterData){
        this.configData.put(parameterName, parameterData);
    }

    private Object getParameter(String parameterName){
        return this.configData.get(parameterName);
    }
}
