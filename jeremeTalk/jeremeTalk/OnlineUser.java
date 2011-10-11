package jeremeTalk;



public class OnlineUser
{
	String UserName,UserIP,UserPort;
	
	public OnlineUser(String name,String ipaddress,String port)
	{
		this.UserName=name;
		this.UserIP=ipaddress;
		this.UserPort=port;
	}
	public String info()
	{
		return getUserName()+"@"+getUserIP()+":"+getUserPort();
	}
	public String getUserName()
	{
		return UserName;
	}
	public String getUserIP()
	{
		return UserIP;
	}
	public String getUserPort()
	{
		return UserPort;
	}
	public void setUserName(String name)
	{
		UserName=name;
	}
	public void setUserIP(String ip)
	{
		UserIP=ip;
	}
	public void setUserPort(String port)
	{
		UserPort=port;
	}
	public boolean equals(OnlineUser user)
	{
		if(this.UserName==user.UserName)
			if(this.UserIP==user.UserIP)
				if(this.UserPort==user.UserPort)
					return true;
		return false;
	}
}