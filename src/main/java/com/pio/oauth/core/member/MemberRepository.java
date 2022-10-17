package com.pio.oauth.core.member;

import com.pio.oauth.core.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberId(String memberId);

    boolean existsMemberByMemberId(String memberId);
}
