package serverCore;

import jeremeTalk.ConfigFileIO;
import jeremeTalk.GUI;
import jeremeTalk.JTC;
import jeremeTalk.NetworkInterface;
import jeremeTalk.OnlineUser;
import jeremeTalk.SenderThread;

public class JTServer extends NetworkInterface
{
	
	boolean restart;
	SenderThread sender;
	GUI UserInterface;
	ClientCommunicationThread ClientCommunicator;
	public JTServer()
	{
		super("","","","SERVER");
		initializeReciever(this);
		
		restart=false;
		ClientCommunicator=new ClientCommunicationThread(this);
		ClientCommunicator.start();
		UserInterface=new GUI("SERVER",this);
		ClientCommunicator.die();
	}
	public void test()
	{
		System.out.println("JTServer active!");
	}
	public JTServer(String IP,String Port,String Name)
	{
		super(IP,Port,Name,"SERVER");
		initializeReciever(this);
	
		restart=false;
		
		System.out.println("JTServer invoked with "+getLocalUserName()+"@"+getLocalUserIP()+":"+getLocalUserPort());
		ClientCommunicator=new ClientCommunicationThread(this);
		ClientCommunicator.start();
		UserInterface=new GUI("SERVER",this);
		ClientCommunicator.die();
		
	}
	public void clientConnected(String UserName,String UserIP,String UserPort)
	{
		System.out.println("A client is connected");
		JTCM.addUser(new OnlineUser(UserName,UserIP,UserPort));
		addUserToGUI(UserName);
		appendMsgToGUI(UserName+" connected from IP "+UserIP+" and port "+UserPort);
	}
	
	public void clientDisconnected(String UserName,String UserIP,String UserPort)
	{
		JTCM.delUser(new OnlineUser(UserName,UserIP,UserPort));
		removeUserFromGUI(UserName);
		appendMsgToGUI(UserName+" disconnected from IP "+UserIP+" and port "+UserPort);
	}
	public void clientDisconnected(OnlineUser user)
	{
		clientDisconnected(user.getUserName(),user.getUserIP(),user.getUserPort());
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
		else if(ControlData[0].equals(String.valueOf(JTC.ADD_USER)))
		{
			clientConnected(ControlData[1], ControlData[2], ControlData[3]);
		}
		else if(ControlData[0].equals(String.valueOf(JTC.DEL_USER)))
		{
			clientDisconnected(ControlData[1],ControlData[2],ControlData[3]);
		}
		else if(ControlData[0].equals(String.valueOf(JTC.COMMON_TEXT_MSG)))
		{
			MsgRec=true;
			MsgFrom=ControlData[1];
			Msg=ControlData[2];		
		}
		else if(ControlData[0].equals(String.valueOf(JTC.PRIVATE_TEXT_MSG)))
		{
			if(ControlData[2].equals(getLocalUserName()))
			{
				MsgRec=true;
				MsgFrom=ControlData[1];
				Msg=ControlData[3];
			}
			//else send to the req. guy
		}
		
	}
	public OnlineUser[] getOnlineUsersList()
	{
		return JTCM.UserList;
	}
	public int getOnlineUserCount()
	{
		return JTCM.UserCount;
	}
	public void sendTextMsg(int Type,String FromUser,String ToUser,String Msg,String TargetUserIP,String TargetUserPort)
	{
	
		
		
		if(FromUser=="" || FromUser==null)
			FromUser=getLocalUserName();
		
		////System.out.println("Type="+Type);
	//	System.out.println("FromUser="+FromUser);
		//System.out.println("ToUser="+ToUser);
	//	System.out.println("Msg="+Msg);
		if(Type==JTC.COMMON_TEXT_MSG)
		{
			System.out.println(Msg+" to be sent to ALL");
			for(int i=0;i<JTCM.UserCount;i++)
			{
				System.out.println(Msg+" to be sent to "+JTCM.UserList[i].getUserName());
				sendTextMsg(JTC.PRIVATE_TEXT_MSG,FromUser,JTCM.UserList[i].getUserName(),Msg,TargetUserIP,TargetUserPort);
			}
		}
		else if(Type==JTC.PRIVATE_TEXT_MSG)
		{
			System.out.println(Msg+" to be sent to "+ToUser+" as "+FromUser);
			String[] ControlData;
			ControlData=new String[3];
			ControlData[0]=getLocalUserName();
			ControlData[1]=ToUser;
			ControlData[2]=Msg;
			String ControlMsg=JTCM.createControlMessage(JTC.PRIVATE_TEXT_MSG,ControlData);
			System.out.println("ControlMsg to be sent:"+ControlMsg);
			if(TargetUserIP=="" || TargetUserIP==null)
				TargetUserIP=JTCM.getUserIP(ToUser);
			if(TargetUserPort=="" || TargetUserPort==null)
				TargetUserPort=JTCM.getUserPort(ToUser);		
			SenderThread sender=new SenderThread(TargetUserIP,TargetUserPort,ControlMsg);
			sender.start();
		}
	}
	public void switchToClient()
	{
		ConfigFileIO CFIO=new ConfigFileIO(JTC.CONFIG_FILE);
		CFIO.setAttribute(JTC.PROFILE,JTC.CLIENT);
		freePort();
		restart=true;
		
	}
	public boolean getRestart()
	{
		return restart;
	}
	

}