//Reference: https://www.primianotucci.com/os/mc2-multicast-chat
//namespace for storing the related files
package mc2;
//import the necessary packages
import java.io.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
//an abstract class is one which may or may not include abstract methods
public abstract class NetworkDispatcher {
    //declaring and initializing the necessary member variables
    private static final byte BLOCK_ENCRYPTED=(byte)0xba;
    private static final byte BLOCK_UNENCRYPTED=(byte)0xab;
    private boolean _keySet=false;
    private Cipher _encCipher;
    private Cipher _decCipher;
    SecretKeySpec _keyIV; 
    private PBEParameterSpec _paramSpec;
    private SecretKey _secretKey;
    private static byte[] salt = {(byte)0xc9, (byte)0x53, (byte)0x67, (byte)0x9a, (byte)0x5b, (byte)0xc8, (byte)0xae, (byte)0x18 };
    //method to set the key value
    public void SetKey(String iKey){
        if(iKey==null || iKey.length()<=0){
            _keySet=false;    
            return;
        }
        _keySet=true;
        //try block is initiated to manage with exceptions
        try {
            //The JCE follows the same security provider infrastructure as does the rest of the Java security architecture
             Provider sunJce = new com.sun.crypto.provider.SunJCE();
             Security.addProvider(sunJce);
            // This class specifies the set of parameters used with password-based encryption (PBE), as defined in the PKCS #5 standard.
             _paramSpec = new PBEParameterSpec(salt,20);
             PBEKeySpec keySpec = new PBEKeySpec(iKey.toCharArray() );
             //This class represents a factory for secret keys
             SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
             _secretKey = keyFactory.generateSecret(keySpec);

            //This provides the functionality of a cryptographic cipher for encryption and decryption.
            _encCipher.init(Cipher.ENCRYPT_MODE,_secretKey, _paramSpec);
            _decCipher.init(Cipher.DECRYPT_MODE,_secretKey, _paramSpec);
        }
        //catch block is used to catch the exceptions if any is thrown in the above try block
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //method that implements a cipher with instance value for encryption and decryption   
    protected NetworkDispatcher() {
        //try block is initiated to manage with exceptions
        try {
            _encCipher=Cipher.getInstance("PBEWithMD5AndDES");
            _decCipher=Cipher.getInstance("PBEWithMD5AndDES");

        }
         //catch block is used to catch the exceptions if any is thrown in the above try block
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    protected abstract void DispatchToAll(byte []iBuf,int iSize) throws Exception;
    
    protected void DispatchToAll(byte []iBuf) throws Exception{
        DispatchToAll(iBuf,iBuf.length);
    }
    //method to dispatch the message to all users in the channel
    public void DispatchToAll(Message iMsg){
        //try block is initiated to manage with exceptions
        try {
            //his class implements an output stream in which the data is written into a byte array
            ByteArrayOutputStream bOut= new ByteArrayOutputStream();
            boolean EncData=(_keySet && !iMsg.DontEncrypt());
            //segment to encrypt a block
            if(EncData)
                bOut.write(BLOCK_ENCRYPTED);
            else
                bOut.write(BLOCK_UNENCRYPTED);
            
            //This abstract class is the superclass of all classes representing an output stream of bytes
            OutputStream underlayingStream=bOut;
            if(EncData)
                underlayingStream=new CipherOutputStream(bOut,_encCipher);
            //An ObjectOutputStream writes primitive data types and graphs of Java objects to an OutputStream   
            ObjectOutputStream ooStream=new ObjectOutputStream(underlayingStream);
            ooStream.writeObject(iMsg);
            ooStream.close();
            byte []bb=bOut.toByteArray();
            //dispatches the message to the array
            DispatchToAll(bOut.toByteArray());
        }
        //catch block is used to catch the exceptions if any is thrown in the above try block
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    //class to store a data received in an encrypted format
    protected void DataReceived(byte []iBuf,int iLen){
        //try block is initiated to manage with exceptions
        try {        
            //checks if the block is encrypted
            if(iBuf[0]==BLOCK_ENCRYPTED){
                if(!_keySet)
                    return;
                //the output buffer deciphers the text with thhe size to the received buffer
                 byte []outBuf=new byte[_decCipher.getOutputSize(iLen-1)+1];
                 iLen=_decCipher.doFinal(iBuf,1,iLen-1,outBuf,1)+1;
                 iBuf=outBuf;
            }else if(iBuf[0]!=BLOCK_UNENCRYPTED)
                return;
            //A ByteArrayInputStream contains an internal buffer that contains bytes that may be read from the stream.
            ByteArrayInputStream bIn=new ByteArrayInputStream(iBuf,1,iLen-1);
            //An ObjectInputStream deserializes primitive data and objects previously written using an ObjectOutputStream.
            ObjectInputStream ooStream=new ObjectInputStream(bIn);
            //this method read an object from the ObjectInputStream
            Object msgIn=ooStream.readObject();
            ooStream.close();
            Message recMsg=(Message)msgIn;
            Manager.GetInstance().ParseMessage(recMsg);
            } 
        //catch block is used to catch the exceptions if any is thrown in the above try block
        catch(BadPaddingException ex){
            //try block is initiated to manage with exceptions
            try {
                //decrypts the encrypted text
                _decCipher.init(Cipher.DECRYPT_MODE,_secretKey, _paramSpec);
            }
            //catch block is used to catch the exceptions if any is thrown in the above try block
            catch (Exception exc) {}
        }
        //catch block is used to catch the exceptions if any is thrown in the above try block
        catch(Exception ex){
            ex.printStackTrace();
        }
        
    }
}
