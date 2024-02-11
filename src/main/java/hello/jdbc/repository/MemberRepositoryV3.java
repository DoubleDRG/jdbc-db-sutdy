package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

//V3: 트랜잭션 - 트랜잭션 매니저
// DataSource - DataSourceUtils를 씀 
// 커넥션을 끊을 때도 - DataSourceUtils를 씀
@Slf4j
public class MemberRepositoryV3
{
    private final DataSource dataSource;

    public MemberRepositoryV3(DataSource dataSource)
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

        //주의! 트랜잭션 동기화를 사용하려면, DataSourceUtils를 사용해야 한다.
        DataSourceUtils.releaseConnection(conn, dataSource);
    }

    private Connection getConnection() throws SQLException
    {
        //주의! 트랜잭션 동기화를 하려면, DataSourceUtils를 사용해야 한다.
        Connection con = DataSourceUtils.getConnection(dataSource);
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
}