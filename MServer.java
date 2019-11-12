package AOSp3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class MServer {
	
	protected final String[] clientIP= {"10.176.69.56","10.176.69.57"};
	protected final String[] serverIP= {"10.176.69.51","10.176.69.52","10.176.69.53","10.176.69.54","10.176.69.55"};
	protected final String MServerIP = "10.176.69.58";

	protected int numClients,numServers;
	final int clientStartingPort=5000;
	final int serverStartingPort=6000;
	protected Socket[] clientSockets;
	protected Socket[] serverSockets;
	protected DataInputStream[] clientInStreams;
	protected DataOutputStream[] clientOutStreams;
	protected DataInputStream[] serverInStreams;
	protected DataOutputStream[] serverOutStreams;
	
	
	public MServer(int numClients,int numServers)
	{
		this.numClients=numClients;
		this.numServers=numServers;
		clientSockets=new Socket[numClients];
		serverSockets=new Socket[numServers];
		clientInStreams=new DataInputStream[numClients];
		clientOutStreams=new DataOutputStream[numClients];
		serverInStreams=new DataInputStream[numServers];
		serverOutStreams=new DataOutputStream[numServers];
		
		for(int x=0;x<numClients;x++)
		{
			try 
			{
				ServerSocket ss= new ServerSocket(clientStartingPort+x);
				clientSockets[x]=ss.accept();
				clientInStreams[x]=new DataInputStream(clientSockets[x].getInputStream());
				clientOutStreams[x]=new DataOutputStream(clientSockets[x].getOutputStream());
				System.out.println("Client "+x+" connected");
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for(int x=0;x<numServers;x++)
		{
			try 
			{
				ServerSocket ss= new ServerSocket(serverStartingPort+x);
				serverSockets[x]=ss.accept();
				serverInStreams[x]=new DataInputStream(serverSockets[x].getInputStream());
				serverOutStreams[x]=new DataOutputStream(serverSockets[x].getOutputStream());
				System.out.println("Server "+x+" connected");
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException 
	{
		File c=new File(args[0]);
		Scanner config=new Scanner(c);
		
		//int fileNum=Integer.parseInt(config.nextLine());
		int numClients=Integer.parseInt(config.nextLine());
		int numServers=Integer.parseInt(config.nextLine());
		//id clientcount #files
		config.close();
		new MServer(numClients,numServers);
	}
}
