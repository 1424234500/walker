package com.walker.system;

import com.jcraft.jsch.*;
import com.walker.util.Constant;
import com.walker.util.FileUtil;
import lombok.Data;

import java.io.InputStream;
import java.util.Properties;


/**
 * ssh工具 jsch实现
 * 支持 sftp
 */
@Data
public class SshJsch {
	Server server;
	Result result = new Result();
	int timeout = 5000;

	public SshJsch(Server server) {
		this.server = server;
	}

	public SshJsch(String ip, String id, String pwd) {
		this.server = new Server(ip, id, pwd);
	}

	public static void main(String[] argv) {
		String downdir = "D:\\ttt\\";
		SshJsch s = new SshJsch("127.0.0.1", "root", "");
//scp -p root@127.0.0.1:/root/test.txt  D:\ttt\1.txt
		Result res = s.download("/home/walker/test.txt", downdir + "test.txt");
		System.out.println("" + res);
	}

	/**
	 * 远程执行shell脚本或者命令
	 *
	 * @param cmd 即将执行的命令
	 *            命令执行完后返回的结果值
	 */
	public String execute(String cmd) {
		result = new Result();
		result.setKey(cmd);
		Session session = null;
		ChannelExec channelExec = null;
		try {
			session = getSession();
			channelExec = (ChannelExec) session.openChannel("exec");   //exec
			channelExec.setCommand(cmd);
			int status = channelExec.getExitStatus();
			channelExec.connect();
			InputStream inputStream = channelExec.getInputStream();
			result.setRes(FileUtil.readByLines(inputStream, null, this.server.getEncode()));
		} catch (Exception e) {
			result.setIsOk(Constant.ERROR).addInfo(e.getMessage());
		} finally {
			if (channelExec != null && !channelExec.isClosed()) {
				channelExec.disconnect();
			}
			if (session != null && session.isConnected()) {
				session.disconnect();
			}
			result.setCost(System.currentTimeMillis() - result.getTimeStart());
			server.addValues(result);
		}
		return result.getRes();
	}

	public Result upload(String pathFrom, String pathTo) {
		Result result = new Result();
		result.setKey("upload " + pathFrom + " - " + pathTo);
		Session session = null;
		ChannelSftp channelSftp = null;
		try {
			session = getSession();
			channelSftp = (ChannelSftp) session.openChannel("sftp");   //exec
			channelSftp.connect(); //this.timeout
			channelSftp.cd(FileUtil.getFilePath(pathTo));//??
			channelSftp.put(pathFrom, pathTo);
			result.setRes(1);
		} catch (Exception e) {
			result.setIsOk(Constant.ERROR).addInfo(e.getMessage());
		} finally {
			if (channelSftp != null && !channelSftp.isClosed()) {
				channelSftp.disconnect();
			}
			if (session != null && session.isConnected()) {
				session.disconnect();
			}
			result.setCost(System.currentTimeMillis() - result.getTimeStart());
		}
		return result;
	}

	public Result download(String pathFrom, String pathTo) {
		Result result = new Result();
		result.setKey("download " + pathFrom + " - " + pathTo);
		Session session = null;
		ChannelSftp channelSftp = null;
		try {
			session = getSession();
			channelSftp = (ChannelSftp) session.openChannel("sftp");   //exec
			channelSftp.connect();//this.timeout
//			channelSftp.cd(FileUtil.getFilePath(pathTo));//??
			channelSftp.get(pathFrom, pathTo);
			result.setRes(1);
		} catch (Exception e) {
			result.setIsOk(Constant.ERROR).addInfo(e.getMessage());
			e.printStackTrace();
		} finally {
			if (channelSftp != null && !channelSftp.isClosed()) {
				channelSftp.disconnect();
			}
			if (session != null && session.isConnected()) {
				session.disconnect();
			}
			result.setCost(System.currentTimeMillis() - result.getTimeStart());
		}
		return result;
	}

	private Session getSession() throws JSchException {
		Session session = new JSch().getSession(this.server.getId(), this.server.getIp());
		Properties properties = new Properties();
		properties.put("StrictHostKeyChecking", "no");  //跳过公钥确认
		properties.put("PreferredAuthentications", "publickey,keyboard-interactive,password"); //跳过 Kerberos username 身份验证提示
		session.setConfig(properties);
		session.setPassword(this.server.getPwd());
		session.connect(timeout);
		return session;
	}

	@Override
	public String toString() {
		return this.server.toSsh(this.result != null ? this.result.toString() : "");
	}

	public String toIpRes() {
		return this.server.getIp() + " " + this.server.getName() + " " + (this.result != null ? this.result.toRes() : "");
	}


}
