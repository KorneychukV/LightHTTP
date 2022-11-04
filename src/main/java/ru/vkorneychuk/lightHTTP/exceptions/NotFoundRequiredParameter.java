package ru.vkorneychuk.lightHTTP.exceptions;

public class NotFoundRequiredParameter extends RuntimeException{

    public NotFoundRequiredParameter() {
        super("Требуется заполнить все обязательные параметры сервера");
    }

}
