package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
//트랜잭션 - 트랜잭션 동기화 매니저를 사용하는 방식
//트랜잭션 동기화 매니저를 사용하면, 서비스 계층의 코드가 특정 기술(여기서는 DataSource)에 의존하지 않는다.
//여기서는 Spring에서 제공하는 인터페이스를 사용해서 트랜잭션 기술을 변경해도 서비스계층의 코드를 수정하지 않게 한다.
//그리고 반복되는 트랜잭션 관련 코드들이 사라진다.
public class MemberServiceV3_1
{
    //    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private final MemberRepositoryV3 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException
    {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try
        {
            bizLogic(fromId, toId, money);
            transactionManager.commit(status);
        }
        catch (Exception e)
        {
            transactionManager.rollback(status);
            throw new IllegalStateException(e);
        }
    }

    private void bizLogic(String fromId, String toId, int money) throws SQLException
    {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, fromMember.getMoney() + money);
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
