/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.servermaven;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author Dmitriev
 */
public class ContactsHandler implements Runnable {
    private int userID;
    private Socket client;
    private Connection conDb;
    private BufferedReader clientInputStream ;
    private PrintWriter clientOutputStream;
    
    public ContactsHandler(Socket clientSocket, int userID, Connection con) {
        this.client = clientSocket;
        this.userID = userID;
        this.conDb = con;
        try {
            this.clientOutputStream = new PrintWriter(client.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ContactsHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    @Override
    public void run(){
        String sql = "select chatid from chats where first_userid = '" + userID + "' or second_userid = '" + userID + "' ";
        Statement st;
        try {
            st = conDb.createStatement();
            st.executeQuery(sql);
            ResultSet rs = st.executeQuery(sql);
            ArrayList<Integer> ids = new ArrayList<Integer>();
            
            while (rs.next()) {
                    ids.add(rs.getInt("chatid"));
            }
            
            ArrayList<Integer> idOfUsers = new ArrayList<Integer>();
            
            for(int i = 0; i < ids.size(); i++){
                String sql2 = "select first_userid,second_userid from chats where chatid='" + ids.get(i) + "' ";
                st.executeQuery(sql2);
                rs = st.executeQuery(sql2);
                while(rs.next()){
                    if(rs.getInt("first_userid") != userID){
                        idOfUsers.add(rs.getInt("first_userid"));
                    }
                    else if(rs.getInt("second_userid") != userID){
                        idOfUsers.add(rs.getInt("second_userid"));
                    }
                }
            }
            
            ArrayList<String> userNames = new ArrayList<String>();
            
            for(int i = 0; i < idOfUsers.size(); i++){
                String sql2 = "select user_fullname from chatusers where userid='" + idOfUsers.get(i) + "' ";
                st.executeQuery(sql2);
                rs = st.executeQuery(sql2);
                while (rs.next()) {
                    userNames.add(rs.getString(1));
                }
            }
            

            if(!userNames.isEmpty()){
                JSONObject res = new JSONObject();
                res.put("type", "contactsRecived");
                res.put("userIDs", userNames);
                
                try{
                    clientOutputStream.println(res);
                    clientOutputStream.flush();
                    System.out.println(res);
                }catch(Exception ex){
                    ex.getLocalizedMessage();
                }
            }
            else{
                JSONObject res = new JSONObject();
                res.put("type", "noContacts");
                res.put("message", "У вас ещё нет чатов");
                try{
                    clientOutputStream.println(res);
                    clientOutputStream.flush();
                }catch(Exception ex){
                    ex.getLocalizedMessage();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ContactsHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
                
    }
}
