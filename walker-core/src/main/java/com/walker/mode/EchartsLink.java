package com.walker.mode;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Generated
class EchartsLink {
    String source = "0";
    String target = "1";
    EchartsLinkRelation relation = new EchartsLinkRelation("line", "0");
}
