package com.walker.core.mode;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
class EchartsNode {
    String name = "name";
    String id = "0";
    int symbolSize = 60;

}
