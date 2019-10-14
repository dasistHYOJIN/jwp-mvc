package nextstep.mvc.tobe;

import com.google.common.collect.Maps;
import nextstep.mvc.HandlerMapping;
import nextstep.mvc.exception.NoSuchControllerClassException;
import nextstep.mvc.tobe.view.ModelAndView;
import nextstep.web.annotation.RequestMapping;
import nextstep.web.annotation.RequestMethod;
import org.reflections.ReflectionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.reflections.ReflectionUtils.getAllMethods;

public class AnnotationHandlerMapping implements HandlerMapping {
    private final Map<HandlerKey, HandlerExecution> handlerExecutions = Maps.newHashMap();
    private final ControllerScanner controllerScanner;

    public AnnotationHandlerMapping(Object... basePackage) {
        try {
            this.controllerScanner = new ControllerScanner(basePackage);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new NoSuchControllerClassException();
        }
    }

    @Override
    public void initialize() {
        for (Class<?> aClass : controllerScanner.getKeys()) {
            Set<Method> allMethods = getAllMethods(aClass, ReflectionUtils.withAnnotation(RequestMapping.class));
            for (Method method : allMethods) {
                RequestMapping rm = method.getAnnotation(RequestMapping.class);
                handlerExecutions.put(createHandlerKey(rm),
                        ((req, resp) -> (ModelAndView) method.invoke(controllerScanner.getController(aClass), req, resp)));
            }
        }
    }

    @Override
    public boolean supports(HttpServletRequest request) {
        return getMaybeHandler(request).isPresent();
    }

    @Override
    public HandlerExecution getHandler(HttpServletRequest request) {
        return getMaybeHandler(request).orElseThrow();
    }

    private Optional<HandlerExecution> getMaybeHandler(HttpServletRequest request) {
        String url = request.getRequestURI();
        RequestMethod requestMethod = RequestMethod.valueOf(request.getMethod());

        return handlerExecutions.entrySet().stream()
                .filter(entry -> entry.getKey().matchesUrl(url))
                .filter(entry -> entry.getKey().containsMethod(requestMethod))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    private HandlerKey createHandlerKey(RequestMapping rm) {
        return new HandlerKey(rm.value(), rm.method());
    }
}
