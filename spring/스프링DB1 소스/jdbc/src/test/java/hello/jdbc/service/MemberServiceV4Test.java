package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 *  트랜잭션 리소스 자동 등록
 */
@SpringBootTest // 테스트에서 스프링 컨테이너를 생성하여 쓸 수 있게 함
@Slf4j
class MemberServiceV4Test {
    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberServiceV4 memberService;

   @TestConfiguration
   static class TestConfig {

       private final DataSource dataSource;

       public TestConfig(DataSource dataSource) {
           this.dataSource = dataSource;
       }


       @Bean
       MemberRepository memberRepository() {
//           return new MemberRepositoryV4_1(dataSource); // 직접 예외변환
//           return new MemberRepositoryV4_2(dataSource); // 스프링 예외변환
           return new MemberRepositoryV5(dataSource); // JDBC Template
       }

       @Bean
       MemberServiceV4 memberServiceV3_3() {
           return new MemberServiceV4(memberRepository());
       }
   }

    @AfterEach
    void after()  {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상이체")
    void accountTransfer()  {
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);
        Member memberEx = new Member(MEMBER_EX, 10000);

        memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberB.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체중 예외발생")
    void accountTransferEx()  {
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEx = new Member(MEMBER_EX, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEx);

        assertThatThrownBy( () ->
                memberService.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000)
        ).isInstanceOf(IllegalStateException.class);

        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberEx.getMemberId());
        // 트랜잭션이 적용이 안된 상태
        assertThat(findMemberA.getMoney()).isEqualTo(10000);
        assertThat(findMemberB.getMoney()).isEqualTo(10000);


    }
}