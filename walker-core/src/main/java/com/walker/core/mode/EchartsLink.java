package com.walker.core.mode;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
class EchartsLink {
    String source = "0";
    String target = "1";
    EchartsLinkRelation relation = new EchartsLinkRelation("line", "0");
}
