package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test
{
    MemberRepositoryV0 repository = new MemberRepositoryV0();

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