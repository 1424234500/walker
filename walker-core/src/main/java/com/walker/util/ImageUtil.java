package com.walker.util;

import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class ImageUtil {
    public static final List<String> types = Arrays.asList("png", "jpg", "jpeg", "bmp", "gif");
    private static final Logger log = LoggerFactory.getLogger(ImageUtil.class);

    private static void out(Object... objects) {
        log.info(Arrays.toString(objects));
    }

    public static boolean isImage(String ext) {
        return types.contains(ext.toLowerCase());
    }


    /**
     * 放缩图片问题
     *
     * @param path 文件路径	/home/walker/files/2020/aslkdjflaksdjflka
     * @param ext  文件后缀类型	png
     * @param size 放缩尺寸	200x200
     * @return 返回放缩后的尺寸200x200 或者不转换 则 “”
     */
    public static String makeIfImageThenSize(String path, String ext, String size, String toPath) throws IOException {
        String res = "";
        if (isImage(ext)) {
            Watch watch = new Watch("image-turn");
            int width = 200;
            int height = 200;
            if (size != null && size.length() > 0) {
                if (size.indexOf("x") >= 0) {
                    String[] sizes = size.split("x");
                    width = Integer.valueOf(sizes[0]);
                    if (sizes.length > 1) {
                        height = Integer.valueOf(sizes[1]);
                    } else {
                        height = width;
                    }
                }
                res = width + "x" + height;
                watch.cost("size w:" + width + "x" + "h:" + height);
                watch.put("from", path);
                watch.put("to", toPath);

//				创建新文件 避免工具自动创建文件后缀名
                new File(toPath).createNewFile();
                OutputStream os = new FileOutputStream(toPath);
                try {
                    /*
                     * size(width,height) 若图片横比200小，高比300小，不变
                     * 若图片横比200小，高比300大，高缩小到300，图片比例不变 若图片横比200大，高比300小，横缩小到200，图片比例不变
                     * 若图片横比200大，高比300大，图片按比例缩小，横为200或高为300
                     */
                    Thumbnails.of(new File(path))
//						.scale(1.0D)
                            //					.keepAspectRatio(false)	//默认是按照比例缩放的
                            //					.rotate(90)
                            .size(width, height)
                            //					.watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File("images/watermark.png")), 0.5f)
//						.outputQuality(1f)	//质量控制
//						.outputFormatType("png")	//图片格式问题
//						.toFile(new File(toPath))	//自动添加后缀 噩梦！！！
                            .toOutputStream(os)
                    ;
                    os.flush();
                } finally {
                    if (os != null) {
                        os.close();
                    }
                }
                watch.cost("turn");
                log.info(watch.toPrettyString());
            }
        }
        return res;
    }


}
