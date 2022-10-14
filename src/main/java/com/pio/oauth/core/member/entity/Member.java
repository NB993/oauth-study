package com.pio.oauth.core.member.entity;

import com.pio.oauth.auth.ProviderType;
import com.sun.istack.NotNull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberSeq;

    @Column(unique = true)
    @NotNull
    private String memberId;

    private String name;

    private String password;

    private String email;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ProviderType providerType;

    public Member(String memberId, String email, String name) {
        this.memberId = memberId;
        this.email = email;
        this.name = name;
    }
}
