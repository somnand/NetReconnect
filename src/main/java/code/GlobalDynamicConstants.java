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
	}
	
	public String getMessage(String messageKey)throws IOException
	{
		if(inStream!=null)
		{
			prop.load(inStream);			
		}
		else
		{
			throw new FileNotFoundException("messages file is not found");			
		}
		
		if(prop.get(messageKey)!=null)
			return (String)prop.get(messageKey);
		else
			return "INVALID_MESSAGE_KEY";
	}	
}
