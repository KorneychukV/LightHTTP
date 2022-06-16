package ru.vkorneychuk.lightHTTP.test;

import ru.vkorneychuk.lightHTTP.annotations.Controller;
import ru.vkorneychuk.lightHTTP.annotations.PostMethod;
import ru.vkorneychuk.lightHTTP.annotations.RequestBody;

@Controller(path = "/test")
public class TestController {

    @PostMethod(path = "/sdf")
    public void testPostMethod(@RequestBody String data, String temp){
        System.out.println(data);
    }

    public void testMethod(){
        System.out.println("Test method called.");
    }

}
