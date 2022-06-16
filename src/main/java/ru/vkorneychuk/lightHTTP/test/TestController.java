package ru.vkorneychuk.lightHTTP.test;

import ru.vkorneychuk.lightHTTP.annotations.Controller;
import ru.vkorneychuk.lightHTTP.annotations.PostMethod;
import ru.vkorneychuk.lightHTTP.annotations.RequestBody;
import ru.vkorneychuk.lightHTTP.test.DTO.Person;

@Controller(path = "/test")
public class TestController {

    @PostMethod(path = "/post")
    public void testPostMethod(@RequestBody Person data){
        System.out.println(data);
    }

    public void testMethod(){
        System.out.println("Test method called.");
    }

}
