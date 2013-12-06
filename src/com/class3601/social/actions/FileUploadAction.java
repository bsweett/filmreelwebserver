package com.class3601.social.actions;

import java.io.File;
import java.io.FileOutputStream;

import com.opensymphony.xwork2.ActionSupport;

public class FileUploadAction extends ActionSupport{
	 
	private File fileUpload;
	private String fileUploadContentType;
	private String fileUploadFileName;
	private FileOutputStream outputStream;
 
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
 
	public String execute() throws Exception{

		System.out.println("File uploaded");
		return SUCCESS;
 
	}
 
	public String display() {
		return NONE;
	}
 
}
