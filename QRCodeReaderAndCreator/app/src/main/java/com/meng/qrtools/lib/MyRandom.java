package com.meng.qrtools.lib;

import android.graphics.Bitmap;

import java.util.HashSet;
import java.util.Random;


public class MyRandom {

    private Random random = new Random(9961);
    private HashSet<Integer> hashSet = new HashSet<Integer>();
    private int height = 1;
    private int width = 1;

    public MyRandom(Bitmap b) {
        height = b.getHeight();
        width = b.getWidth();
    }

    public int nextW() {
        int num = 0;
        while (true) {
            num = random.nextInt(width);
            if (!hashSet.contains(num)) {
                hashSet.add(num);
                return num;
            }
        }
    }

    public int nextH() {
        int num = 0;
        while (true) {
            num = random.nextInt(height);
            if (!hashSet.contains(num)) {
                hashSet.add(num);
                return num;
            }
        }
    }

}
