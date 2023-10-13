package com.walker.controller;

import com.alibaba.fastjson.JSON;
import com.walker.core.mode.Page;
import com.walker.core.mode.Response;
import com.walker.core.mode.sys.FileIndex;
import com.walker.core.util.*;
import com.walker.dao.JdbcDao;
import com.walker.service.Config;
import com.walker.service.FileIndexService;
import com.walker.spring.config.Context;
import com.walker.spring.util.SpringUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/file")
public class FileController {
    private final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

    static int cacheSize = 4096;

    @Autowired
    @Qualifier("fileIndexService")
    private FileIndexService fileIndexService;

    @Autowired
    JdbcDao jdbcDao;


    @ApiOperation(value = "post 保存 更新/添加 ", notes = "")
    @ResponseBody
    @RequestMapping(value = "/save.do", method = RequestMethod.POST)
    public Response save(
            @RequestParam(value = "ID", required = false, defaultValue = "") String id,
            @RequestParam(value = "S_MTIME", required = false, defaultValue = "") String sMtime,
            @RequestParam(value = "S_ATIME", required = false, defaultValue = "") String sAtime,
            @RequestParam(value = "S_FLAG", required = false, defaultValue = "0") String sFlag,
            @RequestParam(value = "NAME", required = false, defaultValue = "") String name,
            @RequestParam(value = "INFO", required = false, defaultValue = "") String info,
            @RequestParam(value = "LENGTH", required = false, defaultValue = "") String length,
            @RequestParam(value = "PATH", required = false, defaultValue = "") String path,
            @RequestParam(value = "EXT", required = false, defaultValue = "") String ext,
            @RequestParam(value = "OWNER", required = false, defaultValue = "") String owner
    ) {
        FileIndex file = new FileIndex();
        file.setID(id);
        file.setS_MTIME(TimeUtil.getTimeYmdHms());
        file.setS_ATIME(sAtime.length() > 0 ? sAtime : TimeUtil.getTimeYmdHmss());
        file.setS_FLAG(sFlag.equalsIgnoreCase("1") ? "1" : "0");
        file.setNAME(name);
        file.setINFO(info);
        file.setLENGTH(length);
        file.setPATH(path);
        file.setEXT(ext);
        file.setOWNER(owner);

        List<FileIndex> res = fileIndexService.saveAll(Arrays.asList(file));
        return new Response().setTip("").setRes(res);    }

    @ApiOperation(value = "根据crc id删除文件", notes = "只能删除文件")
    @ResponseBody
    @RequestMapping(value = "/deleteByIds.do", method = RequestMethod.GET)
    public Response deleteByIds(
            @RequestParam(value = "ids", required = false, defaultValue = "") String ids
    ) {
        String info = "delete ids:" + ids;
        List<FileIndex> fileIndexList = fileIndexService.findsAllById(Arrays.asList(ids.split(",")));
        Object res = fileIndexService.deleteAll(Arrays.asList(ids.split(",")));
        for (FileIndex fileIndex : fileIndexList) {
            FileUtil.delete(fileIndex.getPATH());
            FileUtil.delete(getUploadPathTempDir(fileIndex.getEXT(), fileIndex.getCHECKSUM()));   //删除该文件的缓存目录
        }
        return new Response().setTip(info).setRes(res);
    }

    @ApiOperation(value = "get 获取", notes = "")
    @ResponseBody
    @RequestMapping(value = "/action.do", method = RequestMethod.GET)
    public Response get(
            @RequestParam(value = "id", required = true) String id
    ) {
        String info = "get id:" + id;
        FileIndex model = fileIndexService.get(new FileIndex().setID(id));
        return new Response().setTip(info).setRes(model).setSuccess(model != null);
    }

    @ApiOperation(value = "get findPage 分页查询", notes = "")
    @ResponseBody
    @RequestMapping(value = "/findPage.do", method = RequestMethod.GET)
    public Response findPage(
            @RequestParam(value = "ID", required = false, defaultValue = "") String id,
            @RequestParam(value = "S_MTIME", required = false, defaultValue = "") String sMtime,
            @RequestParam(value = "S_ATIME", required = false, defaultValue = "") String sAtime,
            @RequestParam(value = "S_FLAG", required = false, defaultValue = "") String sFlag,
            @RequestParam(value = "NAME", required = false, defaultValue = "") String name,
            @RequestParam(value = "INFO", required = false, defaultValue = "") String info,
            @RequestParam(value = "LENGTH", required = false, defaultValue = "") String length,
            @RequestParam(value = "PATH", required = false, defaultValue = "") String path,
            @RequestParam(value = "EXT", required = false, defaultValue = "") String ext,
            @RequestParam(value = "OWNER", required = false, defaultValue = "") String owner,

            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "20") Integer showNum,
            @RequestParam(value = "order", required = false, defaultValue = "") String order
    ) {
        FileIndex file = new FileIndex();
        file.setID(id);
        file.setS_MTIME(sMtime);
        file.setS_ATIME(sAtime);
        file.setS_FLAG(sFlag);
        file.setNAME(name);
        file.setINFO(info);
        file.setLENGTH(length);
        file.setPATH(path);
        file.setEXT(ext);
        file.setOWNER(owner);
        Page page = new Page().setNowpage(nowPage).setShownum(showNum).setOrder(order);

        List<FileIndex> list = fileIndexService.finds(file, page);
        page.setTotal(fileIndexService.count(file));
        return new Response().setTotal(page.getTotal()).setRes(list);
    }


    @ApiOperation(value = "统计socket", notes = "")
    @ResponseBody
    @RequestMapping(value = "/getColsMap.do", method = RequestMethod.GET)
    public Response getColsMap() {
        Map<String, String> colMap = FileUtil.getFileMap();
        Map<String, Object> res = new HashMap<>();
        res.put("colMap", colMap);
        res.put("colKey", colMap.keySet());
        return new Response().setTip("file").setRes(res);
    }

    @ApiOperation(value = "文件上传下载分页查询", notes = "")
    @ResponseBody
    @RequestMapping(value = "/list.do", method = RequestMethod.GET, produces = "application/json")
    public Response add(
            @RequestParam(value = "id", required = false, defaultValue = "") String id,
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "timefrom", required = false, defaultValue = "") String timefrom,
            @RequestParam(value = "timeto", required = false, defaultValue = "") String timeto,
            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "20") Integer showNum,
            @RequestParam(value = "order", required = false, defaultValue = "") String order
    ) {
        Page page = new Page().setNowpage(nowPage).setShownum(showNum).setOrder(order);
        FileIndex obj = new FileIndex().setID(id).setNAME(name);
        List<FileIndex> res = fileIndexService.finds(obj, page);
        return new Response().setTotal(page.getTotal()).setRes(res);
    }

    @ApiOperation(value = "删除文件或文件夹 同步更新索引", notes = "")
    @ResponseBody
    @RequestMapping(value = "/deleteByPath.do", method = RequestMethod.GET)
    public Response deleteByPath(
            @RequestParam(value = "paths", required = true, defaultValue = "") String paths
    ) {
        int count = 0;
        String info = "";
        String[] pathList = paths.split(",");
        for (String path : pathList) {
            if (path.length() > 0) {
                if (path.startsWith(Config.getUploadDir())) {
                    count += FileUtil.delete(path) ? 1 : 0;
//                    count += FileUtil.delete(getUploadPathTempDir());
                    //更新该文件及其子文件索引
                    fileIndexService.deleteAllByStartPath(path);
                } else {
                    info = "无修改权限" + path + ",";
                }
            } else {
                info = "路径为null,";
            }
        }
        return new Response().setSuccess(info.length() == 0).setRes(count).setTip(info);
    }

    @ApiOperation(value = "修改文件", notes = "")
    @ResponseBody
    @RequestMapping(value = "/update.do", method = RequestMethod.GET)
    public Response update(
            @RequestParam(value = "newPath", required = true, defaultValue = "") String newPath,
            @RequestParam(value = "oldPath", required = false, defaultValue = "") String oldPath
    ) {
        int count = 0;
        String info = "";
        if (oldPath.length() > 0 && newPath.length() > 0) {
            if (newPath.startsWith(Config.getUploadDir()) && oldPath.startsWith(Config.getUploadDir())) {
                FileUtil.mv(oldPath, newPath);
                //更新该文件及其子文件索引
                List<FileIndex> fileIndexList = fileIndexService.findsAllByStartPath(oldPath);
                for (FileIndex fileIndex : fileIndexList) {
                    fileIndex.setPATH(fileIndex.getPATH().replace(oldPath, newPath));
                    fileIndex.setNAME(FileUtil.getFileName(fileIndex.getPATH()));
                    fileIndex.setEXT(FileUtil.getFileType(fileIndex.getPATH()));
                }
                fileIndexService.saveAll(fileIndexList);
            } else {
                info = "无修改权限" + newPath + " <- " + oldPath;
            }
        } else {
            info = "路径为null";
        }

        return new Response().setSuccess(info.length() == 0).setRes(count).setTip(info);
    }

    /**
     * 支撑请求头 特定 图片预览问题 img src
     * <p>
     * id是否存在
     * 若是图片 且有尺寸控制
     * 缩略图是否存在
     * 变换路径为缩略图路径
     * 缩略图 并缓存redis
     */
    @ApiOperation(value = "下载文件 ", notes = "")
    @ResponseBody
    @RequestMapping(value = "/download.do")
    public void downloadFile(HttpServletRequest request,
                             HttpServletResponse response,
                             @RequestParam(value = "ID", required = false, defaultValue = "") String id,
                             @RequestParam(value = "PATH", required = false, defaultValue = "") String path,
//                                 图片文件即可 200x200放大缩小
                             @RequestParam(value = "SIZE", required = false, defaultValue = "") String size

    ) throws IOException {
        Watch w = new Watch("download");
        w.put(id);
        w.put(path);

        Boolean res = false;

//        获取文件索引
        FileIndex fileIndex = null;
        if (id.length() > 0) {
            fileIndex = fileIndexService.get(new FileIndex().setID(id));
            path = fileIndex == null ? path : fileIndex.getPATH();
        } else if (path.length() > 0) {
            path = path.startsWith(Config.getDownloadDir()) ? path : Config.getUploadDir() + File.separator + path;
            path = new String(path.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            List<FileIndex> list = fileIndexService.findsAllByPath(Arrays.asList(path));
            if (list.size() > 0) {
                fileIndex = list.get(0);
            }
        }
        w.cost("getIndex");
        //若是图片 且需要放缩
        if (ImageUtil.isImage(fileIndex.getEXT()) && size != null && size.length() > 0) {
            size = size.toLowerCase().replace('*', 'x');

            String toPath = getUploadPathTempFile(fileIndex.getEXT(), fileIndex.getCHECKSUM(), size);//如果是图片则 转换为临时文件放置位置 转换缓存文件
            if (new File(toPath).isFile()) {  //已存在
                w.put("cache file");
            } else {
                size = ImageUtil.makeIfImageThenSize(fileIndex.getPATH(), fileIndex.getEXT(), size, toPath);
                w.cost("turn file");
            }
            path = toPath;
        }

        String info = "";
        if (path.length() > 0) {
            int type = FileUtil.check(path);
            if (type == 1) {
                info = id + " " + path + " is dir";
            } else if (type == 0) {
                info = id + " " + path + " exists";
                res = true;
            } else {
                info = id + " " + path + " not exists";
            }
        } else {
            info = "path is null ?";
        }
        w.cost("check");

        w.put("path", path);
        w.put("info", info);
        if (!res) {
            w.res();
            String s = JSON.toJSONString(new Response().setSuccess(false).setTip(w.toPrettyString()).toString());
            response.getWriter().println(s);
            log.error(w.toString());
//            response.flushBuffer();
        } else {
            String name = fileIndex == null ? FileUtil.getFileName(path) : fileIndex.getNAME();

            File file = new File(path);
            SpringUtil.setHeaderDownFile(request, response, name);
            InputStream inputStream = new FileInputStream(file);
            OutputStream outputStream = response.getOutputStream();
            try {
                FileUtil.copyStream(inputStream, outputStream);
            } finally {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            }
            w.cost("copyStream");
            log.info(w.toPrettyString());
//            return new Response().setTip(w.toPrettyString());
        }
    }


    /**
     * 获取文件缓存的具体路径
     *
     * @param ext      png
     * @param checksum asdflaksdfj
     * @return /home/walker/files/png/2020-20-10/alsjdfadf_temp/100x100
     */
    public String getUploadPathTempFile(String ext, String checksum, String size) {
        return getUploadPathTempDir(ext, checksum) + File.separator + size;
    }

    /**
     * 获取文件的临时缓存目录
     *
     * @param ext      png
     * @param checksum asdflaksdfj
     * @return /home/walker/files/png/2020-20-10/alsjdfadf_temp
     */
    public String getUploadPathTempDir(String ext, String checksum) {
        String res = getTemp(getUploadPathByExtAndChecksum("", ext, checksum));
        FileUtil.mkdir(res);
        return res;
    }

    public String getTemp(String path) {
        return path + "_temp";
    }

    /**
     * 构造文件上传的命名 路径
     *
     * @param dir      预期路径    ""
     * @param ext      后缀  png
     * @param checksum 校验码 alsjdfadf
     * @return /home/walker/files/png/2020-20-10/alsjdfadf
     */
    public String getUploadPathByExtAndChecksum(String dir, String ext, String checksum) {
        String uploadDir = Config.getUploadDir();
        String res = dir.length() > 0 && dir.startsWith(uploadDir) ?
                dir
                :
                uploadDir + File.separator + (
                        ext.length() > 0 ?
                                ext + File.separator + TimeUtil.getTimeYmd()
                                :
                                "unknow" + File.separator + TimeUtil.getTimeYmd()
                );
        FileUtil.mkdir(res);
        res = res + File.separator + checksum;
        return res;
    }


    @ApiOperation(value = "上传文件", notes = "")
    @ResponseBody
    @RequestMapping(value = "/upload.do", method = RequestMethod.POST)
    public Response upload(
            @RequestParam(value = "file", required = true) MultipartFile file,
            @RequestParam(value = "checksum", required = false, defaultValue = "") String checksum,
            @RequestParam(value = "dir", required = false, defaultValue = "") String dir
    ) {
        Watch w = new Watch("upload");
        String name = file.getOriginalFilename();
        String ext = FileUtil.getFileType(name);


        try {
            File tempFile = new File(Config.getUploadDir() + File.separator + name);
            file.transferTo(tempFile);
            if (checksum.length() == 0) {
                checksum = "" + FileUtil.checksumMd5(tempFile);
            }
            String path = getUploadPathByExtAndChecksum(dir, ext, checksum);
            FileIndex fileIndexOld = fileIndexService.get(checksum);
            FileIndex fileIndexNew = new FileIndex();
            fileIndexNew.setID(checksum)    //头次上传 以checksum为键
                    .setCHECKSUM(checksum)
                    .setPATH(path)
                    .setNAME(name)
                    .setEXT(ext)
                    .setINFO("upload")
                    .setOWNER(Context.getUser().getID())
                    .setS_FLAG("1")
                    .setS_ATIME(TimeUtil.getTimeYmdHms())
                    .setS_MTIME(TimeUtil.getTimeYmdHms())
                    .setLENGTH(tempFile.length() + "")
            ;
            if (fileIndexOld == null) {
                w.put("new file");
                FileUtil.mv(tempFile.getAbsolutePath(), path);
                fileIndexService.saveAll(Arrays.asList(fileIndexNew));  //保存记录 然后移动文件
            } else {
                w.put("reupload file");
                FileUtil.mv(fileIndexOld.getPATH(), path);
                fileIndexOld.setPATH(path);
                fileIndexNew.setS_ATIME(fileIndexOld.getS_ATIME()); //集成创建时间
                fileIndexNew.setID(LangUtil.getTimeSeqId());    //重复上传以时序id为键
                fileIndexService.saveAll(Arrays.asList(fileIndexNew, fileIndexOld));  //保存记录 然后移动文件
            }
            tempFile.delete();  //删除临时文件
            return new Response().setRes(fileIndexNew).setTip(w.toPrettyString());
        } catch (Exception e) {
            w.exception(e);
            log.error(w.toPrettyString(), e);
            return new Response().setSuccess(false).setTip(w.toPrettyString());
        }
    }


    @ApiOperation(value = "文件目录", notes = "")
    @ResponseBody
    @RequestMapping(value = "/dir.do", method = RequestMethod.GET)
    public Response dir(
            @RequestParam(value = "dir", required = false, defaultValue = "") String dir
    ) {
        dir = dir.length() > 0 ? dir : Config.getUploadDir();
        if (FileUtil.check(dir) == 1) {
            List<?> list = FileUtil.ls(dir);
            Map<String, Object> res = new HashMap<>();
            res.put("list", list);
            res.put("dir", dir);
            return new Response().setTip("").setRes(res);        } else if (FileUtil.check(dir) == 0) {
            return new Response().setSuccess(false).setTip("这是一个文件" + dir);
        } else {
            if (dir.startsWith(Config.getDownloadDir())) {
                FileUtil.mkdir(dir);
                return new Response().setRes("不存在 创建文件夹" + dir);
            } else {
                return new Response().setSuccess(false).setTip("不存在 无权限创建" + dir);
            }
        }
    }
}
