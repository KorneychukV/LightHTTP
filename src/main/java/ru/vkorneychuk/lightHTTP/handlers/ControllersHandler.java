package ru.vkorneychuk.lightHTTP.handlers;

import org.reflections.Reflections;
import ru.vkorneychuk.lightHTTP.annotations.*;
import ru.vkorneychuk.lightHTTP.containers.EndpointArgument;
import ru.vkorneychuk.lightHTTP.containers.EndpointContainer;
import ru.vkorneychuk.lightHTTP.containers.EndpointMetaData;
import ru.vkorneychuk.lightHTTP.exceptions.ManyArgumentAnnotationsException;
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
            registerPostMethods(controller, endpointContainer, controllerPath);
        }
    }

    private void registerPostMethods(Class<?> controller, EndpointContainer endpointContainer, String controllerPath){
        List<Method> postMethods = getPostEndpoints(controller);

        for (Method postMethod: postMethods){
            String endpointPath = postMethod.getAnnotation(PostMethod.class).path();
            EndpointMetaData endpointMetaData = new EndpointMetaData();

            endpointMetaData.setEndpointPath(endpointPath);
            endpointMetaData.setMethod(postMethod);

            List<EndpointArgument> endpointArguments = getEndpointArguments(postMethod);
            endpointMetaData.setArguments(endpointArguments);

            endpointMetaData.setResponseType(postMethod.getReturnType());

            endpointContainer.addEndpoint(controllerPath.concat(endpointPath), endpointMetaData);
        }
    }

    private List<Method> getPostEndpoints(Class<?> controller){
        return Arrays.stream(controller.getMethods())
                .filter(method -> Arrays.stream(method.getAnnotations())
                        .anyMatch(annotation -> annotation instanceof PostMethod)).toList();
    }

    private List<EndpointArgument> getEndpointArguments(Method method){

        checkAnnotations(method);

        List<EndpointArgument> endpointArguments = new ArrayList<>();
        Arrays.stream(method.getParameters())
                .forEach(parameter -> {
                    endpointArguments.add(getEndpointArgument(parameter));
                });

        return endpointArguments;
    }

    private void checkAnnotations(Method method){
        int requestBodyCount = 0;

        for(Annotation[] annotations: method.getParameterAnnotations()){
            for (Annotation annotation: annotations){
                if (annotation.annotationType() == RequestBody.class){
                    requestBodyCount++;
                }
            }
        }

        if (requestBodyCount > 1) throw new RuntimeException("Many request body annotations");
    }

    private EndpointArgument getEndpointArgument(Parameter parameter){
        EndpointArgument endpointArgument = new EndpointArgument();
        Annotation annotation;
        try {
            annotation = getArgumentAnnotation(parameter);
        } catch (ManyArgumentAnnotationsException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }

        if (annotation != null){
            endpointArgument.setAnnotation(annotation.annotationType());
            endpointArgument.setRequestParameterName(getRequestParameterName(annotation, parameter.getName()));
        }

        endpointArgument.setArgumentName(parameter.getName());
        endpointArgument.setType(parameter.getType());
        return endpointArgument;
    }

    private Annotation getArgumentAnnotation(Parameter argument) throws ManyArgumentAnnotationsException {
        Class<?>[] requiredAnnotations = new Class<?>[3];
        requiredAnnotations[0] = GetParameter.class;
        requiredAnnotations[1] = RequestBody.class;
        requiredAnnotations[2] = RequestHeader.class;

        List<Annotation> annotations = Arrays.stream(argument.getAnnotations())
                .filter(annotation -> isAnnotationPresent(requiredAnnotations, annotation.annotationType())).toList();
        if (annotations.size() == 1){
            return annotations.get(0);
        } else if (annotations.size() > 1){
            throw new ManyArgumentAnnotationsException();
        } else {
            return null;
        }
    }

    private boolean isAnnotationPresent(Class<?>[] annotations, Class<?> requiredAnnotation){
        return Arrays.asList(annotations).contains(requiredAnnotation);
    }

    public String getRequestParameterName(Annotation annotation, String parameterName) {

        String requestParameterName;

        if (annotation.annotationType() == GetParameter.class){
            requestParameterName = ((GetParameter) annotation).name();
        } else if (annotation.annotationType() == RequestBody.class){
            requestParameterName = ((RequestBody) annotation).name();
        } else if (annotation.annotationType() == RequestHeader.class){
            requestParameterName = ((RequestHeader) annotation).name();
        } else {
            return null;
        }

        return (requestParameterName.equals("")) ? parameterName : requestParameterName;

    }

//    public Class<?> getRequestBodyType(Method method) throws ManyBodyExpected {
//        Parameter[] parameters = method.getParameters();
//        List<Class<?>> types = new ArrayList<>();
//        Arrays.stream(parameters)
//                .filter(parameter -> isAnnotationPresent(parameter.getAnnotations(), RequestBody.class))
//                .forEach(parameter -> types.add(parameter.getType()));
//        if (types.size() > 1){
//            throw new ManyBodyExpected();
//        } else if (types.size() == 0){
//            return null;
//        } else {
//            return types.get(0);
//        }
//    }

}
