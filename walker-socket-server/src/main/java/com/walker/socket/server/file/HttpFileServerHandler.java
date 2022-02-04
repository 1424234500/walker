package com.walker.socket.server.file;

import com.walker.mode.Bean;
import com.walker.util.FileUtil;
import com.walker.util.Tools;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author lilinfeng
 * @version 1.0
 * @date 2014年2月14日
 */
public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    public Logger log = LoggerFactory.getLogger(getClass());

    private final String url;

    public HttpFileServerHandler(String url) {
        this.url = url;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        /*如果无法解码400*/
        if (!request.decoderResult().isSuccess()) {
            sendError(ctx, BAD_REQUEST);
            return;
        }

        /*只支持GET方法*/
        if (request.method() != GET) {
            sendError(ctx, METHOD_NOT_ALLOWED);
            return;
        }

        String path = request.uri();
        path = path == null || path.length() == 0 ? this.url : path;
        if(path.endsWith(".html")){
            sendHtml(ctx, path);
            return;
        }
        // 权限控制 只许下载默认目录下面的
        if (! path.startsWith(this.url)) {
            sendError(ctx, FORBIDDEN);
            return;
        }
        File file = new File(path);
        /*如果文件不可访问或者文件不存在*/
        if (file.isHidden() || !file.exists()) {
            sendError(ctx, NOT_FOUND);
            return;
        }
        /*如果是目录*/
        if (file.isDirectory()) {
            //1. 以/结尾就列出所有文件
            if (path.endsWith("/")) {
                sendListing(ctx, file);
            } else {
                //2. 否则自动+/ 302重定向
                sendRedirect(ctx, path + '/');
            }
            return;
        }
        if (!file.isFile()) {
            sendError(ctx, FORBIDDEN);
            return;
        }
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");// 以只读的方式打开文件
        } catch (FileNotFoundException fnfe) {
            sendError(ctx, NOT_FOUND);
            return;
        }
        long fileLength = randomAccessFile.length();
        //创建一个默认的HTTP响应
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        //设置Content Length
        HttpUtil.setContentLength(response, fileLength);
        //设置Content Type
        setContentTypeHeader(response, file);
        //如果request中有KEEP ALIVE信息
        if (HttpUtil.isKeepAlive(request)) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        ctx.write(response);
        ChannelFuture sendFileFuture;
        //通过Netty的ChunkedFile对象直接将文件写入发送到缓冲区中
        sendFileFuture = ctx.write(new ChunkedFile(randomAccessFile, 0,
                fileLength, 8192), ctx.newProgressivePromise());
        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationProgressed(ChannelProgressiveFuture future,
                                            long progress, long total) {
                if (total < 0) { // total unknown
                    log.error("Transfer progress: " + progress);
                } else {
                    log.error("Transfer progress: " + progress + " / " + total);
                }
            }

            @Override
            public void operationComplete(ChannelProgressiveFuture future)throws Exception {
                log.info("Transfer complete.");
            }
        });
        ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        //如果不支持keep-Alive，服务器端主动关闭请求
        if (!HttpUtil.isKeepAlive(request)) {
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            sendError(ctx, INTERNAL_SERVER_ERROR);
        }
    }

    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");


    private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");

    private static void sendHtml(ChannelHandlerContext ctx, String path) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        String buf = "";
        String readPath = "404.html";
        if(!new File(path).isFile()) {
            if(path.startsWith("/")){
                path = path.substring(1);
                if(new File(path).isFile()) {
                    readPath = path;
                }
            }
        }else{
            readPath = path;
        }
        buf += (FileUtil.readByLines(readPath, null, null));
        buf = Tools.replace(buf, "replace", path);
        ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
        response.content().writeBytes(buffer);
        buffer.release();
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

    }
    private static void sendListing(ChannelHandlerContext ctx, File dir) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        StringBuilder buf = new StringBuilder();
        String dirPath = dir.getPath();
        buf.append("<!DOCTYPE html>\r\n");
        buf.append("<html><head><title>");
        buf.append(dirPath);
        buf.append("");
        buf.append("</title></head><body>\r\n");
        buf.append("<h3>");
        buf.append(dirPath).append("");
        buf.append("</h3>\r\n");
        buf.append("<table>");
        Map<String, String> keys = FileUtil.getFileMap();
        List<Bean> files = FileUtil.ls(dirPath);

        buf.append("<tr>");
        keys.keySet().forEach(item -> {
            buf.append("<td>").append(keys.get(item)).append("</td>");
        });
        buf.append("</tr>").append("\r\n");

//        buf.append("<tr><td>").append("<a href=\"../\">..</a>").append("</td></tr>").append("\r\n");
        String name = "../";
        buf.append("<tr>")
                .append("<td>").append("<a href=\"").append(name).append("\">").append(name).append("</a>").append("</td>")
                .append("</tr>").append("\r\n");

        for (Bean f : files) {
            name = f.get("NAME", "");
            String ext = f.get("EXT", "");
//            if (!ALLOWED_FILE_NAME.matcher(name).matches()) {
//                continue;
//            }
            buf.append("<tr>");
            keys.keySet().forEach(item -> {
                buf.append("<td>").append(f.get(item)).append("</td>");
            });
            buf.append("<td>").append("<a href=\"").append(name).append("\">").append(ext.equals("dir") ? "进入" : "下载").append("</a>").append("</td>");

            buf.append("</tr>").append("\r\n");
        }
        buf.append("</table></body></html>\r\n");
        ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
        response.content().writeBytes(buffer);
        buffer.release();
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static void sendRedirect(ChannelHandlerContext ctx, String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
        response.headers().set(HttpHeaderNames.LOCATION, newUri);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static void sendError(ChannelHandlerContext ctx,
                                  HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
                status, Unpooled.copiedBuffer("Failure: " + status.toString()
                + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static void setContentTypeHeader(HttpResponse response, File file) {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,
                mimeTypesMap.getContentType(file.getPath()));
    }
}