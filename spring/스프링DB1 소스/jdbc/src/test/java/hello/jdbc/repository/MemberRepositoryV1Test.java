package hello.jdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 repositoryV1;

    @BeforeEach
    void beforeEach() {

        // 기본 커넥션 획득
        //DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        //커넥션 풀링
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        repositoryV1 = new MemberRepositoryV1(dataSource);

    }

    @Test
    void crud() throws SQLException {

        Member member = new Member("memberV0", 10000);
        repositoryV1.save(member);

        Member findMember = repositoryV1.findById(member.getMemberId());
        log.info("findMember={}", findMember);
        assertThat(findMember).isEqualTo(member);

        repositoryV1.update(member.getMemberId(), 12000);
        Member updateMember = repositoryV1.findById(member.getMemberId());
        assertThat(updateMember.getMoney()).isEqualTo(12000);

        repositoryV1.delete(member.getMemberId());
        assertThatThrownBy( () -> repositoryV1.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);


    }
}