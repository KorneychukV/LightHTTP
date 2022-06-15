package ru.vkorneychuk.lightHTTP.handlers;

import org.reflections.Reflections;
import ru.vkorneychuk.lightHTTP.annotations.Controller;
import ru.vkorneychuk.lightHTTP.annotations.PostMethod;
import ru.vkorneychuk.lightHTTP.containers.EndpointContainer;
import ru.vkorneychuk.lightHTTP.test.TestController;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

public class ControllersHandler {

    public ControllersHandler(){
        this.getAllControllers();
    }

    private void getAllControllers(){
        Set<Class<?>> controllers =  new Reflections("ru.vkorneychuk.lightHTTP").getTypesAnnotatedWith(Controller.class);
        EndpointContainer endpointContainer = EndpointContainer.getInstance();
        for(Class<?> controller: controllers){

            String controllerPath = controller.getAnnotation(Controller.class).path();

            // Get post methods
            List<Method> postMethods = Arrays.stream(controller.getMethods())
                    .filter(method -> Arrays.stream(method.getAnnotations())
                            .anyMatch(annotation -> annotation instanceof PostMethod)).toList();

            for (Method method: postMethods){
                String methodPath = method.getAnnotation(PostMethod.class).path();
                endpointContainer.addMethod(controllerPath.concat(methodPath), method);
            }

        }
    }

}
