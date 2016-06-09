//Reference: https://www.primianotucci.com/os/mc2-multicast-chat
//namespace for storing the related files
package mc2;
//import the necessary package
import mc2.ui.*;
//class private conversation for two users only
public class PrivateConversation {
    //declaring the member variables
    private frmPrivateConv _ui;
    private User _to;
    //method to add a user in a private conversation
    public PrivateConversation(User iTo) {
        _to=iTo;
        _ui=new frmPrivateConv(this);
    }
    //method to get a user in private conversation
    public User GetTo(){
        return _to;
    }
    //method to show a seperate window for private conversation
    public void Show(){
        _ui.setVisible(true);
    }
    //method to retrieve a message
    public void MessageArrival(String iMsg){
        _ui.AddRecvLine("<" + _to.GetName()+"> " + iMsg);
        if(!_ui.isVisible()){
            _ui.setVisible(true);
            _ui.requestFocus();
        }
            
    }
    //method to send a message
    public void SendMessage(String iMsg){
        Manager.GetInstance().SendPrivateMsg(_to,iMsg);
        _ui.AddRecvLine("<" + Manager.GetInstance().GetMe().GetName() +"> " + iMsg);
    }
}
