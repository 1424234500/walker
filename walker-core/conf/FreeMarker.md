# FreeMarker from Java

从 java 的角度编写 FreeMarker 从map键值一一替换模板变量生成代码

## !!! 用到的变量一定要模型传递 or 检测 null or 定义

注释

```
# 包含文件
include "FreeMarkerUtil.ftl"
---
FreeMarker模板定义工具
定义函数 宏 sum
定义成员变量 name
---

# 导入文件成员变量 and 宏 函数
import "FreeMarkerUtil.ftl" as util
util.name=name of util
util.sum(10,20)=30

    # 定义 赋值 输出

    #{id}

    #字符串
    name=HelloName
    name.toUpperFirst=HelloName
    name.toLowerFirst=helloName
    name.toLower=helloname
    name.toUpper=HELLONAME
    name.charAt(2)=l
    substr(0,5)=HelloN
    Hello HelloName !
    Hello HelloName !


    true
    0
    4
    4
    1abcd
    abcd1
    false
    Abcd
         "张三"
         "李思"
         ""
         "王强"
         "柳树"

    v abcd cc
        we
        are
        chinese
        you
        no
        diao

    #浮点数
    floata=12.34
    floata.intValue=12

    #bool
    boola=false

    #时间
    timea=1602729583000
    timea.format=yyyy-MM-dd HH:mm:ss1602729583000

    # 算数运算 分支 循环 遍历
    inta=10
    intb=5
    inta+intb=15   # 支持"+"、"－"、"*"、"/"、"%"运算符

    # if 比较 gte lt
        inta + intb >= 12 || inta - intb < 6=true
        name start with hello

    # if 判断 null
        objectIf is null and new=new objectIf by FreeMarker

    # Map
    mapd={"name":"程序员", "salary":15000}
    mapd["name"]=程序员
        kv - name 程序员
        kv - salary 15,000
        v - 程序员
        v - 15,000
    
    # for List index sort sort_by("map.key") 反转 break
    listd=["L1", "L3", "L2"]
    listd.size=3
        
        0 true L3
        , 
        1 true L2
        , 
        2 false L1


    # macro 宏

    调用宏 macro2=宏 参数定义及调用需一一对应!!! 3

```

