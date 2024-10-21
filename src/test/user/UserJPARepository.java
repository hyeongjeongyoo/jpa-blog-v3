package src.test.user;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.DataJpaTest;
import src.main.java.com.tenco.blog_v2.user.User;

import java.util.Optional;

/**
 *  패키지명이 동일해야한다.
 *  UserRepository 기능을 테스트 하는 클래스입니다.
 */
@DataJpaTest
public class UserJPARepository {

    @Autowired // DI 처리
    private src.main.java.com.tenco.blog_v2.board.UserJPARepository userJPARepository;

    @BeforeEach
    public void setUp() {
        System.out.println("@Test 동작 전에 매번 호출");
    }

    @Test
    @DisplayName("사용자 이름으로 조회하는 테스트")
    public void findByUserName_test() {

        // given - 테스트에 필요한 초기 조건 설정
        String username = "카리나";

        // when - 테스트 대상 메서드 실행
        Optional<User> userOpt= userJPARepository.findByUsername(username);

        // eye ~
        System.out.println(userOpt.toString());

        // then

    }
}
