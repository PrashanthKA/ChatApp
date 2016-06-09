//Reference: https://www.primianotucci.com/os/mc2-multicast-chat
//namespace for storing the related files
package mc2;
//import the necessary package
import java.io.*;
//an abstract class is one which may or may not include abstract methods
public abstract class Message implements Serializable{
    //declaring member variables
    protected String _text;
    protected User _from;
    //method to encrypt the data
    public boolean DontEncrypt(){
        return false;
    }
    //method to get the user sending the data
    public User GetSender(){
        return _from;
    }
    //method to return the text
    public String GetText(){
        return _text;
    }
    //method that defines the format of message 
    protected Message(String iMsg,User iFrom) {
        _text=iMsg;
        _from=iFrom;
    }
    
}
