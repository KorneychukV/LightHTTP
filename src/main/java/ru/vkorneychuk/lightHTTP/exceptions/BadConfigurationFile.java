package ru.vkorneychuk.lightHTTP.exceptions;

public class BadConfigurationFile extends RuntimeException{

    public BadConfigurationFile() {
        super("Ошибка чтения конфигурационного файла");
    }
}
