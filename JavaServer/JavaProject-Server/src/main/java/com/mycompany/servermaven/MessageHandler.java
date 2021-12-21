/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.servermaven;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author1 Dmitriev
 * @author2 Toropchinov
 *   
 * 
 */
public class MessageHandler implements Runnable{
    private int chatID;
    private int authorID;
    private int reciverID = 0;
    private String messageText;
    private Date messageDate;
    private boolean status = false;
    private Socket client;
    private Connection conDb;
    private BufferedReader clientInputStream ;
    private PrintWriter clientOutputStream;
    private String type;
    private Server server;
    
    
    public MessageHandler(Socket clientSocket, int chatID, int authorID, int reciverID, String message , Connection conDb, String type, Server server){
        try {
            this.chatID = chatID;
            this.authorID = authorID;
            this.client = clientSocket;
            this.reciverID = reciverID;
            this.conDb = conDb;
            this.messageText = message;
            this.type = type;
            this.clientOutputStream = new PrintWriter(client.getOutputStream());
            this.server = server;
        } catch (IOException ex) {
            Logger.getLogger(MessageHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run(){
        if(type.equals("getMessages")){
            String sql = "select text from messages where chat_id = '" + chatID + "' ";
            Statement st;
            try {
                st = conDb.createStatement();
                st.executeQuery(sql);
                ResultSet rs = st.executeQuery(sql);
                ArrayList<String> messages = new ArrayList<String>();
                //author, text authorFullName+text
                while (rs.next()) {
                    messages.add(rs.getString(1));
                }

                final StringWriter sw =new StringWriter();
                final ObjectMapper mapper = new ObjectMapper();

                try {
                    mapper.writeValue(sw, messages);
                    JSONObject res = new JSONObject();
                    res.put("type", "chatMessages");
                    res.put("messages", messages);
                    System.out.println(res);
                    clientOutputStream.println(res);
                    clientOutputStream.flush();
                } catch (IOException ex) {
                    Logger.getLogger(MessageHandler.class.getName()).log(Level.SEVERE, null, ex);
                }


            } catch (SQLException ex) {
                Logger.getLogger(MessageHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(type.equals("recivedMessage")){
            String fsql = "SELECT user_fullname from chatusers where userid='" + authorID + "' ";
            Statement st;
            try {
                st = conDb.createStatement();
                st.executeQuery(fsql);
                ResultSet rs = st.executeQuery(fsql);
                String uFN = "";
                if(rs.next()){
                    uFN = rs.getString(1);
                }
                messageText = uFN + ": " + messageText;
                String sql = "INSERT INTO messages (author, reciver, chat_id, text) "
                    + "VALUES('" + authorID + "','"+reciverID+"','"+chatID+"','"+messageText+"')";
                System.out.println("INSERT");
                st.executeUpdate(sql);
                
                
                String sqlf = "select text from messages where chat_id = '" + chatID + "' ";
                st.executeQuery(sqlf);
                
                
                ResultSet rsaf = st.executeQuery(sqlf);
                ArrayList<String> messages = new ArrayList<String>();
                //author, text authorFullName+text
                while (rsaf.next()) {
                    messages.add(rsaf.getString(1));
                }

                final StringWriter sw =new StringWriter();
                final ObjectMapper mapper = new ObjectMapper();

                try {
                    mapper.writeValue(sw, messages);
                    JSONObject res = new JSONObject();
                    res.put("type", "chatMessages");
                    res.put("messages", messages);
                    System.out.println(res);
                    clientOutputStream.println(res);
                    clientOutputStream.flush();
                    
                    for(int i = 0; i < server.clients.size(); i++){
                        if(server.clients.get(i).userID == reciverID){
                            PrintWriter output = new PrintWriter(server.clients.get(i).getSocket().getOutputStream());
                            JSONObject res2 = new JSONObject();
                            res2.put("type", "chatMessages");
                            res2.put("messages", messages);
                            System.out.println(res2);
                            output.println(res2);
                            output.flush();
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MessageHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(MessageHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
    }
}
