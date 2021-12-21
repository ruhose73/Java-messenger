/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.servermaven;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;

/**
 *
 * @author lalal
 */
public class ClientHandler implements Runnable{
    private Socket client;
    private Server server;
    private Connection conDB;
    private BufferedReader clientInputStream;
    public int userID;
    
    ClientHandler(Socket client, Server server, Connection conDB){
        this.client = client;
        this.server = server;
        this.conDB = conDB;
    }
    
    @Override
    public void run(){
        try{
            clientInputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while(true){
                 String input = clientInputStream.readLine();
 
                ObjectMapper mapper = new ObjectMapper();
                JInputMessage message = mapper.readValue(input, JInputMessage.class);
                //System.out.println("message" + message.toString());
 
                if (message.type.equals("login")) {
                    System.out.println("login");
                    LoginHandler login = new LoginHandler(client, server, message, conDB, this);
                    
                    new Thread(login).start();
                }
                 clientInputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
            
                
                if (message.type.equals("message")) {
                    ChatHandler chat = new ChatHandler(client ,message.chatID, Integer.parseInt(message.firstUserID), Integer.parseInt(message.secondUserID), message.userMessage, conDB);
                    new Thread(chat).start();
                }
                
                if(message.type.equals("getChat")){
                    System.out.println("getChat");
                    ChatHandler chat = new ChatHandler(client, Integer.parseInt(message.firstUserID), message.secondUserID,  conDB);
                    new Thread(chat).start();
                }
                
                if(message.type.equals("getContacts")){
                    System.out.println("getContacts");
                    ContactsHandler cont = new ContactsHandler(client, Integer.parseInt(message.userID), conDB);
                    new Thread(cont).start();
                }
                
                if(message.type.equals("getMessages")){
                    System.out.println("getMessages");
                    MessageHandler mes = new MessageHandler(client, message.chatID, Integer.parseInt(message.firstUserID), 0, " ", conDB, message.type, server);
                    new Thread(mes).start();
                }
                if(message.type.equals("recivedMessage")){
                    
                    System.out.println(message.secondUserID);
                    MessageHandler mes = new MessageHandler(client, message.chatID, Integer.parseInt(message.firstUserID), Integer.parseInt(message.secondUserID), message.userMessage, conDB, message.type, server);
                    new Thread(mes).start();
                }
 
            
                
            }
        }catch(Exception ex){
            ex.getLocalizedMessage();
        }
    }
    
    public Socket getSocket(){
        return client;
    }
}
