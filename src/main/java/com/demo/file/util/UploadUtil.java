package com.demo.file.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import sun.misc.BASE64Decoder;

import com.demo.file.exception.ComException;
/**
 * 文件上传到项目目录
 * @author xueyongjun
 *
 */
public class UploadUtil {
	
	private static UploadUtil instance;
	
	public static String schema="http://";

	public static String path="pc/student/js/data/";
	
	public String domain;
	
	public String webRoot;
	
	private UploadUtil(){
		String startType=PropertiesHolderUtil.getInstance().getProperty("startType");
		String projectType=PropertiesHolderUtil.getInstance().getProperty("projectType");
		if(projectType != null){
			
		}
		if(startType==null || startType.equals("2")){
			//在tomcat中用war包启动服务
			String dir=System.getProperty("user.dir");
			String proName=getProName();
			webRoot=dir.substring(0, dir.length()-4)+"\\webapps\\"+proName+"\\";
		}else{
			//在eclipse中启动服务
			webRoot=System.getProperty("user.dir")+"\\src\\main\\webapp\\";
		}
		domain=PropertiesHolderUtil.getInstance().getProperty("projectDomain");
	}
	
	private String getProName() {
		String projectDomain=PropertiesHolderUtil.getInstance().getProperty("projectDomain");
		int index=0;
		if(projectDomain.endsWith("\\") ){
			projectDomain=projectDomain.substring(0, projectDomain.length()-1);
			index=projectDomain.lastIndexOf("\\");
		}else if(projectDomain.endsWith("/")){
			projectDomain=projectDomain.substring(0, projectDomain.length()-1);
			index=projectDomain.lastIndexOf("/");
		}
		String projectName=projectDomain.substring(index+1);
		
		return projectName;
	}
	
	public static UploadUtil getInstance() {
		if (instance == null) {
			instance = new UploadUtil();
		}
		return instance;
	}
	
	/**
	 * 自定义文件目录
	 * @param content
	 * @param fileName
	 * @param path  可以在浏览器访问的路径
	 * @return
	 * @throws IOException
	 */
	public String saveFile(String content,String fileName,String path) throws IOException{
		String dirPath=webRoot+path; //绝对路径
		File dir=new File(dirPath);
		if(!dir.exists()){
			dir.mkdirs();
		}
		String filePath=dirPath+fileName;
		File f=new File(filePath);
		if(f.createNewFile()){
			Writer w=new FileWriter(filePath);
			BufferedWriter out=new BufferedWriter(w);
			out.write(content);
			out.flush();
			out.close();
		}
		String url=schema+domain+path+fileName;
		return url;
	}
	
	/**
	 * (模拟US服务文件上传的路径)
	 * 默认目录             比如：/upload/2018/08/27/abc.js
	 * @param content
	 * @param fileName
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public String saveFile(String content,String fileName) throws IOException{
//		String root=getWebappRoot();
		String dateStr=dateToStr(new Date());
		return saveFile(content,fileName,"/upload/"+dateStr);
	}
	
	private String dateToStr(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
        return sdf.format(date);
	}

	public static void main(String[] args) throws Exception {
//		System.out.println(FileUtil.class.getResource("/").toString().substring(6));
//		System.out.println(FileUtil.class.getResource(""));
//		System.out.println(FileUtil.class.getClassLoader().getResource("").getPath());
		
/*		//获取项目目录
		System.out.println(System.getProperty("user.dir"));
		
		String url=UploadUtil.getInstance().saveFile("abc", "test.js", "\\2018\\");
		System.out.println(url);
		url=UploadUtil.getInstance().saveFile("abc", "test.js");
		System.out.println(url);
		
		String s="abc/bbb/ee/test.js";
		String [] str=s.split("ee");
		for(int i=0;i<str.length;i++){
			System.out.println(str[i]);
		}*/
	
//		UploadUtil.getInstance().doesFileExsit("http://127.0.0.1:4075/staticDatas/402888ea656996990165699870c30000.js");
		
		uploadToLocal_test();
	}
	
	private static void uploadToLocal_test() throws Exception {
		File f=new File("F:/en.txt");
		BufferedReader r=new BufferedReader(new FileReader(f));
		String ss="";
		String s="";
		while((s=r.readLine())!=null){
			ss+=s;
		}
		
		byte [] b=decode(ss);
		String url=UploadUtil.getInstance().uploadToLocal(b, "jpg");
		System.out.println(url);
	}
	
	private static byte[] decode(String imgBase64) {
		BASE64Decoder decoder = new BASE64Decoder();
	    byte[] bytes=null;
	    try {
			bytes = decoder.decodeBuffer(imgBase64);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ComException("解码失败");
		}
		return bytes;
	}
	
	/**
	 * 根据文件名拼接文件url
	 * @param fileName
	 * @return
	 */
	public String getWebappPath(String fileName) {
		String uploadPath=PropertiesHolderUtil.getInstance().getProperty("uploadPath");
		return schema+domain+uploadPath+fileName;
	}
	
	/**
	 * 根据文件url获取文件存放路径，并判断uploadPath这个目录下有没有这个文件
	 * @param path
	 * @return
	 */
	public boolean doesFileExsit(String path) {
		String uploadPath=PropertiesHolderUtil.getInstance().getProperty("uploadPath");
		String str[]=path.split(uploadPath);
		String filePath="";
		if(str.length>1){
			filePath=webRoot+uploadPath+str[1];
		}
		File f=new File(filePath);
		return f.exists();
	}
	
	@Deprecated
	public String saveFile(MultipartFile mfile) throws IOException {
		InputStream in=mfile.getInputStream();
		String fileName=mfile.getOriginalFilename();
		
		//1.获取文件格式
		String suf="";
		if(fileName.split("\\.").length>1){
			suf="."+fileName.split("\\.")[1];
		}else{
			suf="";
		}
		
		//2.生成新的文件名
		String newName=UUID.randomUUID().toString().replaceAll("-", "")+suf;
		
		String relativeDirPath=getRelativeDirPath();  //   /upload/2018/08/28
		String absoluteDirPath=getAbsoluteDirPath(relativeDirPath);//   /upload/2018/08/28
		File dir=new File(absoluteDirPath);
		if(!dir.exists()){
			dir.mkdirs();
		}
		String filePath=absoluteDirPath+newName;
		File f=new File(filePath);
		
		OutputStream os = new FileOutputStream(f);
		int bytesRead = 0;
		byte[] buffer = new byte[8192];
		while ((bytesRead = in.read(buffer, 0, 8192)) != -1) {
			os.write(buffer, 0, bytesRead);
		}
		os.close();
		in.close();
		
		String url=domain+relativeDirPath+newName;
		return url;
	}

	private String getAbsoluteDirPath(String relativePath) {
		return webRoot+relativePath; //绝对路径;
	}

	private String getRelativeDirPath() {
		return "upload/"+dateToStr(new Date());//   /upload/2018/08/28
	}

	public String uploadToLocal(byte[] bytes, String imgFormat){
		String dirPath="upload/"+dateToStr(new Date());
		File dir=new File(webRoot+dirPath);
		if(!dir.exists()){
			dir.mkdirs();
		}
		String fileName=UUID.randomUUID().toString().replaceAll("-", "")+"."+imgFormat;
		File file=new File(webRoot+dirPath+fileName);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				throw new ComException("上传失败");
			}
		}
		
		BufferedOutputStream bos=null;
		OutputStream os=null;
		try {
			os=new FileOutputStream(file);
			bos=new BufferedOutputStream(os);
			bos.write(bytes);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ComException("上传失败");
		}finally{
			if(os != null){
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(bos != null){
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		String url=domain+dirPath+fileName;
		return url;
	}
	
	/**
	 * 尽量用原地址中的内容，以方便在出现问题时进行追溯
	 * @param url_old
	 * @return
	 */
	public String downloadToLocal(String url_old) {
		String imgFormat=getFormat(url_old);
		
		//1.创建文件及目录--------
		String domain_old="exam.mvwchina.com";//这里是死的
		if(!url_old.contains(domain_old)){
			throw new ComException("地址["+url_old+"]错误");
		}
		int start_dir=url_old.indexOf(domain_old)+domain_old.length()+1;
		
		int end_dir=url_old.lastIndexOf("/")+1;
		String dirPath=url_old.substring(start_dir, end_dir);
		File dir=new File(webRoot+dirPath);
		if(!dir.exists()){
			dir.mkdirs();
		}
		
		String fileName=url_old.substring(end_dir);
		File file=new File(webRoot+dirPath+fileName);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				throw new ComException("上传失败");
			}
		}
		
		//1.创建文件及目录--------
		write(url_old,file);
		
		String url=domain+dirPath+fileName;
		return url;
	}
	
	private void write(String url_old,File file) {
		URL url;
		DataInputStream dis=null;
		FileOutputStream fos=null;
		try {
			url = new URL(url_old);
			//打开网络输入流
			dis = new DataInputStream(url.openStream());
			//建立一个新的文件
			fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int length;
			//开始填充数据
			while((length = dis.read(buffer))>0){
				fos.write(buffer,0,length);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ComException("图片["+url_old+"]下载失败");
		}finally{
			if(dis != null){
				try {
					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
		}
	}

	private String getFormat(String url_old) {
		String []s=url_old.split("\\.");
		String format=s[s.length-1];
		return format;
	}
}
