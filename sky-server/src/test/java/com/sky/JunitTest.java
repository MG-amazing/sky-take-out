package com.sky;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class JunitTest {
    @Test
    public void testAdd() {
        assertEquals(3, 1 + 2);
    }

    @DisplayName("加法测试")
    @Test
    void testAdd2() {
        assertNotEquals(6, 1 + 2);
    }

    @Test
    void testAdd12() {
        assertTrue(3 == 1 + 2);
    }

    @Test
    void testAdd13() {
        assertFalse(6 == 1 + 2);
    }

    @Test
    void testAdd14() {
        String a = "6666";
        String b = "6666";
        assertSame(a, b);
    }
    @Test
    void testAdd15() {
        String a = "6666";
        Double b = Double.valueOf(a);
        assertNotSame(a, b);
    }


    //    @BeforeEach//每个测试方法前执行
//    void init() {
//        System.out.println("初始化");
//    }
//    @BeforeAll
//    static void init666() {
//        System.out.println("初始化");
//    }
//    @AfterAll
//    static void init65556() {
//        System.out.println("初始化");
//    }
    @Disabled
    @Test
    void testOldMethod() {
    }

    @RepeatedTest(3)
    void testRepeat() {
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void testParam(int num) {
        assertTrue(num > 0);
    }


}
