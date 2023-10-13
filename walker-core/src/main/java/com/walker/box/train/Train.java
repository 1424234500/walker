package com.walker.box.train;


import com.walker.core.util.Tools;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 车次
 * 如 G1234
 */
public class Train {

    public static final String FILL_BLAN = "一";
    public static final String FILL_TO = "->";
    String secret_str;
    String train_no;
    String stationTrainCode; // G23423
    String start_station;
    String end_station;
    String from_station;
    String to_station;
    String from_time;
    String to_time;
    String use_time;
    String buy_able;
    String left_ticket;
    String start_time;
    String train_location;
    String from_station_no;
    String to_station_no;
    String gaojiruanwo;
    String qita;
    String ruanwo;
    String ruanzuo;
    String tedengzuo;
    String wuzuo;
    String yingwo;
    String yingzuo;
    String erdengzuo;
    String yidengzuo;
    String shangwuzuo;
    String dongwo;
    List<StationNode> stationNodeList = new ArrayList<>();
    boolean trainDealtaFlag = false;

    public static String fun(String wuzuo, String t) {
        t = Tools.nvl(wuzuo, t);
        return t.equals("无") ? t : "有票" + wuzuo;
    }

    public String toString() {
        String res = ""
                + FILL_BLAN + Tools.fillStringByRe(stationTrainCode, " ", 5)
                + FILL_BLAN + Tools.fillStringByRe(from_station.equals(start_station) ? "始" : start_station, FILL_BLAN, 4)
                + FILL_TO + Tools.fillStringByRe(from_station, FILL_BLAN, 4)
                + FILL_TO + Tools.fillStringByRe(to_station, FILL_BLAN, 4)
                + FILL_TO + Tools.fillStringByRe(to_station.equals(end_station) ? "终" : end_station, FILL_BLAN, 4)
                + FILL_BLAN + (from_time + "->" + to_time + "/" + use_time)
                + FILL_BLAN + Tools.fillStringByRe(("无座" + fun(wuzuo, "无")), FILL_BLAN, 4)
                + FILL_BLAN + Tools.fillStringByRe(("硬座" + fun(yingzuo, "无")), FILL_BLAN, 4)
                + FILL_BLAN + Tools.fillStringByRe(("二等" + fun(erdengzuo, "无")), FILL_BLAN, 4)
                + FILL_BLAN + Tools.fillStringByRe(("硬卧" + fun(yingwo, "无")), FILL_BLAN, 4);

        if (stationNodeList != null) {
            boolean enable = false;
            for (StationNode stationNode : stationNodeList) {
                if (stationNode.station_name.equals(from_station)) {
                    enable = true;
                }
                if (enable) {
                    res += (trainDealtaFlag ? "\n" : "") + stationNode;
                }
                if (stationNode.station_name.equals(to_station)) {
                    enable = false;
                    break;
                }
            }
        }
        return res;
    }

    //"5Z9gTxPE%2Bq%2Be5yzyGErP8p4X1jCnTPyugBNI1tFUq6KjUfTfMKc1Bd3jZO%2FIVxS58WLuGwBaMWrj%0AUNbHcAfRA2Es%2Bocpy1NcBYI6xfH9MAq6blNJYrb243T3dXeIuRtV%2FDH%2FEjm1B7Lwb%2BwIzdDqV9cq%0AtTIFBloBuWFsr%2FV6gaqIba6BspisYIfvd%2FUs3xZGGpeeN32nFeJV4H8lfC8NrQFOceU5tOiw6Nhn%0AlZv2zAOPyGFFuMJ2bZEE90HUZUqJfLWyLzXGqQwfqKaqszn%2BI1%2B6wkiykfq5UDpRQXV6pj6%2B49t5%0AYbvCl0P6U%2Btmc62q2mxEoE5OgraeK8Xf9o2M4w%3D%3D
    // |预订|56000G453300|G4533|HGH|NXG|HGH|NXG|00:29|02:57|02:28|N
    // |pAHf0gbBYRao8ksoeOA0d2bQ7NZ3Nf5tisZlh%2FE1fRrJ0gxO
    // |20230930|3|H1|01|02|1|0||||||无|||||无|无|||M0O0P0|MOP|0|1|
    // |M044350000O026350000P050150000|0|||||1|0#0#0#0#z#0#z||7|CHN,CHN|||N#N#",
    public Train build(String train_info, Map<String, Station> stationMap) {
        String[] train_info_list = train_info.split("\\|");
        // # 构造列车信息
        this.secret_str = make(train_info_list[0]);  // # secretStr ;为''时无法购买车票
        // # train_info_list[1]  预定/列车停运
        this.train_no = make(train_info_list[2]);  // # train_no
        this.stationTrainCode = make(train_info_list[3]);  // # stationTrainCode 即车次 // # 展示
        this.start_station = get(stationMap, make(train_info_list[4])).getName();  // # 始发站 // # 展示
        this.end_station = get(stationMap, make(train_info_list[5])).getName();  // # 终点站 // # 展示
        this.from_station = get(stationMap, make(train_info_list[6])).getName();  // # 出发站 // # 展示
        this.to_station = get(stationMap, make(train_info_list[7])).getName();  // # 到达站 // # 展示
        this.from_time = make(train_info_list[8]);  // # 出发时间 // # 展示
        this.to_time = make(train_info_list[9]);  // # 到达时间 // # 展示
        this.use_time = make(train_info_list[10]);  // # 时长 // # 展示
        this.buy_able = make(train_info_list[11]);  // # 能否购买 Y 可以购买 N 不可以购买 IS_TIME_NOT_BUY 停运 // # 展示
        this.left_ticket = make(train_info_list[12]);  // # leftTicket
        this.start_time = make(train_info_list[13]);  // # 车次始发日期 // # 展示
        this.train_location = make(train_info_list[15]);  // # train_location 不知道是啥??
        this.from_station_no = make(train_info_list[16]);  // # 出发站编号
        this.to_station_no = make(train_info_list[17]);  // # 到达站编号
        // # 14,18,19,20,27,34,35未知
        this.gaojiruanwo = make(train_info_list[21]);  // # 高级软卧 // # 展示
        this.qita = make(train_info_list[22]);  // # 其他 // # 展示
        this.ruanwo = make(train_info_list[23]);  // # 软卧 // # 展示
        this.ruanzuo = make(train_info_list[24]);  // # 软座 // # 展示
        this.tedengzuo = make(train_info_list[25]);  // # 特等座 // # 展示
        this.wuzuo = make(train_info_list[26]);  // # 无座 // # 展示
        this.yingwo = make(train_info_list[28]);  // # 硬卧 // # 展示
        this.yingzuo = make(train_info_list[29]);  // # 硬座 // # 展示
        this.erdengzuo = make(train_info_list[30]);  // # 二等座 // # 展示
        this.yidengzuo = make(train_info_list[31]);  // # 一等座 // # 展示
        this.shangwuzuo = make(train_info_list[32]);  // # 商务座 // # 展示
        this.dongwo = make(train_info_list[33]);  // # 动卧 // # 展示
        //trains_infos_list.append(train_info_dict)
        return this;
    }

    private Station get(Map<String, Station> stationMap, String make) {
        Station station = stationMap.get(make);
        if (station == null) {
            station = new Station();
            station.setName(make);
        }
        return station;
    }

    private String make(String s) {
        if (StringUtils.isBlank(s)) {
            s = "";
        }
        return s;
    }

    @Data
    @Accessors(chain = true)
    public static class StationNode {
        String arrive_time;  //"12:17",
        String station_name;  //"溧阳",
        String isChina;  //"1",
        String start_time;  //"12:19",
        String stopover_time;  //"2分钟",
        String station_no;  //"02",
        String country_code;  //"",
        String country_name;  //"",
        String isEnabled;  //false


        Train trainDealta;


        @Override
        public String toString() {
            String res = FILL_TO
                    + station_no
                    + "/" + start_time
                    + "/" + (stopover_time.equals("----") ? "0分钟" : stopover_time).replace("分钟", "m")
                    + "/" + Tools.fillStringByRe(station_name, FILL_BLAN, 4);
            if (trainDealta != null) {
                res = ""
                        + res
                        + FILL_BLAN + Tools.fillStringByRe(("无座" + fun(trainDealta.wuzuo, "无")), FILL_BLAN, 4)
                        + FILL_BLAN + Tools.fillStringByRe(("硬座" + fun(trainDealta.yingzuo, "无")), FILL_BLAN, 4)
                        + FILL_BLAN + Tools.fillStringByRe(("二等" + fun(trainDealta.erdengzuo, "无")), FILL_BLAN, 4)
                        + FILL_BLAN + Tools.fillStringByRe(("硬卧" + fun(trainDealta.yingwo, "无")), FILL_BLAN, 4)
                ;
            }
            return res;
        }


    }


}
