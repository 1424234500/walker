//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.walker.controller;

import com.walker.Response;
import com.walker.core.mode.Page;
import com.walker.core.util.ClassUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping({"/class"})
public class ClassController {
    private Logger log = LoggerFactory.getLogger(getClass());

    @ApiOperation(value="缓存列表分页查询list")
    @ResponseBody
    @RequestMapping(value="/list.do",method= RequestMethod.GET)
    public Response list(
            @RequestParam(value = "package", required = true, defaultValue = "") String packageName,
            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "20") Integer showNum,
            @RequestParam(value = "order", required = false, defaultValue = "") String order
    ) {
        Page page = new Page().setNowpage(nowPage).setShownum(showNum).setOrder(order);
        List<?> list = ClassUtil.getPackageClassBean(packageName, true);
        page.setTotal(list == null ? -1L : (long)list.size());
        return Response.makePage("ok", page, list);
    }
    @ApiOperation(value="查询类详情函数列表")
    @ResponseBody
    @RequestMapping(value="/detail.do",method= RequestMethod.GET)
    public Response detail(
            @RequestParam(value = "package", required = true, defaultValue = "") String className,
            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "20") Integer showNum,
            @RequestParam(value = "order", required = false, defaultValue = "") String order
    ) {
        Page page = new Page().setNowpage(nowPage).setShownum(showNum).setOrder(order);
        List<?> list = ClassUtil.getMethod(className, true, true);
        page.setTotal(list == null ? -1L : (long)list.size());
        return Response.makePage("ok", page, list);
    }
    @ApiOperation(value="查询类详情函数列表")
    @ResponseBody
    @RequestMapping(value="/do.do",method= RequestMethod.GET)
    public Response doMethod(
            @RequestParam(value = "packageName", required = true, defaultValue = "") String packageName,
            @RequestParam(value = "do_args", required = false, defaultValue = "") String doArgs,
            @RequestParam(value = "do_split_arr", required = false, defaultValue = "@") String splitArr,
            @RequestParam(value = "do_split_arg", required = false, defaultValue = "-") String splitArg
    ) {
        Object res = null;

        try {
            if (doArgs.length() > 0) {
                res = ClassUtil.doPackage(packageName, ClassUtil.parseObjectArray(doArgs, splitArr, splitArg));
            } else {
                res = ClassUtil.doPackage(packageName, new ArrayList<>());
            }
            return Response.makeTrue(String.valueOf(res), res);

        } catch (Exception var10) {
            return Response.makeFalse(var10.toString());
        }
    }
    @ApiOperation(value="执行一段代码")
    @ResponseBody
    @RequestMapping(value="/docode.do",method= RequestMethod.GET)
    public Response doMethod(
            @RequestParam(value = "do_args", required = false, defaultValue = "") String args,
            @RequestParam(value = "do_split_arr", required = false, defaultValue = "@") String splitArr
    ) {
        splitArr = splitArr.length() == 0 ? ";" : splitArr;
        args = args.replace("\n", splitArr);
        args = args.replace("\r\n", splitArr);
        args = args.replace(splitArr + " ", splitArr);
        args = args.replace(splitArr + splitArr, splitArr);
        args = args.replace(splitArr + splitArr, splitArr);
        args = args.replace("  ", " ");
        args = args.replace("  ", " ");
        List<String> list = Arrays.asList(args.split(splitArr));
        if (list.size() > 0) {
            return Response.makeTrue("", ClassUtil.doCode(list));
        } else {
            return Response.makeFalse("do_args: eg.Integer in = 0; Bean bean = new Bean();bean.set(\"int\", in)");
        }

    }
}
