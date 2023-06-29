package com.walker.core.encode;

import com.walker.core.Context;
import com.walker.core.util.Tools;
import org.junit.Test;

import java.io.IOException;

public class IOEncodeTest {
    @Test
    public void test(){
        String str = "hello";
        byte[] bs = str.getBytes();
        byte[][] ssL = new byte[8][bs.length];
        byte[][] ssR = new byte[8][bs.length];

        for(int i = 0; i < bs.length; i++){
            System.out.println(i + " " + bs[i]);
            bs[i] = IOEncode.moveRotate(bs[i], 4);
            for(int j = 0; j < 8; j++) {
                ssR[j][i] = IOEncode.moveRotate(bs[i], +(j+1) );
                ssL[j][i] = IOEncode.moveRotate(bs[i], -(j+1) );
            }
        }

        for(int i = 0; i < ssR.length; i++){
            Tools.out(i, "R" ,new String(ssR[i]) );
            Tools.out(i, "L", new String(ssL[i]) );
        }
        Tools.out(new String(bs));

        for(int i = 0; i < bs.length; i++){
            System.out.println(i + " " + bs[i]);
            bs[i] = IOEncode.moveRotate(bs[i], 4);

            for(int j = 0; j < 8; j++) {
                ssR[j][i] = IOEncode.moveRotate(ssR[j][i], -(j+1) );
                ssL[j][i] = IOEncode.moveRotate(ssL[j][i], +(j+1) );
            }
        }

        for(int i = 0; i < ssR.length; i++){
            Tools.out(i, "R" ,new String(ssR[i]) );
            Tools.out(i, "L", new String(ssL[i]) );
        }
        Tools.out(new String(bs));



    }
    @Test
    public void encode() throws IOException {

        String from = Context.getPathConf("jdbc.properties");
        String to = "release/jdbc.properties";

        IOEncode.encode(from, to);


    }

    @Test
    public void decode() throws IOException {

        String to = "release/jdbc.properties";
        String toto = "release/jdbc.decode.properties";

        IOEncode.decode(to, toto);

    }
}