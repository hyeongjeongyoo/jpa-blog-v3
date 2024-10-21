package com.tenco.blog_v2.reply;

import com.tenco.blog_v2.board.Board;
import com.tenco.blog_v2.user.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Service;

public class ReplyDTO {

    @Setter
    @Getter
    public static class SaveDTO{
        private Integer boardId;
        private String comment;
        User sessionUser;
        // DTO -> JPA 영속성 컨텍스트로 저장한다. 엔티티로 변환 해야한다.
        public Reply toEntity(User user, Board board){

            return Reply.builder()
                    .comment(comment)
                    .board(board)
                    .user(sessionUser)
                    .build();
        }
    }

}
