package nextstep.mvc.tobe.resolver;

import nextstep.mvc.exception.ExceptionFunction;
import nextstep.mvc.tobe.view.ModelAndView;
import nextstep.mvc.tobe.view.View;
import org.reflections.Reflections;

import javax.servlet.ServletException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ViewResolverManager {
    private static final String VIEW_RESOLVER_BASE_PACKAGE = "nextstep.mvc.tobe.resolver";

    private final List<ViewResolver> viewResolvers;

    private ViewResolverManager() {
        Reflections reflections = new Reflections(VIEW_RESOLVER_BASE_PACKAGE);
        Set<Class<? extends ViewResolver>> classes = reflections.getSubTypesOf(ViewResolver.class);

        this.viewResolvers =  classes.stream()
                .map(wrapper(aClass -> aClass.getDeclaredConstructor().newInstance()))
                .collect(Collectors.toList());
    }

    public static ViewResolverManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void resolveView(final ModelAndView mav) throws ServletException {
        if (Objects.nonNull(mav.getView())) {
            return;
        }

        View view = this.viewResolvers.stream()
                .filter(viewResolver -> viewResolver.supports(mav))
                .findFirst()
                .orElseThrow(ServletException::new)
                .resolveViewName(mav.getViewName());
        mav.setView(view);
    }

    private <T, R, E extends Exception> Function<T, R> wrapper(ExceptionFunction<T, R, E> f) {
        return t -> {
            try {
                return f.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        };
    }

    private static class LazyHolder {
        private static ViewResolverManager INSTANCE = new ViewResolverManager();
    }
}
