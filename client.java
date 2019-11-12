package AOSp3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class client {
	protected int id;
	protected final String[] clientIP= {"10.176.69.56","10.176.69.57"};
	protected final String[] serverIP= {"10.176.69.51","10.176.69.52","10.176.69.53","10.176.69.54","10.176.69.55"};
	protected final String MServerIP = "10.176.69.58";
	final int MServerPort=5000;
	final int serverPort=6000;
	
	public Socket[] serverSockets;
	public DataInputStream[] serverInStreams;
	public DataOutputStream[] serverOutStreams;
	
	public Socket MServerSocket;
	public DataInputStream MServerInStream;
	public DataOutputStream MServerOutStream;
	
	public client(int id)
	{
		serverSockets=new Socket[serverIP.length];
		serverInStreams=new DataInputStream[serverIP.length];
		serverOutStreams=new DataOutputStream[serverIP.length];
		this.id=id;
		
		try
		{
			MServerSocket=new Socket(MServerIP,MServerPort+id);
			MServerInStream=new DataInputStream(MServerSocket.getInputStream());
			System.out.println("Connected to Metadata Server");
		}
		catch (UnknownHostException e) 
		{
			// TODO Auto-generated catch block
			System.out.print("fail\n");
			e.printStackTrace();
			System.exit(0);
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		
		for(int x=0;x<serverIP.length;x++)
		{
			try 
			{
				serverSockets[x]=new Socket(serverIP[x],serverPort+id);
				serverInStreams[x]=new DataInputStream(serverSockets[x].getInputStream());
				serverOutStreams[x]=new DataOutputStream(serverSockets[x].getOutputStream());
				System.out.print("server"+x+"=connected\t");
			} 
			catch (UnknownHostException e) 
			{
				// TODO Auto-generated catch block
				System.out.print("fail\n");
				e.printStackTrace();
				System.exit(0);
			}
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
			}
			
		}
	}
	public static void main(String[] args)
	{
		File c=new File(args[0]);
		Scanner config;
		try {
			config = new Scanner(c);
			int id=Integer.parseInt(config.nextLine());
			
			new client(id);
			config.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
