package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
//트랜잭션을 고려한 서비스
public class MemberServiceV2
{
    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    //트랜잭션은 비즈니스 로직이 있는 서비스 계층 단위로 이루어져야 한다.
    //그러나 그렇게 하려고 트랜잭션 관련 코드를 서비스 계층에 작성하면, JDBC기술에 의존하게 된다.
    //서비스 계층의 코드는 특정 기술에 의존하면 안된다.
    //다른 DB기술로 교체할 때, 모든 서비스 계층 코드를 수정해야 하기 때문이다.
    //스프링은 순수한 서비스계층을 유지하면서, 트랜잭션도 서비스 계층에서 관리하는 기술을 제공한다.
    public void accountTransfer(String fromId, String toId, int money) throws SQLException
    {
        Connection con = dataSource.getConnection();

        try
        {
            con.setAutoCommit(false);
            bizLogic(fromId, toId, money, con);
            con.commit();
        }
        catch (Exception e)
        {
            con.rollback();
        } finally
        {
            if (con != null)
            {
                try
                {
                    //커넥션풀로 돌려보낼 때, 오토커밋모드를 자동으로 설정한다.
                    con.setAutoCommit(true);
                    con.close();
                }
                catch (Exception e)
                {
                    log.info("error", e);
                }
            }
        }

    }

    private void bizLogic(String fromId, String toId, int money, Connection conn)
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
