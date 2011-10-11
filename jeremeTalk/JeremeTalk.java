import jeremeTalk.ConfigFileIO;
import jeremeTalk.JTC;



public class JeremeTalk
{
	String profile;
	ConfigFileIO CFIO;
	public static void main(String args[])
	{
		new JeremeTalk();
	}
	JeremeTalk()
	{
		CFIO=new ConfigFileIO(JTC.CONFIG_FILE);
		if(CFIO.exists()==true)
		{
			System.out.println("Config File exists!");
			profile=CFIO.getAttribute(JTC.PROFILE);
		}
		else
		{
			System.out.println("config.ini doesn't exists!");
			profile=JTC.SERVER;
			CFIO.setAttribute(JTC.PROFILE,JTC.SERVER);
		}
		
		if(profile.equals(JTC.SERVER)==true)
		{
			System.out.println("profile is SERVER");
			new JeremeTalkServer(null,null,CFIO.getAttribute(JTC.LOCALUSERNAME));
		}
			
		else 
		{
			System.out.println("profile is CLIENT");
			new JeremeTalkClient(CFIO.getAttribute(JTC.SERVERIP),CFIO.getAttribute(JTC.SERVERPORT),null,null,CFIO.getAttribute(JTC.LOCALUSERNAME));
		}
	}
	
}