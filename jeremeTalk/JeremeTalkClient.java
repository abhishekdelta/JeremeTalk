import jeremeTalk.NetworkInterface;
import clientCore.JTClient;

public class JeremeTalkClient
{
	
	NetworkInterface MainClient;
	
	JeremeTalkClient(String TargetIP,String TargetPort,String IP,String Port,String Name)
	{				
		System.out.println("JeremeTalkClient invoked with target "+TargetIP+":"+TargetPort);
		System.out.println(Name+"@"+IP+":"+Port);
		MainClient=new JTClient(TargetIP,TargetPort,IP,Port,Name);
		
		
		checkRestart();
		
	}
	void checkRestart()
	{
		System.out.println("JeremeTalkClient destroyed");
		System.out.println(">"+((JTClient)MainClient).getRestart());
		if(((JTClient)MainClient).getRestart()==true)
		{
			JeremeTalk.main(null);
		}
	}
}