package jeremeTalk;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import serverCore.JTServer;
import clientCore.JTClient;

public class RecieverThread extends Thread
{
	String LocalIP,LocalPort,Profile;
	ServerSocket recieverServer;
	Socket reciever;
	BufferedReader BRD;
	String RecievedData;
	NetworkInterface JTNI;
	boolean StillRunning;
	public RecieverThread(String Profile,String IP,String Port,NetworkInterface JTNI)
	{
		this.Profile=Profile;
		this.LocalIP=IP;
		this.JTNI=JTNI;
		this.LocalPort=Port;
		this.setDaemon(true);
		RecievedData="";
		StillRunning=true;
	}
	public void run()
	{
		System.out.println("Reciever Thread running! at "+LocalIP+":"+LocalPort);
		while(StillRunning)
		{
			try	
			{
				recieverServer=new ServerSocket(Integer.parseInt(LocalPort,10));
				reciever=recieverServer.accept();
				BRD=new BufferedReader(new InputStreamReader(reciever.getInputStream()));
				
				String buffer;
				RecievedData="";
				while((buffer=BRD.readLine())!=null)
				{
					RecievedData+=buffer;
				}
				System.out.println("Recieved Data at ReceiverThread="+RecievedData);
				closeAll();
				
			}
			catch(Exception exc)
			{
				exc.printStackTrace();
			}
			if(RecievedData!="")
			{
				if(Profile==JTC.SERVER)
				 ((JTServer)JTNI).msgRecieved(RecievedData);
				else if(Profile==JTC.CLIENT)
				 ((JTClient)JTNI).msgRecieved(RecievedData);
			}
			
			
		}
		System.out.println("Permanently Killed Reciever Thread at "+LocalPort);
	}
	void closeAll()
	{
		System.out.println("Closing RecieverThread connections!");
		try
		{
			recieverServer.close();
			if(reciever!=null)
			{
				reciever.close();
				BRD.close();
			}
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
		
	}
	public void kill()
	{
		StillRunning=false;
		closeAll();
		
	}
	
}