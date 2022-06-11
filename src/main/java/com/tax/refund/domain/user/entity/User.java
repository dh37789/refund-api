package com.tax.refund.domain.user.entity;

import com.sun.istack.NotNull;
import com.tax.refund.global.config.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.regex.Pattern;

@Entity
@Getter
@Table(name = "tb_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Long id;

    @Column(unique = true)
    @NotNull
    String userId;

    @NotNull
    String password;

    @NotNull
    String name;

    @NotNull
    String regNo;

    @Builder
    public User(Long id, String userId, String password, String name, String regNo) {
        this.id = id;
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.regNo = regNo;
    }

    public static boolean isMatchedRegNo(final String REG_NO_REGEX, String regNo) {
        return Pattern.matches(REG_NO_REGEX, regNo);
    }

    public static boolean isMatchedPassowrd(PasswordEncoder passwordEncoder, String inputPassword, String userPassword) {
        return passwordEncoder.matches(inputPassword, userPassword);
    }
}
