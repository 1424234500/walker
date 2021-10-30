package com.walker.socket.base.encode;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

public class DataEncodeDecodeJson<DATA> implements DataEncodeDecode<DATA> {
    Class<DATA> clz;

    public DataEncodeDecodeJson(Class<DATA> clz) {
        this.clz = clz;
    }

    @Override
    public String encode(DATA data) {
        return JSON.toJSONString(data);
    }

    @Override
    public DATA decode(String str) {
        return JSON.parseObject(str, new TypeReference<DATA>(){});
    }


}
