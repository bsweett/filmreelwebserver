package com.filmreel.actions;

import java.io.File;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;

import org.apache.commons.io.FileUtils;

import com.filmreel.common.MessageStore;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class FileUploadAction extends ActionSupport implements ServletRequestAware {
	 
	private File fileUpload;
	private File fileToCreate;
	private String fileUploadContentType;
	private String fileUploadFileName;
	private String filePath; 
	private HttpServletRequest request;
	
    private static String XML = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n";
    private static String XML_DATA = "<data>";
    private static String XML_XDATA = "</data>\n";
    private static String XML_MESSAGE = "<message>";
    private static String XML_XMESSAGE = "</message>\n";
    
    private MessageStore messageStore;

	public String getFileUploadContentType() 
	{
		return fileUploadContentType;
	}
 
	public void setFileUploadContentType(String fileUploadContentType) 
	{
		this.fileUploadContentType = fileUploadContentType;
	}
 
	public String getFileUploadFileName()
	{
		return fileUploadFileName;
	}
 
	public void setFileUploadFileName(String fileUploadFileName) 
	{
		this.fileUploadFileName = fileUploadFileName;
	}
 
	public File getFileUpload()
	{
		return fileUpload;
	}
 
	public void setFileUpload(File fileUpload)
	{
		this.fileUpload = fileUpload;
	}
 
	public String execute() throws Exception
	{	
		messageStore = new MessageStore();
		
		try 
		{	
			//Get the file path of the temporary image uploaded to the server
			//Save the temporary image in a location we have picked that is
			//accessible by tomcat on the webserver
			
			filePath = ServletActionContext.getServletContext().getRealPath("/");
			fileToCreate = new File("/filmreel/reels/", this.fileUploadFileName);		 
			FileUtils.copyFile(this.fileUpload, fileToCreate);
			
			System.out.println("ACTION :: FileUpload - The image was uploaded successfully.");
		} 
		//Catches unexpected errors
		catch (Exception e) 
		{
			e.printStackTrace();
			addActionError(e.getMessage());
			
			messageStore.appendToMessage(XML);
			messageStore.appendToMessage(XML_DATA);
			messageStore.appendToMessage(XML_MESSAGE);
			messageStore.appendToMessage("Error");
			messageStore.appendToMessage(XML_XMESSAGE);
			messageStore.appendToMessage(XML_XDATA);
			
			System.out.println("ACTION :: FileUpload - The image failed to upload, is the file size to big?");
			return "error";
		}
		return SUCCESS;
	}
 
	public String display() 
	{
		return NONE;
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) 
	{
		this.request = request;
	}
	
	private HttpServletRequest getServletRequest() 
	{
		return request;
	}
 
}
