//Reference: https://www.primianotucci.com/os/mc2-multicast-chat
//namespace for storing the related files
package mc2;
//import the necessary packages
import java.io.IOException;
import java.net.*;
import java.security.*;
//the multicast dispatcher inherits the network dispatcher class
public class MulticastDispatcher extends NetworkDispatcher{
    //declaring member variable and initializing it to a default IP
    public static final String DefaultMulticastGroupIP="230.1.1.1";
    //declaring member variable and initializing it to a default Port
    public static final int DefaultMulticastGroupPort=1314;
    //declaring member variable and initializing a default buffer size
    private static final int RecvBufSize=65536;
    //declaring member variables
    private MulticastSocket _cSock;
    private InetAddress _curAddr;
    private int _curPort;
    //declaring RecvThread class that inherits from thread class
    private class RecvThread extends Thread{
        private MulticastSocket _cSock;
        private NetworkDispatcher _Dispatcher;
        //declaring a constructor for the RevThread class with parameters
        public RecvThread(NetworkDispatcher iDispatcher,MulticastSocket iSock){
            _cSock=iSock;
            _Dispatcher=iDispatcher;
        }
        // this thread was constructed using a separate Runnable run object
        public void run(){
            //create a byte array to store the buffer
            byte inBuf[]=new byte[RecvBufSize];
            //// this thread was constructed using a separate Runnable run object
            DatagramPacket rPack=new DatagramPacket(inBuf,RecvBufSize);
            //try block is initiated to manage with exceptions
            try {
                for(;;){
                    _cSock.receive(rPack);
                    //Represents the method that will handle the data received event of a SerialPort object.
                    _Dispatcher.DataReceived(rPack.getData(),rPack.getLength());
                
                }
            }
             //catch block is used to catch the exceptions if any is thrown in the above try block
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private RecvThread _rThread;
    //method to send a datagram packet to all the users connected to the channel 
    protected void DispatchToAll(byte iBuf[],int iSize) throws Exception{
        //Represents the method that will handle the data received event of a SerialPort object.
        DatagramPacket dPack=new DatagramPacket(iBuf,iSize,_curAddr,_curPort);
        _cSock.send(dPack);
    }
    
    /** Creates a new instance of MulticastDispatcher */
    public MulticastDispatcher() throws Exception{
        //Determines the IP address of a host, given the host's name.
        _curAddr=InetAddress.getByName(DefaultMulticastGroupIP);
        _curPort=DefaultMulticastGroupPort;
        //the port is passed as parameter to the multicast socket
        _cSock=new MulticastSocket(_curPort);
        _cSock.joinGroup(_curAddr);
        _rThread=new RecvThread(this,_cSock);
        _rThread.start();
    }
    
}
