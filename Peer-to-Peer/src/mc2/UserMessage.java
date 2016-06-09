//Reference: https://www.primianotucci.com/os/mc2-multicast-chat
//namespace for storing the related files
package mc2;
//class to implement the user message and it inherits from message class
public class UserMessage extends Message{
    private User _fromuser;
    //method to define the format of a user message
    public UserMessage(String iMsg,User iFrom) {
        super(iMsg,iFrom);
        _fromuser=iFrom;
    }
    
}
