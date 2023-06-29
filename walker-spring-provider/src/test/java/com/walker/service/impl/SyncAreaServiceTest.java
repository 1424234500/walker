package com.walker.service.impl;

import com.walker.core.mode.school.Area;
import com.walker.core.util.Tools;
import com.walker.core.util.Watch;
import com.walker.service.Config;
import org.junit.Test;

import java.util.List;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class SyncAreaServiceTest extends SyncAreaServiceImpl {
//    @Autowired
//    AreaService areaService;
    @Test
    public void getTest() {

//        Area root = getCityRoot();
        //此处按照一级省 分别递归获取树
        Area root = getCityRootChina();
        getCity(root, false, null);
        for(int i = 0; i < root.getChilds().size() && i < 2; i++){
            Area item = root.getChilds().get(i);
            getCity(item, true, null);  //html获取构建省 树
            List<Area> list = tree2list(item, root.getID(), root.getPATH(), root.getPATH_NAME());   //递归构建树
            Tools.formatOut(list);
            //   areaService.saveAll(list);
        }

    }
    @Test
    public void testZave(){

//        Area root = getCityRoot();
        //此处按照一级省 分别递归获取树
        Area root = getCityRootChina();
        getCity(root, false, null);

        for(int i = 2; i < root.getChilds().size() && i < 3; i++){
            Area item = root.getChilds().get(i);

            Watch watch = new Watch("area.sync." + item.getNAME());
            getCity(item, true, null);  //html获取构建省 树
            watch.cost("http");

            List<Area> list = tree2list(item, root.getID(), root.getPATH(), root.getPATH_NAME());   //递归构建树
            watch.put(list.size());
            watch.cost("tree");
            Tools.formatOut(list);
            watch.put("batch", list.size() / Config.getDbsize());
            watch.cost("db");
            Tools.out(watch.toPrettyString());
        }

        Tools.out("sync area end");
        
    }


}
