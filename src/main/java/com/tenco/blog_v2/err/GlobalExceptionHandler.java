import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**

     400 Bad Request 예외 처리
     @param ex
     @param model
     @return*/
    @ExceptionHandler(Exception400.class)
    public ModelAndView handleException400(Exception400 ex, Model model){
        ModelAndView mav = new ModelAndView("err/400");
        mav.addObject("msg",ex.getMessage());
        return mav;

    }

    @ExceptionHandler(Exception401.class)
    public ModelAndView handleException401(Exception401 ex, Model model){
        ModelAndView mav = new ModelAndView("err/401");
        mav.addObject("msg",ex.getMessage());
        return mav;

    }

    @ExceptionHandler(Exception403.class)
    public ModelAndView handleException403(Exception403 ex, Model model){
        ModelAndView mav = new ModelAndView("err/403");
        mav.addObject("msg",ex.getMessage());
        return mav;

    }

    @ExceptionHandler(Exception404.class)
    public ModelAndView handleException404(Exception404 ex, Model model){
        ModelAndView mav = new ModelAndView("err/404");
        mav.addObject("msg",ex.getMessage());
        return mav;

    }

    @ExceptionHandler(Exception500.class)
    public ModelAndView handleException500(Exception500 ex, Model model){
        ModelAndView mav = new ModelAndView("err/500");
        mav.addObject("msg",ex.getMessage());
        return mav;

    }

}