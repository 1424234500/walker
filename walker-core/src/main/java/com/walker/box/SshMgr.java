package com.walker.box;

import com.walker.core.encode.JsonFastUtil;
import com.walker.system.Result;
import com.walker.system.Server;
import com.walker.system.SshJsch;
import com.walker.mode.BeanLinked;
import com.walker.util.FileUtil;
import com.walker.util.Tools;

import java.io.IOException;
import java.util.*;


public class SshMgr {
	public final static String obcpweb = "obcpweb";
	public final static String obcprhdal = "obcprhdal";
	public final static String obcpmsg = "obcpmsg";
	public final static String obcporacle = "obcporacle";
	public final static String obcpmysql = "obcpmysql";
	public final static String obcpredis1 = "obcpredis1";
	public final static String obcpredis2 = "obcpredis2";
	public final static String mccp = "mccp";
	public final static String mccporacle = "mccporacle";
	public final static String mccpredis1 = "mccpredis1";
	public final static String mccpredis2 = "mccpredis2";
	public final static String mccpihs = "mccpihs";
	
	static String[] names = {"obcpweb",  "obcprhdal", 	"obcpmsg",	"obcporacle", "obcpmysql", "obcpredis1", "obcpredis2"
			, "mccp", "mccporacle", "mccpredis1", "mccpredis2", "mccpihs"};

//
	public static void  main(String[] argv) throws Exception{
//		new SshMgr().showDubboZookeeper(Arrays.asList("kf126"));//"cxjc3-obcp8",
//		new SshMgr().testUpDown(Arrays.asList("kf126"));
		SshMgr.makeChromeBookmarks();
		new SshMgr().showAll(Arrays.asList("kf126"));//"cxjc3-obcp8",
	}
	SshMgr(){



	}


	public SshMgr testUpDown(List<String> listName){
		List<String> res = new ArrayList<>();
		String file = "hello.txt";
		FileUtil.saveAs("hello ssh", file, false);

		for(String name : listName) {
			BeanLinked list = serverTree.get(name, new BeanLinked());
			Object[] keys = list.keySet().toArray();

			Server serverObcpweb = list.get(obcpweb, new Server());
			Server serverObcprhdal = list.get(obcprhdal, new Server());
			Server serverObcpmsg = list.get(obcpmsg, new Server());

			new TaskThreadPie(keys.length) {
				@Override
				public void onStartThread(int threadNo) throws IOException, Exception {
					Object key = keys[threadNo];
					Server server = list.get(key, new Server());

					Result result = new SshJsch(server).upload(file, "/approot1/" + file);
					res.add(server.toSsh() + " " + result.toString());
					Result result1 = new SshJsch(server).download("/approot1/" + file, file + ".download.txt");
					res.add(server.toSsh() + " " + result1.toString());
				}
			}.setThreadSize(10).start();
		}


		Tools.out("----------------res -----------");
		Tools.formatOut(res);
		Tools.out("----------------error -----------");

		return this;
	}

	public void test(){
		List<String> res = new ArrayList<>();
		List<String> res1 = new ArrayList<>();
		Object[] keys = serverTree.keySet().toArray();
		new TaskThreadPie(keys.length){

			@Override
			public void onStartThread(int threadNo) throws IOException, Exception {
				//		for(Object key : ) {
				Object key = keys[threadNo];
//				List<Server> list = serverTree.get(key, new ArrayList<>());
				BeanLinked list = serverTree.get(key, new BeanLinked());

				for(Object server : list.values()) {
					SshJsch ssh =  new SshJsch((Server) server);
					String s = ssh.execute("pwd -LP");
					if(ssh.getResult().getIsOk() == 1){
						res.add(ssh.toString() );

					}else
						res1.add(ssh.toString() );
				}
//		}
			}
		}.setThreadSize(10).start();

		Tools.formatOut(res);
		Tools.out("----------------error -----------");

		Tools.formatOut(res1);

	}
//	配置文件 dubbo配置 port zookeeper
//	oracle配置
//	udsp爬取route admin
	public SshMgr showAll(List<String> listName) {
		StringBuffer sbt = new StringBuffer();


		for(String name : listName) {
			BeanLinked list = serverTree.get(name, new BeanLinked());
			Object[] keys = list.keySet().toArray();
			sbt.append("\n\n");
			sbt.append("#############" + name + "\t#################").append("\n");

			new TaskThreadPie(keys.length) {
				@Override
				public void onStartThread(int threadNo) throws IOException, Exception {
					Object key = keys[threadNo];
					Server server = list.get(key, new Server());
					sbt.append("\t" + key + "\t" + server.getIp() + "\t" + server.getId() + "/" + server.getPwd() + "\t" + server.toSsh() ).append("\n");

					if(server.getName().equalsIgnoreCase(obcpweb)){
						SshJsch sshJschZook = getDubboZookeeperIp(server);
						SshJsch sshJschPort = getDubboZookeeperPort(server);
						sbt.append("\t\tzookeeper:" + sshJschZook.getResult().toRes() + " port:" + sshJschPort.getResult().toRes()).append("\n");
					}else if(server.getName().equalsIgnoreCase(obcprhdal)){
						SshJsch sshJschZook = getDubboZookeeperIp(server);
						SshJsch sshJschPort = getDubboZookeeperPort(server);
						sbt.append("\t\tzookeeper:" + sshJschZook.getResult().toRes() + " port:" + sshJschPort.getResult().toRes()).append("\n");

						sbt.append( new SshJsch(server).execute(" cat /approot1/obcp/rhdal_server/application.properties | grep 'redis.sentinel.nodes\\|redis.password\\|sentinel.master' | grep -v '#' ") ).append("\n");
						sbt.append( new SshJsch(server).execute(" cat /approot1/obcp/rhdal_server/application-sharding.properties | grep  'jdbc:mysql' -A 2 | grep -v '#'  ") ).append("\n");

					}else if(server.getName().equalsIgnoreCase(obcpmsg)){
						SshJsch sshJschZook = getDubboZookeeperIp(server);
						sbt.append("\t\tzookeeper:" + sshJschZook.getResult().toRes()  );
						sbt.append( new SshJsch(server).execute(" cat /approot1/message_server/conf/server.conf | grep 'jdbc:oracle' -A  2 | grep -v '#'  ") ).append("\n");
						sbt.append( new SshJsch(server).execute(" cat /approot1/message_server/conf/server.conf | grep 'redis_' | grep -v '#'  ") ).append("\n");
						sbt.append( new SshJsch(server).execute(" cat /approot1/message_server/conf/server.conf | grep '_port'| grep -v '#'   ") ).append("\n");

					}



				}
			}.setThreadSize(1).start();

		}


		Tools.out("----------------res -----------");
		Tools.out(sbt);
		Tools.out("----------------error -----------");

		return this;
	}
//	dubbo配置一致性 火墙检测
	public SshMgr showDubboZookeeper(List<String> listName){
		List<String> res = new ArrayList<>();

		for(String name : listName) {
			BeanLinked list = serverTree.get(name, new BeanLinked());
			Object[] keys = list.keySet().toArray();

			new TaskThreadPie(keys.length) {
				@Override
				public void onStartThread(int threadNo) throws IOException, Exception {
					Object key = keys[threadNo];
					Server server = list.get(key, new Server());
					SshJsch sshJschZook = getDubboZookeeperIp(server);
					SshJsch sshJschPort = getDubboZookeeperPort(server);
					String line = server.toSsh("") + sshJschZook.getResult().toRes() + " " + sshJschPort.getResult().toRes();
					res.add(line);
				}
			}.setThreadSize(10).start();

			Set<String> zookeeps = new HashSet<>();
			List<String> ports = new ArrayList<>();
			boolean isok = true;
			for(int i = 0; i < keys.length; i++){
				Server server = list.get(keys[i], new Server());
				if(server.getValues().size() > 0) {
					Result result = server.getValue(0);
					if (result.getIsOk() != 1) {
						Tools.out("未正常获取数据", server.getValues().toString());
					} else {
						zookeeps.add(result.getRes());
					}
					if (server.getValues().size() > 1) {
						result = server.getValue(1);
						if (result.getIsOk() != 1) {
							Tools.out("未正常获取数据", server.getValues().toString());
						} else {
							ports.add(result.getRes());
						}
					}
				}
			}
			if(zookeeps.size() > 1){
				Tools.out("zookeeper地址不一致 " + zookeeps.toString());
//				isok = false;
			}
			if(isok){
				String ips = zookeeps.toArray(new String[0])[0];
				for(int i = 0; i < keys.length; i++) {
					Server server = list.get(keys[i], new Server());
					telnet(server, ips);
				}
				Server serverObcpweb = list.get(keys[0], new Server());
				Server serverObcprhdal = list.get(keys[1], new Server());
				Server serverObcpmsg = list.get(keys[2], new Server());

//				122.26.157.220:2181,122.26.157.221:2181,122.26.173.39:2181
			}



		}


		Tools.out("----------------res -----------");

		Tools.formatOut(res);
		Tools.out("----------------error -----------");

		return this;
	}
	void telnet(Server server, String zookeepers){
//		122.26.157.220:2181,122.26.157.221:2181,122.26.173.39:2181
		String ipss[] = zookeepers.split(",");
		List<String> eips = new ArrayList<>();
		for(String ipport : ipss) {
			String[] ipo = ipport.split(":");
			String ip = ipo[0];
			String port = ipo[1];
			SshJsch s = telnet(server, ip, port);
			eips.add(s.getResult().getRes());
		}
		Tools.out(server.getIp(), "error fire", eips );
	}
	SshJsch telnet(Server server, String ip, String port){
		SshJsch ssh =  new SshJsch(server);
		String cmd = "timeout 3 telnet " + ip + " " + port;
		String s = ssh.execute(cmd);
		Tools.out(ssh);
		return  ssh;
	}
	SshJsch getDubboZookeeperIp(Server server){
		SshJsch ssh =  new SshJsch(server);
		String cmd = "";
//				{"obcpweb",  "obcprhdal", 	"obcpmsg",	"obcporacle", "obcpmysql", "obcpredis1", "obcpredis2"
//						, "mccp", "mccporacle", "mccpredis1", "mccpredis2", "mccpihs"};
		if(server.getName().equalsIgnoreCase(obcpweb)){
			cmd = "cat ~/webinf/dubbo-server-config.xml  ";
		}else if(server.getName().equalsIgnoreCase(obcprhdal)){
			cmd = "cat /approot1/rhdal_server/dubbo-message-dal-provider.xml  ";
		}else if(server.getName().equalsIgnoreCase(obcpmsg)){
			cmd = "cat /approot1/message_server/conf/dubbo-message-dal-consumer.xml   ";
		}

		if(cmd.length() != 0) {
			cmd += " | grep zook | grep -v dgz | awk '{print $3}' | awk -F'\"' '{print $2}' ";
			String s = ssh.execute(cmd);
			Tools.out(ssh);
		}

		return ssh;
	}

	SshJsch getDubboZookeeperPort(Server server){
		SshJsch ssh =  new SshJsch(server);
		String cmd = "";
//				{"obcpweb",  "obcprhdal", 	"obcpmsg",	"obcporacle", "obcpmysql", "obcpredis1", "obcpredis2"
//						, "mccp", "mccporacle", "mccpredis1", "mccpredis2", "mccpihs"};
		if(server.getName().equalsIgnoreCase(obcpweb)){
			cmd = "cat ~/webinf/dubbo-server-config.xml  ";
		}else if(server.getName().equalsIgnoreCase(obcprhdal)){
			cmd = "cat /approot1/rhdal_server/dubbo-message-dal-provider.xml  ";
		}else if(server.getName().equalsIgnoreCase(obcpmsg)){
			cmd = "cat /approot1/message_server/conf/dubbo-message-dal-consumer.xml   ";
		}

		if(cmd.length() != 0) {
			cmd += " | grep port | awk  -F'\"' '{print $4}' ";
			String s = ssh.execute(cmd);
			Tools.out(ssh);
		}

		return ssh;
	}
//	/approot1/washome/IBM/WebSphere/AppServer/profiles/obcpServ/bin/startServer.sh obcpserver
//	ln -s /approot1/washome/IBM/WebSphere/AppServer/profiles/obcpServ/installedApps/suse6129Node01Cell/obcp-icbc_war.ear/obcp-icbc.war/WEB-INF webinf
//	cat /approot1/washome/IBM/WebSphere/AppServer/profiles/obcpServ/installedApps/suse6129Node01Cell/obcp-icbc_war.ear/obcp-icbc.war/WEB-INF/dubbo-server-config.xml  ";
//	cat ~/webinf/dubbo-server-config.xml  ";



	static BeanLinked serverTree = new BeanLinked();
	static void addServer(String group, String info, String name, String ip, String id, String pwd, String encode){
//		List<Server> list = serverTree.get(group, new ArrayList<>());
//		serverTree.set(group, list);
//		Server server = new Server(group, info, name, ip, id, pwd, encode);
//		list.add(server);
		BeanLinked list = serverTree.get(group, new BeanLinked());
		serverTree.put(group, list);
		Server server = new Server(group, info, name, ip, id, pwd, encode);
		list.put(name, server);

	}

	static String[] ids = {"wasup",		 "icbcmon", 	"root",		"root", 	"wasup"  };
	static String[] pwds = {"wasup123",  "icbcmon123",	"root123",	"Osroot123", "wasup"  };
	static {
		String group;	//分组
		String encode = "utf-8";

		group = "kf126";
//		obcp
		addServer(group, "", obcpweb, "122.21.189.126", ids[0], pwds[0], encode);//obcpweb
		addServer(group, "", obcprhdal, "122.67.125.160", ids[3], pwds[3], encode);	//obcprhdal
		addServer(group, "", obcpmsg, "122.40.141.46", ids[3], pwds[3], encode); //obcpmsg
		addServer(group, "122.18.109.57:1521/ora11 obcp126/obcp126", obcporacle, "122.18.109.57", ids[1], pwds[1], encode);
		addServer(group, "mysql://122.20.61.34:3306/rhdal126", obcpmysql, "122.20.61.34", ids[1], pwds[1], encode);
		addServer(group, "", obcpredis1, "122.67.125.158", ids[3], pwds[3], encode); //redis1
		addServer(group, "", obcpredis2, "122.67.125.159", ids[3], pwds[3], encode); //redis2
////		mccp
//		addServer(group, "", mccp, "122.23.109.23", ids[4], pwds[4], encode); //mccp
//		addServer(group, "122.23.93.75:1521/mccpdb rhcc/rhcc", mccporacle, "122.23.93.75", ids[1], pwds[1], encode);
//		addServer(group, "", mccpredis1, "122.22.45.144", ids[3], pwds[3], encode); //redis1
//		addServer(group, "", mccpredis2, "122.22.45.39", ids[1], pwds[1], encode);  //redis2


		group = "yc31";
//		obcp
		addServer(group, "", obcpweb, "122.18.109.26", ids[0], pwds[0], encode);//obcpweb
		addServer(group, "", obcprhdal, "122.22.45.148", ids[3], pwds[3], encode);	//obcprhdal
		addServer(group, "", obcpmsg, "122.40.130.17", ids[3], pwds[3], encode); //obcpmsg
		addServer(group, "122.18.109.26:1521/ora11 obcp126/obcp126", obcporacle, "122.18.109.26", ids[1], pwds[1], encode);
		addServer(group, "mysql://122.20.61.34:3306/rhdal126", obcpmysql, "122.20.61.34", ids[1], pwds[1], encode);
		addServer(group, "", obcpredis1, "122.20.61.23", ids[3], pwds[3], encode); //redis1
		addServer(group, "", obcpredis2, "122.20.61.32", ids[3], pwds[3], encode); //redis2
////		mccp
		addServer(group, "", mccp, "122.27.93.37", ids[4], pwds[4], encode); //mccp
		addServer(group, "122.27.93.42:1521/mccpdb rhcc/rhcc", mccporacle, "122.27.93.42", ids[1], pwds[1], encode);
		addServer(group, "", mccporacle, "122.22.45.144", ids[3], pwds[3], encode); //redis1
		addServer(group, "", mccpredis1, "122.27.93.43", ids[1], pwds[1], encode);  //redis2
		addServer(group, "", mccpredis2, "122.27.93.44", ids[1], pwds[1], encode);  //ihs

		group = "cxjc6-obcp29";
		addServer(group, "", obcpweb, "122.20.61.29", ids[0], pwds[0], encode);//obcpweb
		addServer(group, "", obcprhdal, "122.67.93.168", ids[1], pwds[1], encode);	//obcprhdal
		addServer(group, "", obcpmsg, "122.40.130.60", ids[2], pwds[2], encode); //obcpmsg
		addServer(group, "122.18.109.57:1521/ora11 obcp170/obcp170", obcporacle, "122.27.93.144", ids[1], pwds[1], encode);
		addServer(group, "mysql://122.20.61.34:3306/rhdal126", obcpmysql, "122.20.61.34", ids[1], pwds[1], encode);
		addServer(group, "", obcpredis1, "122.67.93.164", ids[1], pwds[1], encode); //redis1
		addServer(group, "", obcpredis2, "122.67.93.165", ids[1], pwds[1], encode); //redis2
//		mccp
		addServer(group, "", mccp, "122.23.109.23", ids[4], pwds[4], encode); //mccp
		addServer(group, "122.23.93.75:1521/mccpdb rhcc/rhcc", mccporacle, "122.23.93.75", ids[1], pwds[1], encode);
		addServer(group, "", mccpredis1, "122.22.45.144", ids[3], pwds[3], encode); //redis1
		addServer(group, "", mccpredis2, "122.22.45.39", ids[1], pwds[1], encode);  //redis2


		group = "cxjc3-obcp8";
		addServer(group, "", obcpweb, "122.20.61.8", ids[0], pwds[0], encode);//obcpweb
		addServer(group, "", obcprhdal, "122.20.61.10", ids[1], pwds[1], encode);	//obcprhdal
		addServer(group, "xxx", obcpmsg, "122.40.130.10", ids[2], pwds[2], encode); //obcpmsg
		addServer(group, "122.18.109.57:1521/ora11 obcp126/obcp126", obcporacle, "122.18.109.57", ids[1], pwds[1], encode);
		addServer(group, "mysql://122.20.61.34:3306/rhdal126", obcpmysql, "122.20.61.34", ids[1], pwds[1], encode);
		addServer(group, "", obcpredis1, "122.20.61.10", ids[1], pwds[1], encode); //redis1
		addServer(group, "", obcpredis2, "122.20.61.11", ids[1], pwds[1], encode); //redis2
////		mccp
//		addServer(group, "", mccp, "122.20.141.167", ids[4], pwds[4], encode); //mccp
//		addServer(group, "122.23.93.75:1521/mccpdb rhcc/rhcc", mccporacle, "122.23.93.75", ids[1], pwds[1], encode);
//		addServer(group, "", mccporacle, "122.18.125.84", ids[3], pwds[3], encode); //redis1
//		addServer(group, "", mccpredis1, "122.20.141.166", ids[1], pwds[1], encode);  //redis2
//		addServer(group, "", mccpredis2, "122.40.141.29", ids[1], pwds[1], encode);  //ihs


		group = "cxjc5-obcp9";
		addServer(group, "", obcpweb, "122.20.61.9", ids[0], pwds[0], encode);//obcpweb
		addServer(group, "", obcprhdal, "122.67.93.81", ids[1], pwds[1], encode);	//obcprhdal
		addServer(group, "114.255.225.50", obcpmsg, "122.40.141.189", ids[2], pwds[2], encode); //obcpmsg
		addServer(group, "122.27.93.143:1521/ora11 obcp170/obcp170", obcporacle, "122.27.93.143", ids[1], pwds[1], encode);
		addServer(group, "mysql://122.20.61.34:3306/rhdal126", obcpmysql, "122.20.61.34", ids[1], pwds[1], encode);
		addServer(group, "", obcpredis1, "122.67.93.61", ids[1], pwds[1], encode); //redis1
		addServer(group, "", obcpredis2, "122.67.93.68", ids[1], pwds[1], encode); //redis2
//		mccp
		addServer(group, "", mccp, "122.20.141.167", ids[4], pwds[4], encode); //mccp
		addServer(group, "122.23.93.75:1521/mccpdb rhcc/rhcc", mccporacle, "122.23.93.75", ids[1], pwds[1], encode);
		addServer(group, "", mccpredis1, "122.18.125.84", ids[3], pwds[3], encode); //redis1
		addServer(group, "", mccpredis2, "122.20.141.166", ids[1], pwds[1], encode);  //redis2
		addServer(group, "", mccpihs, "122.40.141.29", ids[1], pwds[1], encode);  //ihs



		group = "cxjc7-obcp125";
		addServer(group, "", obcpweb, "122.21.189.125", ids[0], pwds[0], encode);//obcpweb
		addServer(group, "", obcprhdal, "122.67.93.169", ids[1], pwds[1], encode);	//obcprhdal
		addServer(group, "114.255.225.38", obcpmsg, "122.40.130.61", ids[2], pwds[2], encode); //obcpmsg
		addServer(group, "122.27.93.145:1521/ora11 obcp171/obcp171", obcporacle, "122.27.93.145", ids[1], pwds[1], encode);
		addServer(group, "mysql://122.20.61.34:3306/rhdal126", obcpmysql, "122.20.61.34", ids[1], pwds[1], encode);
		addServer(group, "", obcpredis1, "122.67.93.166", ids[1], pwds[1], encode); //redis1
		addServer(group, "", obcpredis2, "122.67.93.167", ids[1], pwds[1], encode); //redis2
//		mccp
		addServer(group, "", mccp, "122.23.109.23", ids[4], pwds[4], encode); //mccp
		addServer(group, "122.23.93.75:1521/mccpdb rhcc/rhcc", mccporacle, "122.23.93.75", ids[1], pwds[1], encode);
		addServer(group, "", mccpredis1, "122.22.45.144", ids[3], pwds[3], encode); //redis1
		addServer(group, "", mccpredis2, "122.22.45.39", ids[1], pwds[1], encode);  //redis2

		group = "mc38";
//		obcp
		addServer(group, "", obcpweb, "83.21.238.38", ids[0], pwds[0], encode);//obcpweb


	}
	static{
		Tools.out(JsonFastUtil.toStringFormat(serverTree));
	}
//<!DOCTYPE NETSCAPE-Bookmark-file-1>
//<!-- This is an automatically generated file. It will be read and overwritten. DO NOT EDIT! -->
//<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
//<TITLE>Bookmarks</TITLE>
//<H1>Bookmarks</H1>
//<DL><p>
////    <DT><H3 ADD_DATE="1583377216" LAST_MODIFIED="1586857085" PERSONAL_TOOLBAR_FOLDER="true">书签栏</H3>
////    <DL><p>
//        <DT><H3 ADD_DATE="1583803566" LAST_MODIFIED="1583803566">125</H3>
//        <DL><p>
//            <DT><A HREF="http://122.21.189.125:9080/index4icbc_pc.jsp" ADD_DATE="1583803566" ICON="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAADDklEQVQ4jV2TTWgcdRjGf/+dmZ2d2a/upknMbmxdkhrT0KK51Ki1FOKhBG9FBcHVi4ggNDfRkzctStGTWEREBA8ihbYEpCrWQhWs1ihNtmKCm4/afOzOzs7sztfO30MTCT7wwu/wvu9zeR7B/3TmJ6ZKhYmXK5kTpw21P3MsgjDachz76pfN1u8fHXuc63v3xV4eWH7lk3eKT1Unc+ss8S02m7zgJ5GiD5STtO9KGgtvf1qZXnkJkACJ3eOHls5cfLdcrX6Yq3OFC3g0UDBBmiAb0PmMnHeJ8sNnq0vfTVzcNU8AJOafnX21NDNzVrtCTMCvTJNnkCwlEEP3xj4ARGjWB5TGX5uZ/yo9C6BwalR/+pm3Li8XpR6yST8mBn0MYbCfJBWZB99EdLV7nnGEur1OaB2cWrxRO6dw/s3Tp0aOPv99EvoZYxCVAWJ6DDNGj3KcRzgmCAGOD6sWbN5CGZzSK8qPf6ik1EnP0EiTIEUCXY6SRZLHwhFJiHxkbEHDhnYCoeigpTFUj5TOpGrcLgp5X5pCVyWvaWQ1lbSmkUsO0RUhWDVi10W2s0iniGwH0I4gTuHfKQo1/c39qPoAuW2BkRPo2QSkQv6JbUyZwY0OYnYWCTWFuJMkbprEzTz4Kv5qHjV06jW7cxRPZgjimLW1ZTZih/58jiPWfprJMoqrk2r8SZToEOwLoefTbdvc6YQ1QWZw4Pjr1/5SkJmtlUWKOYNCweCEO065V+QRRSL8TUbdN2C7hWw2CEey3NYDZ/r9uRGFwA3qG5GZNRLHLTdGCtjXzTGxfIiuByVb4DbTqM7fpJs3EM0Wnh1z89L1986H4ZwCxGz8PN9KHZ4q9hUOOFHEE7ceRbQNPA+GWxA0YatxmGH/YwIFFhYWrp2027NAW9mJcre3+sPXlvbA+Kg7dujBlSP4HYHfgYoNQQsCV6PRDajXP597zHJeBO4CqDsPJLAmfzlXjcnM/CaSzxX0sSfVZMpYi8CNvO56WLt6U258cQHvMrC1W6a9bdyVBuSBvh0GCIFtoLXD/+lfXuNMjEv2l9UAAAAASUVORK5CYII=">login</A>
//            <DT><A HREF="http://122.21.189.125:9080/desktop/index.html#/main/intact/home" ADD_DATE="1583803566">index</A>
//            <DT><A HREF="http://122.21.189.125:9080/sy/comm/page/page.jsp?rhDevFlag=true" ADD_DATE="1583803566" ICON="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAADDklEQVQ4jV2TTWgcdRjGf/+dmZ2d2a/upknMbmxdkhrT0KK51Ki1FOKhBG9FBcHVi4ggNDfRkzctStGTWEREBA8ihbYEpCrWQhWs1ihNtmKCm4/afOzOzs7sztfO30MTCT7wwu/wvu9zeR7B/3TmJ6ZKhYmXK5kTpw21P3MsgjDachz76pfN1u8fHXuc63v3xV4eWH7lk3eKT1Unc+ss8S02m7zgJ5GiD5STtO9KGgtvf1qZXnkJkACJ3eOHls5cfLdcrX6Yq3OFC3g0UDBBmiAb0PmMnHeJ8sNnq0vfTVzcNU8AJOafnX21NDNzVrtCTMCvTJNnkCwlEEP3xj4ARGjWB5TGX5uZ/yo9C6BwalR/+pm3Li8XpR6yST8mBn0MYbCfJBWZB99EdLV7nnGEur1OaB2cWrxRO6dw/s3Tp0aOPv99EvoZYxCVAWJ6DDNGj3KcRzgmCAGOD6sWbN5CGZzSK8qPf6ik1EnP0EiTIEUCXY6SRZLHwhFJiHxkbEHDhnYCoeigpTFUj5TOpGrcLgp5X5pCVyWvaWQ1lbSmkUsO0RUhWDVi10W2s0iniGwH0I4gTuHfKQo1/c39qPoAuW2BkRPo2QSkQv6JbUyZwY0OYnYWCTWFuJMkbprEzTz4Kv5qHjV06jW7cxRPZgjimLW1ZTZih/58jiPWfprJMoqrk2r8SZToEOwLoefTbdvc6YQ1QWZw4Pjr1/5SkJmtlUWKOYNCweCEO065V+QRRSL8TUbdN2C7hWw2CEey3NYDZ/r9uRGFwA3qG5GZNRLHLTdGCtjXzTGxfIiuByVb4DbTqM7fpJs3EM0Wnh1z89L1986H4ZwCxGz8PN9KHZ4q9hUOOFHEE7ceRbQNPA+GWxA0YatxmGH/YwIFFhYWrp2027NAW9mJcre3+sPXlvbA+Kg7dujBlSP4HYHfgYoNQQsCV6PRDajXP597zHJeBO4CqDsPJLAmfzlXjcnM/CaSzxX0sSfVZMpYi8CNvO56WLt6U258cQHvMrC1W6a9bdyVBuSBvh0GCIFtoLXD/+lfXuNMjEv2l9UAAAAASUVORK5CYII=">conf</A>
//        </DL><p>
////    </DL><p>
//</DL><p>
	static void makeChromeBookmarks(){
		StringBuffer sbt = new StringBuffer();


		StringBuffer sb = new StringBuffer();
		sb.append("<!DOCTYPE NETSCAPE-Bookmark-file-1> ").append("\n");
		sb.append("<!-- This is an automatically generated file. It will be read and overwritten. DO NOT EDIT! --> ").append("\n");
		sb.append("<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\"> ").append("\n");
		sb.append("<TITLE>Bookmarks</TITLE> ").append("\n");
		sb.append("<H1>Bookmarks</H1> ").append("\n");
		sb.append("<DL><p>  ").append("\n");
		Object[] keys = serverTree.keySet().toArray();
		new TaskThreadPie(keys.length){

			@Override
			public void onStartThread(int threadNo) throws IOException, Exception {
				Object name = keys[threadNo];
				BeanLinked list = serverTree.get(name, new BeanLinked());
				sbt.append("\n\n");
				sbt.append("#############" + name + "\t#################").append("\n");

				sb.append("	<DT><H3 ADD_DATE=\"1583803566\" LAST_MODIFIED=\"1583803566\">" + name + "</H3> ").append("\n");
				sb.append("	<DL><p> ").append("\n");

				Object[] keys = list.keySet().toArray();
				for(Object key : keys) {
					Server server = list.get(key, new Server());
					String ext = "_" + server.getIp();

					sbt.append("\t" + key + "\t" + server.getIp() + "\t" + server.getName() + "/" + server.getPwd() + "\t" + server.toSsh() ).append("\n");


//					ICON=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAADDklEQVQ4jV2TTWgcdRjGf/+dmZ2d2a/upknMbmxdkhrT0KK51Ki1FOKhBG9FBcHVi4ggNDfRkzctStGTWEREBA8ihbYEpCrWQhWs1ihNtmKCm4/afOzOzs7sztfO30MTCT7wwu/wvu9zeR7B/3TmJ6ZKhYmXK5kTpw21P3MsgjDachz76pfN1u8fHXuc63v3xV4eWH7lk3eKT1Unc+ss8S02m7zgJ5GiD5STtO9KGgtvf1qZXnkJkACJ3eOHls5cfLdcrX6Yq3OFC3g0UDBBmiAb0PmMnHeJ8sNnq0vfTVzcNU8AJOafnX21NDNzVrtCTMCvTJNnkCwlEEP3xj4ARGjWB5TGX5uZ/yo9C6BwalR/+pm3Li8XpR6yST8mBn0MYbCfJBWZB99EdLV7nnGEur1OaB2cWrxRO6dw/s3Tp0aOPv99EvoZYxCVAWJ6DDNGj3KcRzgmCAGOD6sWbN5CGZzSK8qPf6ik1EnP0EiTIEUCXY6SRZLHwhFJiHxkbEHDhnYCoeigpTFUj5TOpGrcLgp5X5pCVyWvaWQ1lbSmkUsO0RUhWDVi10W2s0iniGwH0I4gTuHfKQo1/c39qPoAuW2BkRPo2QSkQv6JbUyZwY0OYnYWCTWFuJMkbprEzTz4Kv5qHjV06jW7cxRPZgjimLW1ZTZih/58jiPWfprJMoqrk2r8SZToEOwLoefTbdvc6YQ1QWZw4Pjr1/5SkJmtlUWKOYNCweCEO065V+QRRSL8TUbdN2C7hWw2CEey3NYDZ/r9uRGFwA3qG5GZNRLHLTdGCtjXzTGxfIiuByVb4DbTqM7fpJs3EM0Wnh1z89L1986H4ZwCxGz8PN9KHZ4q9hUOOFHEE7ceRbQNPA+GWxA0YatxmGH/YwIFFhYWrp2027NAW9mJcre3+sPXlvbA+Kg7dujBlSP4HYHfgYoNQQsCV6PRDajXP597zHJeBO4CqDsPJLAmfzlXjcnM/CaSzxX0sSfVZMpYi8CNvO56WLt6U258cQHvMrC1W6a9bdyVBuSBvh0GCIFtoLXD/+lfXuNMjEv2l9UAAAAASUVORK5CYII="
					if(key.equals(obcpweb)  ){	//obcpweb
						sb.append("		<DT><A HREF=\"http://" + server.getIp() + ":9080/index4icbc_pc.jsp\" ADD_DATE=\"1583803566\">" + server.getName() + "_login" + ext + "</A> ").append("\n");
						sb.append("		<DT><A HREF=\"http://" + server.getIp() + ":9080/desktop/index.html#/main/intact/home\" ADD_DATE=\"1583803566\">"+ server.getName() + "_index" + ext + "</A> ").append("\n");
						sb.append("		<DT><A HREF=\"http://" + server.getIp() + ":9080/sy/comm/page/page.jsp?rhDevFlag=true\" ADD_DATE=\"1583803566\">"+ server.getName() + "_conf" + ext + "</A> ").append("\n");
						sb.append("		<DT><A HREF=\"http://" + server.getIp() + ":9080/index_mb.html\" ADD_DATE=\"1583803566\">"+ server.getName() + "_mb" +  ext + "</A> ").append("\n");
					}else if(key.equals(mccp)){	//obcpweb
						sb.append("		<DT><A HREF=\"http://" + server.getIp() + ":9080/index4icbc_pc.jsp\" ADD_DATE=\"1583803566\">" + server.getName() + "_login" + ext +  "</A> ").append("\n");
						sb.append("		<DT><A HREF=\"http://" + server.getIp() + ":9080/desktop/index.html#/main/intact/home\" ADD_DATE=\"1583803566\">"+ server.getName() + "_index" + ext +  "</A> ").append("\n");
						sb.append("		<DT><A HREF=\"http://" + server.getIp() + ":9080/sy/comm/page/page.jsp?rhDevFlag=true\" ADD_DATE=\"1583803566\">"+ server.getName() + "_conf" +  ext + "</A> ").append("\n");
						sb.append("		<DT><A HREF=\"http://" + server.getIp() + ":9080/testEvent.jsp\" ADD_DATE=\"1583803566\">"+ server.getName() + "_red" +  ext + "</A> ").append("\n");
					}

				}
				sb.append("	</DL><p>  ").append("\n");
			}
		}.setThreadSize(1).start();

		BeanLinked beanLinked = new BeanLinked();
		beanLinked.put("http://scm-sh.sdc.cs.icbc:8080/?locale=zh#/settings/ssh-keys", "git");
		beanLinked.put("http://adlm-jira.icbc:8080/secure/Dashboard.jspa", "jira");
		beanLinked.put("http://adlm-jira.icbc:8080/issues/?jql=project%20%3D%20SH3%20AND%20status%20in%20(%E5%BE%85%E5%90%AF%E5%8A%A8%2C%20%E5%A4%84%E7%90%86%E4%B8%AD%2C%20%E6%B5%8B%E8%AF%95%E5%88%86%E6%9E%90%E5%AE%8C%E6%88%90%2C%20%E7%BC%96%E5%86%99%E4%B8%AD%2C%20%E6%B5%8B%E8%AF%95%E8%AE%BE%E8%AE%A1%E5%AE%8C%E6%88%90%2C%20%E6%A1%88%E4%BE%8B%E7%BC%96%E5%86%99%E4%B8%AD%2C%20%E6%A1%88%E4%BE%8B%E7%BC%96%E5%86%99%E5%AE%8C%E6%88%90%2C%20%E6%B5%81%E7%A8%8B%E6%B5%8B%E8%AF%95%E5%AE%8C%E6%88%90%2C%20%E8%AE%BE%E8%AE%A1%E4%B8%AD%2C%20%E8%AE%BE%E8%AE%A1%E5%AE%8C%E6%88%90%2C%20%E7%BC%96%E7%A0%81%E4%B8%AD%2C%20%22%E7%BC%96%E7%A0%81%E8%87%AA%E6%B5%8B%E5%AE%8C%E6%88%90%2F%E5%BE%85%E6%B5%8B%E8%AF%95%22%2C%20%E9%9C%80%E6%B1%82%E5%88%86%E6%9E%90%E4%B8%AD%2C%20%E5%8A%9F%E8%83%BD%E6%B5%8B%E8%AF%95%E4%B8%AD%2C%20%E9%9C%80%E6%B1%82%E5%BE%85%E5%88%86%E6%9E%90%2C%20%E6%B5%81%E7%A8%8B%E6%B5%8B%E8%AF%95%E4%B8%ADs)%20AND%20%E5%9B%A2%E9%98%9F%EF%BC%88%E4%B8%8A%E6%B5%B7%EF%BC%89%20%3D%20%E4%B8%8A%E6%B5%B7%E4%B8%89%E9%83%A8-%E5%B7%A5%E9%93%B6e%E5%8A%9E%E5%85%AC%E4%B8%80%E7%BB%84%20AND%20assignee%20%3D%20%22555022795%22", "jiram");
		beanLinked.put("file:///D:/my/sg-cph/sg/automachine.html", "sg");
		beanLinked.put("http://udsp.sdc.cs.icbc/icbc/usp/cms/gggl/detail.shtml?noticeId=1485", "udsp-dsf");

		for(Object key : beanLinked.keySet()){
			String name = beanLinked.get(key, "");
			sb.append("		<DT><A HREF=\"" + key + "\" ADD_DATE=\"1583803566\">"+ name + "</A> ").append("\n");
		}

		sb.append("</DL><p> ").append("\n");
		Tools.out(sb);
		Tools.out(sbt);
		FileUtil.saveAs(sb.toString(), "bootmark_auto.html", false);
		FileUtil.saveAs(sbt.toString(), "pwd.c", false);

	}




	 
}
