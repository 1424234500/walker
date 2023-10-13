package com.walker.core.system;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.walker.core.exception.ErrorUtil;
import com.walker.core.mode.Error;
import com.walker.core.mode.ResponseO;
import com.walker.core.mode.Tuple;
import com.walker.core.util.FileUtil;
import com.walker.core.util.Tools;

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
		String c = doCmdString("top -bn 2 ").getRes();
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
			String[] ss = k.split(" +");
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


	public static String getOs() {
		Properties prop = System.getProperties();
		// todo mac
		String os = prop.getProperty("os.name");
		if (os != null && os.toLowerCase().indexOf("linux") > -1) {
			return "linux";
		} else {
			return "window";
		}
	}
	public static List<String> getIps()  {
		List<String> ips = new ArrayList<>();
		try {
			String str = "";
			if ("linux".equals(getOs())) {
				str = Pc.doCmdString("ifconfig").getRes();
			} else {
				str = Pc.doCmdString("ipconfig").getRes();
			}
			java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");
			java.util.regex.Matcher matcher = pattern.matcher(str);
			while (matcher.find()) {
//                System.out.println(matcher.group());
				ips.add(matcher.group());
			}
			ips = ips.stream().filter(item -> !item.startsWith("255") && !item.endsWith("255")).collect(Collectors.toList());

		}catch (Exception e){
			ErrorUtil.build(e);
		}
		if(ips.size() <= 0){
			ips.add("127.0.0.1");
		}
		return ips;
	}

	public static ResponseO<String> doCmdString(String command) {
		return doCmdString(command, "");
	}
	/**
	 * runtime方式执行
	 * @param command
	 */
	public static ResponseO doCmdString(String command, String dir) {
		long st = System.currentTimeMillis();
		ResponseO res = new ResponseO();
		StringBuilder stdout = new StringBuilder();
		try {
			Process process = Runtime.getRuntime().exec(command, new String[]{}, new File(dir));
//			Process process = new ProcessBuilder(Arrays.asList(command.split(" +"))).redirectErrorStream(true).start(); // 标准错误和标准输出合并
			int code = process.waitFor();
			if(code == 0){
				res.setSuccess(true);
			}else{
				res.setError(new Error(Error.LEVEL_ERROR, code + "", ""));
			}
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
			res.setCost(System.currentTimeMillis() - st);
			res.setRes(stdout.toString());
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

	public static void waitTime(Long timemills) {
		try {
			Thread.sleep(timemills);
		} catch (InterruptedException e) {
//			e.printStackTrace();
		}
	}

	public class KeyboardHook implements Runnable {
		private WinUser.HHOOK hhk;

		// 钩子回调函数
		private final WinUser.LowLevelKeyboardProc keyboardProc = new WinUser.LowLevelKeyboardProc() {
			@Override
			public WinDef.LRESULT callback(int nCode, WinDef.WPARAM wParam, WinUser.KBDLLHOOKSTRUCT event) {
				// 输出按键值和按键时间
				if (nCode >= 0) {
					// 按下F7退出
					if (event.vkCode == 118) {
						KeyboardHook.this.setHookOff();
						System.exit(0);
					}
				}

				return User32.INSTANCE.CallNextHookEx(hhk, nCode, wParam, null);
			}
		};

		public void run() {
			setHookOn();
		}

		// 安装钩子
		public void setHookOn() {
			WinDef.HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
			hhk = User32.INSTANCE.SetWindowsHookEx(User32.WH_KEYBOARD_LL, keyboardProc, hMod, 0);

			int result;
			WinUser.MSG msg = new WinUser.MSG();
			while ((result = User32.INSTANCE.GetMessage(msg, null, 0, 0)) != 0) {
				if (result == -1) {
					setHookOff();
					break;
				} else {
					User32.INSTANCE.TranslateMessage(msg);
					User32.INSTANCE.DispatchMessage(msg);
				}
			}
		}

		// 移除钩子并退出
		public void setHookOff() {
			User32.INSTANCE.UnhookWindowsHookEx(hhk);
		}
	}


//	//创建一个窗口
//const int WM_CREATE = 0x01;
////当一个窗口被破坏时发送
//const int WM_DESTROY = 0x02;
////移动一个窗口
//const int WM_MOVE = 0x03;
////改变一个窗口的大小
//const int WM_SIZE = 0x05;
////一个窗口被激活或失去激活状态
//const int WM_ACTIVATE = 0x06;
////一个窗口获得焦点
//const int WM_SETFOCUS = 0x07;
////一个窗口失去焦点
//const int WM_KILLFOCUS = 0x08;
////一个窗口改变成Enable状态
//const int WM_ENABLE = 0x0A;
////设置窗口是否能重画
//const int WM_SETREDRAW = 0x0B;
////应用程序发送此消息来设置一个窗口的文本
//const int WM_SETTEXT = 0x0C;
////应用程序发送此消息来复制对应窗口的文本到缓冲区
//const int WM_GETTEXT = 0x0D;
////得到与一个窗口有关的文本的长度（不包含空字符）
//const int WM_GETTEXTLENGTH = 0x0E;
////要求一个窗口重画自己
//const int WM_PAINT = 0x0F;
////当一个窗口或应用程序要关闭时发送一个信号
//const int WM_CLOSE = 0x10;
////当用户选择结束对话框或程序自己调用ExitWindows函数
//const int WM_QUERYENDSESSION = 0x11;
////用来结束程序运行
//const int WM_QUIT = 0x12;
////当用户窗口恢复以前的大小位置时，把此消息发送给某个图标
//const int WM_QUERYOPEN = 0x13;
////当窗口背景必须被擦除时（例在窗口改变大小时）
//const int WM_ERASEBKGND = 0x14;
////当系统颜色改变时，发送此消息给所有顶级窗口
//const int WM_SYSCOLORCHANGE = 0x15;
////当系统进程发出WM_QUERYENDSESSION消息后，此消息发送给应用程序，通知它对话是否结束
//const int WM_ENDSESSION = 0x16;
////当隐藏或显示窗口是发送此消息给这个窗口
//const int WM_SHOWWINDOW = 0x18;
////发此消息给应用程序哪个窗口是激活的，哪个是非激活的
//const int WM_ACTIVATEAPP = 0x1C;
////当系统的字体资源库变化时发送此消息给所有顶级窗口
//const int WM_FONTCHANGE = 0x1D;
////当系统的时间变化时发送此消息给所有顶级窗口
//const int WM_TIMECHANGE = 0x1E;
////发送此消息来取消某种正在进行的摸态（操作）
//const int WM_CANCELMODE = 0x1F;
////如果鼠标引起光标在某个窗口中移动且鼠标输入没有被捕获时，就发消息给某个窗口
//const int WM_SETCURSOR = 0x20;
////当光标在某个非激活的窗口中而用户正按着鼠标的某个键发送此消息给//当前窗口
//const int WM_MOUSEACTIVATE = 0x21;
////发送此消息给MDI子窗口//当用户点击此窗口的标题栏，或//当窗口被激活，移动，改变大小
//const int WM_CHILDACTIVATE = 0x22;
////此消息由基于计算机的训练程序发送，通过WH_JOURNALPALYBACK的hook程序分离出用户输入消息
//const int WM_QUEUESYNC = 0x23;
////此消息发送给窗口当它将要改变大小或位置
//const int WM_GETMINMAXINFO = 0x24;
////发送给最小化窗口当它图标将要被重画
//const int WM_PAINTICON = 0x26;
////此消息发送给某个最小化窗口，仅//当它在画图标前它的背景必须被重画
//const int WM_ICONERASEBKGND = 0x27;
////发送此消息给一个对话框程序去更改焦点位置
//const int WM_NEXTDLGCTL = 0x28;
////每当打印管理列队增加或减少一条作业时发出此消息
//const int WM_SPOOLERSTATUS = 0x2A;
////当button，combobox，listbox，menu的可视外观改变时发送
//const int WM_DRAWITEM = 0x2B;
////当button, combo box, list box, list view control, or menu item 被创建时
//const int WM_MEASUREITEM = 0x2C;
////此消息有一个LBS_WANTKEYBOARDINPUT风格的发出给它的所有者来响应WM_KEYDOWN消息
//const int WM_VKEYTOITEM = 0x2E;
////此消息由一个LBS_WANTKEYBOARDINPUT风格的列表框发送给他的所有者来响应WM_CHAR消息
//const int WM_CHARTOITEM = 0x2F;
////当绘制文本时程序发送此消息得到控件要用的颜色
//const int WM_SETFONT = 0x30;
////应用程序发送此消息得到当前控件绘制文本的字体
//const int WM_GETFONT = 0x31;
////应用程序发送此消息让一个窗口与一个热键相关连
//const int WM_SETHOTKEY = 0x32;
////应用程序发送此消息来判断热键与某个窗口是否有关联
//const int WM_GETHOTKEY = 0x33;
////此消息发送给最小化窗口，当此窗口将要被拖放而它的类中没有定义图标，应用程序能返回一个图标或光标的句柄，当用户拖放图标时系统显示这个图标或光标
//const int WM_QUERYDRAGICON = 0x37;
////发送此消息来判定combobox或listbox新增加的项的相对位置
//const int WM_COMPAREITEM = 0x39;
////显示内存已经很少了
//const int WM_COMPACTING = 0x41;
////发送此消息给那个窗口的大小和位置将要被改变时，来调用setwindowpos函数或其它窗口管理函数
//const int WM_WINDOWPOSCHANGING = 0x46;
////发送此消息给那个窗口的大小和位置已经被改变时，来调用setwindowpos函数或其它窗口管理函数
//const int WM_WINDOWPOSCHANGED = 0x47;
////当系统将要进入暂停状态时发送此消息
//const int WM_POWER = 0x48;
////当一个应用程序传递数据给另一个应用程序时发送此消息
//const int WM_COPYDATA = 0x4A;
////当某个用户取消程序日志激活状态，提交此消息给程序
//const int WM_CANCELJOURNA = 0x4B;
////当某个控件的某个事件已经发生或这个控件需要得到一些信息时，发送此消息给它的父窗口
//const int WM_NOTIFY = 0x4E;
////当用户选择某种输入语言，或输入语言的热键改变
//const int WM_INPUTLANGCHANGEREQUEST = 0x50;
////当平台现场已经被改变后发送此消息给受影响的最顶级窗口
//const int WM_INPUTLANGCHANGE = 0x51;
////当程序已经初始化windows帮助例程时发送此消息给应用程序
//const int WM_TCARD = 0x52;
////此消息显示用户按下了F1，如果某个菜单是激活的，就发送此消息个此窗口关联的菜单，否则就发送给有焦点的窗口，如果//当前都没有焦点，就把此消息发送给//当前激活的窗口
//const int WM_HELP = 0x53;
////当用户已经登入或退出后发送此消息给所有的窗口，//当用户登入或退出时系统更新用户的具体设置信息，在用户更新设置时系统马上发送此消息
//const int WM_USERCHANGED = 0x54;
////公用控件，自定义控件和他们的父窗口通过此消息来判断控件是使用ANSI还是UNICODE结构
//const int WM_NOTIFYFORMAT = 0x55;
////当用户某个窗口中点击了一下右键就发送此消息给这个窗口
////const int WM_CONTEXTMENU = ??;
////当调用SETWINDOWLONG函数将要改变一个或多个 窗口的风格时发送此消息给那个窗口
//const int WM_STYLECHANGING = 0x7C;
////当调用SETWINDOWLONG函数一个或多个 窗口的风格后发送此消息给那个窗口
//const int WM_STYLECHANGED = 0x7D;
////当显示器的分辨率改变后发送此消息给所有的窗口
//const int WM_DISPLAYCHANGE = 0x7E;
////此消息发送给某个窗口来返回与某个窗口有关连的大图标或小图标的句柄
//const int WM_GETICON = 0x7F;
////程序发送此消息让一个新的大图标或小图标与某个窗口关联
//const int WM_SETICON = 0x80;
////当某个窗口第一次被创建时，此消息在WM_CREATE消息发送前发送
//const int WM_NCCREATE = 0x81;
////此消息通知某个窗口，非客户区正在销毁
//const int WM_NCDESTROY = 0x82;
////当某个窗口的客户区域必须被核算时发送此消息
//const int WM_NCCALCSIZE = 0x83;
////移动鼠标，按住或释放鼠标时发生
//const int WM_NCHITTEST = 0x84;
////程序发送此消息给某个窗口当它（窗口）的框架必须被绘制时
//const int WM_NCPAINT = 0x85;
////此消息发送给某个窗口仅当它的非客户区需要被改变来显示是激活还是非激活状态
//const int WM_NCACTIVATE = 0x86;
////发送此消息给某个与对话框程序关联的控件，widdows控制方位键和TAB键使输入进入此控件通过应
//const int WM_GETDLGCODE = 0x87;
////当光标在一个窗口的非客户区内移动时发送此消息给这个窗口 非客户区为：窗体的标题栏及窗 的边框体
//const int WM_NCMOUSEMOVE = 0xA0;
////当光标在一个窗口的非客户区同时按下鼠标左键时提交此消息
//const int WM_NCLBUTTONDOWN = 0xA1;
////当用户释放鼠标左键同时光标某个窗口在非客户区十发送此消息
//const int WM_NCLBUTTONUP = 0xA2;
////当用户双击鼠标左键同时光标某个窗口在非客户区十发送此消息
//const int WM_NCLBUTTONDBLCLK = 0xA3;
////当用户按下鼠标右键同时光标又在窗口的非客户区时发送此消息
//const int WM_NCRBUTTONDOWN = 0xA4;
////当用户释放鼠标右键同时光标又在窗口的非客户区时发送此消息
//const int WM_NCRBUTTONUP = 0xA5;
////当用户双击鼠标右键同时光标某个窗口在非客户区十发送此消息
//const int WM_NCRBUTTONDBLCLK = 0xA6;
////当用户按下鼠标中键同时光标又在窗口的非客户区时发送此消息
//const int WM_NCMBUTTONDOWN = 0xA7;
////当用户释放鼠标中键同时光标又在窗口的非客户区时发送此消息
//const int WM_NCMBUTTONUP = 0xA8;
////当用户双击鼠标中键同时光标又在窗口的非客户区时发送此消息
//const int WM_NCMBUTTONDBLCLK = 0xA9;
////WM_KEYDOWN 按下一个键
//const int WM_KEYDOWN = 0x0100;
////释放一个键
//const int WM_KEYUP = 0x0101;
////按下某键，并已发出WM_KEYDOWN， WM_KEYUP消息
//const int WM_CHAR = 0x102;
////当用translatemessage函数翻译WM_KEYUP消息时发送此消息给拥有焦点的窗口
//const int WM_DEADCHAR = 0x103;
////当用户按住ALT键同时按下其它键时提交此消息给拥有焦点的窗口
//const int WM_SYSKEYDOWN = 0x104;
////当用户释放一个键同时ALT 键还按着时提交此消息给拥有焦点的窗口
//const int WM_SYSKEYUP = 0x105;
////当WM_SYSKEYDOWN消息被TRANSLATEMESSAGE函数翻译后提交此消息给拥有焦点的窗口
//const int WM_SYSCHAR = 0x106;
////当WM_SYSKEYDOWN消息被TRANSLATEMESSAGE函数翻译后发送此消息给拥有焦点的窗口
//const int WM_SYSDEADCHAR = 0x107;
////在一个对话框程序被显示前发送此消息给它，通常用此消息初始化控件和执行其它任务
//const int WM_INITDIALOG = 0x110;
////当用户选择一条菜单命令项或当某个控件发送一条消息给它的父窗口，一个快捷键被翻译
//const int WM_COMMAND = 0x111;
////当用户选择窗口菜单的一条命令或//当用户选择最大化或最小化时那个窗口会收到此消息
//const int WM_SYSCOMMAND = 0x112;
////发生了定时器事件
//const int WM_TIMER = 0x113;
////当一个窗口标准水平滚动条产生一个滚动事件时发送此消息给那个窗口，也发送给拥有它的控件
//const int WM_HSCROLL = 0x114;
////当一个窗口标准垂直滚动条产生一个滚动事件时发送此消息给那个窗口也，发送给拥有它的控件
//const int WM_VSCROLL = 0x115;
////当一个菜单将要被激活时发送此消息，它发生在用户菜单条中的某项或按下某个菜单键，它允许程序在显示前更改菜单
//const int WM_INITMENU = 0x116;
////当一个下拉菜单或子菜单将要被激活时发送此消息，它允许程序在它显示前更改菜单，而不要改变全部
//const int WM_INITMENUPOPUP = 0x117;
////当用户选择一条菜单项时发送此消息给菜单的所有者（一般是窗口）
//const int WM_MENUSELECT = 0x11F;
////当菜单已被激活用户按下了某个键（不同于加速键），发送此消息给菜单的所有者
//const int WM_MENUCHAR = 0x120;
////当一个模态对话框或菜单进入空载状态时发送此消息给它的所有者，一个模态对话框或菜单进入空载状态就是在处理完一条或几条先前的消息后没有消息它的列队中等待
//const int WM_ENTERIDLE = 0x121;
////在windows绘制消息框前发送此消息给消息框的所有者窗口，通过响应这条消息，所有者窗口可以通过使用给定的相关显示设备的句柄来设置消息框的文本和背景颜色
//const int WM_CTLCOLORMSGBOX = 0x132;
////当一个编辑型控件将要被绘制时发送此消息给它的父窗口通过响应这条消息，所有者窗口可以通过使用给定的相关显示设备的句柄来设置编辑框的文本和背景颜色
//const int WM_CTLCOLOREDIT = 0x133;
//
////当一个列表框控件将要被绘制前发送此消息给它的父窗口通过响应这条消息，所有者窗口可以通过使用给定的相关显示设备的句柄来设置列表框的文本和背景颜色
//const int WM_CTLCOLORLISTBOX = 0x134;
////当一个按钮控件将要被绘制时发送此消息给它的父窗口通过响应这条消息，所有者窗口可以通过使用给定的相关显示设备的句柄来设置按纽的文本和背景颜色
//const int WM_CTLCOLORBTN = 0x135;
////当一个对话框控件将要被绘制前发送此消息给它的父窗口通过响应这条消息，所有者窗口可以通过使用给定的相关显示设备的句柄来设置对话框的文本背景颜色
//const int WM_CTLCOLORDLG = 0x136;
////当一个滚动条控件将要被绘制时发送此消息给它的父窗口通过响应这条消息，所有者窗口可以通过使用给定的相关显示设备的句柄来设置滚动条的背景颜色
//const int WM_CTLCOLORSCROLLBAR = 0x137;
////当一个静态控件将要被绘制时发送此消息给它的父窗口通过响应这条消息，所有者窗口可以 通过使用给定的相关显示设备的句柄来设置静态控件的文本和背景颜色
//const int WM_CTLCOLORSTATIC = 0x138;
////当鼠标轮子转动时发送此消息个当前有焦点的控件
//const int WM_MOUSEWHEEL = 0x20A;
////双击鼠标中键
//const int WM_MBUTTONDBLCLK = 0x209;
////释放鼠标中键
//const int WM_MBUTTONUP = 0x208;
////移动鼠标时发生，同WM_MOUSEFIRST
//const int WM_MOUSEMOVE = 0x200;
////按下鼠标左键
//const int WM_LBUTTONDOWN = 0x201;
////释放鼠标左键
//const int WM_LBUTTONUP = 0x202;
////双击鼠标左键
//const int WM_LBUTTONDBLCLK = 0x203;
////按下鼠标右键
//const int WM_RBUTTONDOWN = 0x204;
////释放鼠标右键
//const int WM_RBUTTONUP = 0x205;
////双击鼠标右键
//const int WM_RBUTTONDBLCLK = 0x206;
////按下鼠标中键
//const int WM_MBUTTONDOWN = 0x207;
//
//const int WM_USER = 0x0400;
//const int MK_LBUTTON = 0x0001;
//const int MK_RBUTTON = 0x0002;
//const int MK_SHIFT = 0x0004;
//const int MK_CONTROL = 0x0008;
//const int MK_MBUTTON = 0x0010;
//const int MK_XBUTTON1 = 0x0020;
//const int MK_XBUTTON2 = 0x0040;

}