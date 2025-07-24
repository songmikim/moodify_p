package xyz.moodf.mypage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import xyz.moodf.member.entities.Member;
import xyz.moodf.member.repositories.MemberRepository;
import xyz.moodf.mypage.services.MyPageService;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest
public class MyPageServiceTest {

    @Autowired
    private MyPageService myPageService;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setEmail("mypage@test.org");
        member.setPassword("1234");
        member.setName("테스터");
        member.setMobile("01000000000");
        member = memberRepository.save(member);
    }

    @Test
    void changePassword() {
        myPageService.changePassword(member, "Newpass1!");
        Member saved = memberRepository.findById(member.getSeq()).orElseThrow();
        assertNotNull(saved.getCredentialChangedAt());
        assertNotEquals("Newpass1!", saved.getPassword());
    }

    @Test
    void deleteAccount() {
        myPageService.deleteAccount(member);
        Member saved = memberRepository.findById(member.getSeq()).orElseThrow();
        assertNotNull(saved.getDeletedAt());
    }
}