package ru.vkorneychuk.lightHTTP.handlers;

import ru.vkorneychuk.lightHTTP.enums.HTTPStatus;

public class HttpStatusHandler {

    public static boolean is1xxStatus(HTTPStatus status){
        return status.code / 100 == 1;
    }

    public static boolean is2xxStatus(HTTPStatus status){
        return status.code / 100 == 2;
    }

    public static boolean is3xxStatus(HTTPStatus status){
        return status.code / 100 == 3;
    }

    public static boolean is4xxStatus(HTTPStatus status){
        return status.code / 100 == 4;
    }

    public static boolean is5xxStatus(HTTPStatus status){
        return status.code / 100 == 5;
    }

}
