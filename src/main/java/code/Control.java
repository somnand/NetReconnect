package code;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Control 
{
	//make an URL to know source
	private static URL url;
	private static int INTERVAL=5*60*1000;
	private static final String CONSTANT_URL="http://www.google.com";
	
	//Engaging the logging system
	public static Logger logger=Logger.getLogger(Control.class);
	static{
		String configFileName="log4j.properties";
		PropertyConfigurator.configure(configFileName);
	}
	
	//Commands and connection activity status.
	private static final String command="rasdial \"Vodafone Connection\" /PHONE:*99#";
	private static boolean isAvailable;
	
	//GlobalDynamicConstants fetched from messages property file.
	private static GlobalDynamicConstants globalDynamicConstants=null;	
	
	/**
	 * Method to identify the connection status. Dials google.com and confirms connectivity. 
	 * @return true if connection is present or else false.
	 */
	public static boolean isInternetReachable()
	{
		try
		{
			url=new URL(CONSTANT_URL);
			HttpURLConnection urlConnect=(HttpURLConnection)url.openConnection();
			Object objData=urlConnect.getContent();//this line returns error if connection is not present
			logger.info(globalDynamicConstants.getMessage("INFO_CONNECTION_PRESENT")+new Date());			
			/*
			 * Commenting this code for optimization. Plan to replace with ping.
			Object objData=urlConnect.getContent();//this line returns error if connection is not present
			logger.info("INTERNET CONNECTION PRESENT at "+new Date());
			*/
		}
		catch(MalformedURLException mue)
		{
			logger.error(globalDynamicConstants.getMessage("ERROR_MALFORMED_URL")+mue.getMessage());
		}
		catch(IOException ioe)
		{
			logger.error(globalDynamicConstants.getMessage("INFO_CONNECTION_NOT_PRESENT")+new Date());
			//reConnect();
			return false;//returning false 
		}				
		return true;
	}
	
	/**
	 * Method to reconnect to specific Internet connection
	 * after confirmation of disconnection 
	 */
	private static void reConnect()
	{
		try 
		{
			Process cmd;			
			cmd=Runtime.getRuntime().exec(command);
			logger.info(globalDynamicConstants.getMessage("INFO_ATTEMPTING_TO_RECONNECT"));
			/*Adding the exitValue() to check for success for the connection*/
			
			try
			{
				cmd.waitFor();
				//Setting up Internet available status.
				if(cmd.exitValue()!=0)
					isAvailable=false;
				else
					isAvailable=true;				
			}
			catch(IllegalThreadStateException itse)
			{
				logger.error(globalDynamicConstants.getMessage("ERROR_ILLEGAL_THREAD_STATE"));
			}
			catch(InterruptedException ie)
			{
				logger.error(globalDynamicConstants.getMessage("ERROR_THREAD_INTERRUPTED"));
			}			
		}
		catch(IOException ioe)
		{
			logger.error(globalDynamicConstants.getMessage("ERROR_GENERIC_INTERNET_NOT_CONNECTING"));
		}				
	}	
	/**
	 * Main() to trigger the program.
	 * @param args
	 */
	public static void main(String[] args) 
	{
		try
		{
			globalDynamicConstants=new GlobalDynamicConstants("messages_en.properties");
			while(true)
			{
				isAvailable=isInternetReachable();
				/*Adding the intelligent time control system */
				if(isAvailable)
					INTERVAL=5*60*1000;//5 mins
				else 
				{
					INTERVAL=1*60*1000;//1 min
					reConnect();
				}
				//Suspending the main thread with specific interval
				Thread.sleep(INTERVAL);
			}
		}
		catch(IOException ioe)
		{
			logger.error("CATASTROPHIC SYSTEM FAILURE\nClosing the application . .");
			logger.error(ioe.getMessage());
		}
		catch(InterruptedException ie)
		{
			logger.error("CATASTROPHIC SYSTEM FAILURE\nClosing the application . .");
			logger.error(ie.getMessage());
		}
	}
}
