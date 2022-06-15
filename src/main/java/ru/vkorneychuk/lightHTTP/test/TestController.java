package ru.vkorneychuk.lightHTTP.test;

import ru.vkorneychuk.lightHTTP.annotations.Controller;
import ru.vkorneychuk.lightHTTP.annotations.PostMethod;

@Controller(path = "/test")
public class TestController {

    @PostMethod(path = "/sdf")
    public void testPostMethod(){
        System.out.println("Test post method called.");
    }

    public void testMethod(){
        System.out.println("Test method called.");
    }

}
