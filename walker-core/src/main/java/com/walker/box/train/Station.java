package com.walker.box.train;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 车次
 * 如 G1234
 */
@Data
@Accessors(chain = true)
public class Station {
    String code;
    String name;
    String city;

    // bjy|北京朝阳|IFP|beijingchaoyang|bjcy|6|0357|北京|||
    public Station build(String s) {
        String[] ss = s.split("\\|");
        this.code = ss[2];
        this.name = ss[1];
        this.city = ss[7];
        return this;
    }

    @Override
    public String toString() {
        return city + "/" + name + ":" + code;
    }

}
