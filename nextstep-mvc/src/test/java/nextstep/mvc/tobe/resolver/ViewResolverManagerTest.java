package nextstep.mvc.tobe.resolver;

import nextstep.mvc.tobe.view.JspView;
import nextstep.mvc.tobe.view.ModelAndView;
import nextstep.mvc.tobe.view.RedirectView;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletException;

import static org.assertj.core.api.Assertions.assertThat;

class ViewResolverManagerTest {
    private ViewResolverManager manager = ViewResolverManager.getInstance();

    @Test
    void 싱글톤_확인() {
        assertThat(manager).isEqualTo(ViewResolverManager.getInstance());
    }

    @Test
    void jspView를_resolve하는지_테스트() throws ServletException {
        ModelAndView mav = new ModelAndView("/asd.jsp");
        manager.resolveView(mav);
        assertThat(mav.getView()).isInstanceOf(JspView.class);
    }

    @Test
    void redirectView를_resolve하는지_테스트() throws ServletException {
        ModelAndView mav = new ModelAndView("redirect:/index.html");
        manager.resolveView(mav);
        assertThat(mav.getView()).isInstanceOf(RedirectView.class);
    }
}