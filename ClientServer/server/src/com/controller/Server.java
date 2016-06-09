//refernce http://www.cn-java.com/download/data/book/socket_chat.pdf
// reference for major part of the project is http://www.codeproject.com/Articles/524120/A-Java-Chat-Application

package com.controller;
//import the necessary packages
import java.io.*;
import java.net.*;
import com.controller.Serverconsole;
//class for Serverthread that inherts from thread class
class ServerThread extends Thread { 
    //declare and initialize the necessary objects and variables
    public Server server = null;
    public Socket socket = null;
    public int ID = -1;
    public String username = "";
    public ObjectInputStream Input  =  null;
    public ObjectOutputStream Output = null;
    public Serverconsole serv;
    //a constructor for the serverthread class with server and socket as parameters
    public ServerThread(Server _server, Socket _socket){  
    	super();
        server = _server;
        socket = _socket;
        ID     = socket.getPort();
        serv = _server.inter;
    }
    //method to send message with msg object as parameter
    public void send(Chat msg){
        //try block is initiated to manage with exceptions
        try {
            //write the msg object to the ObjectOutputStream
            Output.writeObject(msg);
            //flushes this output stream and forces any buffered output bytes to be written out
            Output.flush();
        } 
        //catch block is used to catch the exceptions if any is thrown in the above try block
        catch (IOException ex) {
            System.out.println("Exception [SocketClient : send(...)]");
        }
    }
    //method to get the ID
    public int getID(){  
	    return ID;
    }
    //indicates that the named compiler warnings should be suppressed in the annotated element
    @SuppressWarnings("deprecation")
    //run method to run the server thread
    public void run(){  
    	serv.jTextArea1.append("\nServer  " + ID + " running.");
        while (true){ 
            //try block is initiated to manage with exceptions
    	    try{  
                //read an object from the ObjectInputStream and typecasts to chat and store it in object "msg"
                Chat msg = (Chat) Input.readObject();
    	    	//the server thread handles the ID and the msg object
                server.handle(ID, msg);
            }
            //catch block is used to catch the exceptions if any is thrown in the above try block
            catch(Exception ioe){  
            	System.out.println(ID + " ERROR reading: " + ioe.getMessage());
                server.remove(ID);
                stop();
            }
        }
    }
    //method to open the thread 
    public void open() throws IOException {  
        //the ObjectOutputStream write the data to the socket's output stream   
        Output = new ObjectOutputStream(socket.getOutputStream());
        //flushes this output stream and forces any buffered output bytes to be written out
        Output.flush();
        //the ObjectInputStream reads the data to the socket's input stream
        Input = new ObjectInputStream(socket.getInputStream());
    }
    //method to close the thread
    public void close() throws IOException {  
    	if (socket != null)    socket.close();
        if (Input != null)  Input.close();
        if (Output != null) Output.close();
    }
}
//server class with the runnable interface
public class Server implements Runnable {
    //declare and initialize the necessary objects and variables
    public ServerThread clients[];
    public ServerSocket server = null;
    public Thread       thread = null;
    public int clientCount = 0; 
    public int port=1300;
    public Serverconsole inter;
    public Database db;
    //constructor of the server class with server design frame object as the parameter
    public Server(Serverconsole frame){
        //a thread is assigned to each of the client
        clients = new ServerThread[50];
        inter = frame;
        //a database object is been assigned for the client
        db = new Database(inter.filePath);
        //try block is initiated to manage with exceptions
	try{  
            //serversocket is initialized with port 
	    server = new ServerSocket(port);
            //the serversocket's local accessible port is got
            port = server.getLocalPort();
            //appropriate information message is displayed
	    inter.jTextArea1.append("Server started. IP : " + InetAddress.getLocalHost() + ", Port : " + server.getLocalPort());
	    start(); 
        }
        //catch block is used to catch the exceptions if any is thrown in the above try block
	catch(IOException ioe){  
            inter.jTextArea1.append("Can't bind to port : " + port + "\ntrying again"); 
            inter.RetryStart(0);
	}
    }
    //constructor of the server class with design object and port number
    public Server(Serverconsole frame, int Port){
        //a thread is assigned to each of the client
        clients = new ServerThread[50];
        inter = frame;
        port = Port;
         //a database object is been assigned for the client
        db = new Database(inter.filePath);
        //try block is initiated to manage with exceptions
	try{  
            //serversocket is initialized with port 
	    server = new ServerSocket(port);
             //the serversocket's local accessible port is got
            port = server.getLocalPort();
            //appropriate information message is displayed
	    inter.jTextArea1.append("Server startet. IP : " + InetAddress.getLocalHost() + ", Port : " + server.getLocalPort());
	    start(); 
        }
        //catch block is used to catch the exceptions if any is thrown in the above try block
	catch(IOException ioe){  
            //appropriate error message is displayed
            inter.jTextArea1.append("\nCan not bind to port " + port + ": " + ioe.getMessage()); 
	}
    }
    //run method for thread
    public void run(){ 
        //checks if thread exists
	while (thread != null){  
            //try block is initiated to manage with exceptions
            try{  
                //appropriate information message is displayed
		inter.jTextArea1.append("\nWaiting for a client ..."); 
	        //the thread is added to the socketserver
                addThread(server.accept()); 
	    }
            //catch block is used to catch the exceptions if any is thrown in the above try block
	    catch(Exception ioe){ 
                //appropriate error message is displayed
                inter.jTextArea1.append("\nServer accept error: \n");
                inter.RetryStart(0);
	    }
        }
    }
    //start method for the thread
    public void start(){  
    	if (thread == null){  
            thread = new Thread(this); 
	    thread.start();
	}
    }
    //indicates that the named compiler warnings should be suppressed in the annotated element
    @SuppressWarnings("deprecation")
    //method to  stop the thread
    public void stop(){  
        if (thread != null){  
            thread.stop(); 
	    thread = null;
	}
    }
    //method to find the client with ID as parameter
    private int findClient(int ID){ 
        //checks for the client within the clientcount and returns the location if it is present
    	for (int i = 0; i < clientCount; i++){
        	if (clients[i].getID() == ID){
                    return i;
                }
	}
	return -1;
    }
    //method to handle the thread with ID and msg object as parameters
    public synchronized void handle(int ID, Chat msg){  
        //if the msg's content is bye then signs off the ID with notification
	if (msg.content.equals(".bye")){
            //notification message is made
            Announce("signout", "SERVER", msg.sender);
            remove(ID); 
	}
        // checks if the msg's type is login credential
	else{
            if(msg.type.equals("login")){
                // checks if it is a user thread
                if(findUserThread(msg.sender) == null){
                    //checks if the user is there in database
                    if(db.checkLogin(msg.sender, msg.content)){
                        //the client ID is found and assigned as sender
                        clients[findClient(ID)].username = msg.sender;
                        clients[findClient(ID)].send(new Chat("login", "SERVER", "TRUE", msg.sender));
                        //notification message is made
                        Announce("newuser", "SERVER", msg.sender);
                        //sender added to the list
                        SendUserList(msg.sender);
                    }
                    else{
                        //display the appropriate error message
                        clients[findClient(ID)].send(new Chat("login", "SERVER", "FALSE", msg.sender));
                    } 
                }
                else{
                    //display the appropriate error message
                    clients[findClient(ID)].send(new Chat("login", "SERVER", "FALSE", msg.sender));
                }
            }
            //check if the msg is of type "message"
            else if(msg.type.equals("message")){
                //if all recipients is selected
                if(msg.recipient.equals("All")){
                    //notification message is made
                    Announce("message", msg.sender, msg.content);
                }
                else{
                    //to send to a particular individual
                    findUserThread(msg.recipient).send(new Chat(msg.type, msg.sender, msg.content, msg.recipient));
                    clients[findClient(ID)].send(new Chat(msg.type, msg.sender, msg.content, msg.recipient));
                }
            }
            // checks if the msg's type is test  condition
            else if(msg.type.equals("test")){
                clients[findClient(ID)].send(new Chat("test", "SERVER", "OK", msg.sender));
            }
            //checks if the msg object's type is equal to sign up
            else if(msg.type.equals("signup")){
                // checks if it is a user thread
                if(findUserThread(msg.sender) == null){
                    //checks if the user is there in database
                    if(!db.userExists(msg.sender)){
                        //adding the user to the database
                        db.addUser(msg.sender, msg.content);
                        //the client ID is found and assigned to appropriate information
                        clients[findClient(ID)].username = msg.sender;
                        clients[findClient(ID)].send(new Chat("signup", "SERVER", "TRUE", msg.sender));
                        clients[findClient(ID)].send(new Chat("login", "SERVER", "TRUE", msg.sender));
                        //notification message is made
                        Announce("newuser", "SERVER", msg.sender);
                        SendUserList(msg.sender);
                    }
                    else{
                        //display the appropriate error message
                        clients[findClient(ID)].send(new Chat("signup", "SERVER", "FALSE", msg.sender));
                    }
                }
                else{
                    //display the appropriate error message
                    clients[findClient(ID)].send(new Chat("signup", "SERVER", "FALSE", msg.sender));
                }
            }
            
	}
    }
    //annouce method with type, sender and content as parameters
    public void Announce(String type, String sender, String content){
        //chat object is created 
        Chat msg = new Chat(type, sender, content, "All");
        //checks for the client within the clientcount and returns the location if it is present
        for(int i = 0; i < clientCount; i++){
            clients[i].send(msg);
        }
    }
    //send user list  method to determine the recipient's list
    public void SendUserList(String toWhom){
        //checks for the client within the clientcount and returns the location if it is present
        for(int i = 0; i < clientCount; i++){
            findUserThread(toWhom).send(new Chat("newuser", "SERVER", clients[i].username, toWhom));
        }
    }
    //find user method to find a client
    public ServerThread findUserThread(String usr){
        //checks for the client within the clientcount and returns the location if it is present
        for(int i = 0; i < clientCount; i++){
            if(clients[i].username.equals(usr)){
                return clients[i];
            }
        }
        return null;
    }
    //indicates that the named compiler warnings should be suppressed in the annotated element
    @SuppressWarnings("deprecation")
    //method to remove the client
    public synchronized void remove(int ID){  
    //determine th client position through findClient method
    int pos = findClient(ID);
    //checks the position of client     
    if (pos >= 0){  
        //terminates the client at pos location
            ServerThread toTerminate = clients[pos];
            //notification message
            inter.jTextArea1.append("\nUser logged off " + ID + " at " + pos);
	    //checks if the position is less than clientcount and valid one
            if (pos < clientCount-1){
                for (int i = pos+1; i < clientCount; i++){
                    clients[i-1] = clients[i];
	        }
	    }
	    clientCount--;
	    //try block is initiated to manage with exceptions
            try{  
	      	toTerminate.close(); 
	    }
            //catch block is used to catch the exceptions if any is thrown in the above try block
	    catch(IOException ioe){  
                //displays appropriate error message
	      	inter.jTextArea1.append("\nError closing thread: " + ioe); 
	    }
	    toTerminate.stop(); 
	}
    }
    //method to add the thread to the socket
    private void addThread(Socket socket){  
        //checks if the count of client is less than its length
	if (clientCount < clients.length){  
            inter.jTextArea1.append("\nClient accepted: " + socket);
            //a new thread is been assigned
	    clients[clientCount] = new ServerThread(this, socket);
	    //try block is initiated to manage with exceptions
            try{ 
                //the thread is opened and started
	      	clients[clientCount].open(); 
	        clients[clientCount].start();  
	        //increment the client count
                clientCount++; 
	    }
            //catch block is used to catch the exceptions if any is thrown in the above try block
	    catch(IOException ioe){  
                //displaying error message
	      	inter.jTextArea1.append("\nError opening thread: " + ioe); 
	    } 
	}
	else{
            //displaying error message
            inter.jTextArea1.append("\nChat room  " + clients.length + " full.");
	}
    }
}
