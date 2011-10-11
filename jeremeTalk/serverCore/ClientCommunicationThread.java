package serverCore;

import jeremeTalk.JTC;
import jeremeTalk.OnlineUser;

public class ClientCommunicationThread extends Thread
{
	JTServer server;
	OnlineUser[] clients;
	boolean running;
	int count;
	public ClientCommunicationThread(JTServer server)
	{
		this.server=server;
		this.setDaemon(true);
		running=true;
		System.out.println("ClientCommunicationThread Initialized!");
	}
	public void die()
	{
		running=false;
	}
	public void run()
	{
		System.out.println("ClientCommunicationThread Started!");
		while(running)
		{
			
			clients=server.getOnlineUsersList();
			count=server.getOnlineUserCount();
			System.out.println("ClientCommunicationThread : trying for "+count+"clients....!");
			for(int i=0;i<count;i++)
			{
				if(server.testHostActive(clients[i].getUserIP(), clients[i].getUserPort())==false)
				{
					server.clientDisconnected(clients[i]);
				}
			}
			System.out.println("ClientCommunicationThread : one cycle over....!");
			try
			{
				Thread.sleep(JTC.WAIT);
			}
			catch(Exception exc)
			{
				exc.printStackTrace();
			}
			
		}
		System.out.println("ClientCommunicationThread Died!");
	}
}
