import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * IoC를 하지 않은 상태
 */
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * 컨트롤러 메서드 호출 전에 실행되는 메서드
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler chosen handler to execute, for type and/or instance evaluation
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        System.out.println("LoginInterceptor preHandle 실행");
        HttpSession session = request.getSession(false);

        if(session == null){
            throw new Exception401("로그인이 필요합니다.");
        }

        // 키-값 --> 세션 메모리지에 저장 박식은 map 구조 저장(sessionUser) 문자열 사용 중
        User sessionUser = (User) session.getAttribute("sessionUser");
        if(sessionUser == null){
            throw new Exception401("로그인이 필요합니다.");
        }

        //return HandlerInterceptor.super.preHandle(request, response, handler);
        // return false; <- 컨트롤러를 타지 않는다.
        return true;
    }

}
