//Reference: https://www.primianotucci.com/os/mc2-multicast-chat
//namespace for storing the related files
package mc2;
//import the necessary packages
import java.util.*;
import mc2.ui.*;
//class for channel to connect to other devices
public class Channel{
    //declaring the member  variables
    private String _name;
    private String _key;
    private User _owner;
    private frmChan _ui;
    private LinkedList _users=new LinkedList();
    private static TreeMap _knownchannels=new TreeMap();
    //method to get the name of the channels existing
    public static Channel GetByName(String iName){
        if(_knownchannels.containsKey(iName))
            return (Channel)_knownchannels.get(iName);
        else
            return null;
    }
    //method to get the new channel name
    public String GetName(){
        return _name;
    }
   //method to get the existing users
    public User[] GetUsers(){
        User[] outArr=new User[_users.size()];
        
        _users.toArray(outArr);
        return outArr;
    }
    //method to add a user  
    public void AddUser(User iUser){
        //checks if the user name already exists
        if(_users.contains(iUser))
           return;
        //add the user and updat ethe list
        _users.add(iUser);
        _ui.UpdateNickList(GetUsers());
    }
    //method to join a user in a channel
    public void Join(User iUser){
        Notice(iUser.GetName() + " joined " + GetName());
        AddUser(iUser);
    }
   //method to give notification to all nodes once a new user has joined in the channel
    public void Notice(String iStr){
        _ui.AddRecvLine("::: "+iStr);
    }
    //method to give notification to all nodes once a new user has left in the channel
    public void Part(User iUser){
        if(_users.contains(iUser))
            _users.remove(iUser);
        Notice(iUser.GetName() + " left " + GetName());
        _ui.UpdateNickList(GetUsers());
    }
    
    //method to indicate new message in the channel
    public void MessageReceived(ChannelMessage iMsg){
         _ui.AddRecvLine("< "+iMsg.GetSender().GetName() + "> " + iMsg.GetText());
    }
    //method to set the key of a channel
    public void SetKey(String iKey){
        _key=iKey;
        Manager.GetInstance().GetDispatcher().SetKey(iKey);
    }
    
    // Creates a new instance of Channel
    public Channel(String iName) {
        _name=iName;
        _ui=new frmChan();
        _ui.setTitle("Channel " + _name);
        _ui.setVisible(true);
        //adds the channel name to the known channel list
        _knownchannels.put(_name,this);
    }
    //method to remove a channel from the known channel list
    public void finalize() throws Throwable{
        _knownchannels.remove(this);
        super.finalize();
    }
    //method to check existence of a particular channel name
    public boolean equals(Channel iTo){
        if(iTo==null) return false;
        return _name.equals(iTo._name);
    }
    //method to create a new channel and advertises to the manager
    public static void CreateNew(String iName,String iKey){
        Channel newChan=new Channel(iName);
      if(iKey!=null && iKey.length()>0)
        newChan.SetKey(iKey);
      Manager.GetInstance().SetAndAdvertiseChannel(newChan);
    }
    //method to check constrints and join an existing channel
    public static void JoinExisting(String iName,String iKey) throws Exception{
        Manager.GetInstance().GetDispatcher().SetKey(iKey);
        ServiceMessage newMsg=new ServiceMessage(Manager.GetInstance().GetMe(),ServiceMessage.CODE_JOIN,iName);
        Manager.GetInstance().GetDispatcher().DispatchToAll(newMsg);
    }
}
