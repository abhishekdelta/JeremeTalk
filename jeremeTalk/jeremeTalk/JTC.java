package jeremeTalk;


public class JTC
{
	
	//UserManager Options
	public static final int CAPACITY_INI=50;
	public static final int CAPACITY_INC=25;
	//ServerCommunicationThread options
	public static final int WAIT_FOR_CONNECTED=1000;
	public static final int WAIT_FOR_DISCONNECTED=10000;
	
	//ClientCommunicationThread options
	public static final int WAIT=10000;
	
	//Following are the CONFIRM WINDOW OPTIONS
	public static final int YESNO=1;
	public static final int YESNOCANCEL=2;
	public static final int OK=4;
	public static final int OKCANCEL=8;
	
	
	public static final String TEXT_EXITCONFIRM="Are you sure you want to exit?";
	public static final String TEXT_ABOUT="JeremeTalk v1.0\nby Jereme (Abhishek Shrivastava)\nProtected by GNU GPL";
	
	//Following are the Server/Client Related Options
	public static final String PROFILE="PROFILE";
	public static final String CLIENT="CLIENT";
	public static final String SERVER="SERVER";
	public static final String LOCALUSERIP="LOCAL_USER_IP";
	public static final String CONFIG_FILE="config.ini";
	public static final String LOCALUSERPORT="LOCAL_USER_PORT";
	public static final String LOCALUSERNAME="USER_NAME";
	public static final String SERVERIP="SERVER_IP";
	public static final String SERVERPORT="SERVER_PORT";
	
	//Following are the ControlMsg Options
	public static final String DELIM=":~:";
	public static final int TEST_MSG=0;
	public static final int PRIVATE_TEXT_MSG=1;
	public static final int COMMON_TEXT_MSG=2;
	
	public static final int TEST_Q_MSG=4;
	public static final int TEST_A_MSG=8;
	public static final int ADD_USER=16;
	public static final int DEL_USER=32;
	
	static{};
	
}
