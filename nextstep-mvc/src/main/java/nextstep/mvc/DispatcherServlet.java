package nextstep.mvc;

import nextstep.mvc.tobe.HandlerExecution;
import nextstep.mvc.tobe.adapter.HandlerAdapter;
import nextstep.mvc.tobe.resolver.ViewResolverManager;
import nextstep.mvc.tobe.view.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@WebServlet(name = "dispatcher", urlPatterns = "/", loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(DispatcherServlet.class);

    private final List<HandlerMapping> handlerMappings;
    private final HandlerAdapter handlerAdapter;
    private final ViewResolverManager viewResolverManager;

    public DispatcherServlet(List<HandlerMapping> handlerMappings, HandlerAdapter handlerAdapter) throws ServletException {
        this.handlerMappings = handlerMappings;
        this.handlerAdapter = handlerAdapter;
        this.viewResolverManager = ViewResolverManager.getInstance();
    }

    @Override
    public void init() {
        handlerMappings.forEach(HandlerMapping::initialize);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        String requestUri = req.getRequestURI();
        logger.debug("Method : {}, Request URI : {}", req.getMethod(), requestUri);

        HandlerExecution handler = getHandler(req);
        ModelAndView mav = handlerAdapter.handle(req, resp, handler);
        this.viewResolverManager.resolveView(mav);
        applyView(req, resp, mav);
    }

    private HandlerExecution getHandler(HttpServletRequest req) throws ServletException {
        return handlerMappings.stream()
                .filter(handlerMapping -> handlerMapping.supports(req))
                .findFirst()
                .orElseThrow(ServletException::new)
                .getHandler(req);
    }

    private void applyView(HttpServletRequest req, HttpServletResponse resp, ModelAndView mav) throws ServletException {
        try {
            mav.getView().render(mav.getModel(), req, resp);
        } catch (Exception e) {
            logger.error("Exception : {}", e.getMessage());
            throw new ServletException(e.getMessage());
        }
    }
}
