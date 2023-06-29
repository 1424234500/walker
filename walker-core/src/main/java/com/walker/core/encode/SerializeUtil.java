package com.walker.core.encode;

import com.mchange.lang.ByteUtils;
import com.walker.core.mode.Bean;
import com.walker.core.util.Tools;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SerializeUtil {
    private static class TestMain {
        public static void main(String[] argv) {
            Bean bean1 = new Bean().put("k1", "v1").put("k2", 2).put("k3", 3L);
            Bean bean = new Bean().put("k1", "v1").put("k2", 2).put("k3", 3L).put("bean", bean1);

            Tools.out(bean);
            byte[] bb = SerializeUtil.serialize(bean);
            Tools.out(ByteUtils.toHexAscii(bb));
            Object bs = SerializeUtil.deserialize(bb);
            Tools.out(bs);
            Tools.out("bean==bs", bean.hashCode(), bs.hashCode(), bean == bs, "bean.equals(bs)", bean.equals(bs), "bean.class", bean.getClass(), "bs.class", bs.getClass());

            List<Object> list = new ArrayList<>();
            list.add(1);
            list.add(1000L);
            list.add("ssss");
            list.add(null);
            list.add(bean1);
            list.add(null);
            list.add("end");
            Tools.out(list);
            bb = SerializeUtil.serializeList(list);
            Tools.out(ByteUtils.toHexAscii(bb));
            Object listBs = SerializeUtil.deserializeList(bb);
            Tools.out(listBs);
            Tools.out("bean==bs", listBs.hashCode(), list.hashCode(), list == listBs, "bean.equals(bs)", listBs.equals(list), "bean.class", listBs.getClass(), "bs.class", list.getClass());


        }
    }



    private static void out(Object... objects) {
        Tools.out(objects);
    }


    public static byte[] serialize(Object obj) {
        byte[] res = null;

        if (obj != null) {
            ByteArrayOutputStream baos = null;
            ObjectOutputStream oos = null;
            try {
                baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                oos.writeObject(obj);
                res = baos.toByteArray();
            } catch (IOException e) {
                out("序列化异常 obj是否未实现serialize接口 ", obj.toString());
            } finally {
                try {
                    if (oos != null)
                        oos.close();
                    if (baos != null)
                        baos.close();
                } catch (IOException e) {
                    out("序列化关闭流异常", obj.toString());
                }
            }
        }
        return res;
    }

    public static Object deserialize(String str) {
        return deserialize(str.getBytes());
    }

    public static Object deserialize(byte[] in) {
        Object res = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream is = null;
        try {
            if (in != null) {
                bis = new ByteArrayInputStream(in);
                is = new ObjectInputStream(bis);
                //此处序列化会执行构造函数 ! 如果在构造里放入特殊用途代码就会被执行
                res = is.readObject();
            }
        } catch (IOException e) {
            out("反序列化异常", in.toString(), new String(in));
        } catch (ClassNotFoundException e) {
            out("反序列化 ClassNotFoundException", in.toString(), new String(in));
        } finally {
            try {
                if (is != null)
                    is.close();
                if (bis != null)
                    bis.close();
            } catch (IOException e) {
                out("反序列化流关闭异常", in.toString(), new String(in));
            }
        }
        return res;
    }

    /**
     * 按照list元素序列化集合
     *
     * @param list
     */
    public static byte[] serializeList(List<?> list) {
        byte[] res = null;

        if (list != null) {
            ByteArrayOutputStream baos = null;
            ObjectOutputStream oos = null;
            try {
                baos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(baos);
                for (Object obj : list) {
                    oos.writeObject(obj);
                }
                oos.writeObject(new Eof()); //写入结束类标识
                res = baos.toByteArray();
            } catch (IOException e) {
                out("序列化异常");
            } finally {
                try {
                    if (oos != null)
                        oos.close();
                    if (baos != null)
                        baos.close();
                } catch (IOException e) {
                    out("序列化关闭流异常");
                }
            }
        }
        return res;
    }

    public static List<Object> deserializeList(String str) {
        return deserializeList(str.getBytes());
    }

    /**
     * 按照list分别解序列
     *
     * @param in
     */
    public static List<Object> deserializeList(byte[] in) {
        List<Object> res = new ArrayList<>();
        ByteArrayInputStream bis = null;
        ObjectInputStream is = null;
        try {
            if (in != null) {
                bis = new ByteArrayInputStream(in);
                is = new ObjectInputStream(bis);
                Object obj = null;
                while (true) {
                    obj = is.readObject();
                    if (obj instanceof Eof) {
                        break;
                    }
                    res.add(obj);
                }
            }
        } catch (IOException e) {
            out("反序列化List异常");
        } catch (ClassNotFoundException e) {
            out("反序列化List ClassNotFoundException");
        } finally {
            try {
                if (is != null)
                    is.close();
                if (bis != null)
                    bis.close();
            } catch (IOException e) {
                out("反序列化List 流关闭异常");
            }
        }
        return res;
    }

    /**
     * 结束符标识类
     */
    static final class Eof implements Serializable {

        private static final long serialVersionUID = 1L;

    }
}

