package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
//트랜잭션을 고려한 서비스
public class MemberServiceV2
{
    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public MemberServiceV2(DataSource dataSource, MemberRepositoryV2 memberRepository)
    {
        this.dataSource = dataSource;
        this.memberRepository = memberRepository;
    }

    //가상의 이체 중 오류발생 시뮬레이션 메서드
    public void accountTransfer(String fromId, String toId, int money) throws SQLException
    {
        Connection conn = dataSource.getConnection();

        try
        {
            conn.setAutoCommit(false);
            bizLogin(fromId, toId, money, conn);
            conn.commit();
        }
        catch (Exception e)
        {
            conn.rollback();
        } finally
        {
            if (conn != null)
            {
                try
                {
                    //커넥션풀로 돌려보낼 때, 오토커밋모드를 자동으로 설정한다.
                    conn.setAutoCommit(true);
                    conn.close();
                }
                catch (Exception e)
                {
                    log.info("error", e);
                }
            }
        }

    }

    private void bizLogin(String fromId, String toId, int money, Connection conn)
    {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(conn, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(conn, toId, fromMember.getMoney() + money);
    }

    //검증 중 예외 발생 시나리오.
    private static void validation(Member toMember)
    {
        if (toMember.getMemberId().equals("ex"))
        {
            throw new IllegalStateException("이체 중 예외가 발생함.");
        }
    }
}
