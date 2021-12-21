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
 * 
 */
public class User {
    private int userID;
    private boolean online;
    
    public User(int userID){
        this.userID = userID;
    }
    
    public void setOnline(boolean bol){
        this.online = bol;
    }
    
    public int getUserID(){
        return userID;
    }
    
    public boolean isOnline(){
        return online;
    }
}
