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
	final int reconnectionPort=7000;
	
	public Socket[] serverSockets;
	public DataInputStream[] serverInStreams;
	public DataOutputStream[] serverOutStreams;
	
	public Socket MServerSocket;
	public DataInputStream MServerInStream;
	public DataOutputStream MServerOutStream;
	
	public ArrayList<Integer> cohort;
	
	public client(int id)
	{
		serverSockets=new Socket[serverIP.length];
		serverInStreams=new DataInputStream[serverIP.length];
		serverOutStreams=new DataOutputStream[serverIP.length];
		cohort=new ArrayList<Integer>();
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
		
		Message start=new Message(MServerInStream.readUTF());
		if(start.getType().equals("START"))
		{
			System.out.println("Received start message");
			(new MServerListener()).start();
			
			for(int x=0;x<serverIP.length;x++)
			{
				(new ServerListener(id)).start();
			}
		}
		else
		{
			System.out.println("Error in starting... ");
			shutdown();
			System.exit(0);
		}
	}
	class ServerListener extends Thread
	{
		int threadId;
		public ServerListener(int id)
		{
			threadId=id;
		}
		public void run()
		{
			while(true)
			{
				Message msg=new Message(serverInStreams[threadId].readUTF());
				if(msg.getType().equals("READYACK"))
				{
					cohort.remove(msg.getId());
					while(cohort.size()>0)
					{
						continue;
					}
					Message commit=new Message("COMMIT",msg.getFileName(),id,contents);
					serverOutStreams[threadId].writeUTF(commit.toString);
					serverOutStreams[threadId].flush();
				}
				else if(msg.getType().equals("COMMITACK"))
				{
					System.out.println("Appended successfully to file"); // TODO: Add data fields to this log line
					Message success=new Message("SUCCESS",msg.getFilename(),id,contents);
					MServerOutStream.writeUTF(success);
					MServerOutStream.flush();
				}
				else
				{
					System.out.println("Received malformed message: "+msg.toString());
				}
			}
		}
	}
	class MServerListener extends Thread
	{
		public MServerListener()
		{
			
		}
		public void run()
		{
			
		}
		
	}
	public void shutdown()
	{
		System.out.println("Shutting down system");
		try 
		{
			MServerInStream.close();
			MServerOutStream.close();
			MServerSocket.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		for(int x=0;x<serverSockets.length;x++)
		{
			try
			{
				serverInStreams[x].close();
				serverOutStreams[x].close();
				serverSockets[x].close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
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
