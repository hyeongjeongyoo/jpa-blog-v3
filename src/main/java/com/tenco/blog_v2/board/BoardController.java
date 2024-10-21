package com.tenco.blog_v2.board;

import com.tenco.blog_v2.common.errors.Exception403;
import com.tenco.blog_v2.common.errors.Exception404;
import com.tenco.blog_v2.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class BoardController {

    private final BoardJPARepository boardJPARepository;
    private final BoardRepository boardRepository;
    private final BoardService boardService;
    private final HttpSession session;

    /**
     * 게시글 상세보기 서비스, 게시글 주인 여부 판별
     */
    public Board getBoardDetails(int boardId, User sessionUser) {
        Board board = boardJPARepository
                .findById(boardId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없어요"));
        // 현재 사용자가 게시글을 작성했는지 여부 판별
        boolean isBoardOwner = false;
        if(sessionUser != null ) {
            if(sessionUser.getId().equals(board.getUser().getId())) {
                isBoardOwner = true;
            }
        }

        // 집중 - 코드 추가
        // 내가 작성한 댓글인가를 구현 해야 한다.
        board.getReplies().forEach( reply -> {
            boolean isReplayOwner = false;
            if(sessionUser != null) {
                if(sessionUser.getId().equals(reply.getUser().getId())) {
                    isReplayOwner = true;
                }
            }
            // 객체에서만 존재하는 필드 - 리플 객체 엔티티 상태값 변경 처리
            reply.setReplyOwner(isReplayOwner);
        });

        board.setBoardOwner(isBoardOwner);
        return  board;
    }

    // 게시글 수정 화면 요청
    // board/id/update
    @GetMapping("/board/{id}/update-form")
    public String updateForm(@PathVariable(name = "id") Integer id, HttpServletRequest request) {
        // 1. 게시글 조회
        Board board = boardRepository.findById(id);
        // 2. 요청 속성에 조회한 게시글 속성 및 값 추가
        request.setAttribute("board", board);
        // 뷰 리졸브 - 템플릿 반환
        return "board/update-form"; // src/main/resources/templates/board/update-form.mustache
    }


    // 게시글 수정 요청 기능
    // board/{id}/update
    @PostMapping("/board/{id}/update")
    public String update(@PathVariable(name = "id") Integer id, @ModelAttribute BoardDTO.UpdateDTO reqDto) {

        // 1. 데이터 바인딩 방식 수정

        // 2. 인증 검사 - 로그은 여부 판단
        User sessionUser = (User) session.getAttribute("sessionUser");
        if(sessionUser == null) {
            return "redirect:/login-form";
        }
        // 3. 권한 체크 - 내 글이 맞니?
        Board board = boardRepository.findById(id);
        if(board == null) {
            return "redirect:/";  // 게시글이 없다면 에러 페이지 추후 수정
        }
        if (!board.getUser().getId().equals(sessionUser.getId())) {
            throw new Exception403("게시글을 수정할 권한이 없습니다.");
        }
        // 4. 유효성 검사 - 생략

        // 5. 서비스 측 위임 (직접 구현) - 레파지토리 사용
        boardRepository.updateByIdJPA(id, reqDto.getTitle(), reqDto.getContent());

        // 6. 리다이렉트 처리

        return "redirect:/board/" + id;
    }

    // 주소설계 - http://localhost:8080/board/10/delete ( form 활용이기 때문에 delete 선언)
    // form 태크에서는 GET, POST 방식만 지원하기 때문이다.
    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable(name = "id") Integer id) {
        // 유효성, 인증검사
        // 세션에서 로그인 사용자 정보 가져오기 -> 인증(로그인 여부) , 인가(권한 - 내글?)
        User sessionUser = (User) session.getAttribute("sessionUser");

        if (sessionUser == null) {
            return "redirect:/login-form";
        }

        // 권한 체크 - 조회
        Board board = boardRepository.findById(id);
        if(board == null) {
            return "redirect:/error-404";
        }
        if( ! board.getUser().getId().equals(sessionUser.getId())) {
            throw new Exception403("게시글을 삭제할 권한이 없습니다.");
        }

        // 게시글 삭제
        boardRepository.deleteById(id);
        // boardNativeRepository.deleteById(id);
        return "redirect:/";

    }

    // 특정 게시글 요청 화면
    // 주소설계 - http://localhost:8080/board/1
    @GetMapping("/board/{id}")
    public String detail(@PathVariable(name = "id") Integer id, HttpServletRequest request) {
        // JPA API 사용
        //Board board = boardRepository.findById(id);
        // JPQL FETCH join 사용
        Board board = boardRepository.findByIdJoinUser(id);
        User sessionUser = (User)session.getAttribute("sessionUser");
        // 현재 로그인한 유저와 == 게시글 작성한 유저가 같아면
        // isOwner = true, !isOwner = false
        boolean isOwner = false;
        if(sessionUser != null ) {
            if(sessionUser.getId().equals(board.getUser().getId())) {
                isOwner = true;
            }
        }
        request.setAttribute("board", board);
        request.setAttribute("isOwner", isOwner);
        return "board/detail";
    }

    @GetMapping("/")
    public String index(Model model) {

        //List<Board> boardList = boardNativeRepository.findAll();
        // 코드 수정
        List<Board> boardList = boardRepository.findAll();
        model.addAttribute("boardList", boardList);
        return "index";
    }

    // 주소설계 - http://localhost:8080/board/save-form
    // 게시글 작성 화면
    @GetMapping("/board/save-form")
    public String saveForm() {
        return "board/save-form";
    }

    // 게시글 저장
    // 주소설계 - http://localhost:8080/board/save
    @PostMapping("/board/save")
    public String save(@ModelAttribute BoardDTO.SaveDTO reqDto) {
        User sessionUser =  (User) session.getAttribute("sessionUser");

        if(sessionUser == null) {
            return "redirect:/login-form";
        }
        // 파라미터가 올바르게 전달 되었는지 확인
        log.warn("save 실행: 제목={}, 내용={}", reqDto.getTitle(), reqDto.getContent());

        // boardNativeRepository.save(title, content);
        // SaveDTO 에서 toEntity 사용해서 Board 엔티로 변환하고 인수 값으로 User 정보 정보를 넣었다.
        // 결국 Board 엔티티로 반환이 된다.
        boardRepository.save(reqDto.toEntity(sessionUser));
        return "redirect:/";
    }

}