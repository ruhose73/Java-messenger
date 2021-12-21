/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.clientmaven;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import org.json.JSONObject;

/**
 *
 * @author lalal
 */
public class ContactsFrame extends javax.swing.JFrame {

    /**
     * Creates new form ContactsFrame
     */
    private ArrayList<String> userIDs = new ArrayList<String>();
    private String userID;
    private Socket client;
    private BufferedReader messageIn;
    private PrintWriter messageOut;
    private ChatWindow window;
    private boolean flag = true;
    public ContactsFrame(Socket clientSocket, String userId) {
         initComponents();
        
        //this.setLocationRelativeTo(null);
        this.client = clientSocket;
        this.userID = userId;
        try {
            
            messageIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
            messageOut = new PrintWriter(client.getOutputStream()); 
            
        } catch (IOException ex) {
            Logger.getLogger(ContactsFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        new Thread(new Runnable(){
            @Override
            public void run(){
                getContacts();
               
                while(true){
                    String input;
                    
                    
                    try {
                        input = messageIn.readLine();
                        ObjectMapper mapper = new ObjectMapper();
                        JInputMessage message = mapper.readValue(input, JInputMessage.class);
                        //Если нам пришел чат айди , то тогда создаем окно чата        
                        if(message.type.matches("recivedChat")){
                            window = new ChatWindow(client, message.chatName, message.chatID, userID, message.secondUserID, ContactsFrame.this);
                            window.setVisible(true);
                            setVisible(false);
                           
                        }
                        if(message.type.matches("invalid")){
                            JOptionPane.showMessageDialog(null, message.error);
                        }
                        if(message.type.matches("contactsRecived")){
                            userIDs = message.userIDs;
                            System.out.println(message.userIDs);
                            jToolBar1.removeAll();
                            for(int i = 0; i < userIDs.size(); i++){
                                JButton lbl = new JButton(userIDs.get(i));
                                System.out.println(lbl.getText());
                                lbl.setName(userIDs.get(i));
                                GridLayout layout = new GridLayout(userIDs.size(), 1);
                                jToolBar1.setLayout(layout);
                                
                                lbl.addActionListener(new ActionListener(){
                                    @Override
                                    public void actionPerformed(ActionEvent e){
                                        System.out.println("sss");
                                        JSONObject req = new JSONObject();
                                        req.put("type", "getChat");
                                        req.put("firstUserID", userID);
                                        req.put("secondUserID", ((JButton)e.getSource()).getName());
                                        
                                        messageOut.println(req);
                                        messageOut.flush();
                                    } 
                                });
                                
                                
                                lbl.setVisible(true);
                                jToolBar1.add(lbl);
                                jToolBar1.revalidate();
                                
                                revalidate();
                                //Добавляем обработку события нажатия на это всё( формируем запрос к серверу на получение ответа) 

                            }
                            jToolBar1.revalidate();
                            jToolBar1.repaint();
                            flag = false;
                            
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(ContactsFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                   

                }
            }
        }).start();
          
        
        
        
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jToolBar1 = new javax.swing.JToolBar();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 48)); // NOI18N
        jLabel1.setText("Чаты");

        jTextField1.setText("Введите имя человека");

        jButton1.setText("Найти");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jToolBar1.setRollover(true);

        jButton2.setText("Выйти");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(130, 130, 130)
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JSONObject req = new JSONObject();
        req.put("type", "getChat");
        req.put("firstUserID", userID);
        req.put("secondUserID", jTextField1.getText());
        
        System.out.println(req);
        messageOut.println(req);
        messageOut.flush();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            // TODO add your handling code here:
            client.close();
        } catch (IOException ex) {
            Logger.getLogger(ContactsFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.exit(0);
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ContactsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ContactsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ContactsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ContactsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ContactsFrame(new Socket(), new String()).setVisible(true);
            }
        });
    }
    
     public void getContacts(){
        JSONObject req = new JSONObject();
        req.put("type", "getContacts");
        req.put("userID", this.userID);
        
        messageOut.println(req);
        messageOut.flush();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
}
