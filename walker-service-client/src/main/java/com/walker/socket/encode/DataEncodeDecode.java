package com.walker.socket.encode;

public interface DataEncodeDecode<DATA> {

    String encode(DATA data);

    DATA decode(String str);


}
