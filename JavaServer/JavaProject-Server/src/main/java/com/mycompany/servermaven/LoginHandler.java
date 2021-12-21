/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.servermaven;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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
public class LoginHandler implements Runnable {
    private Server server;
    
    private Socket client = null;
    private String clientLogin;
    private String clientPassword;
    public int userID;
    private PrintWriter messageToClient;
    private BufferedReader clientInputStream;
    private Connection conDatabase;
    public ClientHandler currentClient;
    
    public LoginHandler(Socket clientSocket, Server server, JInputMessage auth, Connection conDb, ClientHandler thisClient){
        
        try {
            this.server = server;
            this.client = clientSocket;
            this.clientLogin = auth.login;
            this.clientPassword = auth.password;
            this.messageToClient = new PrintWriter(client.getOutputStream());
            this.conDatabase = conDb;
            this.currentClient = thisClient;
            
        } catch (IOException ex) {
            Logger.getLogger(LoginHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run(){
        try{
            Authorize();
           
        }catch(Exception ex){
            ex.getLocalizedMessage();
        }
    }
    
    
    private void Authorize(){
        try{
            //запрос в БД
            String csql = "SELECT userID, user_login FROM chatusers WHERE user_login= '" +clientLogin+ "' "
                    + "AND user_password = '"+clientPassword+"'";    
            
            //Создали statement 
            Statement st = conDatabase.createStatement();
            st.executeQuery(csql);
            ResultSet rs = st.executeQuery(csql);
            
            //вернули userID и user_login
            if(rs.next()){
                int id = rs.getInt ("userid");
                this.userID = id;
                //String login = rs.getString("user_login");
                JSONObject res = new JSONObject();
                res.put("type", "success");
                res.put("userID", id);
                currentClient.userID = id;
                messageToClient.println(res);
                messageToClient.flush();
                //break;
            }
            else {
                JSONObject res = new JSONObject();
                res.put("type", "invalid");
                res.put("error","Пользователь не найден");
                messageToClient.println(res);
                messageToClient.flush();
            }

        }catch(Exception ex){
            ex.getStackTrace();
        }
        finally{
            this.close();
        }
        
    }
    
    private void close(){
        //Здесь допишем сообщение о резульате авторизации
        server.removeClientOnLogin(this);
    }
}
