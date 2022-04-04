package com.szs.szsrefund.user;

import com.szs.szsrefund.domain.user.entity.User;
import com.szs.szsrefund.domain.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("local")
public class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @Test
    @Order(1)
    @DisplayName("findByRegNo 테스트")
    void findByRegNo_테스트() {
        // given
        User user = User.builder()
                    .id(1L)
                    .name("홍길동")
                    .userId("abcd123")
                    .regNo("860824-1655068")
                    .password("1234")
                    .build();
        when(userRepository.findByRegNo(any())).thenReturn(Optional.of(user));

        // when
        final String regNo = "860824-1655068";
        User result = userRepository.findByRegNo(regNo).get();

        // then
        assertThat(result.getUserId()).isEqualTo(user.getUserId());
    }

    @Test
    @Order(2)
    @DisplayName("findByUserId 테스트")
    void findByUserId_테스트() {
        // given
        User user = User.builder()
                .id(1L)
                .name("홍길동")
                .userId("abcd123")
                .regNo("860824-1655068")
                .password("1234")
                .build();
        when(userRepository.findByUserId(any())).thenReturn(Optional.of(user));

        // when
        final String userId = "abcd123";
        User result = userRepository.findByUserId(userId).get();

        // then
        assertThat(result.getUserId()).isEqualTo(user.getUserId());
    }

}
