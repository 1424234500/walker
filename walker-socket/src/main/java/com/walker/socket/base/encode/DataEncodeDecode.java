package com.walker.socket.base.encode;

public interface DataEncodeDecode<DATA> {

    String encode(DATA data);

    DATA decode(String str);


}
