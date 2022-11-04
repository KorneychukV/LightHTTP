package ru.vkorneychuk.lightHTTP.exceptions;

public class ManyArgumentAnnotationsException extends RuntimeException {

    public ManyArgumentAnnotationsException() {
        super("Argument cannot contain more than one framework annotations");
    }
}
