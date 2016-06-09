//this is the handler for the overall client program.
//this code is taken from the refernce http://www.cn-java.com/download/data/book/socket_chat.pdf
// reference for major part of the project is http://www.codeproject.com/Articles/524120/A-Java-Chat-Application
package com.controller;
//import the necessary packages
import com.Userinterface.ChatConsole;
import java.io.*;
import java.net.*;
import java.util.Date;
import javax.swing.table.DefaultTableModel;
//class for client which implenets a runnable interface
public class Client implements Runnable{
    //declaring the necessary variable to perfrom socket  programming, access the history file
    public int roomid;
    public String serverAddr;
    public Socket socket;
    public ChatConsole user;
    public ObjectInputStream Input;
    public ObjectOutputStream Output;
    public History history;
    //constructor to the client class with the client frame design as parameter 
    public Client(ChatConsole frame) throws IOException{
        //the user in the chat frame; the host is refered to the server address; the room id is refered to the chatroom id
        user = frame; this.serverAddr = user.Address; this.roomid = user.roomid1;
        //the serevr IPaddress and the chatroom id is passed to the socket object
        socket = new Socket(InetAddress.getByName(serverAddr), roomid);
         //the ObjectOutputStream write the data to the socket's output stream   
        Output = new ObjectOutputStream(socket.getOutputStream());
        //flushes this output stream and forces any buffered output bytes to be written out
        Output.flush();
        //the ObjectInputStream reads the data to the socket's input stream
        Input = new ObjectInputStream(socket.getInputStream());
        //history is assigned to the user's history
        history = user.hist;
    }
    //indicates that a method declaration is intended to override a method declaration in a supertype
    @Override
    //run method is declared
    public void run() {
        //keeprunning variable is declared and set to true
        boolean keepRunning = true;
        while(keepRunning){
            //try block is initiated to manage with exceptions
            try {
                //read an object from the ObjectInputStream and typecasts to chat and store it in object "msg"
                Chat msg = (Chat) Input.readObject();
                System.out.println("Incoming : "+msg.toString());
                //check if the msg is of type "message"
                if(msg.type.equals("message")){
                    //check if the msg's recipient equals to the username
                    if(msg.recipient.equals(user.username)){
                        //the information is appended to the TextArea design with the sender and content
                        user.jTextArea1.append("["+msg.sender +" > Me] : " + msg.content + "\n");
                    }
                    //if the message is been received then append the recipient along with the sender and the content
                    else{
                        user.jTextArea1.append("["+ msg.sender +" > "+ msg.recipient +"] : " + msg.content + "\n");
                    }
                    // check if the msg's content is not equal to bye and the sender is not the same user                       
                    if(!msg.content.equals(".bye") && !msg.sender.equals(user.username)){
                        //the time of the message is saved as string in msgTime object 
                        String msgTime = (new Date()).toString();
                        //try block is initiated to manage with exceptions
                        try{
                            //the messaeg and the time is been added to the history file
                            history.addMessage(msg, msgTime);
                            //this is an implementation of TableModel that uses a Vector of Vectors to store the cell value objects
                            DefaultTableModel table = (DefaultTableModel) user.historyFrame.jTable1.getModel();
                            //a new row is added to the history table with the sender, content and time information
                            table.addRow(new Object[]{msg.sender, msg.content, "Me", msgTime});
                        }
                        //catch block is used to catch the exceptions if any is thrown in the above try block
                        catch(Exception ex){}  
                    }
                }
                // chcks if the msg's type is login credential
                else if(msg.type.equals("login")){
                    if(msg.content.equals("TRUE")){
                        //the necessary buttons in the frame are enabled or disabled
                        user.jButton2.setEnabled(false); 
                        user.jButton3.setEnabled(false);                        
                        user.jButton4.setEnabled(true); 
                        //a login successful text is appended 
                        user.jTextArea1.append("[SERVER > Me] : Login Successful\n");
                        user.jTextField3.setEnabled(false);
                        user.jPasswordField1.setEnabled(false);
                        user.jInternalFrame2.setVisible(true);
                        user.jInternalFrame1.setVisible(false);
                    }
                    //if the above condition fails then a lodin fail message is been displayed 
                    else{
                        user.jLabel9.setText("Login Failed");
                    }
                }
                // chcks if the msg's type is test  condition
                else if(msg.type.equals("test")){
                    //the necessary buttons in the frame are enabled or disabled
                    user.jButton1.setEnabled(false);
                    user.jButton2.setEnabled(true); user.jButton3.setEnabled(true);
                    user.jTextField3.setEnabled(true); user.jPasswordField1.setEnabled(true);
                    user.jTextField1.setEditable(false); user.jTextField2.setEditable(false);
                    user.jButton7.setEnabled(true);
                }
                //checks if the msg object's type is new user
                else if(msg.type.equals("newuser")){
                    //checks if the username is been entered in the field
                    if(!msg.content.equals(user.username)){
                        boolean exists = false;
                        //condition to check if the username is in the size limits
                        for(int i = 0; i < user.model.getSize(); i++){
                            if(user.model.getElementAt(i).equals(msg.content)){
                                exists = true; break;
                            }
                        }
                        //the user credentials is been added to the elment in the database
                        if(!exists){ user.model.addElement(msg.content);
                        user.jTextArea1.append("[SERVER > Me] :"+msg.content+" signed in\n");}
                    }
                }
                //checks if the msg object's type is equal to sign up
                else if(msg.type.equals("signup")){
                    if(msg.content.equals("TRUE")){
                        //the necessary buttons in the frame are enabled or disabled
                        user.jButton2.setEnabled(false); user.jButton3.setEnabled(false);
                        user.jButton4.setEnabled(true); 
                        user.jTextArea1.append("[SERVER > Me] : Singup Successful\n");
                    }
                    else{
                        //sign up failed message is displayed
                        user.jLabel9.setText("Signup Failed");
                        user.jTextArea1.append("[SERVER > Me] : Signup Failed\n");
                    }
                }
                //checks if the msg object's type is signout
                else if(msg.type.equals("signout")){
                    if(msg.content.equals(user.username)){
                        user.jTextArea1.append("["+ msg.sender +" > Me] : Bye\n");
                        //the necessary buttons in the frame are enabled or disabled
                        user.jButton1.setEnabled(true); user.jButton4.setEnabled(false); 
                        user.jTextField1.setEditable(true); user.jTextField2.setEditable(true);
                        //condition to remove the user
                        for(int i = 1; i < user.model.size(); i++){
                            user.model.removeElementAt(i);
                        }
                        //user thread is destroyed
                        user.clientThread.stop();
                    }
                    else{
                        //the sign out notification is been displayed
                        user.model.removeElement(msg.content);
                        user.jTextArea1.append("["+ msg.sender +" > All] : "+ msg.content +" has signed out\n");
                    }
                }
                
               //if the msg object's type is not known
                else{
                    user.jTextArea1.append("[SERVER > Me] : Unknown message type\n");
                }
            }
            //catch block is used to catch the exceptions if any is thrown in the above try block
            catch(Exception ex) {
                keepRunning = false;
                //displaying the appropriate error messages when exceptions are been thrown
                user.jLabel9.setText("Connection Failed");
                user.jTextArea1.append("[Application > Me] : Connection Lost\n");
                user.jButton1.setEnabled(true); user.jTextField1.setEditable(true); user.jTextField2.setEditable(true);
                user.jButton4.setEnabled(false); 
                //condition to remove the user
                for(int i = 1; i < user.model.size(); i++){
                    user.model.removeElementAt(i);
                }
                //user thread is destroyed
                user.clientThread.stop();
                System.out.println("Exception SocketClient run()");
                //to print the real exception stack to system.err
                ex.printStackTrace();
            }
        }
    }
    //send class with msg object as parameter
    public void send(Chat msg){
        //try block is initiated to manage with exceptions
        try {
            //write the msg object to the ObjectOutputStream
            Output.writeObject(msg);
            //flushes this output stream and forces any buffered output bytes to be written out
            Output.flush();
            System.out.println("Outgoing : "+msg.toString());
            //if the msg object's type equals to "message" and not "bye"
            if(msg.type.equals("message") && !msg.content.equals(".bye")){
                //the time parameter is  stored to the msgTime as string
                String msgTime = (new Date()).toString();
                //try block is initiated to manage with exceptions
                try{
                     //the messaeg and the time is been added to the history file
                    history.addMessage(msg, msgTime);               
                    //this is an implementation of TableModel that uses a Vector of Vectors to store the cell value objects
                    DefaultTableModel table = (DefaultTableModel) user.historyFrame.jTable1.getModel();
                    //a new row is added to the history table with the sender, content, recipient and time information
                    table.addRow(new Object[]{"Me", msg.content, msg.recipient, msgTime});
                }
                //catch block is used to catch the exceptions if any is thrown in the above try block
                catch(Exception ex){}
            }
        } 
        //catch block is used to catch the exceptions if any is thrown in the above try block
        catch (IOException ex) {
            System.out.println("Exception SocketClient send()");
        }
    }
    //method to close the client thread
    public void closeThread(Thread t){
        t = null;
    }
}
