/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatcliente;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author GuachaWTF
 */
public class Ventana extends javax.swing.JFrame {

    private DataOutputStream out;
    private String username;
    private DefaultListModel dm;
    /**
     * Creates new form Ventana
     * @param ip La IP del servidor a conectar
     * @param puerto el puerto del servidor a conectar
     * @param username el nombre que se mostrará en el chat
     * @throws java.io.IOException Se tira excepción si la conexión no sirve
     */
    public Ventana(String ip, int puerto, String username) throws IOException {
        initComponents();
        this.username = username;
        this.setTitle("Chat: " + this.username);
        connect(ip, puerto);
    }
    
    public void connect(String ip, int puerto) throws IOException {
            
            // Crear objeto socket con los parámetros de conexión
            Socket socket = new Socket(ip, puerto);

            System.out.println("Conectado exitosamente al Servidor!");
            
            // Obtener la salida de Datos del socket
            OutputStream output = socket.getOutputStream();
            out = new DataOutputStream(output);
            
            // Iniciar un hilo para esperar mensajes del servidor
            HiloLectura hl = new HiloLectura(socket, this);
            hl.start();
            
            // Enviarle al servidor nuestro nombre
            this.out.writeUTF(this.username);

    }
    
    public void update(String mensaje) {
        
        //Obtener los objetos para display de chat
        StyledDocument doc = chat.getStyledDocument();
        Style style = chat.addStyle("Style", null);
        StyleConstants.setForeground(style, Color.black);
        
        //Verificar si es un mensaje del servidor
        //Los mensajes del servidor siempre empiezan con ";/"
        if (mensaje.substring(0, 2).equals(";/")) {
            
            //Conexión nueva de usuario
            if (mensaje.contains(";/connect")) {
                StyleConstants.setForeground(style, Color.blue);
                String usr = mensaje.substring(10);
                mensaje = "[SERVIDOR]: " + usr + " se ha conectado!";
                this.addUser(usr);
            
            //Desconexión de usuario
            } else if(mensaje.contains(";/discon")) {
                StyleConstants.setForeground(style, Color.blue);
                String usr = mensaje.substring(9);
                mensaje = "[SERVIDOR]: " + usr + " se ha desconectado!";
                this.removeUser(usr);
            }
        } else {
            StyleConstants.setForeground(style, Color.black);
        }
        
        addText(doc, mensaje, style);
        
    }
    
    // Agregar texto al textPane
    public void addText(StyledDocument doc, String text, Style style) {
        try {
            doc.insertString(doc.getLength(), text + "\n", style);
        } catch (BadLocationException ex) {
            Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Enviar mensaje al servidor
    public void sendMensaje(String Mensaje) {
        try {
            this.out.writeUTF(Mensaje);
        } catch (IOException ex) {
            System.err.println("No se ha podido envier el mensaje");
        }

    }
    
    //Agregar usuario a la lista
    public void addUser(String username) {
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < lista.getModel().getSize(); i++) {
            Object o = lista.getModel().getElementAt(i);
            model.addElement(o);
        }
        
        model.addElement(username);
        lista.setModel(model);
    }
    
    // Eliminar usuario de lista
    public void removeUser(String username) {
        DefaultListModel model = (DefaultListModel)lista.getModel();
        for (int i = 0; i < lista.getModel().getSize(); i++) {
            Object o = lista.getModel().getElementAt(i);
            if (((String)o).equals(username)) {
                model.remove(i);
            }
        }
        
        lista.setModel(model);
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
        jLabel2 = new javax.swing.JLabel();
        sendData = new javax.swing.JTextField();
        enviarButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        chat = new javax.swing.JTextPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        lista = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Chat");

        jLabel2.setText("Usuarios conectados");

        sendData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendDataActionPerformed(evt);
            }
        });
        sendData.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                sendDataKeyPressed(evt);
            }
        });

        enviarButton.setText("Enviar");
        enviarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enviarButtonActionPerformed(evt);
            }
        });

        chat.setEditable(false);
        jScrollPane3.setViewportView(chat);

        jScrollPane1.setViewportView(lista);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 480, Short.MAX_VALUE))
                    .addComponent(sendData)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(enviarButton, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sendData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(enviarButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sendDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendDataActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sendDataActionPerformed

    private void enviarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enviarButtonActionPerformed
        String mensaje = sendData.getText();
        
        this.sendMensaje(mensaje);
        
        StyledDocument doc = chat.getStyledDocument();
        Style style = chat.addStyle("Style", null);
        StyleConstants.setForeground(style, Color.black);
        
        this.addText(doc, "[TÚ]: " + mensaje, style);
        this.sendData.setText("");
    }//GEN-LAST:event_enviarButtonActionPerformed

    private void sendDataKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sendDataKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER){
            enviarButtonActionPerformed(null);
        }
    }//GEN-LAST:event_sendDataKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextPane chat;
    private javax.swing.JButton enviarButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList<String> lista;
    private javax.swing.JTextField sendData;
    // End of variables declaration//GEN-END:variables
}
