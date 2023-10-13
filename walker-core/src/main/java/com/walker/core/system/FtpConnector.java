package com.walker.core.system;

import com.walker.core.aop.FunArgsReturn;
import com.walker.core.mode.Error;
import com.walker.core.mode.ResponseO;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * ftp 连接器
 * 两天持续下载导致连接数满 buffer不足问题分析
 */
@Data
@Accessors(chain = true)
public class FtpConnector {
	private final static Logger log = LoggerFactory.getLogger(FtpConnector.class);
	private static final int BUFFER_SIZE = 1024 * 1024 * 1;

	IpModel ipModel;
	int timeout = 5000;
	int port = 21;

	boolean moreLog = false;
	AtomicInteger count = new AtomicInteger(0);

	public FtpConnector(IpModel ipModel){
		this.ipModel = ipModel;
	}


	public FTPClient getFTPClient() throws Exception {
		FTPClient ftpClient = null;
		ftpClient = new FTPClient();
		ftpClient.setConnectTimeout(timeout);
		ftpClient.setControlEncoding(ipModel.getEncode());

		ftpClient.connect(ipModel.getIp(), port);
		actionRes(ftpClient, "connect");
		ftpClient.login(ipModel.getId(), ipModel.getPwd());
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
	public <T> T doFtpClient(FunArgsReturn<FTPClient, T> fun)  {
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
				log.error("ftp logout error " + this + " " + e.getMessage(), e);
			}finally{
				if (ftp.isConnected()) {
					try {
						ftp.disconnect();
					} catch (IOException ioe) {
						log.error("ftp disconnect error " + this + " " + ioe.getMessage(), ioe);
					}
				}
			}
	}


	/**
	 * 下载目录或文件
	 * @param fromPath /home/walker   /home/walker/01.txt
	 * @param toPath D:/home/walker   D:/home/walker/   D:/home/walker/01.txt
	 */
	public ResponseO<Boolean> download(String fromPath, String toPath) {
		return doFtpClient(ftpClient -> {
			long st = System.currentTimeMillis();
			ResponseO<Boolean> result = new ResponseO<>();
			OutputStream out = null;
			try {
				out = new FileOutputStream(toPath);
				// 跳转到文件目录
//					ftpClient.cwd(fromPathDir);
//					actionRes(ftpClient, "cwd");
				ftpClient.retrieveFile(fromPath, out);
				actionRes(ftpClient, "retrieveFile");
				result.setRes(true);
			}catch (Exception e){
				result.setSuccess(false).setError(new Error(Error.LEVEL_ERROR, result.getTip(), e.getMessage()));
			}finally {
				result.setCost(System.currentTimeMillis() - st);
				if(out != null) {
					try {
						out.flush();
						out.close();
					} catch (IOException e) {
						log.error(e.getMessage(), e);
					}
				}
			}
			return result;
		});
	}

	public ResponseO<Boolean> upload(String fromPath,String toPath){
		return doFtpClient(ftpClient -> {
			long st = System.currentTimeMillis();
			ResponseO<Boolean> result = new ResponseO<>();
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
				result.setRes(true);
			}catch (Exception e){
				result.setSuccess(false).setError(new Error(Error.LEVEL_ERROR, result.getTip(), e.getMessage()));
			}finally {
				result.setCost();
			}
			return result;
		} );
	}





}
