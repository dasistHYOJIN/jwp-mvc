package slipp;

import nextstep.mvc.asis.Controller;

public interface LegacyHandlerMapping {
    void initialize();

    Controller getHandler(String requestUri);
}