package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;

public class MemberServiceV1
{
    private final MemberRepositoryV1 memberRepository;

    public MemberServiceV1(MemberRepositoryV1 memberRepository)
    {
        this.memberRepository = memberRepository;
    }

    //가상의 이체 중 오류발생 시뮬레이션 메서드
    public void accountTransfer(String fromId, String toId, int money)
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
