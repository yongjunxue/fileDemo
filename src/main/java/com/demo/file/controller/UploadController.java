package com.demo.file.controller;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.demo.file.exception.ComException;
import com.demo.file.util.UploadUtil;

@Controller
public class UploadController {
	
	@RequestMapping("/upload")
	@ResponseBody
	public String upload(@RequestParam("file") MultipartFile uploadFile , HttpServletRequest request,HttpServletResponse response){
		JSONObject result = new JSONObject();
		
		try{
			String url=UploadUtil.getInstance().saveFile(uploadFile);
			result.put("url", url);
			System.out.println(url);
			result.put("result", "true");
		}catch(ComException e){
			e.printStackTrace();
			result.put("result", "false");
			result.put("msg", e.getMessage());
		}catch(Exception e){
			e.printStackTrace();
			result.put("result", "false");
			result.put("msg", "请求异常，请稍后再试");
		}
        return result.toString();
	}
	
	@RequestMapping("/upload2")
	@ResponseBody
	public String upload2(HttpServletRequest request,HttpServletResponse response){
		CommonsMultipartResolver multipartResolver=new CommonsMultipartResolver(
                request.getSession().getServletContext());
        if(multipartResolver.isMultipart(request))
        {	
            MultipartHttpServletRequest multiRequest=(MultipartHttpServletRequest)request;
            Iterator iter=multiRequest.getFileNames();
            String fileName=null;
            while(iter.hasNext())
            {
                MultipartFile file=multiRequest.getFile(iter.next().toString());
                fileName=file.getOriginalFilename();
                System.out.println(fileName);
                try{
        			String url=UploadUtil.getInstance().saveFile(file);
        			System.out.println(url);
        		}catch(ComException e){
        			e.printStackTrace();
        		}catch(Exception e){
        			e.printStackTrace();
        		}
            }
            
        }
        return null;
	}
}
