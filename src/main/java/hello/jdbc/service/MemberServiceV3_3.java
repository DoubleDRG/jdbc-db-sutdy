package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
//트랜잭션 - @Transactional AOP 버전
//@Transactional을 사용하여 Service클래스의 프록시를 생성한다.
//코드가 더러워지는 트랜잭션 처리, 예외처리는 이 프록시가 알아서 코드를 작성한다.
public class MemberServiceV3_3
{
    private final MemberRepositoryV3 memberRepository;

    @Transactional
    public void accountTransfer(String fromId, String toId, int money) throws SQLException
    {
        bizLogic(fromId, toId, money);
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
