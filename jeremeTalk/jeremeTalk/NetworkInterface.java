package jeremeTalk;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkInterface
{
	public OnlineUser LocalUser;
	RecieverThread reciever;
	public ControlManager JTCM;
	public boolean MsgRec,SystemMsgRec,AddUser,RemoveUser;
	public String Msg,SystemMsg,AddUserName,RemoveUserName,MsgFrom,Profile;
	/*public JTNetworkInterface(String profile)
	{
		MsgRec=false;
		System.out.println("JTNetworkInterface started with no-params!");
		JTCM=new JTControlManager();
		LocalUser=new OnlineUser("Noname",getSystemIPAddress(),getSystemFreePort());
		reciever=new RecieverThread(LocalUser.getUserIP(),LocalUser.getUserPort(),this);
		reciever.start();
		
	}*/
	public NetworkInterface(String ip,String port,String name,String profile)
	{
		MsgRec=false;
		SystemMsgRec=false;
		AddUser=false;
		RemoveUser=false;
		Profile=profile;
		if(checkValidIP(ip)==false)
		{
			ip=getSystemIPAddress();
			(new ConfigFileIO(JTC.CONFIG_FILE)).setAttribute(JTC.LOCALUSERIP,ip);
		}
		if(checkValidPort(port)==false)
		{
			port=getSystemFreePort();
			(new ConfigFileIO(JTC.CONFIG_FILE)).setAttribute(JTC.LOCALUSERPORT,port);
		}
		if(checkValidName(name)==false)
			name="";
		System.out.println("JTNetworkInterface started with "+name+"@"+ip+":"+port+" AS "+profile);
		MsgRec=false;
		JTCM=new ControlManager();
		LocalUser=new OnlineUser(name,ip,port);	
	}
	public void initializeReciever(NetworkInterface ChildObj)
	{
		reciever=new RecieverThread(Profile,LocalUser.getUserIP(),LocalUser.getUserPort(),ChildObj);
		reciever.start();	
	}
	public void appendMsgToGUI(String Msg)
	{
		SystemMsgRec=true;
		SystemMsg="System : "+Msg;
	}
	public void addUserToGUI(String UserName)
	{
		AddUser=true;
		this.AddUserName=UserName;
	}
	public void removeUserFromGUI(String UserName)
	{
		RemoveUser=true;
		this.RemoveUserName=UserName;
	}
	
	public boolean testHostActive(String TIP,String TPort)
	{
		if(checkValidIP(TIP)==false)
			return false;
		if(checkValidPort(TPort)==false)
			return false;
		Socket tester;
		ServerSocket checker;
		OutputStreamWriter OSW;
		BufferedReader BRD;
		String TestPort,read="";
		String[] ControlData;
		System.out.println("Testing For Connection at "+TIP+":"+TPort);
		try
		{
			tester=new Socket(TIP,Integer.parseInt(TPort,10));
			OSW=new OutputStreamWriter(tester.getOutputStream());
			TestPort=getSystemFreePort();
			ControlData=new String[2];
			ControlData[0]=getLocalUserIP();
			ControlData[1]=TestPort;
			String ControlMsg=JTCM.createControlMessage(JTC.TEST_Q_MSG, ControlData);
			OSW.write(ControlMsg);
			OSW.close();
			tester.close();
			System.out.println("ControlMsg sent = "+ControlMsg);
		}
		catch(Exception exc)
		{
			System.out.println("Error in connecting!");
			exc.printStackTrace();
			return false;
		}
		try
		{
			System.out.println("Waiting for Incoming Test Connection!");
			checker=new ServerSocket(Integer.parseInt(TestPort,10));
			checker.setSoTimeout(10000);
			tester=checker.accept();
			System.out.println("Connected!");
			BRD=new BufferedReader(new InputStreamReader(tester.getInputStream()));
			
			String buffer;
			while((buffer=BRD.readLine())!=null)
			{
				read+=buffer;
			}
			BRD.close();
			tester.close();
			checker.close();
			System.out.println("Read Data = "+read);
		}
		catch(Exception exc)
		{
			System.out.println("Error in recieving! data="+read);
			exc.printStackTrace();
			return false;
		}
		String[] parsed;
		parsed=JTCM.parseControlMessage(read);
		if(parsed[0].equals(String.valueOf(JTC.TEST_MSG)) && parsed[1].equals(String.valueOf(JTC.TEST_A_MSG)))
		{
			System.out.println("Host Reply was Authenticated!");
		 return true;
		}
		System.out.println("Host Reply was Unexpected!");
		return false;
		
	}
	public void freePort()
	{
		reciever.kill();
	}
	public boolean setLocalUserName(String name)
	{
		if(checkValidName(name)==true)
		{
			(new ConfigFileIO(JTC.CONFIG_FILE)).setAttribute(JTC.LOCALUSERNAME,name);
		   	LocalUser.setUserName(name);
		   	return true;
		}
		else System.out.println("Invalid Username");
		return false;
	}
	public void setLocalUserPort(String port)
	{
		if(checkValidPort(port)==true)
		{
			(new ConfigFileIO(JTC.CONFIG_FILE)).setAttribute(JTC.LOCALUSERPORT,port);
			LocalUser.setUserPort(port);
			reciever.kill();
			reciever=new RecieverThread(Profile,LocalUser.getUserIP(),LocalUser.getUserPort(),this);
			reciever.start();
		}
		else System.out.println("Invalid Port");
	}
	public String getLocalUserIP()
	{
		return LocalUser.getUserIP();
	}
	public String getLocalUserName()
	{
		return LocalUser.getUserName();
	}
	public String getLocalUserPort()
	{
		return LocalUser.getUserPort();
	}

	public String getSystemIPAddress()
	{
		
		String RetVal="";
		try
		{
			RetVal= (InetAddress.getLocalHost()).getHostAddress();
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
		System.out.println("Found Host IP Address : "+RetVal);
		return RetVal;
	}
	public String getSystemFreePort()
	{
		String RetVal="";
		try
		{
			ServerSocket SS=new ServerSocket(0);
			RetVal=Integer.toString(SS.getLocalPort());
			SS.close();
		}	
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
		System.out.println("Found Host Free Port : "+RetVal);
		return RetVal;
	}
	public boolean checkValidIP(String ip)
	{		
		if(ip==null || ip=="")
			return false;
		try
		{
			ip=ip.replace('.', ':');
			String[] ipSplits=ip.split(":");
			if(ipSplits.length<4)
				return false;
			for(int i=0;i<ipSplits.length;i++)
				if(ipSplits[i].length()==0 || ipSplits[i].length()>3)
					return false;
		}
		catch(Exception exc)
		{
			return false;
		}
		return true;
	}
	public boolean checkValidName(String name)
	{		
		if(name==null || name=="")
			return false;
		try
		{
			return name.matches("[a-zA-Z0-9S]{"+name.length()+"}");
		}
		catch(Exception exc)
		{
			return false;
		}
	}
	public boolean checkValidPort(String port)
	{		
		if(port==null || port=="")
			return false;
		try
		{
			if(port!="" && Integer.parseInt(port,10)>0 && Integer.parseInt(port,10)<65535)
				return true;
		}
		catch(Exception exc)
		{
		}
		return false;
	}
}