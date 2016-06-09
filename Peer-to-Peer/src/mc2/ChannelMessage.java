//Reference: https://www.primianotucci.com/os/mc2-multicast-chat
//namespace for storing the related files
package mc2;
//import the necessary packages
import java.io.*;
//class for the structure of channel message and it inherits from message class
public class ChannelMessage extends Message implements Serializable{
    private Channel _channel;
    //returns the channel name
    public Channel GetChannel(){
        return _channel;
    }
    //defiines th echannel messgae and the parameters describe the format of channel message 
    public ChannelMessage(String iMsg,User iFrom,Channel iToChan) {
        super(iMsg,iFrom);
        _channel=iToChan;
    }
    //writes primitive data types and graphs of Java objects to an OutputStream
   private void writeObject(java.io.ObjectOutputStream out) throws IOException{
     out.writeUTF(_channel.GetName());   
   }
 // deserializes primitive data and objects previously written using an ObjectOutputStream
   private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
       _channel=Channel.GetByName(in.readUTF());
   }
    
    
}
