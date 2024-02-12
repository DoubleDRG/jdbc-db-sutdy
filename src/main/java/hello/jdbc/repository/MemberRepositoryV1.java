package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

//V1방식: JDBC DataSource 인터페이스 -> DriverManager로 커넥션을 얻는다.
//DataSource 인터페이스를 사용하면,
//나중에 DriverManager방식에서 HikariCP방식으로 바꿔도 기존 코드를 수정하지 않아도 된다.
@Slf4j
public class MemberRepositoryV1
{
    private final DataSource dataSource;

    public MemberRepositoryV1(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    public Member save(Member member)
    {
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try
        {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
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
            close(con, pstmt, null);
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

    public void update(String memberId, int money)
    {
        String sql = "update member set money=? where member_id=?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try
        {
            conn = getConnection();
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
            close(conn, pstmt, null);
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
