package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 템플릿
 */
@Slf4j
public class MemberServiceV3_2 {

    private final MemberRepositoryV3 memberRepository;
//    private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate txTemplate;

    public MemberServiceV3_2(MemberRepositoryV3 memberRepository, PlatformTransactionManager transactionManager) {
        this.memberRepository = memberRepository;
        this.txTemplate = new TransactionTemplate(transactionManager);
    }

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        txTemplate.executeWithoutResult((status) -> {
            try{
                Member fromMember = memberRepository.findById(fromId);
                Member toMember = memberRepository.findById(toId);

                memberRepository.update(fromId, fromMember.getMoney() - money);
                if(toMember.getMemberId().equals("ex")) {
                    throw new IllegalStateException("이체중 예외 발생");
                }
                memberRepository.update(toId, toMember.getMoney() + money);
            }catch(SQLException e) { // 체크예외가 발생
                throw new IllegalStateException(e);
                // 체크 예외의 경우 커밋이 됨, 언체크 예외로 지정하여 롤백되게 끔 지정
            }
        });
    }
}
