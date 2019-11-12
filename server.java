package AOSp3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;



public class server {

	protected final String[] clientIP= {"10.176.69.56","10.176.69.57"};
	protected final String[] serverIP= {"10.176.69.51","10.176.69.52","10.176.69.53","10.176.69.54","10.176.69.55"};
	protected final String MServerIP = "10.176.69.58";
	
	public Socket MServerSocket;
	public DataInputStream MServerInStream;
	public DataOutputStream MServerOutStream;
	
	public Socket[] clientSockets;
	public DataInputStream[] clientInStreams;
	public DataOutputStream[] clientOutStreams;
	
	final int MServerPort=6000;
	
	final int clientPort=6000;
	
	protected int id;
	public server(int id)
	{
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
		
		for(int x=0;x<clientIP.length;x++) //instantiates connections to clients
		{
			try {
				System.out.print("Waiting on connection from client "+x+" ...");
				ServerSocket s= new ServerSocket(clientPort+x);
				clientSockets[x]=s.accept();
				System.out.print("success!\n");
				clientInStreams[x]=new DataInputStream(clientSockets[x].getInputStream());
				clientOutStreams[x]=new DataOutputStream(clientSockets[x].getOutputStream());
				
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
		
		int id=Integer.parseInt(config.nextLine());
		//int fileNum=Integer.parseInt(config.nextLine());
		//id clientcount #files
		config.close();
		new server(id);
	}
}
