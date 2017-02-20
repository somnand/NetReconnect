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
		//BasicConfigurator.configure();
		//File configFile=new File("C:/Users/1021623/git/NetReconnect/src/main/resources/log4j_config.properties");
		//System.out.println(configFile.exists());
		String configFileName=System.getProperty("user.dir")+File.separator+"src/main/resources/"+"log4j_config.properties";
		//PropertyConfigurator.configure("C:/Users/1021623/git/NetReconnect/src/main/resources/log4j_config.properties");
		PropertyConfigurator.configure(configFileName);
	}
	
	//Commands and connection activity status.
	private static final String command="rasdial \"Vodafone Connection\" /PHONE:*99#";
	private static boolean isAvailable;
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
			logger.info("INTERNET CONNECTION PRESENT at "+new Date());			
			/*
			 * Commenting this code for optimization. Plan to replace with ping.
			Object objData=urlConnect.getContent();//this line returns error if connection is not present
			logger.info("INTERNET CONNECTION PRESENT at "+new Date());
			*/
		}
		catch(MalformedURLException mue)
		{
			logger.error("ERROR > Malformed URL :"+mue.getMessage());
		}
		catch(IOException ioe)
		{
			logger.error("INTERNET CONNECTION NOT PRESENT : "+new Date());
			reConnect();
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
			logger.info("Attempting to reconnect Vodafone Connection(Roaming). . . .");
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
				logger.error("ERROR : Thread in illegal state");
			}
			catch(InterruptedException ie)
			{
				logger.error("ERROR : Thread Interrupted");
			}			
		}
		catch(IOException ioe)
		{
			logger.error("Error in Connecting the Internet");
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
			while(true)
			{
				isAvailable=isInternetReachable();
				/*Adding the intelligent time control system */
				if(isAvailable)
					INTERVAL=5*60*1000;//5 mins
				else 
					INTERVAL=1*60*1000;//1 min
				//Suspending the main thread with specific interval
				Thread.sleep(INTERVAL);
			}
		}		
		catch(InterruptedException ie)
		{
			logger.error("CATASTROPHIC SYSTEM FAILURE\nClosing the application . .");			
		}
	}
}
