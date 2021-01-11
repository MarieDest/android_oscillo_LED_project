package com.example.tp_eb03;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    ByteRingBuffer brb = new ByteRingBuffer(5);

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void putandGet(){
        assertEquals(0,brb.getWriteIndex());
        assertEquals(0,brb.getReaderIndex());
        assertEquals(5,brb.getCapacité());
        byte by=1;
        brb.put(by);
        assertEquals(1,brb.bytesToRead());
        assertEquals(1,brb.getWriteIndex());
        assertEquals(0,brb.getReaderIndex());

        byte[] bytes2 = new byte[]{1,2,3};
        System.out.println(bytes2.length);
        brb.put(bytes2);
       // System.out.println(brb.getWriteIndex());
        assertEquals(4,brb.bytesToRead());
        assertEquals(4,brb.getWriteIndex());
        assertEquals(0,brb.getReaderIndex());
        byte b = brb.get();
        assertEquals(1,b);
        assertEquals(3,brb.bytesToRead());
        assertEquals(4,brb.getWriteIndex());
        assertEquals(1,brb.getReaderIndex());
        byte bb= brb.get();
        assertEquals(1,bb);
        byte[] bytes=brb.getAll();
        //System.out.println();
        assertEquals(2,bytes.length);
        assertEquals(2,bytes[0]);
        assertEquals(3,bytes[1]);
        assertEquals(0,brb.bytesToRead());
        assertEquals(4,brb.getWriteIndex());
        assertEquals(4,brb.getReaderIndex());
        byte[]  bbb =new byte[]{4};
        System.out.println(bbb[0]);
        brb.put(bbb);
        assertEquals(1,brb.bytesToRead());
        assertEquals(0,brb.getWriteIndex());
        assertEquals(4,brb.getReaderIndex());
        brb.put(b);
        assertEquals(2,brb.bytesToRead());
        assertEquals(1,brb.getWriteIndex());
        assertEquals(4,brb.getReaderIndex());
        bytes = brb.getAll();
        assertEquals(2,bytes.length);
        assertEquals(4,bytes[0]);
        assertEquals(1,bytes[1]);
        assertEquals(0,brb.bytesToRead());
        assertEquals(1,brb.getWriteIndex());
        assertEquals(1,brb.getReaderIndex());
    }
}