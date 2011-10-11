package jeremeTalk;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SenderThread extends Thread
{
	String Msg,IP,Port;
	Socket sender;
	BufferedWriter BWR;
	public SenderThread(String IP,String Port,String Msg)
	{
		this.IP=IP;
		this.Port=Port;
		this.Msg=Msg;
		this.setDaemon(true);
		
	}
	public void run()
	{
		System.out.println("Sending "+Msg+" to "+IP+":"+Port);
		try
		{
			sender=new Socket(IP,Integer.parseInt(Port,10));
			BWR=new BufferedWriter(new OutputStreamWriter(sender.getOutputStream()));
			if(BWR==null)
			{
				System.out.println("Could not establish connection to reciever on "+IP+":"+Port);	
			}
			else 
			{
				BWR.write(Msg);
				System.out.println("Message sent!");
			}
			destroy();
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
	
	}
	public void destroy()
	{
		System.out.println("destroying senderThread!");
		try
		{
			BWR.close();
			sender.close();
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
		
	}
}