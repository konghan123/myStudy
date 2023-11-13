package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * 예외 누수 해결
 * 체크 예외르 런타임 예외로 변경
 * MemberRepository 인터페이스 사용
 * SQLExceptionTranslator 추가
 */
@Slf4j
public class MemberRepositoryV4_2 implements MemberRepository{
    private final DataSource dataSource;
    private final SQLErrorCodeSQLExceptionTranslator exTranslator;//인터페이스

    public MemberRepositoryV4_2(DataSource dataSource) {

        this.dataSource = dataSource;
        this.exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);//구현체 넣어줌
    }
    @Override
    public Member save(Member member)  {
        String sql = "INSERT INTO MEMBER (MEMBER_ID, MONEY) VALUES (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
            return member;
        } catch (SQLException e) {
            DataAccessException ex = exTranslator.translate("save", sql, e);
            throw ex;
        } finally {
            close(con, pstmt, null);
        }
    }
    @Override
    public Member findById(String memberId) {
        String sql = "SELECT * FROM MEMBER WHERE MEMBER_ID = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("MEMBER_ID"));
                member.setMoney(rs.getInt("MONEY"));
                return member;
            } else {
                throw new NoSuchElementException("member not found member_id = " + memberId);
            }
        } catch (SQLException e) {
            throw exTranslator.translate("findById", sql, e);
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
            // connection 유지를 위해 여기서 닫지 않고 모든 비즈니스로직이 끝나면 닫음
            //JdbcUtils.closeConnection(con);
        }
    }
    @Override
    public void update(String memberId, int money)  {
        String sql = "UPDATE MEMBER SET MONEY = ? WHERE MEMBER_ID = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw exTranslator.translate("update", sql, e);
        } finally {
            JdbcUtils.closeStatement(pstmt);
            // connection 유지를 위해 여기서 닫지 않고 모든 비즈니스로직이 끝나면 닫
            //JdbcUtils.closeConnection(con);
        }
    }
    @Override
    public void delete(String memberId)  {
        String sql = "DELETE FROM MEMBER WHERE MEMBER_ID = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw exTranslator.translate("delete", sql, e);
        } finally {
            close(con, pstmt, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        // 주의 ! 트랜잭션 동기화를 사용하러면 DataSourceUtils을 사용해야함
        DataSourceUtils.releaseConnection(con, dataSource); // 닫음

    }

    private Connection getConnection() {
        // 주의!! 트랜잭션 동기화를 사용하려면 DataSourceUtils을 사용해야함
        Connection con = DataSourceUtils.getConnection(dataSource); // 얻음

        return con;
    }
}
