package com.karim;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class VerificationCodeServiceTest {

    @Mock
    private Clock clock = mock();

    private ZoneId zoneId = ZoneId.of("Europe/London");
    private ZonedDateTime fixedZdt = ZonedDateTime.of(2025, 1, 1, 0, 0, 0, 0, zoneId);

    private Duration duration = Duration.of(15, ChronoUnit.MINUTES);
    private VerificationCodeService underTest;

    @BeforeEach
    void setUp(){
        underTest = new VerificationCodeService(clock, duration);
        given(clock.getZone()).willReturn(zoneId);
        given(clock.instant()).willReturn(fixedZdt.toInstant());
    }

    @Test
    void canReturnFalseWhenCodeIsNotExpired() {
        // given
        ZonedDateTime createdAt = ZonedDateTime.now(clock);
        VerificationCode code = new VerificationCode("kb-123", createdAt);
        // when
        boolean isExpired = underTest.isExpired(code);
        // then
        assertThat(isExpired).isFalse();
    }

    @Test
    void canReturnFalseWhenElapsedIs15Minutes() {
        // given
        ZonedDateTime createdAt = ZonedDateTime.now(clock);
        VerificationCode code = new VerificationCode("kb-123", createdAt);
        // advance clock 15min
        given(clock.instant()).willReturn(createdAt.plusMinutes(15).toInstant());
        // when
        boolean isExpired = underTest.isExpired(code);
        // then
        assertThat(isExpired).isFalse();
    }

    @Test
    void canReturnTrueWhenElapsedIs15MinutesAnd1Second() {
        // given
        ZonedDateTime createdAt = ZonedDateTime.now(clock);
        VerificationCode code = new VerificationCode("kb-123", createdAt);
        // advance clock 15min 1 sec
        given(clock.instant()).willReturn(createdAt.plusMinutes(15).plusSeconds(1).toInstant());
        // when
        boolean isExpired = underTest.isExpired(code);
        // then
        assertThat(isExpired).isTrue();
    }
}