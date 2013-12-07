package com.class3601.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.ActionSupport;

public class ImageAction extends ActionSupport implements ServletRequestAware
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	byte[] imageInByte = null;
	String imageId;
	
	private HttpServletRequest request;
	
	public String getImageId()
	{
		return imageId;
	}
	
	public void setImageId(String imageName)
	{
		this.imageId = imageName;
	}
	
	public String execute()
	{
		return SUCCESS;
	}
	
	//Change file path name here to get this working with where images are stored
	private File getImageFile(String imageName)
	{
		String filePath = request.getSession().getServletContext().getRealPath(File.separator);
		File file = new File(filePath + "/Image/", imageName);
		System.out.println("Found file? " + file.exists());
		return file;
	}
	
	public byte[] getImageInBytes()
	{
		System.out.println("Name of Image:: " + imageId);
		BufferedImage originalImage; 
		
		try
		{
			originalImage = ImageIO.read(getImageFile(this.imageId));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(originalImage, "jpg", baos);
			baos.flush();
			imageInByte = baos.toByteArray();
			baos.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return imageInByte;
	}
	
	public String getCustomContentType()
	{
		return "image/jpeg";
	}
	
	public String getCustomContentDisposition()
	{
		return "anyname.jpg";
	}
	
	@Override
	public void setServletRequest(HttpServletRequest req)
	{
		this.request = req;
	}
}
