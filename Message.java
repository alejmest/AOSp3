package AOSp3;

/**
 * 
 * @author aleja
 *
 */
public class Message implements Comparable<Message>
{
	private String type;
	private int id;
	private long timestamp;
	private String fileName;
	private String contents;
	/**
	 * 
	 * @param type: The type of request the message is sending
	 * @param filename: The file in question on the server
	 * @param senderId: The id of the sender of the request
	 * @param senderIp: the ip of the sender in request
	 * @param contents: the contents of the message
	 */
	public Message(String type,String fileName,int senderId,String contents)
	{
		this.type=type;
		this.id=senderId;
		this.fileName=fileName;
		this.contents=contents;
		this.timestamp=System.currentTimeMillis();
	}
	/**
	 * 
	 * @param type: The type of request the message is sending
	 * @param filename: The file in question on the server
	 * @param senderId: The id of the sender of the request
	 * @param senderIp: the ip of the sender in request
	 * @param timestamp: the timestamp of the message
	 * @param contents: the contents of the message
	 */
	public Message(String type,String fileName,int senderId,long timestamp,String contents)
	{
		this.fileName=fileName;
		this.timestamp=timestamp;
		this.contents=contents;
	}
	/**
	 * 
	 * @param message: Parses toString() of the message back to a message object manually
	 */
	public Message(String message)
	{
		String[] messageFields=message.split(",");
		if (messageFields.length!=5)
		{
			System.out.println("Received malformed Message string! \t"+message);
		}
		else
		{
			type=messageFields[0];
			fileName=messageFields[1];
			id=Integer.parseInt(messageFields[2]);
			timestamp=Long.parseLong(messageFields[3]);
			contents=messageFields[4];
		}
	}
	public long getTimestamp()
	{
		return timestamp;
	}
	public String getType()
	{
		return type;
	}
	public String getFileName()
	{
		return fileName;
	}
	public void setType(String fn)
	{
		type=fn;
	}
	public int getId()
	{
		return id;
	}
	public String getContents()
	{
		return contents;
	}
	/**
	 * returns -1 if this timestamp is less than the param
	 * returns 0 if this timestamp is the same as the param
	 * returns 1 if this timestamp is greater than the param
	 */
	@Override
	public int compareTo(Message b)//compareTo is based on timestamps between two Message objects 
	{ 
		if (timestamp<b.getTimestamp())
			return -1;
		else if (timestamp==b.getTimestamp())
		{
			if(id<b.getId())
				return -1;
			else if(id==b.getId())
				return 0;
			else
				return 1;
		}
		else
			return 1;
	}
	public String toString()
	{
		return type+","+fileName+","+id+","+timestamp+","+contents;
	}
	
}
