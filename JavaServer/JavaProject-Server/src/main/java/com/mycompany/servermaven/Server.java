/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.servermaven;
 
/**
 *
 * @author1 Dmitriev
 * @author2 Toropchinov
 * 
 */
 
 
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.runtime.ScriptObject;
 
public class Server extends ScriptObject {
 
    static final int PORT = 5600;
    
    private ArrayList<LoginHandler> clientsOnLogin = new ArrayList<LoginHandler>();
    public ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
    public ArrayList<Integer> users = new ArrayList<Integer>();
    
    private BufferedReader clientInputStream ;
 
    public Server() {
 
        Socket clientSocket = null;
 
        ServerSocket serverSocket = null;
 
        String dbUser = "postgres";
        String dbPassword = "root";
        String dbUrl = "jdbc:postgresql://localhost:5432/iatemessenger";
        String drvName = "org.postgresql.Driver";
        Connection conDatabase = null;
 
        try {
            Class.forName(drvName);
            conDatabase = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port = " + PORT);
            
            
            
            while (true) {
                clientSocket = serverSocket.accept();
                ClientHandler client = new ClientHandler(clientSocket, this, conDatabase);
                clients.add(client);
                new Thread(client).start();
 
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                // закрываем подключение
                
                if(serverSocket != null){
                    System.out.println("Server has been stopped");
                    serverSocket.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
 
    
 
    
    // удаляем клиента из коллекции при выходе из чата
    
 
    public void removeClientOnLogin(LoginHandler login) {
        clientsOnLogin.remove(login);
    }
 
}