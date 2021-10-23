package com.walker.system;

import com.walker.util.Constant;
import lombok.Data;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * ftp
 * 两天持续下载导致连接数满 buffer不足问题分析
 */
@Data
public class FtpModel {
	private final static Logger log = LoggerFactory.getLogger(FtpModel.class);
	private static final int BUFFER_SIZE = 1024 * 1024 * 1;

	public interface Fun<T>{
		T make(FTPClient ftpClient) throws IOException;
	}

	Server server;
	Result result = new Result();
	int timeout = 5000;
	int port = 21;

	boolean moreLog = false;
	AtomicInteger count = new AtomicInteger(0);

	public FtpModel(String ip, String id, String pwd){
		this.server = new Server(ip, id, pwd);
	}

	@Override
	public String toString() {
		return "FtpModel{" +
				"server=" + server +
				", result=" + result +
				", timeout=" + timeout +
				", port=" + port +
				'}';
	}

	public FTPClient getFTPClient() throws Exception {
		FTPClient ftpClient = null;
		ftpClient = new FTPClient();
		ftpClient.setConnectTimeout(timeout);
		ftpClient.setControlEncoding(server.getEncode());

		ftpClient.connect(server.getIp(), port);
		actionRes(ftpClient, "connect");
		ftpClient.login(server.getId(), server.getPwd());
		actionRes(ftpClient, "login");

		// 设置PassiveMode传输
		ftpClient.enterLocalPassiveMode();
		//设置二进制传输，使用BINARY_FILE_TYPE，ASC容易造成文件损坏
		ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
		//限制缓冲区大小
		ftpClient.setBufferSize(BUFFER_SIZE);
		return ftpClient;
	}

	private void actionRes(FTPClient ftpClient, String info) {
		if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
			throw new RuntimeException("ftp " + count + " " + info + " error " + ftpClient.getReplyString());
		}else if(moreLog){
			log.info("ftp " + count + " " + info + " ok " + ftpClient.getReplyString());
		}
		count.addAndGet(1);
	}

	/**
	 * 回调环绕执行 操作
	 */
	public <T> T doFtpClient(Fun<T> fun)  {
		if(fun != null) {
			FTPClient ftpClient = null;
			try {
				ftpClient = this.getFTPClient();
				return fun.make(ftpClient);
			}catch (Exception e){
				throw new RuntimeException(this.toString(), e);
			}finally {
				closeFTP(ftpClient);
			}
		}
		return null;
	}


	/**
	 * 关闭FTP方法
	 * @param ftp
	 */
	public void closeFTP(FTPClient ftp){
		if(ftp != null)
			try {
				ftp.logout();
			} catch (Exception e) {
				log.error("ftp logout error " + this.toString() + " " + e.getMessage(), e);
			}finally{
				if (ftp.isConnected()) {
					try {
						ftp.disconnect();
					} catch (IOException ioe) {
						log.error("ftp disconnect error " + this.toString() + " " + ioe.getMessage(), ioe);
					}
				}
			}
	}


	/**
	 * 下载目录或文件
	 * @param fromPath /home/walker   /home/walker/01.txt
	 * @param toPath D:/home/walker   D:/home/walker/   D:/home/walker/01.txt
	 */
	public Result download(String fromPath, String toPath) {
		return doFtpClient(new Fun<Result>() {
			@Override
			public Result make(FTPClient ftpClient) throws IOException {
				Result res = new Result();
				res.setKey("download");
				res.addInfo("download " + fromPath + " -> " + toPath);
				OutputStream out = new FileOutputStream(toPath);
				try {
					// 跳转到文件目录
//					ftpClient.cwd(fromPathDir);
//					actionRes(ftpClient, "cwd");
					ftpClient.retrieveFile(fromPath, out);
					actionRes(ftpClient, "retrieveFile");
					res.setRes(true);
				}catch (Exception e){
					res.setIsOk(Constant.ERROR).addInfo(" exception " + e.getMessage());
				}finally {
					result.setCost(System.currentTimeMillis() - result.getTimeStart());
					out.flush();
					out.close();
				}
				return res;
			}
		});
	}

	public Result upload(String fromPath,String toPath){
		return doFtpClient(ftpClient -> {
			Result res = new Result();
			res.setKey("upload");
			res.addInfo("upload " + fromPath + " -> " + toPath);
			try {
				String toPathDir = new File(toPath).getParent();
				//判断FPT目标文件夹时候存在不存在则创建
				if( ! ftpClient.changeWorkingDirectory(toPathDir)){
					ftpClient.mkd(toPathDir);
					actionRes(ftpClient, "mkdir");
				}
				InputStream in = new FileInputStream(new File(fromPath));
//				new String (tempName.getBytes("UTF-8"),"ISO-8859-1")
				ftpClient.storeFile(toPath, in);
				actionRes(ftpClient, "storeFile");
				res.setRes(true);
			}catch (Exception e){
				res.setIsOk(Constant.ERROR).addInfo(" exception " + e.getMessage());
			}finally {
				result.setCost(System.currentTimeMillis() - result.getTimeStart());
			}
			return res;
		} );
	}







	public static void main(String[] argv){
		String[] argvd = new String[]{"4", "127.0.0.1","root","", "/home/test.txt", "D:\\ttt\\"};
		for (int i = 0; i < argv.length; i++) {
			argvd[i] = argv[i];
		}
		argv = argvd;
		String from = argv[4];
		String downdir = argv[5];

		FtpModel server = new FtpModel(argv[1],argv[2],argv[3]);

		server.setMoreLog(true);
		Result res = server.upload(downdir + "test.txt", new File(from).getParent() + File.separator + "test.upload.txt");
		System.out.println("" + res);
		server.setMoreLog(false);
        String to = downdir + "tno-" + "-no-" + "-name-" + new File(from).getName();
        Result resd = server.download(from, to);
        System.out.println("" + resd);
	}


}
