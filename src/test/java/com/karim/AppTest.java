package com.karim;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AppTest{

    private final List<String> myMock = mock();

    @Test
    void name() {

        myMock.add("hello");
        when(myMock.get(0)).thenReturn("hello");
        verify(myMock).add("hello");

        String actual = myMock.get(0);
        assertThat(actual).isEqualTo("hello");

    }
    @Test
    void testAnyMatcher(){
        Map<String, String> mockMap = mock();
        when(mockMap.get(anyString())).thenReturn("hello");
        assertThat(mockMap.get("1")).isEqualTo("hello");
        assertThat(mockMap.get("2")).isEqualTo("hello");
        verify(mockMap, times(2)).get(anyString());
    }
    @Test
    void testEqMatcher(){
        Map<String, String> mockMap = mock();
        when(mockMap.put(anyString(), eq("1"))).thenReturn("hello");

        String actual = mockMap.put("hello", "1");
        assertThat(actual).isEqualTo("hello");
        verify(mockMap).put("hello", "1");
    }
    @Test
    void shouldVerifyNoInteractions(){
        // given
        List<String> listMock = mock();
        // when
        listMock.clear();
        listMock.add("hello");
        // then
        verify(listMock).clear();
        verify(listMock).add("hello");
        verifyNoMoreInteractions(listMock);
    }

    @Test
    void shouldVerifyInteractionMode(){
        // given
        List<String> listMock = mock();
        // when
        listMock.clear();
        listMock.clear();
        // then
        verify(listMock, times(2) ).clear();
        verifyNoMoreInteractions(listMock);
    }
}
