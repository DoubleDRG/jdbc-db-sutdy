package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
//MemberRepository에 의존한다.
//MemberRepository구현체의 SQLException을 RuntimeException으로 한 번 감쌌기 때문에
//SQLException에 의존하지 않는다.
public class MemberServiceV4
{
    private final MemberRepository memberRepository;

    @Transactional
    public void accountTransfer(String fromId, String toId, int money)
    {
        bizLogic(fromId, toId, money);
    }

    private void bizLogic(String fromId, String toId, int money)
    {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, fromMember.getMoney() + money);
    }

    private static void validation(Member toMember)
    {
        if (toMember.getMemberId().equals("ex"))
        {
            throw new IllegalStateException("이체 중 예외가 발생함.");
        }
    }
}
