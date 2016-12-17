package code;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

public class Control 
{
	//make an URL to know source
	private static URL url;
	private static int interval=5*60*1000;
	private static final String PING_COMMAND="ping -n 1 -w 5000 www.google.com";
	
	//Commands and connection activity status.
	private static final String command="rasdial \"Vodafone Connection\" /PHONE:*99#";
	private static boolean isAvailable;
	/**
	 * Method to identify the connection status. Dials google.com and confirms connectivity. 
	 * @return true if connection is present or else false. 
	 */
	public static boolean isInternetReachable()throws IOException 
	{
		Runtime currentRuntime=Runtime.getRuntime();
		Process pingProcess=currentRuntime.exec(PING_COMMAND);
		while(pingProcess.isAlive())
		{
			//asking process to finish
		}
		
		if(pingProcess.exitValue()==0)
		{
			System.out.println("INTERNET CONNECTION PRESENT at "+new Date());
			return true; //returning true as Internet is present
		}
		else
		{
			System.out.println("INTERNET CONNECTION NOT PRESENT : "+new Date());			
			return false;//returning false as Internet is not present
		}		
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
			System.out.println("Attempting to reconnect Vodafone Connection(Roaming). . . .");
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
				System.out.println("ERROR : Thread in illegal state");
			}
			catch(InterruptedException ie)
			{
				System.out.println("ERROR : Thread Interrupted");
			}			
		}
		catch(IOException ioe)
		{
			System.out.println("Error in Connecting the Internet");
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
					interval=5*60*1000;//5 mins
				else
				{
					reConnect(); 
					interval=1*60*1000;//1 min
				}					
				//Suspending the main thread with specific interval
				Thread.sleep(interval);
			}
		}		
		catch(InterruptedException ie)
		{
			System.out.println("CATASTROPHIC SYSTEM FAILURE\nClosing the application . .");
			ie.printStackTrace();
		}		
		catch(IOException ioe)
		{
			System.out.println("CATASTROPHIC SYSTEM FAILURE\nClosing the application . .");	
			ioe.printStackTrace();
		}
	}
}
