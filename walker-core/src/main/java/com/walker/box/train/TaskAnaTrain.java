package com.walker.box.train;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.walker.core.util.HttpBuilder;
import com.walker.core.util.MapBuilder;
import com.walker.core.util.ThreadUtil;
import com.walker.core.util.Tools;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 车次
 * 如 G1234
 */
public class TaskAnaTrain {
    public static boolean logDt = false;
    public static long WAIT = 1000;
    public static long WAIT_D = 100;
    String cookie;
    String who = "ffff";
    List<Station> stationList = new ArrayList<>();
    Map<String, Station> stationNameMap = new LinkedHashMap<>();
    Map<String, Station> stationCodeMap = new LinkedHashMap<>();
    Map<String, List<Station>> stationCityMap = new LinkedHashMap<>();

    public TaskAnaTrain() throws Exception {

        cookie = "_uab_collina=169595874969722123676918; JSESSIONID=903532475139BAD6C1645514C; guidesStatus=off; highContrastMode=defaltMode; cursorStatus=off; BIGipServerpassport=770179338.50215.0000; route=9036359bb8a8a461c164a04f8f50b252; _jc_save_fromStation=%u676D%u5DDE%u4E1C%2CHGH; _jc_save_toStation=%u5357%u660C%2CNCG; _jc_save_toDate=2023-09-29; _jc_save_wfdc_flag=dc; BIGipServerportal=3134456074.16671.0000; BIGipServerpool_passport=266600970.50215.0000; _jc_save_fromDate=2023-09-29; BIGipServerotn=2715222282.64545.0000; fo=r2ku3wkl0ixkzo5xyJe-HPlhOGSp_MYecel4JfGvvk--QfPUGNDSsujpLUdp1_ZMQ_md458smVQoKxisGwZy1lyoOtx_bS5VCSfEZt6rLqQpi_Z7MIR7fgFHblXT6Tl1MObMPiZHoVOInlFB1Gq0AJ59fB0IuzeM6PyigdCxCSDu2uNF7Za-nTqYxOU";
        String day = "2023-09-30";
        String fromHour = "00:00";
        String toHour = "24:45";
        String from = "杭州东";
        String to = "南昌西";
        List<String> whiteList = new ArrayList<>();
//         whiteList = Arrays.asList("G997, G1397".split(", *"));

        int i = 0;
        Tools.out();
        Tools.out((i++) + ". 初始化cityMap");
        loadCityMap();

        Tools.out();
        Tools.out((i++) + ". 开始扫描车次 是否有票");
        List<Train> trainList = listTrain(from, to, day, null, null, null);

        for (Train train : trainList) {
            if (logDt) {
                Tools.out(train);
            }
        }
        Tools.out();
        Tools.out((i++) + ". 过滤后的车次");
        List<Train> trainListFilter = listTrain(from, to, day, whiteList, fromHour, toHour);

        Tools.out();
        Tools.out((i++) + ". 按下任意键, 开始遍历所有过滤后的车次时刻表");
//        System.in.read();
        for (Train train : trainListFilter) {
            List<Train.StationNode> list = getTrainTime(train, day);
            train.stationNodeList = list;
            Tools.out(train);
        }
        Tools.out();
        Tools.out((i++) + ". 构建路网图 获取所有车次城市可选图");
        // 起点 终点 车次列表
        Map<String, List<Train>> mapRoad = calcMapRoad(trainListFilter);

        Tools.out();
        Tools.out((i++) + ". 按下任意键, 开始扫描区间票");
//        System.in.read();
        scanAndFill(from, mapRoad, day);
        for (Train train : trainListFilter) {
            Tools.out(train);
        }

    }

    public static void main(String[] args) throws Exception {

        new TaskAnaTrain();

    }

    private void scanAndFill(String from, Map<String, List<Train>> mapRoad, String day) throws Exception {

        for (String to : mapRoad.keySet()) {
            // 目标火车
            List<Train> trains = mapRoad.get(to);

            List<Train> filterTrain = listTrain(from, to, day, trains.stream().map(item -> item.stationTrainCode).collect(Collectors.toList()), null, null);
            // 中间站 to的 目标火车 的查询票情况
            // 中间站的余票 放置于 目标火车的时刻表中
            for (Train train : filterTrain) {
                for (Train train1 : trains) {
                    if (train1.stationNodeList == null) {
                        continue;
                    }
                    for (Train.StationNode stationNode : train1.stationNodeList) {
                        if (stationNode.station_name.equals(to)) {
                            if (train1.stationTrainCode.equals(train.stationTrainCode)) {
                                stationNode.trainDealta = train;
                            }
                        }
                    }
                    train1.trainDealtaFlag = true;
                }
            }
        }
    }

    private Map<String, List<Train>> calcMapRoad(List<Train> trainList) {
        Map<String, List<Train>> mapRoad = new LinkedHashMap<>();
        for (Train train : trainList) {
            if (train.stationNodeList != null) {
                boolean enable = false;
                for (Train.StationNode stationNode : train.stationNodeList) {

                    // 终点站不需要处理
                    if (stationNode.station_name.equals(train.to_station)) {
                        enable = false;
                        break;
                    }
                    if (enable) {
                        List<Train> list = mapRoad.getOrDefault(stationNode.station_name, new ArrayList<>());
                        mapRoad.put(stationNode.station_name, list);
                        list.add(train);
                    }
                    // 起点站也不需要
                    if (stationNode.station_name.equals(train.from_station)) {
                        enable = true;
                    }
                }
            }
        }
        // 最远距离排序 todo check
        List<String> list = new ArrayList<>(mapRoad.keySet());
        for (int i = list.size() - 1; i >= 0; i--) {
            mapRoad.put(list.get(i), mapRoad.get(list.get(i)));
        }
        for (String stationName : mapRoad.keySet()) {
            Tools.out(stationName, mapRoad.get(stationName).stream().map(item -> item.stationTrainCode).collect(Collectors.toList()));
        }
        return mapRoad;
    }

    private void loadCityMap() throws Exception {
        // 获取站点简称
        String url = "https://kyfw.12306.cn/otn/resources/js/framework/station_name.js?station_version=1.9270";
        ThreadUtil.sleep(50);
        String resp = new HttpBuilder(url, HttpBuilder.Type.GET)
                .setWho(who)
                .buildString();
//        var station_names = '
//        @bjb|北京北|VAP|beijingbei|bjb|0|0357|北京|||
//        @bjd|北京东|BOP|beijingdong|bjd|1|0357|北京|||
//        @bji|北京|BJP|beijing|bj|2|0357|北京|||
//        @bjn|北京南|VNP|beijingnan|bjn|3|0357|北京|||
//        @bjx|北京大兴|IPP|beijingdaxing|bjdx|4|0357|北京|||
//        @bjx|北京西|BXP|beijingxi|bjx|5|0357|北京|||
//        @bjy|北京朝阳|IFP|beijingchaoyang|bjcy|6|0357|北京|||
//        @cqb|重庆北|CUW|chongqingbei|cqb|7|1717|重庆|||
//        @cqi|重庆|CQW|chongqing|cq|9|17...
        String[] rss = resp.split("@");
        for (int i = 0; i < rss.length; i++) {
            if (i == 0) {
                continue;
            }
            Station station = new Station().build(rss[i]);
            stationList.add(station);
            stationNameMap.put(station.getName(), station);
            stationCodeMap.put(station.getCode(), station);
            List<Station> list = stationCityMap.getOrDefault(station.getCity(), new ArrayList<>());
            stationCityMap.put(station.getCity(), list);
            list.add(station);
        }
        int i = 0;
        for (String city : stationCityMap.keySet()) {
            String st = "";
            for (Station station : stationCityMap.get(city)) {
                st += "," + station.toString();
            }
            if (logDt) {
                Tools.out(i++, city, st);
            }
        }
    }

    private List<Train> listTrain(String from, String to, String day, List<String> filterNo, String fromHour, String toHour) throws Exception {
        String url = "https://kyfw.12306.cn/otn/leftTicket/queryZ?leftTicketDTO.train_date=2023-09-30&leftTicketDTO.from_station=HGH&leftTicketDTO.to_station=NCG&purpose_codes=ADULT"
                .replaceAll("2023-09-30", day)
                .replaceAll("HGH", stationNameMap.get(from).getCode())
                .replaceAll("NCG", stationNameMap.get(to).getCode());
        ThreadUtil.sleep(WAIT_D + (long) (Math.random() * WAIT));
        if (logDt) {
            Tools.out(url);
        }
        String resp = new HttpBuilder(url, HttpBuilder.Type.GET)
                .setWho(who)
                .setHeaders(new MapBuilder<String, String>()
                        .put("Cookie", cookie)
                        .build()
                )
                .buildString();
        Response<TrainListData> response = JSON.parseObject(resp, new TypeReference<Response<TrainListData>>() {
        });
        List<Train> trainList = new ArrayList<>();

        if (200 == response.getHttpstatus()) {
            TrainListData trainListData = response.getData();
            for (String s : trainListData.getResult()) {
                Train train = new Train().build(s, stationCodeMap);
                trainList.add(train);
            }

        } else {
            Tools.out(resp);
        }


        List<Train> trainListFilter = new ArrayList<>(trainList);

        if (filterNo != null && filterNo.size() > 0) {
            trainListFilter = trainListFilter.stream().filter(item -> filterNo.contains(item.stationTrainCode)).collect(Collectors.toList());
        }
        if (fromHour != null) {
            trainListFilter = trainListFilter.stream().filter(item -> item.from_time.compareTo(fromHour) >= 0).collect(Collectors.toList());
        }
        if (toHour != null) {
            trainListFilter = trainListFilter.stream().filter(item -> item.to_time.compareTo(toHour) <= 0).collect(Collectors.toList());
        }

        return trainListFilter;
    }

    private List<Train.StationNode> getTrainTime(Train train, String day) throws Exception {

        // 每个车次需要获取时刻表
        String url1 = "https://kyfw.12306.cn/otn/czxx/queryByTrainNo?train_no=5l000G2335C0&from_station_telecode=HGH&to_station_telecode=NXG&depart_date=2023-09-30"
                .replaceAll("5l000G2335C0", train.train_no)
                .replaceAll("2023-09-30", day)
                .replaceAll("HGH", stationNameMap.get(train.from_station).getCode())
                .replaceAll("NXG", stationNameMap.get(train.to_station).getCode());

        ThreadUtil.sleep(WAIT_D + (long) (Math.random() * WAIT));
        //Tools.out(url1);
        String resp1 = new HttpBuilder(url1, HttpBuilder.Type.GET)
                .setWho(who)
                .setHeaders(new MapBuilder<String, String>()
                        .put("Cookie", "_uab_collina=169595874969722123676918; JSESSIONID=8C0CC87B951FDB7686C2DC403FA740D6; guidesStatus=off; highContrastMode=defaltMode; cursorStatus=off; BIGipServerpassport=770179338.50215.0000; route=9036359bb8a8a461c164a04f8f50b252; _jc_save_fromStation=%u676D%u5DDE%u4E1C%2CHGH; _jc_save_toStation=%u5357%u660C%2CNCG; _jc_save_toDate=2023-09-29; _jc_save_wfdc_flag=dc; BIGipServerportal=3134456074.16671.0000; BIGipServerpool_passport=266600970.50215.0000; fo=93p4u9hi3a0dts39SuCtqaBEdmo8K88tG8uwauNCNuY32AE2Fwl-Z01hxfjkd7nY4ap7pPiIuT2d8LvIEYp47zQhrtft8JZN_syQtKVqSjynhirTs4UYEzDVa_JJJ_17pkvyJX5gJC0ekOphQxnQ4CRxcCkh-yYqwBEX9azKrDe0vZKpSPwd4M8uswE; _jc_save_fromDate=2023-09-29; BIGipServerotn=2715222282.64545.0000")
                        .build()
                )
                .buildString();
        Response<StationTrainData> response1 = JSON.parseObject(resp1, new TypeReference<Response<StationTrainData>>() {
        });
        if (200 == response1.getHttpstatus()) {
            return response1.getData().getData();
        }
        return new ArrayList<>();
    }

    @Data
    @Accessors(chain = true)
    public static class Response<T> {
        int httpstatus; // 200
        T data;
        String messages;
        boolean status;
    }

    @Data
    @Accessors(chain = true)
    public static class TrainListData {
        List<String> result;
        String flag;
        Map<String, String> map;
    }

    @Data
    @Accessors(chain = true)
    public static class StationTrainData {
        List<Train.StationNode> data;
    }
}
