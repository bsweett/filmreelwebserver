package com.class3601.social.actions;

import java.io.File;
import java.io.FileOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;

import org.apache.commons.io.FileUtils;
import com.opensymphony.xwork2.ActionSupport;

public class FileUploadAction extends ActionSupport implements ServletRequestAware {
	 
	private File fileUpload;
	private String fileUploadContentType;
	private String fileUploadFileName;
<<<<<<< HEAD
	private FileOutputStream outputStream;
=======
	private HttpServletRequest request;
>>>>>>> bee38900aca5c8722b4963ac4ccf5a66f03ee796
 
	public String getFileUploadContentType() {
		return fileUploadContentType;
	}
 
	public void setFileUploadContentType(String fileUploadContentType) {
		this.fileUploadContentType = fileUploadContentType;
	}
 
	public String getFileUploadFileName() {
		return fileUploadFileName;
	}
 
	public void setFileUploadFileName(String fileUploadFileName) {
		this.fileUploadFileName = fileUploadFileName;
	}
 
	public File getFileUpload() {
		return fileUpload;
	}
 
	public void setFileUpload(File fileUpload) {
		this.fileUpload = fileUpload;
	}
 
<<<<<<< HEAD
	public String execute() throws Exception{

		System.out.println("File uploaded");
=======
	public String execute() throws Exception
	{
		try 
		{	
			String filePath = ServletActionContext.getServletContext().getRealPath("/");
			File fileToCreate =null;
			System.out.println("FilePath = " + filePath);
			fileToCreate = new File(filePath, this.fileUploadFileName);		 
			FileUtils.copyFile(this.fileUpload, fileToCreate);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			addActionError(e.getMessage());
		}
>>>>>>> bee38900aca5c8722b4963ac4ccf5a66f03ee796
		return SUCCESS;
 
	}
 
	public String display() {
		return NONE;
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	private HttpServletRequest getServletRequest() {
		return request;
	}
 
}
