package code;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class GlobalDynamicConstants {

	private static Properties prop = new Properties();
	private static InputStream inStream=null;
	
	public GlobalDynamicConstants(String messageFileName)throws IOException 
	{
		inStream=this.getClass().getClassLoader().getResourceAsStream(messageFileName);
		if(inStream==null)
			throw new FileNotFoundException("messages file is not found");
	}
	
	public String getMessage(String messageKey)
	{
		try
		{
			prop.load(inStream);			
		}
		catch(IOException ioe)
		{
			return "Error in initialization messages files";
		}
		
		if(prop.get(messageKey)!=null)
			return (String)prop.get(messageKey);
		else
			return "INVALID_MESSAGE_KEY";
	}	
}
