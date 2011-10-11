package jeremeTalk;


public class ControlManager extends UserManager
{
	//IMPLEMENT USING VECTOR
	
	public ControlManager()
	{
		super();
	}
	public String createControlMessage(int Type,String[] ControlData)
	{
		if(Type==JTC.ADD_USER)
		{
			//ControlMsg FORMAT : JTC.TEST_MSG:JTC.TEST_Q_MSG:testip:testport
			return JTC.ADD_USER+JTC.DELIM+ControlData[0]+JTC.DELIM+ControlData[1]+JTC.DELIM+ControlData[2];
		}
		if(Type==JTC.TEST_Q_MSG)
		{
			//ControlMsg FORMAT : JTC.TEST_MSG:JTC.TEST_Q_MSG:testip:testport
			return JTC.TEST_MSG+JTC.DELIM+JTC.TEST_Q_MSG+JTC.DELIM+ControlData[0]+JTC.DELIM+ControlData[1];
		}
		if(Type==JTC.TEST_A_MSG)
		{
			//ControlMsg FORMAT : JTC.TEST_MSG:JTC.TEST_A_MSG
			return JTC.TEST_MSG+JTC.DELIM+JTC.TEST_A_MSG;
		}
		if(Type==JTC.PRIVATE_TEXT_MSG)
		{
			//ControlMsg FORMAT : JTC.TEXT_MSG:from:to:msg
			return JTC.PRIVATE_TEXT_MSG+JTC.DELIM+ControlData[0]+JTC.DELIM+ControlData[1]+JTC.DELIM+ControlData[2];
		}
		if(Type==JTC.COMMON_TEXT_MSG)
		{
			//ControlMsg FORMAT : JTC.COMMON_TEXT_MSG:from:msg
			return JTC.COMMON_TEXT_MSG+JTC.DELIM+ControlData[0]+JTC.DELIM+ControlData[1];
		}
		return "";
	}
	public String[] parseControlMessage(String msg)
	{
		if(msg=="" || msg==null)
			return null;
		return msg.split(JTC.DELIM);	
	}
	
	
}