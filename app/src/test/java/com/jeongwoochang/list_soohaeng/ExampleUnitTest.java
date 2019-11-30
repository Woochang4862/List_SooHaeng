package com.jeongwoochang.list_soohaeng;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        try {
            String text = "ABCDEFGHIJKLMNOP"; // translating text String to 7 bit ASCII encoding
            byte[] bytes = text.getBytes("US-ASCII");
            System.out.println("ASCII value of " + text + " is following");
            System.out.println(Arrays.toString(bytes));
            assertEquals(Arrays.toString(bytes), "");
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}