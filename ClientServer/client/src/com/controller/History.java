//this file is used for saving the chat messages in a given xml.having the right format.
//refernce http://www.cn-java.com/download/data/book/socket_chat.pdf
// reference for major part of the project is http://www.codeproject.com/Articles/524120/A-Java-Chat-Application
package com.controller;
//import the necessary packages
import java.io.*;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import javax.swing.table.DefaultTableModel;
//class to store the histroy of chat
public class History {
    //declaring a historyfie variable
    public String historyfile;
    //constructor of the history class with filepath as parameter
    public History(String filePath){
        this.historyfile = filePath;
    }
    //method to add the message and time to the history
    public void addMessage(Chat msg, String time){
         //try block is initiated to manage with exceptions
        try {
            //defines a factory API that enables applications to obtain a parser that produces DOM object trees from XML documents
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            //defines the API to obtain DOM Document instances from an XML document. Using this class, an application programmer can obtain a Document from XML.
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            //parse the content of the given file as an XML document and return a new DOM Document object
            Document xmldoc = docBuilder.parse(historyfile);
            //gets the first child of this node
            Node data = xmldoc.getFirstChild();
            //the format in which the data is saved!
            //the message is added to the element object
            Element message = xmldoc.createElement("message");
            //the sender is added to the element object
            Element _sender = xmldoc.createElement("sender"); _sender.setTextContent(msg.sender);
            //the content is been added to the element object
            Element _content = xmldoc.createElement("content"); _content.setTextContent(msg.content);
            //the recipient is been added to the element object
            Element _recipient = xmldoc.createElement("recipient"); _recipient.setTextContent(msg.recipient);
            //the time at which the message is been sent is also added to the element object
            Element _time = xmldoc.createElement("time"); _time.setTextContent(time);
            //the above information is been appended to the child node
            message.appendChild(_sender); message.appendChild(_content); message.appendChild(_recipient); message.appendChild(_time);
            data.appendChild(message);
            //a TransformerFactory instance can be used to create Transformer and Templates objects
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
             //an instance of this abstract class can transform a source tree into a result tree.
            Transformer transformer = transformerFactory.newTransformer();
            //acts as a holder for a transformation Source tree in the form of a Document Object Model (DOM) tree
            DOMSource source = new DOMSource(xmldoc);
            //acts as an holder for a transformation result, which may be XML, plain Text, HTML, or some other form of markup
            StreamResult result = new StreamResult(new File(historyfile));
            //the source to result transformation is carried out
            transformer.transform(source, result);
 
	   } 
           //catch block is used to catch the exceptions if any is thrown in the above try block
           catch(Exception ex){
		System.out.println("Cannot write to file");
	   }
	}
    //method to fill the history table and the frame is passed from the design
    public void FillTable(com.Userinterface.ShowHistory frame){
        //this is an implementation of TableModel that uses a Vector of Vectors to store the cell value objects
        DefaultTableModel model = (DefaultTableModel) frame.jTable1.getModel();
        //try block is initiated to manage with exceptions
        try{
            //a file object is created and historyfile is passed as parameter 
            File fXmlFile = new File(historyfile);
            //defines a factory API that enables applications to obtain a parser that produces DOM object trees from XML documents
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            //defines the API to obtain DOM Document instances from an XML document. Using this class, an application programmer can obtain a Document from XML.
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            //parse the content of the given file as an XML document and return a new DOM Document object
            Document doc = dBuilder.parse(fXmlFile);
            //the getDocument Element allows to acces the child node directly
            doc.getDocumentElement().normalize();
            //the list of messages is got as nodes
            NodeList nList = doc.getElementsByTagName("message");
            //condition to check the end of list and perform memory swapping
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                //if the type of node is in element_node
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    //typecast the node to element
                    Element eElement = (Element) nNode;
                    //a row is added to the table by indicating the sender, content, recipient and time
                    model.addRow(new Object[]{getTagValue("sender", eElement), getTagValue("content", eElement), getTagValue("recipient", eElement), getTagValue("time", eElement)});
                }
            }
        }
        //catch block is used to catch the exceptions if any is thrown in the above try block
        catch(Exception ex){
            System.out.println("Filling Exception");
        }
    }
    //returns the tag value of the element object in the history file
    public static String getTagValue(String sTag, Element eElement) {
	NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
	return nValue.getNodeValue();
  }
}
