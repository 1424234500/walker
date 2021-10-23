package com.walker.box;


import com.walker.core.encode.PinyinUtil;
import com.walker.util.*;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;


/**
 * 多线程分工模型共用抽象
 * 监控统计定时线程进度条
 * <p>
 * 生产消费模式
 * 分片自取模式
 */

public class TaskMakeTest {
    static String baseClass = "com.ruaho.rhdal.BaseTest";
    static String topath = "D:\\workspace\\obcp\\obcprhdal\\src\\test\\java";
    static String[][] ks = {
            {"com/rh/core/icbc/imp/NImpTableDataTask.java", "cph"},
            {"com/rh/core/FileServlet.java", "cph"},
    };
    //生成文件路径
    static Bean userIndex = new Bean();// com.walker.test 		cph
    static int i = 0;
    static Set<String> my;

    static {
        my = new LinkedHashSet<>();
        FileUtil.readByLines("TEST.txt", new FileUtil.Fun<String>() {
            @Override
            public void make(String obj) {
                String[] sss = obj.split(" +");

                String k = sss.length > 1 ? sss[1] : obj;
                String user = sss.length > 1 ? sss[0] : "no";
                k = k.replace("mobileserver_icbc/src/", "");
                k = k.replace("server/src/main/java/", "");
                k = k.replace("src/main/java/", "");

                String ss = k.replace('/', '.');
                ss = ss.substring(0, ss.length() - ".java".length());
                my.add(ss);
                userIndex.put(ss, PinyinUtil.getPinYinHeadChar(user));
                Tools.out(i++, ss, user);
            }
        }, "utf-8");


    }

    public static void main(String[] argv) throws InterruptedException {
//		List<Bean> list = ClassUtil.getClassBeanByFile("E:\\workspace\\obcp\\obcpweb\\pro\\WEB-INF\\classes", true);
        List<Bean> list = ClassUtil.getPackageClassBean("", true);
        final List<Bean> listOn = new ArrayList<>();
        Tools.out(list);
        for (Bean bean : list) {
            String className = bean.get("PACKAGE", "");
            String cname = FileUtil.getFileType(className);    //Main
            String testClassName = cname + "TestModel";    //MainTest
            String pak = className.substring(0, className.length() - cname.length() - 1);    //com.walker
//
//			Tools.out("inner clz: " + className);
            Class<?> clz = ClassUtil.loadClass(className);
            if (className.contains("$") || className.contains("package-info")) {
                Tools.out("inner clz or ?: " + className);
            } else if (!className.startsWith("com.walker") && !pak.equalsIgnoreCase("com")) {
//				if(my.contains(className)  ) {
                if (className.contains("com.ruaho.rhdal.generator") || className.contains("com.ruaho.rhdal.repository")) {
                    String modify = Modifier.toString(clz.getModifiers());
                    if (modify.contains("abstract")) {
                        Tools.out("my  but abstract  clz: " + className);
                        listOn.add(bean);
                    } else {
                        Tools.out("my    clz: " + className);
                        listOn.add(bean);
                    }
                } else {
                    Tools.out("not my clz: " + className);
//					listOn.add(bean);

                }
            } else {
                Tools.out("except " + className);
            }
        }
        new TaskThreadPie(listOn.size()) {

            @Override
            public void onStartThread(int threadNo) {
                Watch sw = new Watch();

                Bean bbb = listOn.get(threadNo);
                String className = bbb.get("PACKAGE", "");    // com.walker.Main

                Tools.out(className);

                String cname = FileUtil.getFileType(className);    //Main
                String instanceName = cname.substring(0, 1).toLowerCase() + cname.substring(1);
                String testClassName = cname + "TestModel";    //MainTest
                String pak = className.substring(0, className.length() - cname.length() - 1);    //com.walker
//				xxx/com.walker.TestAna
                String filepath = topath + File.separator
//						+ userIndex.get(className, "nobody") + File.separator
                        + pak.replace('.', File.separatorChar)    //com/walker
                        + File.separatorChar + testClassName + ".java";    // MainTest.java

                sw.cost("ana " + className);
                Class<?> clz = ClassUtil.loadClass(className);
                String modify = Modifier.toString(clz.getModifiers());

                //不包括父类 包括私有
                List<Method> methods = ClassUtil.getMethodNative(className, false);
//				Tools.out(methods);
                List<Method> mcopy = new ArrayList<>();
                for (Method method : methods) {
                    if (method.toString().contains("getClientUserVersion")) {
                        Tools.out(method.toString());
                    }
                    if (method.toString().contains("private")) {    //不处理私有
                        Tools.out("private method " + method);
                    } else if (method.getName().contains("$")) {    //不处理???
                        Tools.out("??? method " + method);
                    } else if (method.getName().contains("main")) {    //不处理???
                        Tools.out("??? method " + method);
                    } else {
                        mcopy.add(method);
                    }
                }
                methods.clear();
                methods.addAll(mcopy);
//				Collections.sort(methods, new Comparator<Method>() {
//					@Override
//					public int compare(Method o1,  Method o2) {
//						return o1.getParameterTypes().length < o2.getParameterTypes().length ? -1 : 1;
//					}
//				});
//				//参数个数排序 重载方法只测一个 参数最少的
//				Map<String, Method> index = new LinkedHashMap<>( );
//				for(Method method : methods){
//					if(index.containsKey(method.getName())){
//						Tools.out("repeat method " + method.toString());
//					} else  {	//
//						index.set(method.getName(), method);
//					}
//				}
//				methods.clear();
//				methods.addAll(index.values());

                Tools.out("job no " + threadNo, className, "method:" + methods.size(), pak, cname);

                StringBuilder sb = new StringBuilder();
                sb.append("\n");
                sb.append("/**").append("\n");
                sb.append(" * startTime: " + TimeUtil.getTimeYmdHmss()).append("\n");
//				sb.append(" * saveFile: " + filepath).append("\n");
                sb.append(" * " + className).append("\n");
                sb.append(" * " + pak).append("\n");
                sb.append(" * " + cname).append("\n");
                sb.append(" * Methods: " + methods.size()).append("\n");
                int i = 0;
                for (Method method : methods) {
//					sb.append(" *  #" + i++ + "." + method.getName() + "(" + method.getParameterTypes() + ")").append("\n");
                    sb.append(" *  #" + i++ + "." + method.toString()).append("\n");
                }
                sb.append(" *  ").append("\n");
                sb.append(" */").append("\n");
//					生成测试类
                sb.append("package " + pak + ";").append("\n");

//				sb.append("import com.rh.core.base.Bean;").append("\n");
//				sb.append("import com.rh.core.serv.ParamBean;").append("\n");
                sb.append("import org.junit.Assert;").append("\n");
                sb.append("import java.lang.reflect.Method;").append("\n");
                sb.append("import java.util.ArrayList;").append("\n");
                sb.append("import java.util.List;").append("\n");
                sb.append("import com.ruaho.rhdal.BaseTest; ").append("\n");
                sb.append("import org.springframework.beans.factory.annotation.Autowired;").append("\n");
                sb.append("import " + className + ";").append("\n");
                sb.append("public class " + testClassName + " extends " + baseClass + "  {").append("\n");


//				注解判断是否 注入
                boolean isentity = false;
                boolean isRepon = false;
                Annotation[] as = clz.getDeclaredAnnotations();
                for (Annotation a : as) {
                    if (a.annotationType().toString().contains("Entity")) {
                        isentity = true;
                    }
                    if (a.annotationType().toString().endsWith("Repository.class")) {
                        isRepon = true;
                    }
                }

                if (isentity || as == null || as.length == 0) {
//					sb.append("    " + className + " " + instanceName + " = new " + className + "();").append("\n");

                    sb.append("\t static " + className + " " + instanceName + " = null;").append("\n");
                    sb.append("\t static { ").append("\n");
                    sb.append("        " + instanceName + " = new  " + className + "(");
//					Tools.out("args", argsstr);
//					sb.append("			" + className + " instance = new " + className + "(");
                    Constructor<?>[] ccc = clz.getConstructors();

                    List<Constructor<?>> listcccPublic = new ArrayList<>();
                    for (Constructor<?> c : ccc) {
                        if (!getModifier(c).contains("private")) {    //不要私有的
                            listcccPublic.add(c);
                        }
                    }
                    Collections.sort(listcccPublic, new Comparator<Constructor<?>>() {
                        @Override
                        public int compare(Constructor<?> o1, Constructor<?> o2) {
                            return o1.getParameterTypes().length < o2.getParameterTypes().length ? -1 : 1;
                        }
                    });//参数个数排序
                    if (listcccPublic.size() > 0) {
                        Constructor<?> nowc = ccc[0];
                        StringBuilder sbc = new StringBuilder();
                        for (Class<?> clzArg : nowc.getParameterTypes()) {
                            String value = makeValue(clzArg);
                            sbc.append(value).append(", ");
                        }
                        if (nowc.getParameterTypes().length > 0)
                            sbc.setLength(sbc.length() - ", ".length());
                        sb.append(sbc);
                    } else {
                        Tools.out("no public construct " + className);
                        return;
                    }
                    sb.append(");").append("\n");
                    sb.append("    }").append("\n");

                } else {
                    sb.append("    @Autowired").append("\n");
                    sb.append("    " + className + " " + instanceName + ";").append("\n");
                }


                sw.cost("all");

//				for(String exceptClass : lexceptClass){
//					if(className.toLowerCase().contains(exceptClass)){
//							methods.clear();
//					}
//				}

                if (
                        (!isRepon &&
                                (
                                        modify.toLowerCase().contains("abstract")
                                                ||
                                                modify.toLowerCase().contains("interface")
                                )
                        )
                                || methods.size() == 0
                ) {    //无则生成默认test
                    methods.clear();

                    sb.append("	@org.junit.TestModel").append("\n");
                    sb.append("	public void defaultTest()   {").append("\n");
                    sb.append("		boolean flag = true;").append("\n");
                    sb.append("		try {").append("\n");
                    sb.append("			System.out.println(String.valueOf( \"default test \" ));").append("\n");
                    sb.append("		}catch (Exception e){").append("\n");
                    sb.append("			e.printStackTrace();").append("\n");
                    sb.append("			flag = true;").append("\n");
                    sb.append("		}").append("\n");
                    sb.append("		Assert.assertTrue(flag);").append("\n");
                    sb.append("	}").append("\n");
                }

                for (int ti = 0; ti < methods.size(); ti++) {
                    Method method = methods.get(ti);
                    //public static String toString(String s1, String s2)
                    String modifier = getModifier(method);    //public static
                    Class<?> returnType = method.getReturnType();//void,   class org.apache.log4j,class java.lang.String
                    Class<?>[] paramTypes = method.getParameterTypes();//[String,String]
                    String name = method.getName();//toString

                    sw.cost("" + method);

                    StringBuilder sba = new StringBuilder();
                    for (Class<?> clzArg : method.getParameterTypes()) {
                        String value = makeValue(clzArg);
                        sba.append(value).append(", ");
                    }
                    if (method.getParameterTypes().length > 0) {
                        sba.setLength(sba.length() - ", ".length());
                    }
                    String argsstr = sba.toString();

//					生成测试方法
                    sb.append("	@org.junit.TestModel").append("\n");
                    sb.append("	public void " + name + ti + "TestModel()   {").append("\n");
                    sb.append("		boolean flag = true;").append("\n");
                    sb.append("		try {").append("\n");
//					sb.append("			TestArgTest.out(\"args\", \"" + argsstr.replace('"', ' ') + "\");").append("\n");
//					if(method.getReturnType().toString().contains("void")){
                    sb.append("			" + instanceName + "." + name + "(" + argsstr + ");").append("\n");
//					}
                    sb.append("		}catch (Exception e){").append("\n");
                    sb.append("			e.printStackTrace();").append("\n");
                    sb.append("			flag = true;").append("\n");
                    sb.append("		}").append("\n");
                    sb.append("		Assert.assertTrue(flag);").append("\n");
                    sb.append("	}").append("\n");


//					ThreadUtil.sleep(10);
                    sw.cost("all");

                }

                sb.append("}").append("\n");
                sb.append("//").append(sw.toPrettyString().replace("\n", "\n//"));

//				Tools.out(sb.toString());
                FileUtil.mkfile(filepath);
                FileUtil.saveAs(sb.toString(), filepath, false);
//				File file = new File(filepath);


//				ThreadUtil.sleep(100);

            }

        }.start();

    }

    static String makeValue(Class<?> clzArg) {
        String name = clzArg.getName();
        if (name.startsWith("[")) {
            return "null";
        } else if (name.equalsIgnoreCase("java.lang.String")) {
            return ("\"hello" + (int) (Math.random() * 10) + " \"");
        } else if (name.equalsIgnoreCase("java.lang.boolean") || name.equalsIgnoreCase("boolean")) {
            return "true";
        } else if (name.equalsIgnoreCase("java.lang.Double") || name.equalsIgnoreCase("double")) {
            return "1.0D";
        } else if (name.equalsIgnoreCase("java.lang.Integer") || name.equalsIgnoreCase("int")) {
            return "2";
        } else if (name.equalsIgnoreCase("java.lang.Long") || name.equalsIgnoreCase("long")) {
            return "3L";
        } else if (name.equalsIgnoreCase("java.lang.Float") || name.equalsIgnoreCase("float")) {
            return "1.3F";
        } else if (name.equalsIgnoreCase("java.lang.Char") || name.equalsIgnoreCase("char")) {
            return "'c'";
        } else if (name.equalsIgnoreCase("com.rh.core.serv.ParamBean")) {
            return "new com.rh.core.serv.ParamBean().set(\"from\", \"000\").set(\"to\", \"222 " + name + " \")";
        } else if (name.equalsIgnoreCase("com.rh.core.base.Bean")) {
            return "new com.rh.core.base.Bean().set(\"from\", \"000\").set(\"to\", \"111 " + name + " \")";
        } else if (name.equalsIgnoreCase("com.rh.qixin.plugins.RhAckRequest")) {
            return "(" + name + ")null";
        } else if (name.equalsIgnoreCase("com.rh.qixin.plugins.RhPluginParameter")) {
            return "new " + name + "()";
        } else if (name.startsWith("com.rh.qixin.plugins")) {
            return "new " + name + "()";
        } else if (name.equals("java.lang.Object")) {
            return "null";
        } else {
//			try {
//				return "new " + clzArg.getName() + "()";
//			} catch (Exception e) {
//				return "null";
//			}
            return "(" + name + ")null";

        }


    }

    /**
     * 获取Method 或者Field 的修饰符 public static final
     *
     * @param member
     */
    public static String getModifier(Member member) {
        int mod = member.getModifiers() & Modifier.methodModifiers();
        if (mod != 0) {
            return Modifier.toString(mod);
        }
        return "";
    }

    public static List<Method> getMethod(String className, boolean ifFather) {
        Class<?> cls = ClassUtil.loadClass(className);
        List<Method> res = new ArrayList<>();


        List<Method> methodAll = new ArrayList<>();//所有方法
        Collections.addAll(methodAll, cls.getMethods());

        List<Method> methodSelf = new ArrayList<>();//自有方法
        Collections.addAll(methodAll, cls.getDeclaredMethods());

        methodAll.removeAll(methodSelf); //余下的是基类的方法

        if (cls != null) {
            if (ifFather) { //显示父类(仅public) 以及自己 的(不包括private)所有方法 不要变量
                for (Method item : cls.getMethods()) {
                    res.add(item);
                }
                //附加自己私有域
//				for(Method item : cls.getDeclaredMethods()){
//					if(getModifier(item).indexOf("private") >= 0){
//						res.add(item);
//					}
//				}
            } else {//只显示自己的(所有的 包括private)
                for (Method item : cls.getDeclaredMethods()) {
                    res.add(item);
                }
            }
        }

        return res;
    }
}