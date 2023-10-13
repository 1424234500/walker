package com.walker.core.mode;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
class EchartsLinkRelation {
    String name = "line";
    String id = "0";
}
