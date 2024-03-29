package com.walker.spring.controller;


import com.walker.core.mode.Page;
import com.walker.core.mode.Response;
import com.walker.core.mode.school.Teacher;
import com.walker.service.TeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/*
测试 jap teacherService

 */
@Api(value = "测试jpa操作 service层 ")
@Controller
@RequestMapping("/teacher")
public class TeacherController {
    private final Logger log = LoggerFactory.getLogger(getClass());


    @Autowired
    @Qualifier("teacherJpaService")
    private TeacherService teacherService;

    //    public Teacher add( Teacher test);
    @ApiOperation(value = "post 添加", notes = "post参数 RequestParam ")
    @ResponseBody
    @RequestMapping(value = "/action.do", method = RequestMethod.POST, produces = "application/json")
    public Response add(
            @RequestParam(value = "id", required = true) String id,
            @RequestParam(value = "name", required = true, defaultValue = "default") String name,
            @RequestParam(value = "time", required = false, defaultValue = "default") String time
    ) {
        String info = "post id:" + id + " name:" + name + " time:" + time;
        List<Teacher> res = teacherService.saveAll(Arrays.asList(new Teacher().setID(id).setNAME(name)));
        return new Response().setRes(res).setTip(info);
    }

    //    public Integer update(Teacher test);
    @ApiOperation(value = "set 更新", notes = "put参数 RequestParam ")
    @ResponseBody
    @RequestMapping(value = "/action.do", method = RequestMethod.PUT, produces = "application/json")
    public Response update(
            @RequestParam(value = "id", required = true) String id,
            @RequestParam(value = "name", required = false, defaultValue = "default") String name,
            @RequestParam(value = "time", required = false, defaultValue = "default") String time,
            @RequestParam(value = "pwd", required = false, defaultValue = "default") String pwd
    ) {
        String info = "update id:" + id + " name:" + name + " time:" + time + " pwd:" + pwd;
        List<Teacher> res = teacherService.saveAll(Arrays.asList(new Teacher().setID(id).setNAME(name)));
        return new Response().setRes(res).setTip(info);
    }

    //    public void delete(Teacher test);
    @ApiOperation(value = "delete 删除", notes = "delete参数 restful 路径 PathVariable ")
    @ResponseBody
    @RequestMapping(value = "/{id}/action.do", method = RequestMethod.DELETE)
    public Response delete(
            @PathVariable(value = "id", required = true) String id
    ) {
        String res = "delete id:" + id;
        return new Response().setRes(teacherService.delete(new Teacher().setID(id))).setTip(res);
    }

    //    public Teacher get(Teacher test);
    @ResponseBody
    @RequestMapping(value = "/action.do", method = RequestMethod.GET, produces = "application/json")
    public Response get(
            @RequestParam(value = "id", required = true) String id
    ) {
        String res = "get id:" + id;
        Teacher model = teacherService.get(new Teacher().setID(id));
        return new Response().setRes(teacherService.delete(model)).setTip(res);
    }
//
//    public Page<Teacher> finds(Teacher test, Pageable page) ;


    @ApiOperation(value = "get 分页查询", notes = "url restful参数 PathVariable")
    @ResponseBody
    @RequestMapping(value = "/findPage.do", method = RequestMethod.GET)
    public Response findPage(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "3") Integer showNum
    ) {
        String res = "get   name:" + name;
        Page page1 = new Page().setNowpage(nowPage).setShownum(showNum);

        Teacher tea = new Teacher().setNAME(name);
        List<Teacher> list = teacherService.finds(tea, page1);
        long total = teacherService.count(tea);
        return new Response().setRes(list).setTip(res).setTotal(total);
    }


}
