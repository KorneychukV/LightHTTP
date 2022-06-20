package ru.vkorneychuk.lightHTTP.enums;

public enum HTTPStatus {

    OK(200, "Success"),
    BAD_REQUEST(401, "Bad request"),
    NOT_FOUND(404, "Not found"),
    SERVER_INTERNAL_ERROR(500, "Server internal error");

    public final int code;
    public final String message;

    private HTTPStatus(int code, String error){
        this.code = code;
        this.message = error;
    }

}
