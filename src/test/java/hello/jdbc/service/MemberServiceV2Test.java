package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import hello.jdbc.repository.MemberRepositoryV2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

//트랜잭션이 없어서 문제가 발생하는 상황 시뮬레이션
class MemberServiceV2Test
{
    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberRepositoryV2 memberRepository;
    private MemberServiceV2 memberService;

    @BeforeEach
    void before()
    {

        DriverManagerDataSource dataSource =
                new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepository = new MemberRepositoryV2(dataSource);
        memberService = new MemberServiceV2(dataSource, memberRepository);

        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        Member memberEX = new Member(MEMBER_EX, 10000);

        memberRepository.save(memberA);
        memberRepository.save(memberB);
        memberRepository.save(memberEX);
    }

    @AfterEach
    void after()
    {
        memberRepository.clear();
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException
    {
        memberService.accountTransfer(MEMBER_A, MEMBER_B, 2000);
        Member memberA = memberRepository.findById(MEMBER_A);
        Member memberB = memberRepository.findById(MEMBER_B);

        assertThat(memberA.getMoney()).isEqualTo(8000);
        assertThat(memberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체 오류")
    void accountTransferError()
    {
//        assertThatThrownBy(() -> memberService.accountTransfer(MEMBER_A, MEMBER_EX, 2000))
//                .isInstanceOf(IllegalStateException.class);

        Member memberA = memberRepository.findById(MEMBER_A);
        Member memberEX = memberRepository.findById(MEMBER_EX);

        assertThat(memberA.getMoney()).isEqualTo(10000);
        assertThat(memberEX.getMoney()).isEqualTo(10000);
    }
}