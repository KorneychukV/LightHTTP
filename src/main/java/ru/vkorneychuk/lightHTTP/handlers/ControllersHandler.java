package ru.vkorneychuk.lightHTTP.handlers;

import org.reflections.Reflections;
import ru.vkorneychuk.lightHTTP.annotations.Controller;
import ru.vkorneychuk.lightHTTP.annotations.GetParameter;
import ru.vkorneychuk.lightHTTP.annotations.PostMethod;
import ru.vkorneychuk.lightHTTP.annotations.RequestBody;
import ru.vkorneychuk.lightHTTP.containers.EndpointArguments;
import ru.vkorneychuk.lightHTTP.containers.EndpointContainer;
import ru.vkorneychuk.lightHTTP.containers.EndpointMetaData;
import ru.vkorneychuk.lightHTTP.exceptions.ManyBodyExpected;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class ControllersHandler {

    public ControllersHandler(){}

    public void getAllControllers(){
        Set<Class<?>> controllers =  getControllerClasses();
        registrationEndpoints(controllers);
    }

    private Set<Class<?>> getControllerClasses(){
        return new Reflections("ru.vkorneychuk.lightHTTP").getTypesAnnotatedWith(Controller.class);
    }

    private void registrationEndpoints(Set<Class<?>> controllers){
        EndpointContainer endpointContainer = EndpointContainer.getInstance();
        for(Class<?> controller: controllers){

            String controllerPath = controller.getAnnotation(Controller.class).path();

            List<Method> postMethods = getPostEndpoints(controller);

            for (Method postMethod: postMethods){
                String endpointPath = postMethod.getAnnotation(PostMethod.class).path();
                EndpointMetaData endpointMetaData = new EndpointMetaData();
                endpointMetaData.setEndpointPath(endpointPath);
                endpointMetaData.setMethod(postMethod);
                // Todo get arguments
                endpointMetaData.setArguments();
                endpointMetaData.setResponseType(postMethod.getReturnType());

                endpointContainer.addEndpoint(controllerPath.concat(endpointPath), endpointMetaData);
            }

        }
    }

    private List<Method> getPostEndpoints(Class<?> controller){
        return Arrays.stream(controller.getMethods())
                .filter(method -> Arrays.stream(method.getAnnotations())
                        .anyMatch(annotation -> annotation instanceof PostMethod)).toList();
    }

    public List<EndpointArguments> getMethodArguments(Method method) {
        Parameter[] parameters = method.getParameters();
        List<EndpointArguments> requestParameters = new ArrayList<>();
        Arrays.stream(parameters)
                .filter(parameter -> isOneOfAnnotationsPresent(parameter.getAnnotations(), GetParameter.class))
                .forEach(parameter -> {
                    String annotationName = parameter.getAnnotation(GetParameter.class).name();
                    requestParameters.add(new EndpointArguments(parameter.getType(),
                            annotationName.equals("") ? parameter.getName() : annotationName,
                            GetParameter.class));
                });
        if (requestParameters.size() == 0){
            return null;
        } else {
            return requestParameters;
        }
    }

    private void getArgumentAnnotation(Parameter argument){
        List<Class<?>> requiredAnnotations = new ArrayList<>();
        requiredAnnotations.add(GetParameter.class);
        requiredAnnotations.add(RequestBody.class);
        // TODO проверить, есть ли у аргумента одна из необходимых аннотаций, если есть вернуть её, если несколько ошибка, если нет, то null
    }

    private boolean isOneOfAnnotationsPresent(Annotation[] annotations, Class<?> requiredAnnotation){
        System.out.println(requiredAnnotation);
        Arrays.stream(annotations).forEach(System.out::println);
        return Arrays.asList(annotations).contains(requiredAnnotation);
    }

    public List<EndpointArguments> getGetParameters(Method method) {
        Parameter[] parameters = method.getParameters();
        List<EndpointArguments> requestParameters = new ArrayList<>();
        Arrays.stream(parameters)
                .filter(parameter -> isAnnotationPresent(parameter.getAnnotations(), GetParameter.class))
                .forEach(parameter -> {
                    String annotationName = parameter.getAnnotation(GetParameter.class).name();
                    requestParameters.add(new EndpointArguments(parameter.getType(),
                            annotationName.equals("") ? parameter.getName() : annotationName,
                            GetParameter.class));
                });
        if (requestParameters.size() == 0){
            return null;
        } else {
            return requestParameters;
        }
    }

    public Class<?> getRequestBodyType(Method method) throws ManyBodyExpected {
        Parameter[] parameters = method.getParameters();
        List<Class<?>> types = new ArrayList<>();
        Arrays.stream(parameters)
                .filter(parameter -> isAnnotationPresent(parameter.getAnnotations(), RequestBody.class))
                .forEach(parameter -> types.add(parameter.getType()));
        if (types.size() > 1){
            throw new ManyBodyExpected();
        } else if (types.size() == 0){
            return null;
        } else {
            return types.get(0);
        }
    }

}
