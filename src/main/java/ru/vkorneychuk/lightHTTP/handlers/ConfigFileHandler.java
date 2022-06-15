package ru.vkorneychuk.lightHTTP.handlers;

import org.yaml.snakeyaml.Yaml;
import ru.vkorneychuk.lightHTTP.containers.ConfigContainer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ConfigFileHandler {

    // TODO Relative to the resource folder
    private String configFilePath = "config.yml";

    public ConfigFileHandler(String configFilePath){
        this.configFilePath = configFilePath;
        read();
    }

    public ConfigFileHandler(){
        read();
    }

    public void read() {
        Map<String, Object> configData = new HashMap<>();

        try {
            configData = readConfigFile(this.configFilePath);
        } catch (InterruptedException e) {
            // TODO why this exception can be raise?
            System.err.println("Ошибка выполнения потока.");
        } catch (FileNotFoundException e) {
            System.err.println("Ошибка чтения файла.");
        }

        ConfigContainer serverConfig = ConfigContainer.getInstance();
        serverConfig.setConfigData(configData);
        serverConfig.saveDefaultParameters();
    }

    public Map<String, Object> readConfigFile(String configFilePath) throws InterruptedException, FileNotFoundException {
        Yaml yaml = new Yaml();
        File configFile;
        configFile = new File(configFilePath);
        InputStream inputStream = new FileInputStream(configFile);
        return yaml.load(inputStream);
    }

}
