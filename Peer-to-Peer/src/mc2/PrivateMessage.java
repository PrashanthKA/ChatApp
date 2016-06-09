//Reference: https://www.primianotucci.com/os/mc2-multicast-chat
//namespace for storing the related files
package mc2;
//import the necessary packages
import java.io.*;
//class to implement private message and it inherits message class
public class PrivateMessage extends Message implements Serializable{
    private User _to;
    //method to retrieve a user to send
    public User GetTo(){
        return _to;
    }
    //method to define the private message format
    public PrivateMessage(String iMsg,User iFrom,User iTo) {
        super(iMsg,iFrom);
        _to=iTo;
    }
    
}
