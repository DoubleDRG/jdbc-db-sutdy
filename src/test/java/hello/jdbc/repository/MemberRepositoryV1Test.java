package hello.jdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.NoSuchElementException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class MemberRepositoryV1Test
{
    MemberRepositoryV1 repository;

    @BeforeEach
    void beforeEach()
    {
        //1.DataSource -> DriverManager방식 - 항상 새로운 커넥션을 획득 (ex.con1,con2,con3,...con5)
        //DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        //2.DataSource -> HikariCP방식 - 커넥션풀에서 커넥션을 획득 (ex.conn0, conn0, ... )
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("DoubleDRG Pool");

        //레포지토리에 dataSource 끼우기
        repository = new MemberRepositoryV1(dataSource);
    }

    @Test
    void crud()
    {
        Member member = new Member("memberV3", 10000);
        repository.save(member);

        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember = {}", findMember);

        repository.update(member.getMemberId(), 20000);
        Member updateMember = repository.findById(member.getMemberId());
        log.info("updateMember = {}", updateMember);

        repository.delete(member.getMemberId());
        assertThatThrownBy(() -> repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);
    }
}