package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 매니저
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

    private final MemberRepositoryV3 memberRepository;
    private final PlatformTransactionManager transactionManager;


    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        //트랜잭션 시작
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try{

            Member fromMember = memberRepository.findById(fromId);
            Member toMember = memberRepository.findById(toId);

            memberRepository.update(fromId, fromMember.getMoney() - money);
            if(toMember.getMemberId().equals("ex")) {
                throw new IllegalStateException("이체중 예외 발생");
            }
            memberRepository.update(toId, toMember.getMoney() + money);

            transactionManager.commit(status); // 커밋됨
            // 트랜잭션이 커밋, 롤백되면 자동으로 커넥션 정리해줌

        }catch(Exception e) {
            transactionManager.rollback(status); // 실패 시 롤백
            throw new IllegalStateException(e);
        }

    }
}
