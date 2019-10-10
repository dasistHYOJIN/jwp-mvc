package slipp.controller;

import nextstep.mvc.tobe.view.ModelAndView;
import nextstep.web.annotation.Controller;
import nextstep.web.annotation.RequestMapping;
import nextstep.web.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class ForwardController {

    @RequestMapping(value = "/users/form", method = RequestMethod.GET)
    public ModelAndView showUserForm(HttpServletRequest req, HttpServletResponse resp) {
        return new ModelAndView("/user/form.jsp");
    }

    @RequestMapping(value = "/users/loginForm", method = RequestMethod.GET)
    public ModelAndView showLoginForm(HttpServletRequest req, HttpServletResponse resp) {
        return new ModelAndView("/user/login.jsp");
    }
}