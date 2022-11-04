package ru.vkorneychuk.lightHTTP.test;

import ru.vkorneychuk.lightHTTP.annotations.arguments.GetParameter;
import ru.vkorneychuk.lightHTTP.annotations.controllers.Controller;
import ru.vkorneychuk.lightHTTP.annotations.methods.GetMethod;
import ru.vkorneychuk.lightHTTP.annotations.methods.PostMethod;
import ru.vkorneychuk.lightHTTP.annotations.arguments.RequestBody;
import ru.vkorneychuk.lightHTTP.annotations.arguments.RequestHeader;
import ru.vkorneychuk.lightHTTP.test.DTO.Person;

@Controller(path = "/test")
public class TestController {

    @PostMethod(path = "/post")
    public Person testPostMethod(@RequestHeader(name = "head") String head,
                               @RequestBody Person data){
        return data;
    }

    @GetMethod(path = "/get")
    public void testGetMethod(@RequestHeader(name = "data") String headerData,
                              @GetParameter int dig, @GetParameter String string){
        System.out.println(headerData);
        System.out.println(dig);
        System.out.println(string);
    }

    public void testMethod(){
        System.out.println("Test method called.");
    }

}
