//Reference: https://www.primianotucci.com/os/mc2-multicast-chat
//namespace for storing the related files
package mc2;
//class to implement the users in thhe channel
public class User implements java.io.Serializable,Comparable{
   //declaring the member  variables and initializing them
    public static User Anonymous=new User();
    public static final char STATUS_NOTAUTH='0';
    public static final char STATUS_ASKINGNICK='1';
    public static final char STATUS_NICKFAILED='2';
    public static final char STATUS_AUTH='3';
    private String _name;
    private boolean _anonymous=false;
    private char _status=STATUS_NOTAUTH;
   
    //method to detect an anonymous user
    public boolean IsAnonymous(){
        return _anonymous;
    }
    //method to get the status of the user
    public char GetStatus(){
        return _status;
    }
    //method to set the status of the user
    public void SetStatus(char iStatus){
        _status=iStatus;
    }
    //method to get the name of the user
    public String GetName(){
        return _name;
    }
    /** Creates a new instance of User */
    public User(String iName) {
        _name=iName;
    }
    
    //Creates an anonymous user
    private User(){
        _anonymous=true;
        _name="???";
    }
    //method to check the user is anonymous or not
    public boolean equals(Object obj){
        if(!(obj instanceof User))
            return false;
        if(obj==null) return false;
        if(IsAnonymous() || ((User)obj).IsAnonymous()) return false;
        
        return _name.equals(((User)obj)._name);
    }
    //method to set the string to send
    public String toString(){
        return _name;
    }
    //method to check the name of the user
    public int compareTo(Object iTo){
        if(! (iTo instanceof User))
            return 0;
        return _name.compareTo(((User)iTo)._name);
    }
}
