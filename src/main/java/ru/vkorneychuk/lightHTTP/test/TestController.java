package ru.vkorneychuk.lightHTTP.test;

import ru.vkorneychuk.lightHTTP.annotations.Controller;
import ru.vkorneychuk.lightHTTP.annotations.PostMethod;
import ru.vkorneychuk.lightHTTP.annotations.RequestBody;
import ru.vkorneychuk.lightHTTP.annotations.RequestHeader;
import ru.vkorneychuk.lightHTTP.test.DTO.Person;

@Controller(path = "/test")
public class TestController {

    @PostMethod(path = "/post")
    public Person testPostMethod(@RequestHeader(name = "head") String head,
                               @RequestBody Person data){

        return data;
    }

    public void testMethod(){
        System.out.println("Test method called.");
    }

}
