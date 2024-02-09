package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

//V2: 커넥션을 파라미터로 전달받는 방식.
//트랜잭션을 관리하려면, 동일한 세션에서 하나의 서비스 로직이 실행되어야함.
@Slf4j
public class MemberRepositoryV2
{
    private final DataSource dataSource;

    public MemberRepositoryV2(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    public Member save(Member member)
    {
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try
        {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            int count = pstmt.executeUpdate();
            return member;
        }
        catch (SQLException e)
        {
            log.error("db error", e);
            throw new RuntimeException(e);
        } finally
        {
            close(conn, pstmt, null);
        }
    }

    public Member findById(String memberId)
    {
        String sql = "select * from member where member_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try
        {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();

            if (rs.next())
            {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }
            else
            {
                throw new NoSuchElementException("member not found memberId = " + memberId);
            }
        }
        catch (SQLException e)
        {
            log.error("db error", e);
            throw new RuntimeException(e);
        } finally
        {
            close(conn, pstmt, rs);
        }
    }

    public Member findById(Connection conn, String memberId)
    {
        String sql = "select * from member where member_id = ?";

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try
        {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();

            if (rs.next())
            {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }
            else
            {
                throw new NoSuchElementException("member not found memberId = " + memberId);
            }
        }
        catch (SQLException e)
        {
            log.error("db error", e);
            throw new RuntimeException(e);
        } finally
        {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
            //JdbcUtils.closeConnection(conn);
            //커넥션은 동일한 트랜잭션의 다른 쿼리를 보낼 때도 사용하기 때문에,
            //절대로 반납하면 안된다.
        }
    }

    public void update(Connection conn, String memberId, int money)
    {
        String sql = "update member set money=? where member_id=?";

        PreparedStatement pstmt = null;

        try
        {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            pstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        } finally
        {
            JdbcUtils.closeStatement(pstmt);
        }
    }

    public void delete(String memberId)
    {
        String sql = "delete from member where member_id=?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try
        {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        } finally
        {
            close(conn, pstmt, null);
        }
    }

    public void clear()
    {
        String sql = "delete from member";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try
        {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        } finally
        {
            close(conn, pstmt, null);
        }
    }

    private void close(Connection conn, Statement stmt, ResultSet rs)
    {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(conn);
    }

    private Connection getConnection() throws SQLException
    {
        Connection con = dataSource.getConnection();
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
}
