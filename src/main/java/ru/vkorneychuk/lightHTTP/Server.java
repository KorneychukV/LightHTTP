package ru.vkorneychuk.lightHTTP;

import com.sun.net.httpserver.HttpServer;
import ru.vkorneychuk.lightHTTP.containers.EndpointContainer;
import ru.vkorneychuk.lightHTTP.handlers.ConfigFileHandler;
import ru.vkorneychuk.lightHTTP.containers.ConfigContainer;
import ru.vkorneychuk.lightHTTP.handlers.ControllersHandler;
import ru.vkorneychuk.lightHTTP.handlers.RequestHandler;

import java.io.IOException;
import java.net.*;

public class Server {

    private HttpServer server;
    private ConfigContainer serverConfig;
    private EndpointContainer endpointContainer;

    public Server(){
        ConfigFileHandler configFileHandler = new ConfigFileHandler();
        this.serverConfig = ConfigContainer.getInstance();

        ControllersHandler controllersHandler = new ControllersHandler();
        this.endpointContainer = EndpointContainer.getInstance();

        start();
    }

    public Server(String configPath){
        ConfigFileHandler configFileHandler = new ConfigFileHandler(configPath);
        this.serverConfig = ConfigContainer.getInstance();

        ControllersHandler controllersHandler = new ControllersHandler();
        this.endpointContainer = EndpointContainer.getInstance();

        start();
    }

    private void start(){
        HttpServer server = null;

        try {
            server = HttpServer.create();
            server.bind(new InetSocketAddress(this.serverConfig.getPort()), 0);
        } catch (IOException e) {
            System.err.printf("Ошибка запуска сервера: %s", e);
            return;
        }

        server.setExecutor(null);
        server.start();
        this.server = server;
        this.setContext();
    }

    public void setContext(){
        this.server.createContext("/", new RequestHandler());
    }

}
