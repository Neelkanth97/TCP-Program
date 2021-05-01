import java.io.*;
import java.net.*;
import java.util.*;

class DownloadClient {
  String ipAddress;
  int sPortNumber;
  int portNumber;
  String fileName;
  String aFileName;
  Socket connectingSocket;

  public DownloadClient(String aPort, String sPort , String aIP) {
    
    try {
      portNumber = Integer.parseInt(aPort);
    } catch(NumberFormatException e) {
      System.out.println("Your port number " + aPort + " is not a valid input. Required: Port Number (Integer).");
      System.exit(0);
    }
	try {
      sPortNumber = Integer.parseInt(sPort);
    } catch(NumberFormatException e) {
      System.out.println("Your port number " + sPort + " is not a valid input. Required: Port Number (Integer).");
      System.exit(0);
    }
	try {
      InetAddress Addr;
      Addr = InetAddress.getByName(aIP);
      ipAddress = aIP;
    } catch(UnknownHostException e) {
      System.out.println("Your IP address " + aIP + " is in incorrect format. Required: IP Address (IP Address Format)");
      System.exit(0);
    }
	 try{
	 BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
	 System.out.println("Enter the file Name you wish to download");
	 aFileName=br.readLine();
	 fileName=aFileName;
	 }
	 catch(IOException e){
		e.printStackTrace();	
	 }
		
	//File file=new File (fileName);
    //Scanner inputFile=new Scanner (file);
  }

  public void connectToConnectableSocket(int aPort, int sPort, String aIP) {
    try {
      connectingSocket = new Socket(aIP, sPort);
      System.out.println("Connected to the server at: <" +sPort+ "," + aIP + ", " + aPort + ">.");
      sendMessages(connectingSocket);
      closeConnection(connectingSocket);
    } catch(UnknownHostException e) {
      System.out.println("Host unreachable at <" +sPort+ "," + aIP + ", " + aPort + ">.");
    } catch(ConnectException e) {
      System.out.println("Connection timed out. Could not connect to <" +sPort+ "," + aIP + ", " + aPort + ">.");
    } catch (IOException e) {
      System.out.println(e);
    }
  }

  public void closeConnection(Socket aSocket) {
    try {
      aSocket.close();
    } catch(IOException e)
    {
      System.out.println(e);
    }
  }

  public void recieveMessage(Socket aSocket) {
    try {
    int fileBytes;
	
    //fileName = aFileName;
		
     InputStream inputStream = aSocket.getInputStream();
     DataInputStream dataInputStream = new DataInputStream(inputStream);
	  
      fileName = dataInputStream.readUTF();
      OutputStream outputStream = new FileOutputStream("from_server_" + fileName);
      long fileSize = dataInputStream.readLong();
      byte[] fileBuffer = new byte[1024];
      while(fileSize > 0 && (fileBytes = dataInputStream.read(fileBuffer, 0, (int) Math.min(fileBuffer.length, fileSize))) != 1) {
        outputStream.write(fileBuffer, 0, fileBytes);
        fileSize -= fileBytes;
      }
      outputStream.close();
      inputStream.close();
      System.out.println(fileName + " recieved from the server. Saved as: from_server_" + fileName);
    } 
	catch(IOException e) {
      System.out.println(e);
    }
  }

  public void sendMessages(Socket aSocket) {
    try {
      DataOutputStream outputToServer = new DataOutputStream(aSocket.getOutputStream());
      outputToServer.writeBytes(fileName + "\n");
      //Recieving the messages
      recieveMessage(aSocket);
    } catch(IOException e) {
      System.out.println(e);
    }
  }

  public static void main(String[] args) throws Exception {
    if(args.length != 3) {
      System.out.println("Incorrect number of arguments. Required: 3 (Port Number, Server Port Number, IP Address)");
      System.exit(0);
    }
    DownloadClient myClient = new DownloadClient(args[0], args[1], args[2]);
    System.out.println("Attempting to connect to nominated server...");
    myClient.connectToConnectableSocket(myClient.portNumber, myClient.sPortNumber, myClient.ipAddress);
  }
}
