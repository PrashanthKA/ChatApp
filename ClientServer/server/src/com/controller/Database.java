//refernce http://www.cn-java.com/download/data/book/socket_chat.pdf
// reference for major part of the project is http://www.codeproject.com/Articles/524120/A-Java-Chat-Application

package com.controller;
//import the necessary packages
import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
//open a database class
public class Database {
    //the filepath variable is initialized
    public String filePath;
    
    //constructoe to the database class with the filepath parameter
    public Database(String filePath){
        this.filePath = filePath;
    }
    //method to check whether user exists, this returns either true or false
    public boolean userExists(String username){
        //try block is initiated to manage with exceptions
        try{
            //a file object is created and the filepath value is passed as parameter 
            File fXmlFile = new File(filePath);
            //defines a factory API that enables applications to obtain a parser that produces DOM object trees from XML documents
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            //defines the API to obtain DOM Document instances from an XML document. Using this class, an application programmer can obtain a Document from XML.
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            //parse the content of the given file as an XML document and return a new DOM Document object
            Document doc = dBuilder.parse(fXmlFile);
            //the getDocument Element allows to acces the child node directly
            doc.getDocumentElement().normalize();
            //the list of users is got as nodes
            NodeList nList = doc.getElementsByTagName("user");
            //condition to check the end of list and perform memory swapping
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                //if the type of node is in element_node
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    //typecast the node to element
                    Element eElement = (Element) nNode;
                    //if the username in node is equal to the entered username then return true
                    if(getTagValue("username", eElement).equals(username)){
                        return true;
                    }
                }
            }
            return false;
        }
        //catch block is used to catch the exceptions if any is thrown in the above try block
        catch(Exception ex){
            System.out.println("Database exception : userExists()");
            return false;
        }
    }
    //method to check the Login,i.e., validation
    public boolean checkLogin(String username, String password){
        //condition to check if the username already exists in the database
        if(!userExists(username)){ return false; }
        //try block is initiated to manage with exceptions
        try{
            //a file object is created and the filepath value is passed as parameter 
            File fXmlFile = new File(filePath);
            //defines a factory API that enables applications to obtain a parser that produces DOM object trees from XML documents
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            //defines the API to obtain DOM Document instances from an XML document. Using this class, an application programmer can obtain a Document from XML.
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            //parse the content of the given file as an XML document and return a new DOM Document object
            Document doc = dBuilder.parse(fXmlFile);
             //the getDocument Element allows to acces the child node directly
            doc.getDocumentElement().normalize();
            //the list of users is got as nodes
            NodeList nList = doc.getElementsByTagName("user");
            //condition to check the end of list and perform memory swapping
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                 //if the type of node is in element_node
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                     //typecast the node to element
                    Element eElement = (Element) nNode;
                    //if the username and password in node is equal to the entered username and password then return true
                    if(getTagValue("username", eElement).equals(username) && getTagValue("password", eElement).equals(password)){
                        return true;
                    }
                }
            }
            //if the user does not exist
            System.out.println("Hippie");
            return false;
        }
        //catch block is used to catch the exceptions if any is thrown in the above try block
        catch(Exception ex){
            System.out.println("Database exception : userExists()");
            return false;
        }
    }
    //method to add user to the database with userame and password as parameters
    public void addUser(String username, String password){
        //try block is initiated to manage with exceptions
        try {
            //defines a factory API that enables applications to obtain a parser that produces DOM object trees from XML documents
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            //defines the API to obtain DOM Document instances from an XML document. Using this class, an application programmer can obtain a Document from XML.
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            //parse the content of the given file as an XML document and return a new DOM Document object
            Document doc = docBuilder.parse(filePath);
            //gets the first child of this node
            Node data = doc.getFirstChild();
            //a newuser is added to the element object
            Element newuser = doc.createElement("user");
            //a new username is added to the element object
            Element newusername = doc.createElement("username"); newusername.setTextContent(username);
            //a new password is been added to the element object
            Element newpassword = doc.createElement("password"); newpassword.setTextContent(password);
            //the information of the new user, username, password is appended to the child node
            newuser.appendChild(newusername); newuser.appendChild(newpassword); data.appendChild(newuser);
            //a TransformerFactory instance can be used to create Transformer and Templates objects
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            //an instance of this abstract class can transform a source tree into a result tree.
            Transformer transformer = transformerFactory.newTransformer();
            //acts as a holder for a transformation Source tree in the form of a Document Object Model (DOM) tree
            DOMSource source = new DOMSource(doc);
            //acts as an holder for a transformation result, which may be XML, plain Text, HTML, or some other form of markup
            StreamResult result = new StreamResult(new File(filePath));
            //the source to result transformation is carried out
            transformer.transform(source, result);
 
	   } 
            //catch block is used to catch the exceptions if any is thrown in the above try block
           catch(Exception ex){
		System.out.println("Exceptionmodify xml");
	   }
	}
    //returns the tag value of the element object in the databse
    public static String getTagValue(String sTag, Element eElement) {
	NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
	return nValue.getNodeValue();
  }
}
