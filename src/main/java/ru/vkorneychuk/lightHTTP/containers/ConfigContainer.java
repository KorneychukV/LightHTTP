package ru.vkorneychuk.lightHTTP.containers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import ru.vkorneychuk.lightHTTP.enums.DefaultParametersNames;
import ru.vkorneychuk.lightHTTP.enums.ServerArgsNames;
import ru.vkorneychuk.lightHTTP.exceptions.BadConfigurationFile;
import ru.vkorneychuk.lightHTTP.exceptions.NotFoundRequiredParameter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Slf4j
public class ConfigContainer {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static ConfigContainer instance;
    @Setter(AccessLevel.NONE)
    private String configFilePath = "config.yml";
    @Setter(AccessLevel.NONE)
    private Map<String, Object> configData;

    private int port = 8080;
    private String defaultApiPath = "/";

    private ConfigContainer(){};

    public static ConfigContainer getInstance(){
        if (instance == null) instance = new ConfigContainer();
        return instance;
    }

    // TODO Relative to the resource folder

    @SuppressWarnings("unchecked")
    private Map<String, Object> safeObjectMapConvert(Object object){
        try{
            assert (object instanceof HashMap);
            return (HashMap<String, Object>) object;
        } catch (AssertionError e) {
            log.error("Ошибка чтения параметра.");
            e.printStackTrace();
            return null;
        }
    }

    private Map<String, Object> parseConfig(String configFilePath)
            throws InterruptedException, FileNotFoundException {
        Yaml yaml = new Yaml();
        File configFile;
        configFile = new File(configFilePath);
        InputStream inputStream = new FileInputStream(configFile);
        return yaml.load(inputStream);
    }

    public void addParameter(String parameterName, Object parameterData){
        this.configData.put(parameterName, parameterData);
    }

    public Object getParameter(String parameterName){
        return this.configData.get(parameterName);
    }

    public void saveDefaultParameters(){
        Map<String, Object> serverParameters = safeObjectMapConvert(configData.get("LightHTTPParameters"));
        // TODO заполнять необходимые параметры дефолтными значениями
        if (serverParameters == null){
            throw new NotFoundRequiredParameter();
        }

        this.port = (int) serverParameters.getOrDefault(DefaultParametersNames.PORT.getName(), this.port);
        this.defaultApiPath = (String) serverParameters.getOrDefault(
                DefaultParametersNames.DEFAULT_API_PATH.getName(),
                this.defaultApiPath);

    }

    public void readConfig(Map<ServerArgsNames, Object> args) {
        ConfigContainer serverConfig = ConfigContainer.getInstance();
        this.configFilePath = (String) args.getOrDefault(ServerArgsNames.CONFIG_FILE_PATH, this.configFilePath);

        Map<String, Object> configData = new HashMap<>();

        try {
            configData = parseConfig(serverConfig.configFilePath);
        } catch (InterruptedException | FileNotFoundException e) {
            throw new BadConfigurationFile();
        }

        this.configData = configData;
        serverConfig.saveDefaultParameters();
    }
}
