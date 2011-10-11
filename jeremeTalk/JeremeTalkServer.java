import jeremeTalk.NetworkInterface;
import serverCore.JTServer;

class JeremeTalkServer
{
	
	NetworkInterface MainServer;
	
	JeremeTalkServer(String IP,String Port,String Name)
	{				
		System.out.println("JeremeTalkServer invoked with "+Name+":"+IP+"&"+Port);
		MainServer=new JTServer(IP,Port,Name);
		
		
		checkRestart();
		
	}
	void checkRestart()
	{
		System.out.println("JeremeTalkServer destroyed");
		if(((JTServer)MainServer).getRestart()==true)
		{
			JeremeTalk.main(null);
		}
	}
	
	

}