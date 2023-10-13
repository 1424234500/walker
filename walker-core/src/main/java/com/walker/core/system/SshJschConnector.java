package com.walker.core.system;

import com.jcraft.jsch.*;
import com.walker.core.mode.Error;
import com.walker.core.mode.ResponseO;
import com.walker.core.util.FileUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.InputStream;
import java.util.Properties;


/**
 * ssh工具 jsch实现
 * 支持 sftp
 */
@Data
@Accessors(chain = true)
public class SshJschConnector {
	IpModel ipModel;
	int timeout = 5000;

	public SshJschConnector(IpModel ipModel) {
		this.ipModel = ipModel;
	}

	/**
	 * 远程执行shell脚本或者命令
	 *
	 * @param cmd 即将执行的命令
	 *            命令执行完后返回的结果值
	 */
	public ResponseO<String> execute(String cmd) {
		long st = System.currentTimeMillis();
		ResponseO<String> result = new ResponseO<>();
		Session session = null;
		ChannelExec channelExec = null;
		try {
			session = getSession();
			channelExec = (ChannelExec) session.openChannel("exec");   //exec
			channelExec.setCommand(cmd);
			int status = channelExec.getExitStatus();
			result.setTip(status + "");
			channelExec.connect();
			InputStream inputStream = channelExec.getInputStream();
			result.setRes(FileUtil.readByLines(inputStream, null, this.ipModel.getEncode()));
		} catch (Exception e) {
			result.setSuccess(false);
			result.setError(new Error(Error.LEVEL_ERROR, result.getTip(), e.getMessage()));
		} finally {
			if (channelExec != null && !channelExec.isClosed()) {
				channelExec.disconnect();
			}
			if (session != null && session.isConnected()) {
				session.disconnect();
			}
			result.setCost(System.currentTimeMillis() - st);
		}
		return result;
	}

	public ResponseO<Integer> upload(String pathFrom, String pathTo) {
		long st = System.currentTimeMillis();
		ResponseO<Integer> result = new ResponseO<>();
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
			result.setSuccess(false);
			result.setError(new Error(Error.LEVEL_ERROR, result.getTip(), e.getMessage()));
		} finally {
			if (channelSftp != null && !channelSftp.isClosed()) {
				channelSftp.disconnect();
			}
			if (session != null && session.isConnected()) {
				session.disconnect();
			}
			result.setCost(System.currentTimeMillis() - st);
		}
		return result;
	}

	public ResponseO<Integer> download(String pathFrom, String pathTo) {
		long st = System.currentTimeMillis();
		ResponseO<Integer> result = new ResponseO<>();
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
			result.setSuccess(false);
			result.setError(new Error(Error.LEVEL_ERROR, result.getTip(), e.getMessage()));
		} finally {
			if (channelSftp != null && !channelSftp.isClosed()) {
				channelSftp.disconnect();
			}
			if (session != null && session.isConnected()) {
				session.disconnect();
			}
			result.setCost(System.currentTimeMillis() - st);
		}
		return result;
	}
	public ResponseO<String> telnet(String port){
		String cmd = "timeout 3 telnet " + this.ipModel.getIp() + " " + port;
		return execute(cmd);
	}


	private Session getSession() throws JSchException {
		Session session = new JSch().getSession(this.ipModel.getId(), this.ipModel.getIp());
		Properties properties = new Properties();
		properties.put("StrictHostKeyChecking", "no");  //跳过公钥确认
		properties.put("PreferredAuthentications", "publickey,keyboard-interactive,password"); //跳过 Kerberos username 身份验证提示
		session.setConfig(properties);
		session.setPassword(this.ipModel.getPwd());
		session.connect(timeout);
		return session;
	}

}
