package ru.vkorneychuk.lightHTTP;

import com.sun.net.httpserver.HttpServer;
import ru.vkorneychuk.lightHTTP.containers.EndpointContainer;
import ru.vkorneychuk.lightHTTP.enums.ServerArgsNames;
import ru.vkorneychuk.lightHTTP.containers.ConfigContainer;
import ru.vkorneychuk.lightHTTP.handlers.ControllersHandler;
import ru.vkorneychuk.lightHTTP.handlers.RequestHandler;

import java.io.IOException;
import java.net.*;
import java.util.Map;

public class ApplicationServer {

    private HttpServer server;
    private final ConfigContainer serverConfig;
    private EndpointContainer endpointContainer;

    public ApplicationServer(Map<ServerArgsNames, Object> args){
        
        this.serverConfig = ConfigContainer.getInstance();
        this.serverConfig.readConfig(args);

        new ControllersHandler().getAllControllers();
        this.endpointContainer = EndpointContainer.getInstance();

        start();
        this.setContext();
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
    }

    public void setContext(){
        this.server.createContext(this.serverConfig.getDefaultApiPath(), new RequestHandler());
    }

}
