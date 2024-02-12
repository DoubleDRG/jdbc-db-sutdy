package hello.jdbc.exception.translator;

import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import hello.jdbc.repository.ex.MyDuplicateKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static hello.jdbc.connection.ConnectionConst.*;

public class ExTranslatorV1Test
{
    Repository repository;
    Service service;

    @BeforeEach
    void init()
    {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        repository = new Repository(dataSource);
        service = new Service(repository);
    }

    @Test
    void duplicateKeySave()
    {
        service.create("myId");
        service.create("myId");
    }

    @Slf4j
    @RequiredArgsConstructor
    static class Service
    {
        private final Repository repository;

        public void create(String memberId)
        {
            try
            {
                repository.save(new Member(memberId, 0));
                log.info("saveId ={}", memberId);
            }
            catch (MyDuplicateKeyException e)
            {
                log.info("키 중복, 복구시도");
                String retryId = generateNewId(memberId);
                log.info("retryId ={}", retryId);
                repository.save(new Member(retryId, 0));
            }
            catch (MyDbException e)
            {
                log.info("데이터 접근 계층 예외", e);
                throw e;
            }

        }

        private String generateNewId(String memberId)
        {
            return memberId + new Random().nextInt(10000);
        }
    }


    @RequiredArgsConstructor
    static class Repository
    {
        private final DataSource dataSource;
        public Member save(Member member)
        {
            String sql = "insert into member(member_id, money) values(?,?)";
            Connection con = null;
            PreparedStatement pstmt = null;

            try
            {
                con = dataSource.getConnection();
                pstmt = con.prepareStatement(sql);
                pstmt.setString(1, member.getMemberId());
                pstmt.setInt(2,member.getMoney());
                pstmt.executeUpdate();
                return member;
            }
            catch (SQLException e)
            {
                //DB마다 이 오류코드가 다 다르다. DB가 변경될 경우, 다 수정해야 한다.
                //DB의 오류는 수백가지이다. 이 오류를 일일이 다 코드를 짜야할까?
                //Spring이 이런 예외처리를 다 해준다.
                if (e.getErrorCode() == 23505)
                {
                    throw new MyDuplicateKeyException(e);
                }
                throw new MyDbException(e);
            }
            finally
            {
                JdbcUtils.closeStatement(pstmt);
                JdbcUtils.closeConnection(con);
            }
        }
    }
}
