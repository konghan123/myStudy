package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 커넥션 파라미터 연동, 풀 고려 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final MemberRepositoryV2 memberRepository;
    private final DataSource dataSource;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();

        try{
            con.setAutoCommit(false); // 트랜잭션 시작

            Member fromMember = memberRepository.findById(con, fromId);
            Member toMember = memberRepository.findById(con, toId);

            memberRepository.update(con,fromId, fromMember.getMoney() - money);
            if(toMember.getMemberId().equals("ex")) {
                throw new IllegalStateException("이체중 예외 발생");
            }
            memberRepository.update(con, toId, toMember.getMoney() + money);

            con.commit(); // 커밋됨

        }catch(Exception e) {
            con.rollback(); // 실패 시 롤백
            throw new IllegalStateException(e);
        } finally {
            if(con != null) {
                try{
                    con.setAutoCommit(true); // 다시 자동커밋모드로 변경
                    con.close(); // 비즈니스 로직 이후 connection을 닫아줌
                                 // 커넥션 풀 사용 시 커넥션을 반납함
                }catch(Exception e){
                    log.info("error", e);
                }
            }
        }

    }
}
