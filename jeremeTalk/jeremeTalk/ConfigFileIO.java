package jeremeTalk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class ConfigFileIO
{
	String ConfigFileName;
	File ConfigFile;
	boolean FileExists;
	public ConfigFileIO(String name)
	{
		ConfigFileName=name;
		ConfigFile=new File(name);
		FileExists=ConfigFile.exists();
	}
	public boolean exists()
	{
		return FileExists;
	}
	public void setAttribute(String param,String value)
	{
		System.out.println("setting attribute : "+param+"="+value);
		boolean found=false;
		try
		{
		
			if(FileExists==false)
			{
				ConfigFile.createNewFile();
				FileExists=true;
				
			}
			 String data="",contents="";
			 BufferedReader BR=new BufferedReader(new InputStreamReader(new FileInputStream(ConfigFile)));
			 while((data=BR.readLine())!=null)
			 {
				 if(data.startsWith(param+"=")==true)
				 {
					 data=param.toUpperCase()+"="+value+";";
					 found=true;
				 }
				 contents+=data+'\n';
				 
			 }
			 BR.close();
			
			BufferedWriter BW=new BufferedWriter(new FileWriter(ConfigFile,false));
			if(found==false)
			{
				contents+=param.toUpperCase()+"="+value+";";
			}
			BW.append(contents);
			BW.close();
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
		
	}
	public String getAttribute(String param)
	{
		 BufferedReader BR=null;
		 try
		 {
			 if(FileExists==false)
			 {
				return null; 
			 }
			 BR=new BufferedReader(new InputStreamReader(new FileInputStream(ConfigFile)));
		 }
		 catch(Exception exc)
		 {
			 exc.printStackTrace();
		 }
		 String data="",key="";
		 try
		 {
			 
			 while((data=BR.readLine())!=null)
			 {
				 if(data.startsWith(param+"=")==true)
				 {
					 key=data.substring(param.length()+1,data.length()-1);
					 break;
				 }
			 }
			 BR.close();
		 }
		 catch(Exception exc)
		 {
			 exc.printStackTrace();
		 }
		 return key;
	}
}