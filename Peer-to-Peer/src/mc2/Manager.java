//Reference: https://www.primianotucci.com/os/mc2-multicast-chat
//namespace for storing the related files
package mc2;
//import the necessary packages
import java.awt.Frame;
import java.util.*;
import mc2.ui.*;
import javax.swing.*;

public class Manager {
    /*Singleton Structure*/
    private static Manager singObj;
    //instance ojbect for the manager is created     
    public static Manager GetInstance() {
        try {
            if(singObj==null)
                singObj=new Manager();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return singObj;
    }
    /* End of Singleton Structure */
    //declaring the member variables
    public static final int DefaultOperTimeout=3000;
    private NetworkDispatcher _Dispatcher;
    private ArrayList _ChannelList=new ArrayList();
    private TreeMap _privconvs=new TreeMap();
    private Channel _curChan;
    private User _me;
    private ChanAdvertiserThread _advThread;
    private boolean _ChannelFree=true;
    private String _ReqChan="";
    public Object WaitForJoinAck=new Object();
    //returns the network dispatcher class object
    public NetworkDispatcher GetDispatcher(){
        return _Dispatcher;
    }
    //the dispatcher is termed to a multicast dispatcher
    private Manager() throws Exception{
        _Dispatcher=new MulticastDispatcher();
        _me=User.Anonymous; //Anonymous user
    }
    //authentication of a new nick name; checks if the given name is available and returns appropriately
    public boolean TrySetNick(String iNick){
        _me=new User(iNick);
        _me.SetStatus(User.STATUS_ASKINGNICK);
        ServiceMessage newMsg=new ServiceMessage(User.Anonymous,ServiceMessage.CODE_QUERY_NICK_FREE,iNick);        
        GetDispatcher().DispatchToAll(newMsg);
        try {
            Thread.sleep(DefaultOperTimeout);
        } catch (InterruptedException ex) {}
        if(_me.GetStatus()==User.STATUS_NICKFAILED)
            return false;
        _me.SetStatus(User.STATUS_AUTH);
        return true;
    }
    //when a user quits from the channel
    public void Quit(){
        if(_curChan!=null){
            ServiceMessage newMsg=new ServiceMessage(GetMe(),ServiceMessage.CODE_PART,_curChan.GetName());
            GetDispatcher().DispatchToAll(newMsg);
        }
        System.exit(0);
    }
    //to check if a channel is free
    public boolean IsChannelFree(String iChanName){
        _ChannelFree=true;
        ServiceMessage newMsg=new ServiceMessage(GetMe(),ServiceMessage.CODE_QUERY_CHAN_FREE,iChanName);  
        GetDispatcher().DispatchToAll(newMsg);
        try {
            Thread.sleep(DefaultOperTimeout);
        } catch (InterruptedException ex) {}
        return _ChannelFree;
    }
    //returns the channels that are available from the list    
    public String[] GetAvailableChannels(){
        String []outArr=new String[_ChannelList.size()];
        _ChannelList.toArray(outArr);
        return outArr;
    }
    //when a new channel comes up, it is advertised through this method
    public void SetAndAdvertiseChannel(Channel iChan){
        _curChan=iChan;
        _curChan.AddUser(GetMe());
        _ReqChan="";
        if(_advThread!=null)
            _advThread.stop();
        _advThread=new ChanAdvertiserThread(_curChan);
        _advThread.start();
    }
    //method to send channel message
    public void SendChanMessage(String iMsg){
        if(_curChan==null)
            return;
        ChannelMessage newMsg=new ChannelMessage(iMsg,GetMe(),_curChan);
        GetDispatcher().DispatchToAll(newMsg);
    }
    //method to rethrun the current channel name
    public Channel GetCurrentChannel(){
        return _curChan;
    }
    //method to return if any user has joined the channel
    public boolean HasJoined(){
        return _curChan!=null;
    }
    //method to define a private message
    public void SendPrivateMsg(User iTo,String iMsg){
        PrivateMessage newMsg=new PrivateMessage(iMsg,GetMe(),iTo);
        GetDispatcher().DispatchToAll(newMsg);
    }
    //method to start a private conversation with a user
    public void StartPrivateConversation(User iTo){
        if(iTo==null)
            return;
        if(!_privconvs.containsKey(iTo)){
            _privconvs.put(iTo,new PrivateConversation(iTo));
        }else
        	((PrivateConversation)_privconvs.get(iTo)).Show();
    }
    //method to parse the message and categorize it
    public void ParseMessage(Message iMsg){
        if(iMsg instanceof ServiceMessage)
            ParseServiceMessage((ServiceMessage)iMsg);
        else if(iMsg instanceof ChannelMessage)
            ParseChannelMessage((ChannelMessage)iMsg);
        else if(iMsg instanceof PrivateMessage)
            ParsePrivateMessage((PrivateMessage)iMsg);
    }
//method for parsing a private message
    private void ParsePrivateMessage(PrivateMessage iMsg){
        if(!iMsg.GetTo().equals(GetMe()))
            return;
        StartPrivateConversation(iMsg.GetSender());
        ((PrivateConversation)_privconvs.get(iMsg.GetSender())).MessageArrival(iMsg.GetText());
    }
    //method to parse a channel message
    private void ParseChannelMessage(ChannelMessage iMsg){
        if(iMsg.GetChannel()==null)
            return;
        if(!iMsg.GetChannel().equals(_curChan))
            return;
        iMsg.GetChannel().MessageReceived(iMsg);
    }
    //method to parse a service message
    private void ParseServiceMessage(ServiceMessage iMsg){
        if(!iMsg.IsBroadcast() && !iMsg.GetToUser().equals(GetMe()))
            return;
        //the message is passed through switch
        switch(iMsg.GetCode()){
            //if the message is of tyoe chanel advertise, then this method gets executed
            case ServiceMessage.CODE_CHAN_ADV:{
                if(!_ChannelList.contains(iMsg.GetArg())){
                    _ChannelList.add(iMsg.GetArg());
                    System.out.println("New channel discovered " +iMsg.GetArg());
                }
            }
                break;
            //this method gets executed if the message is of type join
            case ServiceMessage.CODE_JOIN:{
                if(_curChan==null){//my join
                    _ReqChan=iMsg.GetArg();
                }else{//somone else join
                    if(!iMsg.GetArg().equals(_curChan.GetName()))
                        return;
                    _curChan.Join(iMsg.GetSender());

                    //Send hello to the client to notify it that i'm in the channel
                    ServiceMessage newMsg=new ServiceMessage(GetMe(),ServiceMessage.CODE_HELOJOIN,_curChan.GetName());
                    GetDispatcher().DispatchToAll(newMsg);
                    
                    
                }
            } break;
            //this method gets executed if the message is of type join and after the hello message is notifies
            case ServiceMessage.CODE_HELOJOIN:{
                if(_curChan==null && _ReqChan.length()>0 && _ReqChan.equals(iMsg.GetArg())){
                    System.out.println("Join Accepted");
                    SetAndAdvertiseChannel(new Channel(iMsg.GetArg()));
                    _curChan.AddUser(GetMe());
                    synchronized(WaitForJoinAck){ WaitForJoinAck.notify();}
                }else if(_curChan==null && _ReqChan.length()<=0)        
                    return;
                
                if(! iMsg.GetArg().equals(_curChan.GetName()))
                    return;
                
                //the user is added to the channel's list
                _curChan.AddUser(iMsg.GetSender());                
            }break;
            //the method is invoked when the parsed message is sender
            case ServiceMessage.CODE_PART:{
                if(_curChan==null) return;
                if(!iMsg.GetArg().equals(_curChan.GetName()))
                    return;
                _curChan.Part(iMsg.GetSender());

            }break;
            
            //method to check whether a nick name is available or not
            case ServiceMessage.CODE_QUERY_NICK_FREE:{
                if(GetMe().GetName().equals(iMsg.GetArg()) && GetMe().GetStatus()==User.STATUS_AUTH){
                    ServiceMessage newMsg=new ServiceMessage(GetMe(),ServiceMessage.CODE_NICK_TAKEN,GetMe().GetName());
                    GetDispatcher().DispatchToAll(newMsg);
                }
            }break;
            //method to check whether a channel is available or not
            case ServiceMessage.CODE_QUERY_CHAN_FREE:{
                if(Channel.GetByName(iMsg.GetArg())!=null){
                    ServiceMessage newMsg=new ServiceMessage(GetMe(),ServiceMessage.CODE_CHAN_TAKEN,iMsg.GetArg());
                    GetDispatcher().DispatchToAll(newMsg);
                }
            }break;
            //the method retuns if a chosen channel name is already taken
            case ServiceMessage.CODE_CHAN_TAKEN:{
                if(_curChan.GetName().equals(iMsg.GetArg()))
                    _ChannelFree=false;
            }break;
            //the method retuns if a chosen nick name is already taken
            case ServiceMessage.CODE_NICK_TAKEN:{
                if(GetMe().GetStatus()!=User.STATUS_ASKINGNICK) return;
                GetMe().SetStatus(User.STATUS_NICKFAILED);
            }break;
        }
    }
    //returns the current user
    public User GetMe(){
        return _me;
    }
    
}
