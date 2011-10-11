package clientCore;

import jeremeTalk.JTC;

public class ServerCommunicationThread extends Thread
{
	JTClient client;
	boolean running;
	public ServerCommunicationThread(JTClient client)
	{
		this.client=client;
		this.setDaemon(true);
		running=true;
		System.out.println("ServerCommunicatorThread Initialized!");
	}
	public void die()
	{
		running=false;
	}
	public void run()
	{
		System.out.println("ServerCommunicatorThread Started!");
		while(running)
		{
			System.out.println("ServerCommunicatorThread : trying....!");
			if(client.ServerConnected==false)
			{
				while(client.testHostActive(client.TargetServerIP, client.TargetServerPort)==false)
				{
					try
					{
						Thread.sleep(JTC.WAIT_FOR_CONNECTED);
					}
					catch(Exception exc)
					{
						exc.printStackTrace();
					}
				}
				client.ServerConnected=true;
				client.serverConnected();
				System.out.println("ServerCommunicatorThread : Server Connected!");
			}
			if(client.ServerConnected==true)
			{
				while(client.ServerConnected==true)
				{
					try
					{
						Thread.sleep(JTC.WAIT_FOR_DISCONNECTED);
					}	
					catch(Exception exc)
					{
						exc.printStackTrace();
					}	
					if(client.testHostActive(client.TargetServerIP, client.TargetServerPort)==false)
						client.ServerConnected=false;
				}
				client.serverDisconnected();
				System.out.println("ServerCommunicatorThread : Server Disconnected!");
			}
		}
		System.out.println("ServerCommunicatorThread Died!");
	}
}