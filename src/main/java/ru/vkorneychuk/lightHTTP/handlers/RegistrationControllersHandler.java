package ru.vkorneychuk.lightHTTP.handlers;

import org.reflections.Reflections;
import ru.vkorneychuk.lightHTTP.annotations.arguments.GetParameter;
import ru.vkorneychuk.lightHTTP.annotations.arguments.RequestBody;
import ru.vkorneychuk.lightHTTP.annotations.arguments.RequestHeader;
import ru.vkorneychuk.lightHTTP.annotations.controllers.Controller;
import ru.vkorneychuk.lightHTTP.annotations.methods.DeleteMethod;
import ru.vkorneychuk.lightHTTP.annotations.methods.GetMethod;
import ru.vkorneychuk.lightHTTP.annotations.methods.PostMethod;
import ru.vkorneychuk.lightHTTP.annotations.methods.PutMethod;
import ru.vkorneychuk.lightHTTP.containers.EndpointArgument;
import ru.vkorneychuk.lightHTTP.containers.EndpointContainer;
import ru.vkorneychuk.lightHTTP.containers.EndpointMetaData;
import ru.vkorneychuk.lightHTTP.exceptions.ManyArgumentAnnotationsException;
import ru.vkorneychuk.lightHTTP.exceptions.ManyBodyExpected;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class RegistrationControllersHandler {

    public RegistrationControllersHandler(){}

    public void registrateControllers(){
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
            registerGetMethods(controller, endpointContainer, controllerPath);
            registerPutMethods(controller, endpointContainer, controllerPath);
            registerDeleteMethods(controller, endpointContainer, controllerPath);
        }
    }

    private void registerPostMethods(Class<?> controller, EndpointContainer endpointContainer, String controllerPath){
        List<Method> postMethods = getPostEndpoints(controller);

        for (Method postMethod: postMethods){
            String endpointPath = postMethod.getAnnotation(PostMethod.class).path();

            endpointContainer.addEndpoint(controllerPath.concat(endpointPath),
                    extractEndpointMetadata(postMethod, endpointPath, "POST"));
        }
    }

    private void registerGetMethods(Class<?> controller, EndpointContainer endpointContainer, String controllerPath){
        List<Method> getMethods = getGetEndpoints(controller);

        for (Method getMethod: getMethods){
            String endpointPath = getMethod.getAnnotation(GetMethod.class).path();

            endpointContainer.addEndpoint(controllerPath.concat(endpointPath),
                    extractEndpointMetadata(getMethod, endpointPath, "GET"));

        }
    }

    private void registerPutMethods(Class<?> controller, EndpointContainer endpointContainer, String controllerPath){
        List<Method> putMethods = getPutEndpoints(controller);

        for (Method putMethod: putMethods){
            String endpointPath = putMethod.getAnnotation(PutMethod.class).path();

            endpointContainer.addEndpoint(controllerPath.concat(endpointPath),
                    extractEndpointMetadata(putMethod, endpointPath, "PUT"));

        }
    }

    private void registerDeleteMethods(Class<?> controller, EndpointContainer endpointContainer, String controllerPath){
        List<Method> deleteMethods = getDeleteEndpoints(controller);

        for (Method deleteMethod: deleteMethods){
            String endpointPath = deleteMethod.getAnnotation(DeleteMethod.class).path();

            endpointContainer.addEndpoint(controllerPath.concat(endpointPath),
                    extractEndpointMetadata(deleteMethod, endpointPath, "DELETE"));

        }
    }

    private EndpointMetaData extractEndpointMetadata(Method method, String endpointPath, String requestMethod){
        EndpointMetaData endpointMetaData = new EndpointMetaData();
        endpointMetaData.setEndpointPath(endpointPath);
        endpointMetaData.setMethod(method);
        endpointMetaData.setRequestMethod(requestMethod);

        List<EndpointArgument> endpointArguments = getEndpointArguments(method);
        endpointMetaData.setArguments(endpointArguments);

        endpointMetaData.setResponseType(method.getReturnType());

        return endpointMetaData;
    }

    private List<Method> getPostEndpoints(Class<?> controller){
        return Arrays.stream(controller.getMethods())
                .filter(method -> Arrays.stream(method.getAnnotations())
                        .anyMatch(annotation -> annotation instanceof PostMethod)).toList();
    }

    private List<Method> getGetEndpoints(Class<?> controller){
        return Arrays.stream(controller.getMethods())
                .filter(method -> Arrays.stream(method.getAnnotations())
                        .anyMatch(annotation -> annotation instanceof GetMethod)).toList();
    }

    private List<Method> getPutEndpoints(Class<?> controller){
        return Arrays.stream(controller.getMethods())
                .filter(method -> Arrays.stream(method.getAnnotations())
                        .anyMatch(annotation -> annotation instanceof PutMethod)).toList();
    }

    private List<Method> getDeleteEndpoints(Class<?> controller){
        return Arrays.stream(controller.getMethods())
                .filter(method -> Arrays.stream(method.getAnnotations())
                        .anyMatch(annotation -> annotation instanceof DeleteMethod)).toList();
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

        int MAX_REQUEST_BODY_COUNT = 1;
        if (requestBodyCount > MAX_REQUEST_BODY_COUNT) throw new ManyBodyExpected();
    }

    private EndpointArgument getEndpointArgument(Parameter parameter){
        EndpointArgument endpointArgument = new EndpointArgument();
        Annotation annotation;
        annotation = getArgumentAnnotation(parameter);

        if (annotation != null){
            endpointArgument.setAnnotation(annotation.annotationType());
            endpointArgument.setRequestParameterName(getRequestParameterName(annotation, parameter.getName()));
        }

        endpointArgument.setArgumentName(parameter.getName());
        endpointArgument.setType(parameter.getType());
        return endpointArgument;
    }

    private Annotation getArgumentAnnotation(Parameter argument){
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

        String requestParameterName = parameterName;

        if (annotation.annotationType() == GetParameter.class){
            requestParameterName = ((GetParameter) annotation).name();
        } else if (annotation.annotationType() == RequestBody.class){
            requestParameterName = ((RequestBody) annotation).name();
        } else if (annotation.annotationType() == RequestHeader.class){
            requestParameterName = ((RequestHeader) annotation).name();
        } else {
            return requestParameterName;
        }

        return (requestParameterName.equals("")) ? parameterName : requestParameterName;

    }


}
