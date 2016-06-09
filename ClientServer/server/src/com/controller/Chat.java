// this file is used to create a format for the message.
//refernce http://www.cn-java.com/download/data/book/socket_chat.pdf
// reference for major part of the project is http://www.codeproject.com/Articles/524120/A-Java-Chat-Application

package com.controller;
//import the necessary packages
import java.io.Serializable;
//chat class with serializable interface
public class Chat implements Serializable{
    //the serial version ID is specified as 1 and is set as final and static as it should not be changed
    private static final long serialVersionUID = 1L;
    public String type, sender, content, recipient;
    //the contructor of the chat class which takes the parameters of type, sender, content, recipient
    public Chat(String type, String sender, String content, String recipient){
        this.type = type; this.sender = sender; this.content = content; this.recipient = recipient;
    }
    //indicates that a method declaration is intended to override a method declaration in a supertype
    @Override
    //this method returns itself a string
    public String toString(){
        return "{type='"+type+"', sender='"+sender+"', content='"+content+"', recipient='"+recipient+"'}";
    }
}
