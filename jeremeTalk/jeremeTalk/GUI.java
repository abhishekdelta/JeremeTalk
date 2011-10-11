package jeremeTalk;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import serverCore.JTServer;
import clientCore.JTClient;
public class GUI
{
	public Display MainDisplay;
	public Shell MainShell;
	
	Text GlobalOutWin,GlobalInpWin;
	Button GlobalSendBut,GlobalClearBut;
	Menu GlobalMainMenu,GlobalOptionsMenu,GlobalSettingsMenu,GlobalProfileMenu;
	MenuItem GlobalExitItem,GlobalAboutItem,GlobalOptionsItem,GlobalSettingsItem,GlobalConnectionItem,GlobalProfileItem;
	MenuItem GlobalServerItem,GlobalClientItem,GlobalUserNameItem;
	Group GlobalUsersGroup;
	List GlobalUsersList;
	Label GlobalConfWinLab,GlobalInpWinLab;
	Font Calibri;
	Color Red,Black,White,Green,Blue,Gray;
	
	String UserProfile;
	
	
	
	JTClient MainClient;
	JTServer MainServer;
	
	
	boolean retValue;
	
	boolean GPFlag,GPFlag2;
	
	GUIUpdaterThread Updater;
	
	public GUI(String Profile,NetworkInterface NetObj)
	{
		
		MainDisplay=new Display();
		MainShell=new Shell(MainDisplay);
		Updater=new GUIUpdaterThread(NetObj,this);
		Updater.setDaemon(true);
		
		
		//restartOpt=false;
		if(Profile.equals("SERVER")==true)
		{
			//FIX THIS ASAP
			UserProfile="SERVER";
			MainServer=(JTServer)NetObj;
			MainServer.test();
			System.out.println("GUI invoked with "+MainServer.getLocalUserIP()+":"+MainServer.getLocalUserPort());
			MainClient=null;
		}
		else if(Profile.equals("CLIENT")==true)
		{
			//FIX THIS ASAP
			UserProfile="CLIENT";
			MainClient=(JTClient)NetObj;
			MainClient.test();
			MainServer=null;
			System.out.println("GUI invoked with target "+MainClient.getTargetIP()+":"+MainClient.getTargetPort());
			System.out.println("and client "+MainClient.getLocalUserName()+"@"+MainClient.getLocalUserIP()+":"+MainClient.getLocalUserPort());
		}
		else
		{
			confirmWin(MainShell, "Internal Error! No extension of JTNetworkInterface active!", jeremeTalk.JTC.OK);
			destroy();
		}
		
		
		Calibri=new Font(MainDisplay,"Calibri",12,SWT.NORMAL);
		
		Red=new Color(MainDisplay,0x80,0,0);
		Green=new Color(MainDisplay,0,0x80,0);
		Blue=new Color(MainDisplay,0,0,0x80);
		White=new Color(MainDisplay,0xFF,0xFF,0xFF);
		Black=new Color(MainDisplay,0,0,0);
		Gray=new Color(MainDisplay,0xCC,0xCC,0xCC);
		
		
		GridLayout GlobalWinGL=new GridLayout();
		GlobalWinGL.numColumns=3;
		GlobalWinGL.marginHeight=10;
		GlobalWinGL.marginWidth=10;
		GlobalWinGL.verticalSpacing=5;
		GlobalWinGL.horizontalSpacing=5;
		
		MainShell.setLayout(GlobalWinGL);
		
		
		initWidgets();
		
		addWidgetListeners();
		
		MainShell.setMenuBar(GlobalMainMenu);
		MainShell.pack();
		
		MainShell.setLocation(centralize(MainDisplay.getPrimaryMonitor().getBounds(),MainShell));
		
		MainShell.setText("JeremeTalk v1.0 : Welcome "+NetObj.getLocalUserName()+" | You are "+UserProfile);
		
		MainShell.open();
		Updater.start();
		while(!MainShell.isDisposed())
		{	
			if(!MainDisplay.readAndDispatch())
				MainDisplay.sleep();
		}
		if(MainServer==null)
			MainClient.freePort();
		else MainServer.freePort();
		Updater.kill();
		System.out.println("GUI Destroyed!");
	}
	void sendMessageToAll(String msg)
	{
		if(UserProfile==JTC.CLIENT)
		{
			MainClient.sendTextMsg(JTC.COMMON_TEXT_MSG,null,msg);
		}
		else if(UserProfile==JTC.SERVER)
		{
			MainServer.sendTextMsg(JTC.COMMON_TEXT_MSG,null,null,msg,null,null);
		}
	}

	public void destroy()
	{
		MainShell.dispose();
		MainDisplay.dispose();
	}

	
	void initWidgets()
	{
		GlobalConfWinLab=new Label(MainShell,SWT.NORMAL|SWT.CENTER);
		GlobalUsersGroup=new Group(MainShell,SWT.NONE);
		GlobalOutWin=new Text(MainShell,SWT.MULTI|SWT.WRAP|SWT.BORDER|SWT.V_SCROLL);
		
		GlobalUsersList=new List(GlobalUsersGroup,SWT.BORDER|SWT.H_SCROLL|SWT.V_SCROLL);
		
		GlobalInpWinLab=new Label(MainShell,SWT.NORMAL|SWT.CENTER);
		GlobalInpWin=new Text(MainShell,SWT.MULTI|SWT.WRAP|SWT.BORDER|SWT.V_SCROLL);
		GlobalSendBut=new Button(MainShell,SWT.PUSH);
		GlobalClearBut=new Button(MainShell,SWT.PUSH);
		
		GlobalMainMenu=new Menu(MainShell,SWT.BAR);
		GlobalOptionsMenu=new Menu(MainShell,SWT.DROP_DOWN);
		GlobalSettingsMenu=new Menu(MainShell,SWT.DROP_DOWN);
		GlobalProfileMenu=new Menu(MainShell,SWT.DROP_DOWN);
		
		GlobalProfileItem=new MenuItem(GlobalSettingsMenu,SWT.CASCADE);
		GlobalOptionsItem=new MenuItem(GlobalMainMenu,SWT.CASCADE);
		
		GlobalOptionsItem.setMenu(GlobalOptionsMenu);
		GlobalSettingsItem=new MenuItem(GlobalMainMenu,SWT.CASCADE);
		GlobalSettingsItem.setMenu(GlobalSettingsMenu);
		GlobalProfileItem.setMenu(GlobalProfileMenu);
		GlobalExitItem=new MenuItem(GlobalOptionsMenu,SWT.CASCADE);
		GlobalConnectionItem=new MenuItem(GlobalSettingsMenu,SWT.CASCADE);
		GlobalUserNameItem=new MenuItem(GlobalSettingsMenu,SWT.CASCADE);
		GlobalAboutItem=new MenuItem(GlobalMainMenu,SWT.CASCADE);
		GlobalServerItem=new MenuItem(GlobalProfileMenu,SWT.RADIO);
		GlobalClientItem=new MenuItem(GlobalProfileMenu,SWT.RADIO);
		
		GlobalConfWinLab.setText("Conference window:");
		GlobalOutWin.setText("");
		GlobalInpWinLab.setText("Input Window:");
		GlobalInpWin.setText("");
		GlobalSendBut.setText("Send");
		GlobalClearBut.setText("Clear");
		GlobalOptionsItem.setText("Options");
		GlobalSettingsItem.setText("Settings");
		GlobalProfileItem.setText("Profile");
		GlobalExitItem.setText("Exit");
		GlobalConnectionItem.setText("Connection");
		GlobalUserNameItem.setText("User");
		GlobalAboutItem.setText("About");
		GlobalServerItem.setText("Server");
		GlobalClientItem.setText("Client");
		GlobalUsersGroup.setText("Online Users");
		
		GlobalOutWin.setFont(Calibri);
		GlobalInpWin.setFont(Calibri);
		GlobalSendBut.setFont(Calibri);
		GlobalClearBut.setFont(Calibri);
		GlobalUsersGroup.setFont(Calibri);
		GlobalUsersList.setFont(Calibri);
		GlobalConfWinLab.setFont(Calibri);
		GlobalInpWinLab.setFont(Calibri);
		
		GlobalOutWin.setBackground(White);
		GlobalInpWin.setBackground(White);
		
		GlobalConfWinLab.setLayoutData(new GridData(SWT.CENTER|SWT.FILL,SWT.CENTER|SWT.FILL,true,true,2,1));
		GlobalOutWin.setLayoutData(new GridData(SWT.CENTER|SWT.FILL,SWT.CENTER|SWT.FILL,true,true,2,1));
		((GridData)GlobalOutWin.getLayoutData()).widthHint=300;
		((GridData)GlobalOutWin.getLayoutData()).heightHint=200;
		
		GlobalInpWinLab.setLayoutData(new GridData(SWT.CENTER|SWT.FILL,SWT.CENTER|SWT.FILL,true,true,2,1));
		GlobalInpWin.setLayoutData(new GridData(SWT.CENTER|SWT.FILL,SWT.CENTER|SWT.FILL,true,true,1,2));
		((GridData)GlobalInpWin.getLayoutData()).widthHint=250;
		((GridData)GlobalInpWin.getLayoutData()).heightHint=100;
		GlobalSendBut.setLayoutData(new GridData(SWT.CENTER|SWT.FILL,SWT.CENTER|SWT.FILL,true,true,1,1));
		((GridData)GlobalSendBut.getLayoutData()).widthHint=50;
		((GridData)GlobalSendBut.getLayoutData()).heightHint=30;
		GlobalClearBut.setLayoutData(new GridData(SWT.CENTER|SWT.FILL,SWT.CENTER|SWT.FILL,true,true,1,1));
		((GridData)GlobalClearBut.getLayoutData()).widthHint=50;
		((GridData)GlobalClearBut.getLayoutData()).heightHint=30;
		GlobalUsersGroup.setLayoutData(new GridData(SWT.CENTER|SWT.FILL,SWT.FILL,true,true,1,5));
		((GridData)GlobalUsersGroup.getLayoutData()).widthHint=150;
		((GridData)GlobalUsersGroup.getLayoutData()).heightHint=300;
		
		GlobalUsersGroup.setLayout(new GridLayout(2,false));
		
		GlobalUsersList.setLayoutData(new GridData(GridData.FILL_BOTH));
		((GridData)GlobalUsersList.getLayoutData()).widthHint=150;
		((GridData)GlobalUsersList.getLayoutData()).heightHint=300;
		
		GlobalOutWin.setEditable(false);
		
		GlobalInpWin.setFocus();
		
		if(UserProfile==JTC.SERVER)
			GlobalServerItem.setSelection(true);
		else GlobalClientItem.setSelection(true);
		
	}
	void addWidgetListeners()
	{
		GlobalUsersList.addMouseListener(new MouseAdapter()
		{
			public void mouseDoubleClick(MouseEvent Evt)
			{
				if(Evt.button==1)
				{
					//OPEN INDIVIDUAL MSG WINDOW
				}
			}
		});
		GlobalSendBut.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent Evt)
			{
				System.out.println(GlobalInpWin.getText()+" to be sent!");
				sendMessageToAll(GlobalInpWin.getText());
				
			}
		});
		GlobalClearBut.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent Evt)
			{
				GlobalInpWin.setText("");
			}
		});
		GlobalExitItem.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent Evt)
			{
				if(confirmWin(MainShell,JTC.TEXT_EXITCONFIRM,JTC.YESNO)==true)
					destroy();
			}
		});
		GlobalAboutItem.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent Evt)
			{
				confirmWin(MainShell,JTC.TEXT_ABOUT,JTC.OK);
			}
		});
		GlobalConnectionItem.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent Evt)
			{
				connectionWin(MainShell);	
			}
		});
		GlobalUserNameItem.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent Evt)
			{
				userNameWin(MainShell);	
			}
		});
		GlobalServerItem.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent Evt)
			{
				if(UserProfile!=JTC.SERVER)
					 if(confirmWin(MainShell,"Are you sure you want to create a Server?",JTC.YESNOCANCEL)==true)
						if(confirmWin(MainShell,"This change requires restarting the application",JTC.OKCANCEL))
						{
							setProfile(JTC.SERVER);				
						}	
				if(UserProfile!=JTC.SERVER)
				{
					GlobalServerItem.setSelection(false);
					GlobalClientItem.setSelection(true);
				}
			}
			
		});
		GlobalClientItem.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent Evt)
			{
				if(UserProfile!=JTC.CLIENT)
					 if(confirmWin(MainShell,"Are you sure you want to be a Client?",JTC.YESNOCANCEL)==true)
						if(confirmWin(MainShell,"This change requires restarting the application",JTC.OKCANCEL))
						{
							setProfile(JTC.CLIENT);				
						}	
				if(UserProfile!=JTC.CLIENT)
				{
					GlobalServerItem.setSelection(true);
					GlobalClientItem.setSelection(false);
				}
			}
		});
		
	}
	
	public String getProfile()
	{
		return UserProfile;
	}
	public void setProfile(String newProfile)
	{
	   if(UserProfile==newProfile)
		return;
	  
	   else if(newProfile==JTC.CLIENT)
	   {
		   UserProfile=newProfile;
		   MainServer.switchToClient();
		   destroy();
		 
	   }
	   else if(newProfile==JTC.SERVER)
	   {
		   UserProfile=newProfile;
		   MainClient.switchToServer();
		   destroy();
		 
	   }
	  
	}
	Point centralize(Rectangle BaseRect,Control ChildObj)
	{	
		Point ChildSize=ChildObj.getSize();
		return new Point(BaseRect.x+(BaseRect.width-ChildSize.x)/2,BaseRect.y+(BaseRect.height-ChildSize.y)/2);
	}
	
	public boolean confirmWin(Shell ParentShell,String ArgText,int type)
	{
		Shell SubShell=new Shell(ParentShell.getDisplay());
		retValue=false;
		GridLayout SubShellGL=new GridLayout();
		SubShell.setLayout(SubShellGL);
		SubShellGL.numColumns=2;
		SubShellGL.makeColumnsEqualWidth=true;
		
		String TxtBut1,TxtBut2,TxtBut3;
		Button But1,But2,But3;
		TxtBut1="";
		TxtBut2="";
		TxtBut3="";
		if(type==JTC.OK)
		{
			SubShellGL.numColumns=1;
			TxtBut1="OK";
			
		}
		else if(type==JTC.OKCANCEL)
		{
			SubShellGL.numColumns=2;
			TxtBut1="OK";
			TxtBut2="CANCEL";
		}
		else if(type==JTC.YESNO)
		{
			SubShellGL.numColumns=2;
			TxtBut1="YES";
			TxtBut2="NO";
		}
		else if(type==JTC.YESNOCANCEL)
		{
			SubShellGL.numColumns=3;
			TxtBut1="YES";
			TxtBut2="NO";
			TxtBut3="CANCEL";
		}
		
		Label ArgQuest=new Label(SubShell,SWT.NORMAL|SWT.CENTER);
		ArgQuest.setText(ArgText);
		ArgQuest.setLayoutData(new GridData(SWT.CENTER|SWT.FILL,SWT.CENTER|SWT.FILL,true,true,SubShellGL.numColumns,1));
		
		
		But1=new Button(SubShell,SWT.PUSH);
		But1.setText(TxtBut1);
		But1.setLayoutData(new GridData(SWT.CENTER,SWT.CENTER,true,true,1,1));
		((GridData)But1.getLayoutData()).widthHint=50;
		But1.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent Evt)
			{
				((Button)Evt.widget).getShell().dispose();
				retValue=true;
			}
		});

		if(SubShellGL.numColumns>1)
		{
			But2=new Button(SubShell,SWT.PUSH);
			But2.setText(TxtBut2);
			But2.setLayoutData(new GridData(SWT.CENTER|SWT.FILL,SWT.CENTER|SWT.FILL,true,true,1,1));
			((GridData)But2.getLayoutData()).widthHint=50;
			But2.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent Evt)
				{
					((Button)Evt.widget).getShell().dispose();
					retValue=false;
				}
			});
		}

		if(SubShellGL.numColumns>2)
		{
			But3=new Button(SubShell,SWT.PUSH);
			But3.setText(TxtBut3);
			
			But3.setLayoutData(new GridData(SWT.CENTER|SWT.FILL,SWT.CENTER|SWT.FILL,true,true,1,1));
			But3.setLayoutData(new GridData(SWT.CENTER|SWT.FILL,SWT.CENTER|SWT.FILL,true,true,1,1));
			But3.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent Evt)
				{
					((Button)Evt.widget).getShell().dispose();
				}
			});
		}
		
	
			
		SubShell.pack();
		SubShell.setLocation(centralize(ParentShell.getBounds(),SubShell));
		SubShell.open();
		
		while(!SubShell.isDisposed())
		{
			if(!SubShell.getDisplay().readAndDispatch())
				SubShell.getDisplay().sleep();
		}
		
		return retValue;
	}
	
	void connectionWin(Shell ParentShell)
	{
		
		GPFlag=false;
		GPFlag2=false;
		Shell SubShell=new Shell(ParentShell.getDisplay());
		GridLayout SubShellGL=new GridLayout();
		SubShell.setLayout(SubShellGL);
		SubShellGL.numColumns=3;
		SubShellGL.makeColumnsEqualWidth=false;
		
		String Portval="",IPval2="",Portval2="";
		
		
		Label ServerIPLab,ServerIPLab2,ServerPortLab,ServerPortLab2;
		Text ServerIPInp,ServerIPInp2,ServerPortInp,ServerPortInp2;
		Button ApplyBut,CancelBut,TestBut;
		
		ServerIPLab=new Label(SubShell,SWT.NORMAL|SWT.CENTER);
		ServerIPLab.setLayoutData(new GridData());
		
		ServerIPInp=new Text(SubShell,SWT.SINGLE|SWT.BORDER);
		ServerIPInp.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,false,2,1));
		
		
		ServerPortLab=new Label(SubShell,SWT.NORMAL|SWT.CENTER);
		ServerPortLab.setLayoutData(new GridData());
		
		ServerPortInp=new Text(SubShell,SWT.SINGLE|SWT.BORDER);
		ServerPortInp.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,false,2,1));
		
		ServerIPLab.setText("Your IP Address :");
		ServerPortLab.setText("Your Connection Port :");
		
		ServerIPInp.setEnabled(false);
		
		ServerIPLab2=null;
		ServerIPInp2=null;
		ServerPortLab2=null;
		ServerPortInp2=null;
		
		
		if(UserProfile==JTC.SERVER)
		{
			ServerIPInp.setText(MainServer.getLocalUserIP());
			ServerPortInp.setText(MainServer.getLocalUserPort());
		
		}
		else if(UserProfile==JTC.CLIENT)
		{
			ServerIPLab2=new Label(SubShell,SWT.NORMAL|SWT.CENTER);
			ServerIPInp2=new Text(SubShell,SWT.SINGLE|SWT.BORDER);
			ServerPortLab2=new Label(SubShell,SWT.NORMAL|SWT.CENTER);
			ServerPortInp2=new Text(SubShell,SWT.SINGLE|SWT.BORDER);
			
			ServerIPInp.setText(MainClient.getLocalUserIP());
			ServerPortInp.setText(MainClient.getLocalUserPort());
			
			
			ServerIPLab2.setLayoutData(new GridData());
			
			
			ServerIPInp2.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,false,2,1));
			
			
			
			ServerPortLab2.setLayoutData(new GridData());
			
			
			ServerPortInp2.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,false,2,1));
			
			ServerIPLab2.setText("Server IP Address :");
			ServerPortLab2.setText("Server Connection Port :");
			
			
			ServerIPInp2.setText(MainClient.getTargetIP());
			ServerPortInp2.setText(MainClient.getTargetPort());
			
		}
		TestBut=new Button(SubShell,SWT.PUSH);
		TestBut.setLayoutData(new GridData());
		TestBut.setText("TEST");
		TestBut.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent Evt)
			{
				GPFlag2=true;
			}
		});
		if(UserProfile==JTC.SERVER)
			TestBut.setEnabled(false);
		ApplyBut=new Button(SubShell,SWT.PUSH);
		ApplyBut.setLayoutData(new GridData());
		ApplyBut.setText("OK");
		ApplyBut.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent Evt)
			{
				if(confirmWin(((Button)Evt.widget).getShell(),"Are you sure you want to save changes?",JTC.YESNO)==true)
				{
				 GPFlag=true;
				}
			}
		});
		CancelBut=new Button(SubShell,SWT.PUSH);
		CancelBut.setLayoutData(new GridData());
		CancelBut.setText("Cancel");
		CancelBut.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent Evt)
			{
				((Button)Evt.widget).getShell().dispose();
			}
		});
			
		SubShell.pack();
		SubShell.setLocation(centralize(ParentShell.getBounds(),SubShell));
		SubShell.open();
		
		while(!SubShell.isDisposed())
		{
			
			if(!SubShell.getDisplay().readAndDispatch())
				SubShell.getDisplay().sleep();
			if(GPFlag==true)
			{
				Portval=ServerPortInp.getText();
				if(UserProfile==JTC.CLIENT)
				{
					IPval2=ServerIPInp2.getText();
					Portval2=ServerPortInp2.getText();
				}
				SubShell.dispose();
			}
			if(GPFlag2==true)
			{
				if(ServerIPInp.getText().equals(ServerIPInp2.getText()) && ServerPortInp.getText().equals(ServerPortInp2.getText()))
					confirmWin(SubShell,"Ofcourse you are up and running!", JTC.OK);
				else if(MainClient.testHostActive(ServerIPInp2.getText(),ServerPortInp2.getText())==true)
					confirmWin(SubShell,"The Server is Active!", JTC.OK);
				else
					confirmWin(SubShell,"The Server is Down!", JTC.OK);
				GPFlag2=false;
			}
		}
		if(GPFlag==true)
		{
			if(UserProfile==JTC.SERVER)
			{
				MainServer.setLocalUserPort(Portval);
			}
			else if(UserProfile==JTC.CLIENT)
			{
				MainClient.setLocalUserPort(Portval);	
				MainClient.setTargetHostIP(IPval2);	
				MainClient.setTargetHostPort(Portval2);
			}
			
		}
		
			
	}
	
	
	void userNameWin(Shell ParentShell)
	{
		
		GPFlag=false;
		Shell SubShell=new Shell(ParentShell.getDisplay());
		GridLayout SubShellGL=new GridLayout();
		SubShell.setLayout(SubShellGL);
		SubShellGL.numColumns=2;
		SubShellGL.makeColumnsEqualWidth=true;
		
		String UserName="";
		
		
		Label UserNameLab;
		Text UserNameInp;
		Button ApplyBut,CancelBut;
		
		UserNameLab=new Label(SubShell,SWT.NORMAL|SWT.CENTER);
		UserNameLab.setLayoutData(new GridData());
		UserNameLab.setText("Your User Name:");
		
		UserNameInp=new Text(SubShell,SWT.SINGLE|SWT.BORDER);
		UserNameInp.setLayoutData(new GridData());
		
		
		ApplyBut=new Button(SubShell,SWT.PUSH);
		ApplyBut.setLayoutData(new GridData());
		ApplyBut.setText("Apply");
		ApplyBut.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent Evt)
			{
				if(confirmWin(((Button)Evt.widget).getShell(),"Are you sure you want to save changes?",JTC.YESNO)==true)
				{
				 GPFlag=true;
				}
			}
		});
		CancelBut=new Button(SubShell,SWT.PUSH);
		CancelBut.setLayoutData(new GridData());
		CancelBut.setText("Cancel");
		CancelBut.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent Evt)
			{
				((Button)Evt.widget).getShell().dispose();
			}
		});
		if(UserProfile==JTC.SERVER)
		{
			UserNameInp.setText(MainServer.getLocalUserName());
		}
		else if(UserProfile==JTC.CLIENT)
		{
			UserNameInp.setText(MainClient.getLocalUserName());
		}
		SubShell.pack();
		SubShell.setLocation(centralize(ParentShell.getBounds(),SubShell));
		SubShell.open();
		
		while(!SubShell.isDisposed())
		{
			
			if(!SubShell.getDisplay().readAndDispatch())
				SubShell.getDisplay().sleep();
			if(GPFlag==true)
			{
				UserName=UserNameInp.getText();
				SubShell.dispose();
			}
		}
		if(GPFlag==true)
		{
			if(UserProfile==JTC.SERVER)
			{
				if(MainServer.setLocalUserName(UserName))
					MainShell.setText("JeremeTalk v1.0 : Welcome "+UserName+" | You are "+UserProfile);
			}
			else if(UserProfile==JTC.CLIENT)
			{
				if(MainClient.setLocalUserName(UserName))
					MainShell.setText("JeremeTalk v1.0 : Welcome "+UserName+" | You are "+UserProfile);
			}
			
		}

	}
	public void appendMessage(String msg)
	{
		System.out.println("trying to append msg");
		GlobalOutWin.append(msg);	
	}
	public void appendUser(String UserName)
	{
		System.out.println("Adding user to GUI = "+UserName);
		GlobalUsersList.add(UserName);
	}
	public void removeUser(String UserName)
	{
		GlobalUsersList.remove(UserName);
	}
	public void removeAllUsers()
	{
		GlobalUsersList.removeAll();
	}
	
}