package com.walker.controller;


import com.walker.Response;
import com.walker.mode.sys.PushBindModel;
import com.walker.mode.sys.PushModel;
import com.walker.service.Config;
import com.walker.service.PushAgentService;
import com.walker.util.TimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

/**
 * 推送业务 绑定 解绑 推送
 */
@Api(value = "service层 USER 实体类对象 ")
@Controller
@RequestMapping("/push")
public class PushController {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("pushAgentService")
    private PushAgentService pushAgentService;

    @ApiOperation(value = "push 推送给目标用户", notes = "")
    @ResponseBody
    @RequestMapping(value = "/push.do", method = RequestMethod.POST)
    public Response save(
            @RequestParam(value = "TITLE", required = true, defaultValue = "title") String title,
            @RequestParam(value = "CONTENT", required = true, defaultValue = "content") String content,
            @RequestParam(value = "USER_ID", required = true, defaultValue = "001,002") String userId,
            @RequestParam(value = "EXT", required = false, defaultValue = "{hello:world}") String ext
    ) {
        PushModel pushModel = new PushModel();
        pushModel.setCONTENT(content);
        pushModel.setTITLE(title);
        pushModel.setUSER_ID(userId);
        pushModel.setEXT(ext);
        pushModel.setS_FLAG(Config.TRUE);
        pushModel.setS_MTIME(TimeUtil.getTimeYmdHms());
        pushModel.setTYPE("0");

        Integer res = pushAgentService.push(pushModel);

        return Response.makeTrue(Arrays.asList(userId, title, content).toString(), res);
    }

    @ApiOperation(value = "bind 绑定用户 1 和设备 n , 1 以及推送id 1", notes = "")
    @ResponseBody
    @RequestMapping(value = "/bind.do", method = RequestMethod.GET)
    public Response bind(
            @RequestParam(value = "USER_ID", required = true, defaultValue = "001") String userId,
            @RequestParam(value = "DEVICE_ID", required = true, defaultValue = "deviceId001") String deviceId,
            @RequestParam(value = "PUSH_ID", required = true, defaultValue = "deviceId's 001's pushId") String pushId,
            @RequestParam(value = "TYPE", required = true, defaultValue = "jpush") String type
    ) {
        PushBindModel pushBindModel = new PushBindModel();
        pushBindModel.setDEVICE_ID(deviceId);
        pushBindModel.setPUSH_ID(pushId);
        pushBindModel.setUSER_ID(userId);
        pushBindModel.setTYPE(type);
        pushBindModel.setS_MTIME(TimeUtil.getTimeYmdHms());
        pushBindModel.setS_FLAG(Config.TRUE);

        List<PushBindModel> res = pushAgentService.bind(pushBindModel);

        return Response.makeTrue(Arrays.asList(userId, deviceId, type).toString(), res);
    }


    @ApiOperation(value = "查询用户绑定的设备和pushId列表", notes = "")
    @ResponseBody
    @RequestMapping(value = "/findBind.do", method = RequestMethod.GET)
    public Response findBind(
            @RequestParam(value = "USER_ID", required = true, defaultValue = "001") String userId
    ) {

        List<PushBindModel> res = pushAgentService.findBind(userId);

        return Response.makeTrue(userId, res);
    }
    @ApiOperation(value = "取消绑定用户id和推送id和推送类别", notes = "")
    @ResponseBody
    @RequestMapping(value = "/unbindDevice.do", method = RequestMethod.GET)
    public Response unbind(
            @RequestParam(value = "USER_ID", required = true, defaultValue = "001") String userId,
            @RequestParam(value = "DEVICE_ID", required = true, defaultValue = "deviceId001") String deviceId,
            @RequestParam(value = "PUSH_ID", required = true, defaultValue = "deviceId's 001's pushId") String pushId,
            @RequestParam(value = "TYPE", required = true, defaultValue = "jpush") String type
    ) {
        PushBindModel pushBindModel = new PushBindModel();
        pushBindModel.setDEVICE_ID(deviceId);
        pushBindModel.setPUSH_ID(pushId);
        pushBindModel.setUSER_ID(userId);
        pushBindModel.setTYPE(type);
        pushBindModel.setS_MTIME(TimeUtil.getTimeYmdHms());
        pushBindModel.setS_FLAG(Config.TRUE);

        List<PushBindModel> res = pushAgentService.unbind(Arrays.asList(pushBindModel));

        return Response.makeTrue(Arrays.asList(userId, deviceId, type).toString(), res);
    }
    /**
     * 取消绑定用户id和推送id和推送类别
     *
     * @param userId
     */
    @ApiOperation(value = "取消绑定用户id和推送id和推送类别", notes = "")
    @ResponseBody
    @RequestMapping(value = "/unbind.do", method = RequestMethod.GET)
    public Response unbind(
            @RequestParam(value = "USER_ID", required = true, defaultValue = "001") String userId
    ) {

        List<PushBindModel> res = pushAgentService.unbind(userId);

        return Response.makeTrue(Arrays.asList(userId).toString(), res);
    }

}
