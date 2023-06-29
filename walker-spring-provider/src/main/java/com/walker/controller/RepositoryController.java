package com.walker.controller;


import com.walker.Response;
import com.walker.core.mode.school.Teacher;
import com.walker.dao.TeacherRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 测试仓库repository
 */
@Api(value = "测试jpa操作 dao层 自定义sql ")
@Controller
@RequestMapping("/repository")
public class RepositoryController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private TeacherRepository teacherRepository;


    @ApiOperation(value = "保存 save cache-teacher-sk  ")
    @ResponseBody
    @RequestMapping(value = "/save.do", method = RequestMethod.POST, produces = "application/json")
    public Response save(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "name", required = true, defaultValue = "default") String name,
            @RequestParam(value = "time", required = false, defaultValue = "default") String time
    ) {
        Teacher model = teacherRepository.save(new Teacher().setID(id).setNAME(name));
        return Response.makeTrue("", model);
    }

    @ApiOperation(value = "删除 deleteJPQLWithCacheSimpleKey cache-teacher-sk")
    @ResponseBody
    @RequestMapping(value = "/deleteJPQLWithCacheSimpleKey.do", method = RequestMethod.POST, produces = "application/json")
    public Response deleteJPQLWithCacheSimpleKey(
            @RequestParam(value = "id", required = true) String id
    ) {
        return Response.makeTrue("", teacherRepository.deleteJPQLWithCacheSimpleKey(id));
    }

    @ApiOperation(value = "查找一个 findOneJPQLWithCacheSimpleKey cache-teacher-sk")
    @ResponseBody
    @RequestMapping(value = "/findOneJPQLWithCacheSimpleKey.do", method = RequestMethod.POST, produces = "application/json")
    public Response findOneJPQLWithCacheSimpleKey(
            @RequestParam(value = "id", required = true) String id
    ) {
        return Response.makeTrue("", teacherRepository.findOneJPQLWithCacheSimpleKey(id));
    }


    @ApiOperation(value = "保存 saveWithCache cache-teacher")
    @ResponseBody
    @RequestMapping(value = "/saveWithCache.do", method = RequestMethod.POST, produces = "application/json")
    public Response selfUpdateJPQL(
            @RequestParam(value = "id", required = true) String id,
            @RequestParam(value = "name", required = false, defaultValue = "default") String name
    ) {
        return Response.makeTrue("", teacherRepository.saveWithCache(id, name));
    }

    @ApiOperation(value = "删除 deleteJPQLWithCache cache-teacher")
    @ResponseBody
    @RequestMapping(value = "/deleteJPQLWithCache.do", method = RequestMethod.POST, produces = "application/json")
    public Response deleteJPQLWithCache(
            @RequestParam(value = "id", required = true) String id
    ) {
        return Response.makeTrue("", teacherRepository.deleteJPQLWithCache(id));
    }

    @ApiOperation(value = "查找一个 findOneJPQLWithCache cache-teacher")
    @ResponseBody
    @RequestMapping(value = "/findOneJPQLWithCache.do", method = RequestMethod.POST, produces = "application/json")
    public Response findOneJPQLWithCache(
            @RequestParam(value = "id", required = true) String id
    ) {
        return Response.makeTrue("", teacherRepository.findOneJPQLWithCache(id));
    }


    @ApiOperation(value = "自定义更新 selfUpdateSql")
    @ResponseBody
    @RequestMapping(value = "/selfUpdateSql.do", method = RequestMethod.POST, produces = "application/json")
    public Response selfUpdateSql(
            @RequestParam(value = "id", required = true) String id,
            @RequestParam(value = "name", required = false, defaultValue = "default") String name
    ) {
        return Response.makeTrue("", teacherRepository.selfUpdateSql(name, id));
    }


    @ApiOperation(value = "自定义查询 selfFindByName")
    @ResponseBody
    @RequestMapping(value = "/selfFindByName.do", method = RequestMethod.POST, produces = "application/json")
    public Response selfFindByName(
            @RequestParam(value = "name", required = false) String name
    ) {
        return Response.makeTrue("", teacherRepository.selfFindByName(name));
    }

    @ApiOperation(value = "分页查询", notes = "url restful参数 PathVariable")
    @ResponseBody
    @RequestMapping(value = "/selfFindPage.do", method = RequestMethod.GET)
    public Response selfFindPage(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "3") Integer showNum
    ) {
        Sort sort = new Sort(Sort.Direction.ASC, "name");
        Pageable pageable = PageRequest.of(nowPage - 1, showNum, sort);
        log.info(pageable.toString());
        com.walker.core.mode.Page page1 = new com.walker.core.mode.Page().setNowpage(nowPage).setShownum(showNum);
        List<Teacher> list = teacherRepository.selfFindPage(name, pageable);
        int count = teacherRepository.selfCount(name);
        page1.setTotal(count);
        log.info(page1.toString());
        return Response.makePage("", page1, list);
    }

    @ApiOperation(value = "分页查询2 repository分页查询jpql", notes = "url restful参数 PathVariable")
    @ResponseBody
    @RequestMapping(value = "/selfFindPage2.do", method = RequestMethod.GET)
    public Response selfFindPage2(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "3") Integer showNum
    ) {
        Sort sort = new Sort(Sort.Direction.ASC, "name");
        Pageable pageable = PageRequest.of(nowPage - 1, showNum, sort);
        log.info(pageable.toString());
        com.walker.core.mode.Page page1 = new com.walker.core.mode.Page().setNowpage(nowPage).setShownum(showNum);
        Page<Teacher> pageOnceJpql = teacherRepository.selfFindPageOnceJpql(name, pageable);
        List<Teacher> list = pageOnceJpql.getContent();

        long count = pageOnceJpql.getTotalElements();//teacherRepository.selfCount(name);
        page1.setTotal(count);
        log.info(page1.toString());
        return Response.makePage("", page1, list);
    }

    @ApiOperation(value = "分页查询2 repository分页查询 sql", notes = "url restful参数 PathVariable")
    @ResponseBody
    @RequestMapping(value = "/selfFindPage3.do", method = RequestMethod.GET)
    public Response selfFindPage3(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "3") Integer showNum
    ) {
        Sort sort = new Sort(Sort.Direction.ASC, "name");
        Pageable pageable = PageRequest.of(nowPage - 1, showNum, sort);
        log.info(pageable.toString());
        com.walker.core.mode.Page page1 = new com.walker.core.mode.Page().setNowpage(nowPage).setShownum(showNum);
        Page<Teacher> pageOnceJpql = teacherRepository.selfFindPageOnceSql(name, pageable);
        List<Teacher> list = pageOnceJpql.getContent();

        long count = pageOnceJpql.getTotalElements();//teacherRepository.selfCount(name);
        page1.setTotal(count);
        log.info(page1.toString());
        return Response.makePage("", page1, list);
    }

    @ApiOperation(value = "自定义查询 selfCount")
    @ResponseBody
    @RequestMapping(value = "/selfCount.do", method = RequestMethod.GET, produces = "application/json")
    public Response selfCount(
            @RequestParam(value = "name", required = false, defaultValue = "") String name
    ) {
        return Response.makeTrue("", teacherRepository.selfCount(name));
    }

    @ApiOperation(value = "getOne 查询")
    @ResponseBody
    @RequestMapping(value = "/findById.do", method = RequestMethod.GET, produces = "application/json")
    public Response getOne(
            @RequestParam(value = "id", required = true, defaultValue = "1") String id
    ) {
        Teacher res = teacherRepository.findById(id).get();
        if (res == null) {
            return Response.makeFalse("not exists");
        } else {
            return Response.makeTrue("", res);
        }
    }

    @ApiOperation(value = "existsById 查询")
    @ResponseBody
    @RequestMapping(value = "/existsById.do", method = RequestMethod.GET, produces = "application/json")
    public Response existsById(
            @RequestParam(value = "id", required = true) String id
    ) {
        return Response.makeTrue("", teacherRepository.existsById(id));
    }

    @ApiOperation(value = "count 计数")
    @ResponseBody
    @RequestMapping(value = "/count.do", method = RequestMethod.GET, produces = "application/json")
    public Response count(
    ) {
        return Response.makeTrue("", teacherRepository.count());
    }

    @ApiOperation(value = "deleteById 删除")
    @ResponseBody
    @RequestMapping(value = "/deleteById.do", method = RequestMethod.PUT, produces = "application/json")
    public Response deleteById(
            @RequestParam(value = "id", required = true) String id
    ) {
        teacherRepository.deleteById(id);
        return Response.makeTrue("", id);
    }

    @ApiOperation(value = "delete 删除")
    @ResponseBody
    @RequestMapping(value = "/delete.do", method = RequestMethod.PUT, produces = "application/json")
    public Response delete(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "time", required = false, defaultValue = "") String time
    ) {
        Teacher model = new Teacher().setID(id).setNAME(name);
        teacherRepository.delete(model);
        return Response.makeTrue("", model);
    }
}
