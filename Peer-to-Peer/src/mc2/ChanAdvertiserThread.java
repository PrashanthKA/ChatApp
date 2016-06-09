//Reference: https://www.primianotucci.com/os/mc2-multicast-chat
//namespace for storing the related files
package mc2;
//declaration of class and it inherits Thread
public class ChanAdvertiserThread extends Thread {
    //declaring member variable
    private Channel _channel;
    //constructor for the class with a parameter
    public ChanAdvertiserThread(Channel iChan){
        _channel=iChan;
    }
    // this thread was constructed using a separate Runnable run object
    public void run(){
        //creating an instance of the thread
        Manager manager=Manager.GetInstance();
        
        for(;;){
            //try block is initiated to manage with exceptions
            try {
                //creating an object for ServiceMessagea and passing it to manager
                ServiceMessage advMsg=new ServiceMessage(manager.GetMe(),ServiceMessage.CODE_CHAN_ADV,_channel.GetName());
                //the manager dispatches the message to all the clients
                manager.GetDispatcher().DispatchToAll(advMsg);
                Thread.sleep(2000);
            } 
            //catch block is used to catch the exceptions if any is thrown in the above try block
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
         }
    }
}
