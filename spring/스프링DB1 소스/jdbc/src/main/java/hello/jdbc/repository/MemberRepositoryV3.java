package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * 트랜잭션 - 트랙잭션 매니저 사용
 * DataSourceUtils 사용
 * (트랙잭션 동기화 매니저를 사용하여 커넥션을 닫고 트랜잭션을 사용함)
 */
@Slf4j
public class MemberRepositoryV3 {
    private final DataSource dataSource;

    public MemberRepositoryV3(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {
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
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    public Member findById(String memberId) throws SQLException {
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
            log.error("error", e);
            throw e;
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
            // connection 유지를 위해 여기서 닫지 않고 모든 비즈니스로직이 끝나면 닫음
            //JdbcUtils.closeConnection(con);
        }
    }

    public void update(String memberId, int money) throws SQLException {
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
            throw e;
        } finally {
            JdbcUtils.closeStatement(pstmt);
            // connection 유지를 위해 여기서 닫지 않고 모든 비즈니스로직이 끝나면 닫
            //JdbcUtils.closeConnection(con);
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql = "DELETE FROM MEMBER WHERE MEMBER_ID = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw e;
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

    private Connection getConnection() throws SQLException {
        // 주의!! 트랜잭션 동기화를 사용하려면 DataSourceUtils을 사용해야함
        Connection con = DataSourceUtils.getConnection(dataSource); // 얻음

        return con;
    }
}
