package jeremeTalk;
public class GUIUpdaterThread extends Thread
{
	NetworkInterface JTNI;
	GUI UserInterface;
	boolean StillRunning;
	public GUIUpdaterThread(NetworkInterface JTNI,GUI UserInterface)
	{
		this.JTNI=JTNI;
		this.UserInterface=UserInterface;
		StillRunning=true;
	}
	public void run()
	{
		System.out.println("GUIUpdater started!");
		try
		{
			while(!UserInterface.MainShell.isDisposed() && StillRunning)
			{
				UserInterface.MainDisplay.asyncExec(new Runnable() 
				{
					public void run()
					{

						if(StillRunning)
						{
							if(JTNI.MsgRec==true)
							{
								UserInterface.appendMessage(JTNI.MsgFrom+" : "+JTNI.Msg+"\n");
								JTNI.MsgRec=false;
							}
							if(JTNI.SystemMsgRec==true)
							{
								UserInterface.appendMessage(JTNI.SystemMsg+"\n");
								JTNI.SystemMsgRec=false;
							}
							if(JTNI.AddUser==true)
							{
								UserInterface.appendUser(JTNI.AddUserName);
								JTNI.AddUser=false;
							}
							if(JTNI.RemoveUser==true)
							{
								UserInterface.removeUser(JTNI.AddUserName);
								JTNI.RemoveUser=false;
							}
						}
					}
				});				
				Thread.sleep(100);
			}
			System.out.println("GUIUpdater killed!");
		}
		catch(Exception exc)
		{
			exc.printStackTrace();			
		}	
	}
	public void kill()
	{
		System.out.println("GUIUpdater killing!");
		StillRunning=false;
	}
}