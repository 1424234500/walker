package com.walker.system;

import com.walker.core.exception.ExceptionUtil;
import com.walker.util.FileUtil;
import com.walker.util.Tools;
import com.walker.util.Tuple;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;


/**
 * win32系统鼠标控制、截图
 * 
 * @author Walker
 *
 */
public class Pc {
	public static volatile Robot robot = null;
	
	private Pc() { }

	private static class SingletonFactory{           
		  private static Robot instance;
		  static {
			 try {
				instance = new Robot();
			} catch (AWTException e) {
				e.printStackTrace();
			}
		  }
	}

	public static Robot getInstance() {
	    return SingletonFactory.instance;           
	}


	/**
	 * 获取cpu使用率
	 */
	public static float getCpu() throws IOException, InterruptedException {
		float res = 100;
		String c = doCmdString("top -bn 2 ").getInfo();
		c = c.toUpperCase();
		//编译正则
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("CPU.*\\d+.* ID");
		//使用正则匹配
		java.util.regex.Matcher matcher = pattern.matcher("");
		String resstr = "-1";
		matcher.reset(c); //新匹配str
		while(matcher.find()){
//			Tools.out(matcher.group());
			String k = matcher.group();
			String ss[] = k.split(" +");
			if(ss.length == 9){
				resstr = ss[7];

				float t = Float.valueOf(resstr);
				if(res < t){
					res = t;
				}

			}

		}
		res = Float.valueOf(resstr);
		return 100 - res;
	}

	/**
	 * 按键按下 KeyEvent.VK_A
	 * @param   keycode Key to release (e.g. <code>KeyEvent.VK_A</code>)
	 */
	public static void keyPress(int keycode) {
		getInstance().keyPress(keycode);
	}
	/**
	 * 按键松开 KeyEvent.VK_A
	* @param   keycode Key to release (e.g. <code>KeyEvent.VK_A</code>)
	 */
	public static void keyRelease(int keycode) {
		getInstance().keyRelease(keycode);
	}

	/**
	 * 按键按下松开 延时 KeyEvent.VK_A
	 * @param   keycode Key to release (e.g. <code>KeyEvent.VK_A</code>)
	 */
	public static int keyClick(int keycode, long dtime) {
		keyPress(keycode);
		if(dtime > 0){
			waitTime(dtime);
		}
		keyRelease(keycode);
		return keycode;
	}

	/**
	 * 鼠标按键按下 MouseEvent.BUTTON1 MouseEvent.BUTTON2 MouseEvent.BUTTON3
	 */
	public static void mousePress(int button123) {
		int button = InputEvent.getMaskForButton(button123);
		getInstance().mousePress(button);
	}
	/**
	 * 鼠标按键松开 MouseEvent.BUTTON1 MouseEvent.BUTTON2 MouseEvent.BUTTON3
	 */
	public static void mouseRelease(int button123) {
		int button = InputEvent.getMaskForButton(button123);
		getInstance().mouseRelease(button);
	}

	/**
	 * 按下 松开  MouseEvent.BUTTON1 MouseEvent.BUTTON2 MouseEvent.BUTTON3
	 */
	public static Point mouseClick(int button123, long dtime){
		mousePress(button123);
		if(dtime > 0){
			waitTime(dtime);
		}
		mouseRelease(button123);
		return mouseGet();
    }

	public static Image getScreenPrint(){
		// 获取截图的大小
		try {
			BufferedImage image = getInstance().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
			return image;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

    public static Tuple<Integer,Integer> getScreenWidthHeight() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int w = (int) screenSize.getWidth();
        int h = (int) screenSize.getHeight();
        return new Tuple<Integer,Integer>(w, h);
    }
	/**
	 * 获取鼠标点
	 */
	public static Point mouseGet() {
		return MouseInfo.getPointerInfo().getLocation();
	}
	/**
	 * 设置鼠标点
	 */
	public static Point mouseSet(int x, int y) {
		getInstance().mouseMove(x, y);
		return mouseGet();
	}
	/**
	 * 移动鼠标点
	 */
	public static Point mouseMove(int dx, int dy) {
		Point p = mouseGet();
		getInstance().mouseMove((int)p.getX() + dx, (int)p.getY() + dy);
		return mouseGet();
	}

	public static Point mouseMove(int dx, int dy, long timeall) {
		return mouseMoveDelta(dx, dy, timeall, timeall / 10, new LineGenerator() {});
	}
	public static Point mouseMoveDelta(int dx, int dy, long timeall, long dtime, LineGenerator lineGenerator) {
		Point point = mouseGet();
		return mouseMoveDirect(point.x, point.y, point.x + dx, point.y + dy, timeall, dtime, lineGenerator);
	}
	/**
	 * 	 * 移动鼠标点 函数线性移动 速度
	 * @param fromX	起点
	 * @param fromY 起点
	 * @param toX	终点
	 * @param toY	终点
	 * @param timeall	总耗时
	 * @param dtime	每增量点区间好耗时	x增量程度
	 * @param lineGenerator	线性函数生成器
	 */
	public static Point mouseMoveDirect(int fromX, int fromY, int toX, int toY, long timeall, long dtime, LineGenerator lineGenerator) {
		int dx = toX - fromX;
		int dy = toY - fromY;
//		Float ddx = 1F * dx / ( 1F * timeall / dtime);
//		Float ddy = 1F * dy / ( 1F * timeall / dtime);

		long timenow = 0;
		while(timenow < timeall){
			if(timenow > 0){
				waitTime(dtime);
			}
			timenow += dtime;
			timenow = Math.min(timenow, timeall);

			Point next = lineGenerator.fun(dx, dy, 1F * timenow / timeall);
			mouseMove(fromX + next.x, fromY + next.y);
		}

		return mouseGet();
	}
	interface LineGenerator{
		default Point fun(int dx, int dy, Float percent){
			return new Point((int)(dx * percent), (int) (dy * percent));
		}
	}



	/**
	 * 获取鼠标点颜色
	 */
	public static Color getColor() {
		Point p = mouseGet();
		return getColor(p.x, p.y);
	}
	/**
	 * 获取指定点颜色
	 */
	public static Color getColor(int x, int y) {
		return getInstance().getPixelColor(x, y);
	}

	/**
	 * 从剪切板获得文字。
	 */
	public static String getClipboardText() {
		String ret = "";
		Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
		// 获取剪切板中的内容
		Transferable clipTf = sysClip.getContents(null);

		if (clipTf != null) {
			// 检查内容是否是文本类型
			if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				try {
					ret = (String) clipTf.getTransferData(DataFlavor.stringFlavor);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return ret;
	}

	/**
	 * 将字符串复制到剪切板。
	 */
	public static void setClipboardText(String str) {
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable tText = new StringSelection(str);
		clip.setContents(tText, null);
	}

	/**
	 * 从剪切板获得图片。
	 */
	public static Image getClipboardImage() throws Exception {
		Clipboard sysc = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable cc = sysc.getContents(null);
		if (cc == null)
			return null;
		else if (cc.isDataFlavorSupported(DataFlavor.imageFlavor))
			return (Image) cc.getTransferData(DataFlavor.imageFlavor);
		return null;
	}

	/**
	 * 复制图片到剪切板。
	 */
	public static void setClipboardImage(final Image image) {
		Transferable trans = new Transferable() {
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[] { DataFlavor.imageFlavor };
			}

			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return DataFlavor.imageFlavor.equals(flavor);
			}

			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				if (isDataFlavorSupported(flavor))
					return image;
				throw new UnsupportedFlavorException(flavor);
			}

		};
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
	}


	public static boolean isLinux() {
		Properties prop = System.getProperties();

		String os = prop.getProperty("os.name");
		if (os != null && os.toLowerCase().indexOf("linux") > -1) {
			return true;
		} else {
			return false;
		}
	}
	public static List<String> getIps()  {
		List<String> ips = new ArrayList<>();
		try {
			String str = "";
			if (isLinux()) {
				str = Pc.doCmdString("ifconfig").getInfo();
			} else {
				str = Pc.doCmdString("ipconfig").getInfo();
			}
			java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");
			java.util.regex.Matcher matcher = pattern.matcher(str);
			while (matcher.find()) {
//                System.out.println(matcher.group());
				ips.add(matcher.group());
			}
			ips = ips.stream().filter(item -> !item.startsWith("255") && !item.endsWith("255")).collect(Collectors.toList());

		}catch (Exception e){
			ExceptionUtil.on(e);
		}
		if(ips.size() <= 0){
			ips.add("127.0.0.1");
		}
		return ips;
	}

	public static Result doCmdString(String command) {
		return doCmdString(command, "");
	}
	/**
	 * runtime方式执行
	 * @param command
	 */
	public static Result doCmdString(String command, String dir) {
		Result res = new Result();
		res.setKey(command);
		StringBuilder stdout = new StringBuilder();
		try {
			Process process = Runtime.getRuntime().exec(command, new String[]{}, new File(dir));
//			Process process = new ProcessBuilder(Arrays.asList(command.split(" +"))).redirectErrorStream(true).start(); // 标准错误和标准输出合并

			int code = process.waitFor();
			res.setIsOk(code); //0是 其他异常code
			InputStream inputStream = process.getInputStream();
			if(inputStream != null){
				stdout.append(FileUtil.readByLines(inputStream, null, null));
			}
			InputStream errorStream = process.getErrorStream();
			if(errorStream != null){
				stdout.append("\n").append(FileUtil.readByLines(errorStream, null, null));
			}
		}catch (Exception e){
			stdout.append("error exception " + e.getMessage());
		}finally {
			res.setCost(System.currentTimeMillis() - res.getTimeStart());
			res.setInfo(stdout.toString());
		}
		return res;
	}

	/**
	 * 获取执行环境信息
	 */
	public static String getRuntime() {
		Runtime runtime = Runtime.getRuntime();
		String res = "{\n ``Runtime: \n";
		res += " {maxMemory: " + Tools.calcSize(runtime.maxMemory()) + " \n";
		res += " , freeMemory: " + Tools.calcSize(runtime.freeMemory()) + " \n";
		res += " , totalMemory: " + Tools.calcSize(runtime.totalMemory()) + " \n}";
		return res;
	}

	/**
	 * 超时应该在3钞以上
	 */
	public static boolean ping(String ipAddress, int timeout)  {
		boolean res = false;
		try {
			res = InetAddress.getByName(ipAddress).isReachable(timeout);
		}catch(Exception e){
			res = false;
		}
		return res;
    }


	/**
	 * 模拟telnet实现socket端口检测
	 */
	public static long telnet(String ip, int port, int timeout){
		long start = System.currentTimeMillis();
		long deta = -1;
		try{
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(ip, port), timeout);
			if(socket != null){
				deta = System.currentTimeMillis() - start;
				socket.close();
			}
		}catch(Exception e){

		}

		return deta;
	}
	public static void waitTime(Long timemills){
		try {
			Thread.sleep(timemills);
		} catch (InterruptedException e) {
//			e.printStackTrace();
		}
	}



}