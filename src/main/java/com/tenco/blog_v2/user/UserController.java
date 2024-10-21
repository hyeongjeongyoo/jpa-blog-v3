package com.tenco.blog_v2.user;

import com.tenco.blog_v2.common.errors.Exception401;
import com.tenco.blog_v2.common.errors.Exception500;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@RequiredArgsConstructor
@Slf4j
@Controller
public class UserController {

    // DI 처리
    private final UserService userService;
    private final HttpSession session;

    /**
     * 회원 정보 수정 페이지 요청
     * 주소설계 : http://localhost:8080/user/update-form
     *
     * @return 문자열
     * 반환되는 문자열을 뷰 리졸버가 처리하며
     * 머스태치 템플릿 엔진을 통해서 뷰 파일을 렌더링 합니다.
     */
    @GetMapping("/user/update-form")
    public String updateForm(HttpServletRequest request, HttpSession session) {
    // String updateForm(HttpServletRequest request, @SessionAttribute(name = "sessionUser") User sessionUser) {

        // @SessionAttribute(name = "sessionUser") User sessionUser
        // 이 어노테이션은 모델에 저장되어 있는 세션 값을 바로 가져오는 어노테이션이다.
        // 단, 이녀석은 뷰, 템블릿 엔진에서 접금하도록 설계되어 있다. 권장 x

        User sessionUser = (User)session.getAttribute("sessionUser");

        User user = (User) session.getAttribute("sessionUser");

        request.setAttribute("user", user);

        return "user/update-form"; // 템플릿 경로 : user/join-form.mustache
    }

    /**
     * 사용자 정보 수정
     * @param reqDTO
     * @return 메인 페이지
     */
    @PostMapping("/user/update")
    public String update(@ModelAttribute(name = "updateDTO") UserDTO.UpdateDTO reqDTO) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login-form";
        }
        User updatedUser = userService.updateUser(sessionUser.getId(), reqDTO);
        // 세션 정보 동기화 처리
        session.setAttribute("sessionUser", updatedUser);
        return "redirect:/";
    }

    /**
     * 회원가입 페이지 요청
     * 주소설계 : http://localhost:8080/join-form
     *
     * @param model
     * @return 문자열
     * 반환되는 문자열을 뷰 리졸버가 처리하며
     * 머스태치 템플릿 엔진을 통해서 뷰 파일을 렌더링 합니다.
     */
    @GetMapping("/join-form")
    public String joinForm(Model model) {
        log.info("회원가입 페이지");
        model.addAttribute("name", "회원가입 페이지");
        return "user/join-form"; // 템플릿 경로 : user/join-form.mustache
    }

    /**
     * 회원 가입 기능 요청
     * @param reqDto
     * @return
     */
    @PostMapping("/join")
    public String join(@ModelAttribute(name = "joinDTO") UserDTO.JoinDTO reqDto)  {
        // 유효성 검사 생략 ...
        try {
            userService.signUp(reqDto);
        } catch (DataIntegrityViolationException e) {
            throw new Exception500("동일한 유저네임이 존재 합니다.");
        }
        return "redirect:/login-form";
    }

    /**
     * 로그인 페이지 요청
     * 주소설계 : http://localhost:8080/login-form
     *
     * @param model
     * @return 문자열
     * 반환되는 문자열을 뷰 리졸버가 처리하며
     * 머스태치 템플릿 엔진을 통해서 뷰 파일을 렌더링 합니다.
     */
    @GetMapping("/login-form")
    public String loginForm(Model model) {
        log.info("로그인 페이지");
        model.addAttribute("name", "로그인 페이지");
        return "user/login-form"; // 템플릿 경로 : user/join-form.mustache
    }

    /**
     * 자원에 요청은 GET 방식이지만 보안에 이유로 예외 !
     * 로그인 처리 메서드
     * 요청 주소 POST : http://localhost:8080/login
     * @param reqDto
     * @return
     */
    @PostMapping("/login")
    public String login(UserDTO.LoginDTO reqDto) {
        try {
            User sessionUser = userService.signIn(reqDto);
            session.setAttribute("sessionUser", sessionUser);
            return  "redirect:/";
        } catch (Exception e) {
            throw new Exception401("유저이름 또는 비밀번호가 틀렸습니다.");
        }
    }

    @GetMapping("/logout")
    public String logout() {
        session.invalidate(); // 세션을 무효화 (로그아웃)
        return "redirect:/";
    }

}