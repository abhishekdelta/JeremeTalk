package clientCore;

import jeremeTalk.ConfigFileIO;
import jeremeTalk.GUI;
import jeremeTalk.JTC;
import jeremeTalk.NetworkInterface;
import jeremeTalk.SenderThread;

public class JTClient extends NetworkInterface
{
	String TargetServerIP,TargetServerPort;
	boolean restart,ServerConnected;
	GUI UserInterface;
	ServerCommunicationThread ServerCommunicator;
	public JTClient()
	{
		super("","","","CLIENT");
		initializeReciever(this);
		TargetServerIP="";
		TargetServerPort="";
		restart=false;
		ServerConnected=false;
		ServerCommunicator=new ServerCommunicationThread(this);
		ServerCommunicator.start();
		UserInterface=new GUI("CLIENT",this);
		
		ServerCommunicator.die();
					
	}
	public JTClient(String TargetIP,String TargetPort,String IP,String Port,String Name)
	{
		super(IP,Port,Name,"CLIENT");
		initializeReciever(this);
		TargetServerIP=TargetIP;
		TargetServerPort=TargetPort;
		if(testHostActive(TargetServerIP, TargetServerPort)==false)
		{
			TargetServerIP="";
			TargetServerPort="";
			ServerConnected=false;
			System.out.println("alnok");
		}
		else
		{
			ServerConnected=true;
			serverConnected();
			System.out.println("alok");
		}
		restart=false;
		ServerCommunicator=new ServerCommunicationThread(this);
		ServerCommunicator.start();
		
		UserInterface=new GUI("CLIENT",this);	
		ServerCommunicator.die();
	}
	
	public void serverConnected()
	{
		appendMsgToGUI("Server is connected at "+TargetServerIP+" on port "+TargetServerPort);
		informServer();
		getOnlineUsers();		
	}
	public void serverDisconnected()
	{
		appendMsgToGUI("Connection to server is lost ! Trying for reconnection ...");
	}
	public void informServer()
	{
		String ControlMsg;
		String[] ControlData;
		ControlData=new String[3];
		ControlData[0]=LocalUser.getUserName();
		ControlData[1]=LocalUser.getUserIP();
		ControlData[2]=LocalUser.getUserPort();
		ControlMsg=JTCM.createControlMessage(JTC.ADD_USER, ControlData);
		System.out.println("Informing Server of Presence with ControlMsg="+ControlMsg);
		SenderThread sender=new SenderThread(TargetServerIP,TargetServerPort,ControlMsg);
		sender.start();
	}
	public void getOnlineUsers()
	{
		System.out.println("Getting list of Online Users!");
	}
	public void msgRecieved(String RecData)
	{
		String ControlData[]=JTCM.parseControlMessage(RecData);
		System.out.println("ControlData[0]="+ControlData[0]);
		if(ControlData[0].equals(String.valueOf(JTC.TEST_MSG)) && ControlData[1].equals(String.valueOf(JTC.TEST_Q_MSG)))
		{
			String TestIP=ControlData[2];
			String TestPort=ControlData[3];
			System.out.println("TEST_MSG Data Recieved from "+TestIP+":"+TestPort);
			SenderThread sender=new SenderThread(TestIP,TestPort,JTCM.createControlMessage(JTC.TEST_A_MSG,null));
			sender.start();		
		}
		else if(ControlData[0].equals(String.valueOf(JTC.PRIVATE_TEXT_MSG)))
		{
				MsgRec=true;
				MsgFrom=ControlData[1];
				Msg=ControlData[3];
		}
	}
	public void test()
	{
		System.out.println("JTClient active!");
	}
	public void sendTextMsg(int Type,String ToUser,String Msg)
	{			
			if(Type==JTC.PRIVATE_TEXT_MSG)
			{
				System.out.println(Msg+" to be sent to "+ToUser);
				String[] ControlData;
				ControlData=new String[2];
				ControlData[0]=getLocalUserName();
				ControlData[1]=ToUser;
				ControlData[2]=Msg;
				String ControlMsg=JTCM.createControlMessage(JTC.PRIVATE_TEXT_MSG,ControlData);
				System.out.println("ControlMsg to be sent:"+ControlMsg);
				SenderThread sender=new SenderThread(TargetServerIP,TargetServerPort,ControlMsg);
				sender.start();	
			}
			else if(Type==JTC.COMMON_TEXT_MSG)
			{
				System.out.println(Msg+" to be sent to all!");
				String[] ControlData;
				ControlData=new String[2];
				ControlData[0]=getLocalUserName();
				ControlData[1]=Msg;
				String ControlMsg=JTCM.createControlMessage(JTC.COMMON_TEXT_MSG,ControlData);
				System.out.println("ControlMsg to be sent:"+ControlMsg);
				SenderThread sender=new SenderThread(TargetServerIP,TargetServerPort,ControlMsg);
				sender.start();
			}
	}
	public String getTargetIP()
	{
		return TargetServerIP;
	}
	public String getTargetPort()
	{
		return TargetServerPort;
	}
	public void setTargetHostIP(String IP)
	{
		if(checkValidIP(IP)==true)
		{
			TargetServerIP=IP;
			(new ConfigFileIO(JTC.CONFIG_FILE)).setAttribute(JTC.SERVERIP,IP);
		}
		else System.out.println("Invalid IP Address.Not setting!");
	}
	public void setTargetHostPort(String Port)
	{
		if(checkValidPort(Port))
		{
			TargetServerPort=Port;
			(new ConfigFileIO(JTC.CONFIG_FILE)).setAttribute(JTC.SERVERPORT,Port);
		}
		else System.out.println("Invalid Port.Not setting!");
	}
	public void switchToServer()
	{
		ConfigFileIO CFIO=new ConfigFileIO(JTC.CONFIG_FILE);
		CFIO.setAttribute(JTC.PROFILE,JTC.SERVER);
		restart=true;		
	}
	public boolean getRestart()
	{
		return restart;
	}
}