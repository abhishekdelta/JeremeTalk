package jeremeTalk;

public class UserManager {
	public OnlineUser[] UserList;
	public int Capacity,UserCount;
	public UserManager()
	{
		Capacity=JTC.CAPACITY_INI;
		UserCount=0;
		UserList=new OnlineUser[Capacity];
	}
	public void addUser(OnlineUser user)
	{
		System.out.println("adding user "+user.info());
		if(UserCount==Capacity)
		{
			extendCapacity();
		}
		UserList[UserCount]=user;
		++UserCount;
	}
	public void delUser(OnlineUser user)
	{
		System.out.println("deleting user "+user.info());
		if(UserCount==0)
			return;
		for(int i=0;i<UserCount;i++)
		{
			if(UserList[i].getUserName()==user.getUserName() && UserList[i].getUserIP()==user.getUserIP() && UserList[i].getUserPort()==user.getUserPort())
			{
				System.out.println("a user found for deleting");
				for(int j=i;j<UserCount-1;j++)
				{
					UserList[j]=UserList[j+1];
				}
				--UserCount;
				break;
			}
		}
	}
	void extendCapacity()
	{
		OnlineUser[] tmp=UserList;
		Capacity+=JTC.CAPACITY_INC;
		UserList=new OnlineUser[Capacity+JTC.CAPACITY_INC];
		for(int i=0;i<tmp.length;i++)
		{
			UserList[i]=tmp[i];
		}	
	}
	public String getUserIP(String UserName)
	{
		for(int i=0;i<UserCount;i++)
			if(UserList[i].getUserName().equals(UserName))
				return UserList[i].getUserIP();
		return "";
	}
	public String getUserPort(String UserName)
	{
		for(int i=0;i<UserCount;i++)
			if(UserList[i].getUserName().equals(UserName))
				return UserList[i].getUserPort();
		return "";
	}
}
