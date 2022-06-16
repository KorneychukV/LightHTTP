package ru.vkorneychuk.lightHTTP.test;

import ru.vkorneychuk.lightHTTP.ApplicationServer;
import ru.vkorneychuk.lightHTTP.enums.ServerArgsNames;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        HashMap<ServerArgsNames, Object> serverArgs = new HashMap<>();
        serverArgs.put(ServerArgsNames.CONFIG_FILE_PATH, "config1.yml");
        ApplicationServer startServer = new ApplicationServer(serverArgs);
    }

}
