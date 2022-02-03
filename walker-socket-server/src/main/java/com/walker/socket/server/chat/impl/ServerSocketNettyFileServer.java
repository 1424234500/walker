package com.walker.socket.server.chat.impl;

import com.walker.socket.base.Session;
import com.walker.socket.base.encode.NettyDecoder;
import com.walker.socket.base.encode.NettyEncoder;
import com.walker.socket.base.frame.ServerSessionEncode;
import com.walker.socket.base.util.SocketUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
/**
 * 文件 ftp http web 服务器
 */


public class ServerSocketNettyFileServer<DATA> extends ServerSessionEncode<ChannelHandlerContext, DATA> {


    EventLoopGroup bossGroup;
    EventLoopGroup workerGroup;

    public ServerSocketNettyFileServer(int port) throws Exception {
        super(port);
    }


    @Override
    public String makeSocketKey(ChannelHandlerContext channelHandlerContext) {
        return SocketUtil.makeKey(channelHandlerContext);
    }


    @Override
    public void eventLoopKeeper() throws Exception {
        // Configure the server.
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
            serverBootstrap.childHandler(
                    new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            // http请求解码器
                            p.addLast("http-decoder", new HttpRequestDecoder());
                            // 将多个消息转换为单一的  FullHttpRequest FullHttpResponse
                            // http解码器在每个http消息中会生成多个消息对象
                            // FullHttpRequest FullHttpResponse
                            // HttpContent
                            // LastHttpContent
                            p.addLast("http-aggregator", new HttpObjectAggregator(65535));
                            p.addLast("http-encoder", new HttpRequestEncoder());
                            // 支持异步发送大的码流 文件 但不占用过多的内存 防止oom
                            p.addLast("http-chunked", new ChunkedWriteHandler());
                            p.addLast("fileServerHandler", new HttpFileServerHandler("/"));
                        }
                    });
            log.info(this + " bind 文件服务 成功 等待连接 端口监听");

            // Start the server.
            ChannelFuture f = serverBootstrap.bind(port).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads.
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
                bossGroup = null;
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
                workerGroup = null;
            }
        }
    }

    @Override
    public String readString(ChannelHandlerContext socket) throws Exception {
        return null;
    }

    @Override
    public void writeString(ChannelHandlerContext socket, String data) throws Exception {
        ByteBuf resp = Unpooled.copiedBuffer(data.getBytes(StandardCharsets.UTF_8));
        socket.write(resp);
//        socket.writeAndFlush(data);
    }

    /**
     * 停止监听
     */
    @Override
    public void stop(long timemillWait) throws Exception {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
            bossGroup = null;
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
            workerGroup = null;
        }
        super.stop(timemillWait);
    }


    /**
     * @author lilinfeng
     * @version 1.0
     * @date 2014年2月14日
     */
    static class HttpFileServerHandler extends
            SimpleChannelInboundHandler<FullHttpRequest> {
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

            final String uri = request.uri();
            /*格式化URL，并且获取路径*/
            final String path = sanitizeUri(uri);
            if (path == null) {
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
                if (uri.endsWith("/")) {
                    sendListing(ctx, file);
                } else {
                    //2. 否则自动+/
                    sendRedirect(ctx, uri + '/');
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
                        System.err.println("Transfer progress: " + progress);
                    } else {
                        System.err.println("Transfer progress: " + progress + " / "
                                + total);
                    }
                }

                @Override
                public void operationComplete(ChannelProgressiveFuture future)
                        throws Exception {
                    System.out.println("Transfer complete.");
                }
            });
            ChannelFuture lastContentFuture = ctx
                    .writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
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


        private String sanitizeUri(String uri) {
            try {
                uri = URLDecoder.decode(uri, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                try {
                    uri = URLDecoder.decode(uri, "ISO-8859-1");
                } catch (UnsupportedEncodingException e1) {
                    throw new Error();
                }
            }
            if (!uri.startsWith(url)) {
                return null;
            }
            if (!uri.startsWith("/")) {
                return null;
            }
            uri = uri.replace('/', File.separatorChar);
            if (uri.contains(File.separator + '.')
                    || uri.contains('.' + File.separator) || uri.startsWith(".")
                    || uri.endsWith(".") || INSECURE_URI.matcher(uri).matches()) {
                return null;
            }
            return System.getProperty("user.dir") + File.separator + uri;
        }

        private static final Pattern ALLOWED_FILE_NAME = Pattern
                .compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");

        private static void sendListing(ChannelHandlerContext ctx, File dir) {
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
            StringBuilder buf = new StringBuilder();
            String dirPath = dir.getPath();
            buf.append("<!DOCTYPE html>\r\n");
            buf.append("<html><head><title>");
            buf.append(dirPath);
            buf.append(" 目录：");
            buf.append("</title></head><body>\r\n");
            buf.append("<h3>");
            buf.append(dirPath).append(" 目录：");
            buf.append("</h3>\r\n");
            buf.append("<ul>");
            buf.append("<li>链接：<a href=\"../\">..</a></li>\r\n");
            for (File f : dir.listFiles()) {
                if (f.isHidden() || !f.canRead()) {
                    continue;
                }
                String name = f.getName();
//                if (!ALLOWED_FILE_NAME.matcher(name).matches()) {
//                    continue;
//                }
                buf.append("<li>链接：<a href=\"");
                buf.append(name);
                buf.append("\">");
                buf.append(name);
                buf.append("</a></li>\r\n");
            }
            buf.append("</ul></body></html>\r\n");
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
}
