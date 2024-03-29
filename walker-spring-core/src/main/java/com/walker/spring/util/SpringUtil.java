package com.walker.spring.util;

import com.walker.core.mode.Bean;
import com.walker.core.mode.Page;
import com.walker.core.util.Tools;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.List;

/**
 * servlet request response printwriter 帮助工具
 * 处理request相关设置
 *
 * @author Walker
 */
public class SpringUtil {


    /**
     * 对应解开客户端进行简单加密的字符串，进一步提高系统的安全性
     * 原理：对应客户端加密的字符串进行拆解，转为Unicode对应的数字，对每一个数字进行恢复的反向调整。
     *
     * @param src 原加密字符串
     * @return String 解密后的字符串
     */
    public static String unEscapeStr(String src) {
        String ret = "";

        if (src == null) {
            return ret;
        }

        for (int i = src.length() - 1; i >= 0; i--) {
            int iCh = src.substring(i, i + 1).hashCode();

            if (iCh == 15) {
                iCh = 10;
            } else if (iCh == 16) {
                iCh = 13;
            } else if (iCh == 17) {
                iCh = 32;
            } else if (iCh == 18) {
                iCh = 9;
            } else {
                iCh = iCh - 5;
            }

            ret += (char) iCh;
        }

        // log.debug("unEscape: input=" + src + " output=" + ret);
        return ret;
    }

    /**
     * 加密字符串，进一步提高系统的安全性
     *
     * @param src 未加密字符串
     * @return String 加密后的字符串
     */
    public static String escapeStr(String src) {
        String ret = "";

        if (src == null) {
            return ret;
        }

        for (int i = src.length() - 1; i >= 0; i--) {
            int iCh = src.substring(i, i + 1).hashCode();

            if (iCh == 10) {
                iCh = 15;
            } else if (iCh == 13) {
                iCh = 16;
            } else if (iCh == 32) {
                iCh = 17;
            } else if (iCh == 9) {
                iCh = 18;
            } else {
                iCh = iCh + 5;
            }

            ret += (char) iCh;
        }

        // log.debug("unEscape: input=" + src + " output=" + ret);
        return ret;
    }

    /**
     * 将html标记转化为规定标示符
     *
     * @param s 要转换的HTML
     * @return HTML对应的文本
     */
    public static String escapeHTML(String s) {
        if ((s == null) || (s.length() == 0)) {
            return s;
        }
        int len = s.length();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            switch (c) {
                case 60: // '<'
                    sb.append("&lt;");
                    break;
                case 62: // '>'
                    sb.append("&gt;");
                    break;
                case 10: // '\n'
                    sb.append("<br>");
                    break;
                case 13: // '\r'
                    sb.append("<br>");
                    i++;
                    break;
                case 32: // ' '
                    sb.append("&nbsp;");
                    break;
                case 39: // '\''
                    sb.append("&acute;");
                    break;
                case 34: // '"'
                    sb.append("&quot;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 通过Direct方式向JSP或Servlet跳转.
     *
     * @param response response对象
     * @param url      目标的URL相对路径
     * @throws IOException
     */
    public static void sendRedirect(HttpServletResponse response, String url) throws IOException {
        response.sendRedirect(url);
    }

    /**
     * 通过Dispatcher方式向JSP或Servlet跳转.
     *
     * @param request  reqeust对象
     * @param response response对象
     * @param url      目标的URL相对路径
     */
    public static void sendDispatcher(HttpServletRequest request, HttpServletResponse response, String url) {
        RequestDispatcher rd = null;
        if (url.charAt(0) != '/') {
            url = "/" + url;
        }
        rd = request.getRequestDispatcher(url);
        try {
            rd.forward(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取IP地址
     *
     * @param request request对象
     * @return IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        return ip;
    }

    /**
     * 设置下载 文件 长度
     */
    public static void setDownFileLength(HttpServletResponse response, int length) {
        response.setContentLength(length);
    }

    /**
     * 设置下载 文件 头
     */
    public static void setHeaderDownFile(HttpServletRequest request, HttpServletResponse response, String fileName)
            throws UnsupportedEncodingException {

//		name = URLEncoder.encode(name,"UTF-8");      //转码，免得文件名中文乱码
//        //设置文件下载头
//        response.addHeader("Content-Disposition", "attachment;filename=" + name);
//        //设置文件ContentType类型，这样设置，会自动判断下载文件类型
//        response.setHeader("content-type", "application/octet-stream");
//        response.setContentType("application/octet-stream");

//		text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8
        String accept = request.getHeader("accept");
        if (accept != null && accept.contains("image")) {
            response.setContentType("image/png; charset=UTF-8");
        } else {
            response.setContentType("multipart/form-data");
        }
        String userbrowser = request.getHeader("User-Agent");
        //2.设置文件头：最后一个参数是设置下载文件名(假如我们叫a.pdf)
        response.addHeader("Content-Disposition", SpringUtil.getDispo(userbrowser, fileName));
        if (userbrowser == null) {
            userbrowser = "Chrome";
        }
    }

    public static String getDispo(String userbrowser, String fileName) throws UnsupportedEncodingException {

        if (-1 < userbrowser.indexOf("MSIE 6.0") || -1 < userbrowser.indexOf("MSIE 7.0")) {
            // IE6、7
            return "attachment;filename=" + new String(fileName.getBytes(), "ISO8859-1");
        } else if (-1 < userbrowser.indexOf("MSIE 8.0")) {
            // IE8
            return "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8");
        } else if (-1 < userbrowser.indexOf("MSIE 9.0")) {
            // IE9
            return ("attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        } else if (-1 < userbrowser.indexOf("Chrome")) {
            // chrome
            return ("attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8"));
        } else if (-1 < userbrowser.indexOf("Safari")) {
            // safari
            return ("attachment;filename=" + new String(fileName.getBytes(), "ISO8859-1"));
        } else {
            // other brower
            return ("attachment;filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8"));
        }
    }

    /**
     * 进行跨域模式判断，对跨域进行相应支持。 跨域设置忽略以下三种情况：请求为JSONP方式、系统没有设置跨域支持、请求头不包含Origin
     * 如果系统配置了跨域域名，同时为跨域模式访问，则进行域名匹配判断
     *
     * @param request     request对象
     * @param response    response对象
     * @param callbackStr 回调js名称，如果不为空，说明为jsonp方式请求
     * @return stopFlag true:直接返回，false继续执行
     */
    public static boolean crossDomainRequest(HttpServletRequest request, HttpServletResponse response,
                                             String callbackStr) {
        boolean stopFlag = request.getMethod().equals("OPTIONS");
        String crossList = "walker,localhost,127.0.0.1";// 跨域配置 允许跨域的请求 才做处理 *
        String origin = request.getHeader("Origin");
        if (callbackStr.isEmpty() && (origin != null) && !crossList.isEmpty()) {
            String[] cxrs = crossList.split(",");
            String domain = null;
            if (cxrs[0].equals("*")) {
                domain = "*";
            } else {

                for (int i = 0; i < cxrs.length; i++) {
                    if (origin != null && origin.indexOf(cxrs[i]) >= 0) {
                        domain = origin;
                        break;
                    }
                }
            }
            if (domain != null) {
                response.addHeader("Access-Control-Allow-Origin", domain);
                response.addHeader("Access-Control-Allow-Credentials", "true");
                if (stopFlag) { // 只有option请求才返回下面设定
                    response.addHeader("Access-Control-Allow-Headers", "X-DEVICE-NAME,X-XSRF-TOKEN,Content-Type");
                    response.addHeader("Access-Control-Max-Age", "1728000");
                }
            }
        }
        return stopFlag;
    }

    /**
     * 获取request所有参数Bean
     *
     * @param request
     * @return Bean
     */
    public static Bean getRequestBean(HttpServletRequest request) {
        Bean res = new Bean();
        Enumeration<?> enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String name = (String) enu.nextElement();
            String value = SpringUtil.getKey(request, name);
            res.put(name, value);
        }
        return res;
    }

    /**
     * 获取指定列表的 参数集合bean
     */
    public static Bean getParam(HttpServletRequest request, List<Object> colNames) {
        Bean res = new Bean();
        for (Object key : colNames) {
            res.put(key, SpringUtil.getKey(request, (String) key));
        }
        return res;
    }

    /**
     * 动态参数 集合bean获取
     */
    public static Bean getParam(HttpServletRequest request, Object... colNames) {
        Bean res = new Bean();
        if (colNames.length <= 0) {
            res = getRequestBean(request);
        } else {
            for (Object key : colNames) {
                res.put(key, SpringUtil.getKey(request, String.valueOf(key)));
            }
        }
        return res;
    }

    /**
     * 获取指定 key的参数map
     */
    public static Bean getKeyParam(HttpServletRequest request, String keyName) {
        Bean res = new Bean();
        res.put(keyName, request.getParameter(keyName));
        return res;
    }

    /**
     * 获取某个key的参数值 兼容大小写
     */
    public static String getKey(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (!Tools.notNull(value)) { // 兼容全小写
            value = request.getParameter(name.toLowerCase());
        }
        if (!Tools.notNull(value)) { // 兼容全大写
            value = request.getParameter(name.toUpperCase());
        }
        if (value == null)
            value = "";
        return value;
    }

    public static void echo401(HttpServletResponse response, String json) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, json);
    }

    /**
     * SpringUtil.echoErr(response,  HttpServletResponse.SC_UNAUTHORIZED, token);
     *
     * @param code HttpServletResponse.SC_UNAUTHORIZED
     * @param json
     * @throws IOException
     */
    public static void echoErr(HttpServletResponse response, int code, String json) throws IOException {
        response.sendError(code, json);
    }

    public static void echo(HttpServletResponse response, String json) throws Exception {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(json);
        } catch (IOException e) {
            throw new RuntimeException(json + " " + e.getMessage());
        } finally {
            if (writer != null)
                writer.close();
        }

    }

    public static Pageable turnTo(Page page) {
        String order = page.getOrder();
        String[] orders = order.split(" ");

        /**
         * 如何构造多排序条件问题
         */
        Sort sort = null;
        if (orders[0].length() > 0) {
            String key = "\\Q" + orders[0] + "\\E";
            if (orders.length > 1 && orders[1].equalsIgnoreCase("DESC")) {
//                sort = new Sort(Sort.Direction.DESC, key);
//                sort = new Sort( new Sort.Order(Sort.Direction.DESC, key).ignoreCase() );
            } else {
//                sort = new Sort(Sort.Direction.ASC, key);
//                sort = new Sort( new Sort.Order(Sort.Direction.ASC, key).ignoreCase() );
            }
        }
        //jpa分页从0开始
        Pageable pageable =
                sort == null
                        ? PageRequest.of(page.getNowpage() - 1, page.getShownum())
                        : PageRequest.of(page.getNowpage() - 1, page.getShownum(), sort);
        return pageable;
    }

}