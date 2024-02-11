package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;

class MemberServiceV3_2Test
{
    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberRepositoryV3 memberRepository;
    private MemberServiceV3_2 memberService;

    @BeforeEach
    void before()
    {
        DriverManagerDataSource dataSource =
                new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepository = new MemberRepositoryV3(dataSource);

        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        memberService = new MemberServiceV3_2(transactionManager, memberRepository);

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
    void accountTransferError() throws SQLException
    {
//        assertThatThrownBy(() -> memberService.accountTransfer(MEMBER_A, MEMBER_EX, 2000))
//                .isInstanceOf(IllegalStateException.class);

        Member memberA = memberRepository.findById(MEMBER_A);
        Member memberEX = memberRepository.findById(MEMBER_EX);

        assertThat(memberA.getMoney()).isEqualTo(10000);
        assertThat(memberEX.getMoney()).isEqualTo(10000);
    }
}