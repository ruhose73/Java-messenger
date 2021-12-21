/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 
package com.mycompany.servermaven;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
 * @author1 Dmitriev
 * @author2 Toropchinov
 *   
 * 
 */
 
public class ChatHandler implements Runnable {
 
    
    private int chatID;
    private int firstUserID;
    private String userMessage;
    private int secondUserID = 0;
    private String user_fullname;
    private Socket client;
    private Connection conDatabase;
    
    private BufferedReader clientInputStream ;
    private PrintWriter clientOutputStream;
    
    private PrintWriter messageToClient;
 
    public ChatHandler(Socket clientSocket ,int chatID,  int firstUserID, int secondUserID, String userMessage, Connection conDb) throws IOException {
        
        this.chatID = chatID;
        this.firstUserID = firstUserID;
        this.secondUserID = secondUserID;
        this.conDatabase = conDb;
        this.client = clientSocket;
        this.messageToClient = new PrintWriter(clientSocket.getOutputStream());
    }

    public ChatHandler(Socket clientSocket, int firstUserId, String userFullName, Connection conDatabase) {
        try {
            this.client = clientSocket;
            this.firstUserID = firstUserId;
            this.conDatabase = conDatabase;
            this.user_fullname = userFullName;
            this.messageToClient = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ChatHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    
    
 
    @Override
    public void run() {
        try {
            while (true) {
                boolean chatFounded = false;
                this.clientInputStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
                this.clientOutputStream = new PrintWriter(client.getOutputStream());
                //Ищем чат по userid1 и userid2 в бд и возвращаем chatID
               
                this.findOrCreateNew();
                break;
                
 
                /* Это код ответа, он будет дописан после бд
              for(int i = 0; i < clients.size(); i++){
                  ClientHandler client = clients.get(i);
                  if(client.getUser().getUserID() == userID && client.getUser().isOnline()){
                      
                  }
              }
                 */
            }
        } catch (IOException ex) {
            Logger.getLogger(ChatHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
    private void sendResponse() {
           this.clientOutputStream.println("sss");
           this.clientOutputStream.flush();
    }
    
    
 
    private void findOrCreateNew()  {
        //Пишем запрос к бд , где создаем новый объект чата и добавляем туда два userID
        //возвращаем chatID нашего нового чата
 
        //создаем новый чат (добавляем 2 пользователей в таблицу)
        
        try {
            String sql0 = "SELECT userid FROM chatusers WHERE user_fullname='"+user_fullname+ "' ";
            Statement st;
            st = conDatabase.createStatement();
            st.executeQuery(sql0);
            ResultSet idFromTable = st.executeQuery(sql0);
            int sUId = 0;
            if (idFromTable.next()){
                sUId = idFromTable.getInt("userid");
                
            }
            
            if(firstUserID == sUId){
                JSONObject res = new JSONObject();
                res.put("type", "invalid");
                res.put("error", "Ошибка создания чата");
                messageToClient.println(res);
                messageToClient.flush();
            }
            else{
                String csql = "SELECT chatid FROM chats WHERE first_userid= '" + firstUserID + "' "
                        + "AND second_userid = '" + sUId + "' or first_userid= '" + sUId + "' and second_userid='" + firstUserID + "' ";
 
                
                st.executeQuery(csql);
                ResultSet rsf = st.executeQuery(csql);

                //вернули chatID
                if (rsf.next()) {
                    int chatID = rsf.getInt("chatid");

                    JSONObject res = new JSONObject();
                    res.put("type", "recivedChat");
                    res.put("chatID", chatID);
                    res.put("chatName", user_fullname);
                    res.put("secondUserID", sUId);
                    messageToClient.println(res);
                    messageToClient.flush();

                }
                else {
                    if(sUId != 0){
                        String csql1 = "INSERT INTO chats (first_userid, second_userid) VALUES"
                        + "('" + firstUserID + "','" + sUId + "')";

                        st.executeUpdate(csql1);

                        //возвращаем наш chatID
                        String csql2 = "SELECT chatid FROM chats WHERE first_userid= '" + firstUserID + "' "
                                + "AND second_userid = '" + sUId + "'";

                        st.executeQuery(csql2);
                        ResultSet rs = st.executeQuery(csql2);

                        if (rs.next()) {
                            int chatID = rs.getInt("chatID");
                            JSONObject res = new JSONObject();
                            res.put("type", "recivedChat");
                            res.put("chatID", chatID);
                            res.put("chatName", user_fullname);
                            res.put("secondUserID", sUId);
                            messageToClient.println(res);
                            messageToClient.flush();
                        }else {
                            JSONObject res = new JSONObject();
                            res.put("type", "invalid");
                            res.put("error", "Ошибка создания чата");
                            messageToClient.println(res);
                            messageToClient.flush();
                        }
                    }
                    else{
                        JSONObject res = new JSONObject();
                        res.put("type", "invalid");
                        res.put("error", "Ошибка создания чата");
                        messageToClient.println(res);
                        messageToClient.flush();
                    }
                    
                    
                }
            }
            
            
        } catch (SQLException ex) {
            Logger.getLogger(ChatHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}