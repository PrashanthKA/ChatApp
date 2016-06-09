//Reference: https://www.primianotucci.com/os/mc2-multicast-chat
//namespace for storing the related files
package mc2;
//import the necessary package
import java.io.*;
//class to setup a service message for notification
public class ServiceMessage extends Message implements Serializable{
    private char _code;
    String _arg;
    User _to;
    //declare and initialize the necessary member variables
    public final static char CODE_CHAN_ADV='a';
    public final static char CODE_QUERY_NICK_FREE='n';
    public final static char CODE_QUERY_CHAN_FREE='c';
    public final static char CODE_NICK_TAKEN='t';
    public final static char CODE_CHAN_TAKEN='h';
    public final static char CODE_HELOJOIN='i';
    public final static char CODE_JOIN='j';
    public final static char CODE_PART='p';

    //method to decide either to broadcast
    public boolean IsBroadcast(){
        return _to==null;
    }
    //method to get the user initiating
    public User GetToUser(){
        return _to;
    }
    //method to decide to encrypt or not
    public boolean DontEncrypt(){
        if(_code==CODE_CHAN_ADV || _code==CODE_QUERY_NICK_FREE || _code==CODE_QUERY_CHAN_FREE || _code==CODE_NICK_TAKEN ||_code==CODE_CHAN_TAKEN)
            return true;
        return false;
    }
   //method to retrieve the code in service type
    public char GetCode(){
        return _code;
    }
    public String GetArg(){
        return _arg;
    }
    //method to denote the format of the service message
    public ServiceMessage(User iFrom,User iTo,char iCode,String iArg) {
        this(iFrom,iCode,iArg);
        _to=iTo;
    }
    public ServiceMessage(User iFrom,char iCode,String iArg) {
        super("",iFrom);
        _code=iCode;
        _arg=iArg;
    }
    
}
