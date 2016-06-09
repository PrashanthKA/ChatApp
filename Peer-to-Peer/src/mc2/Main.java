//Reference: https://www.primianotucci.com/os/mc2-multicast-chat
//namespace for storing the related files
package mc2;
//import the necessary package
import mc2.ui.*;
//the main class of the chat application
public class Main {
    //the main method where the execution starts
    public static void main(String[] args) {
        //overrides the run method of the interface runnable and passed to the EventQueue.invokeLater method, which is a static method
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Manager.GetInstance();
                frmStart.GetInstance().setVisible(true);
            }
        });          
        
    }
    
}
