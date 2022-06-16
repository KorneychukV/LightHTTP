package ru.vkorneychuk.lightHTTP.exceptions;

public class ManyArgumentAnnotationsException extends Exception {

    public ManyArgumentAnnotationsException() {
        super("Argument cannot contain more than one framework annotations");
    }
}
