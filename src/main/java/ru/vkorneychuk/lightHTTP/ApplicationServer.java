package ru.vkorneychuk.lightHTTP;

import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import ru.vkorneychuk.lightHTTP.enums.ServerArgsNames;
import ru.vkorneychuk.lightHTTP.containers.ConfigContainer;
import ru.vkorneychuk.lightHTTP.handlers.RegistrationControllersHandler;
import ru.vkorneychuk.lightHTTP.handlers.RequestHandler;

import java.io.IOException;
import java.net.*;
import java.util.Map;

@Slf4j
public class ApplicationServer {

    private HttpServer server;
    private final ConfigContainer serverConfig;

    public ApplicationServer(Map<ServerArgsNames, Object> args){

        log.debug("Создание экземпляра сервера");
        this.serverConfig = ConfigContainer.getInstance();
        log.debug("Получение настроек сервера из файла конфигурации");
        this.serverConfig.readConfig(args);

        log.debug("Регистрация контроллеров");
        new RegistrationControllersHandler().registrateControllers();

        log.debug("Запуск сервера");
        start();
        this.setContext();
        log.debug(String.format("Сервер запущен на порту: %d", this.serverConfig.getPort()));
    }

    private void start(){
        HttpServer server = null;

        try {
            server = HttpServer.create();
            server.bind(new InetSocketAddress(this.serverConfig.getPort()), 0);
        } catch (IOException e) {
            log.error(String.format("Ошибка запуска сервера: %s", e));
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
