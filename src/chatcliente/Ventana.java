/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatcliente;

import GameLogic.Player;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 *
 * @author GuachaWTF
 */
public class Ventana extends javax.swing.JFrame {

    private DataOutputStream out;
    private String username;
    private DefaultListModel dm;
    private Webcam cam;
    private ArrayList<String> usuarios;

    /**
     * Creates new form Ventana
     *
     * @param ip La IP del servidor a conectar
     * @param puerto el puerto del servidor a conectar
     * @param username el nombre que se mostrará en el chat
     * @throws java.io.IOException Se tira excepción si la conexión no sirve
     */
    public Ventana(String ip, int puerto, String username) throws IOException {
        initComponents();
        this.username = username;
        this.setTitle("Merge: " + this.username);
        connect(ip, puerto);

        usuarios = new ArrayList();
        //this.player = new Player();

        try {
            cam = Webcam.getDefault();
            cam.close();
            cam.setViewSize(new Dimension(320, 240));
            cam.open();
            isOpen = true;
            new VideoTaker().start();
        } catch (WebcamException e) {
            //toOpenCam.setVisible(false);
            //toOpenCam.setEnabled(false);
            cam = null;
            scaleImage("camera.jpg", camera);
            System.out.println(e.getMessage());
        }
        cameraLabel.setText(username + "'s Webcam");
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
            } else if (mensaje.contains(";/discon")) {
                StyleConstants.setForeground(style, Color.blue);
                String usr = mensaje.substring(9);
                mensaje = "[SERVIDOR]: " + usr + " se ha desconectado!";
                this.removeUser(usr);
            }
        } else {
            StyleConstants.setForeground(style, Color.black);
        }

        //addText(doc, mensaje, style);
        applyLogic(doc, mensaje, style);

    }

    // Agregar texto al textPane
    public void addText(StyledDocument doc, String text, Style style) {
        try {
            doc.insertString(doc.getLength(), text + "\n", style);
        } catch (BadLocationException ex) {
            Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Aplica la logica del juego
    //private Player player;
    private ArrayList<Integer> cola;

    public void applyLogic(StyledDocument doc, String text, Style style) {
        String[] line = text.split(":");

        if (line[1].substring(1, 2).equalsIgnoreCase("!")) {

            String mensaje = line[1].substring(2);
            String[] postText = mensaje.split(" ");
            try {
                if (postText[0].equalsIgnoreCase("reproducir") || postText[0].equalsIgnoreCase("play")) {
                    String emisor = line[0].substring(1, line[0].length() - 1);

                    if (postText[1].equalsIgnoreCase("rock")) {
                        boolean capable = true;
                        try {
                            int toPlay = 0;
                            if (postText[2] != null) {
                                if (postText[2].equalsIgnoreCase("random")) {
                                    Random ran = new Random();
                                    toPlay = ran.nextInt(12);
                                } else {
                                    try {
                                        toPlay = Integer.parseInt(postText[2]);
                                    } catch (NumberFormatException e) {
                                        capable = false;
                                    }
                                }
                            }
                            if (capable) {
                                String genre = postText[1].toLowerCase();
                                String song = getSongName(genre, toPlay);
                                System.out.println("Playing: " + song);

                                playMusic("/Music/" + genre + "/" + song);
                                String msj = emisor + " puso la cancion: " + song + "\nPara detener la reproduccion escriba !stop\n";
                                addText(doc, msj, style);
                            } else {
                                String msj = "Recuerda pedir la cancion con [index] o [random]\n";
                                addText(doc, msj, style);
                            }

                        } catch (NullPointerException e) {
                            String msj = "Recuerda pedir la cancion con [index] o [random]\n";
                            addText(doc, msj, style);
                        }

                    } else if (postText[1].equalsIgnoreCase("lovely")) {
                        boolean capable = true;
                        try {
                            int toPlay = 0;
                            if (postText[2] != null) {
                                if (postText[2].equalsIgnoreCase("random")) {
                                    Random ran = new Random();
                                    toPlay = ran.nextInt(9);
                                } else {
                                    try {
                                        toPlay = Integer.parseInt(postText[2]);
                                    } catch (NumberFormatException e) {
                                        capable = false;
                                    }
                                }
                            }
                            if (capable) {
                                String genre = postText[1].toLowerCase();
                                String song = getSongName(genre, toPlay);
                                System.out.println("Playing: " + song);

                                playMusic("/Music/" + genre + "/" + song);
                                String msj = emisor + " puso la cancion: " + song + "\nPara detener la reproduccion escriba !stop\n";
                                addText(doc, msj, style);
                            } else {
                                String msj = "Recuerda pedir la cancion con [index] o [random]\n";
                                addText(doc, msj, style);
                            }

                        } catch (Exception e) {
                            String msj = "Recuerda pedir la cancion con [index] o [random]\n";
                            addText(doc, msj, style);
                        }
                    }
                } else if (postText[0].equalsIgnoreCase("stop")) {
                    System.out.println("stop");
                    stopMusic();
                }else if(postText[0].equalsIgnoreCase("pause")){
                    String emisor = line[0].substring(1, line[0].length() - 1);
                    pauseMusic();
                    String msj = "Cancion pausada por " + emisor + "\n";
                    addText(doc, msj, style);
                }else if(postText[0].equalsIgnoreCase("resume")){
                    String emisor = line[0].substring(1, line[0].length() - 1);
                    pauseMusic();
                    String msj = "Cancion resumida por " + emisor + "\n";
                    addText(doc, msj, style);
                } else if (postText[0].equalsIgnoreCase("MusicList")) {
                    String msj = getSongName("rock", -1);
                    addText(doc, msj, style);
                    msj = getSongName("lovely", -1);
                    addText(doc, msj, style);
                } else if (postText[0].equalsIgnoreCase("help") || postText[0].equalsIgnoreCase("ayuda")) {
                    String msj = "Comandos de musica:\n!MusicList: ver la musica disponible.\n!Play [genero] [index / random]: colocar una cancion por su posicion o aleatoria.\n"
                            + "!Stop: detener la cancion que se reproduce.";
                    addText(doc, msj, style);
                }
            } catch (LineUnavailableException | UnsupportedAudioFileException e) {
                System.out.println("Error de reproduccion");
            }

        } else if (line[1].substring(1, 2).equalsIgnoreCase("#")) {
            //String msj = line[0] + ": " +  line[1].substring(2) + " - interactuar";
            //filtrar por accion
            //#abrazar kemer
            String mensaje = line[1].substring(2);
            String[] postText = mensaje.split(" ");
            try {
                if (postText[0].equalsIgnoreCase("abrazar") || postText[0].equalsIgnoreCase("hug")) {
                    boolean found = false;
                    String emisor = line[0].substring(1, line[0].length() - 1);
                    for (String user : usuarios) {
                        if (user.equalsIgnoreCase(postText[1])) {
                            if (user.equalsIgnoreCase(this.username) && user.equalsIgnoreCase(emisor)) {
                                String msj = "Pobre " + emisor + ", se abrazo a si mismo";
                                addText(doc, msj, style);
                                found = true;
                                break;
                            } else {
                                String msj = emisor + " abrazo a " + user;
                                //agregar imagen al label
                                addText(doc, msj, style);
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        String msj = emisor + " abrazo a la nada";
                        addText(doc, msj, style);
                    }
                } else if (postText[0].equalsIgnoreCase("besar") || postText[0].equalsIgnoreCase("kiss")) {
                    boolean found = false;
                    String emisor = line[0].substring(1, line[0].length() - 1);
                    for (String user : usuarios) {
                        if (user.equalsIgnoreCase(postText[1])) {
                            if (user.equalsIgnoreCase(this.username) && user.equalsIgnoreCase(emisor)) {
                                String msj = "Un momento " + emisor + "! NO ES POSIBLE besarse a si mismo";
                                addText(doc, msj, style);
                                found = true;
                                break;
                            } else {
                                String msj = emisor + " besó a " + user;
                                addText(doc, msj, style);
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        String msj = emisor + " besó al aire";
                        addText(doc, msj, style);
                    }
                } else if (postText[0].equalsIgnoreCase("help") || postText[0].equalsIgnoreCase("ayuda")) {
                    String msj = "Comando de interaccion:\n#hug [participante]\n#kiss [participante]";
                    addText(doc, msj, style);
                } else {
                    addText(doc, "Accion no registrada", style);
                }
            } catch (Exception e) {
                addText(doc, "Nada con que interactuar", style);
            }

        } else {//Server mesajes
            if(text.equalsIgnoreCase(""))
                text = " ";
            addText(doc, text, style);
        }
    }

    //reporducir musica
    Clip clip;

    public void playMusic(String path) throws UnsupportedAudioFileException, LineUnavailableException {
        AudioInputStream music;
        String songPath = System.getProperty("user.dir");
        songPath = songPath.substring(0, songPath.length() - 4);
        songPath += path;
        try {
            music = AudioSystem.getAudioInputStream(new File(songPath));
            clip = AudioSystem.getClip();
            clip.open(music);
            clip.start();
            isPaused = false;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void stopMusic() throws LineUnavailableException {
        clip.stop();
        StyledDocument doc = chat.getStyledDocument();
        Style style = chat.addStyle("Style", null);
        StyleConstants.setForeground(style, Color.black);
        addText(doc, "Se detuvo la cancion\n", style);
    }
    
    private boolean isPaused = false;
    private long pgrs;
    public void pauseMusic(){
        if(!isPaused){
            pgrs = clip.getMicrosecondPosition();
            clip.stop();
            isPaused = true;
        }else{
            clip.setMicrosecondPosition(pgrs);
            clip.start();
            isPaused = false;
        }
    }

    //obtener el nombre de la cancion
    public String getSongName(String genero, int i) {
        String song;
        switch (genero) {
            case "rock":
                switch (i) {
                    case -1:
                        song = "Rock: Pentakill lost chapther\n1 - Lost Chapter.wav\n2 - Predator.wav\n3 - Edge of Night.wav\n4 - Gathering Storm.wav\n5 - Conqueror.wav\n"
                                + "6 - Executioner's Calling.wav\n7 - Stormrazor.wav\n8 - Aftershock.wav\n9 - Last Stand.wav\n"
                                + "10 - Redemption.wav\n11 - Lightbringer (Acoustic).wav\n";
                        break;
                    case 1:
                        song = "01 - Lost Chapter.wav";
                        break;
                    case 2:
                        song = "02 - Predator.wav";
                        break;
                    case 3:
                        song = "03 - Edge of Night.wav";
                        break;
                    case 4:
                        song = "04 - Gathering Storm.wav";
                        break;
                    case 5:
                        song = "05 - Conqueror.wav";
                        break;
                    case 6:
                        song = "06 - Executioner's Calling.wav";
                        break;
                    case 7:
                        song = "07 - Stormrazor.wav";
                        break;
                    case 8:
                        song = "08 - Aftershock.wav";
                        break;
                    case 9:
                        song = "09 - Last Stand.wav";
                        break;
                    case 10:
                        song = "10 - Redemption.wav";
                        break;
                    case 11:
                        song = "11 - Lightbringer (Acoustic).wav";
                        break;
                    default:
                        song = null;
                        break;
                }
                break;
            case "lovely":
                switch (i) {
                    case -1:
                        song = "Lovely:\n1 - Paulo Londra - Adan y Eva.wav\n2 - Paulo Londra - Tal Vez.wav\n"
                                + "3 - Jósean Log - Chachachá\n4 - Natalia Lafourcade - Nunca Es Suficiente\n"
                                + "5 - NICKI NICOLE - Verte\n6 - Pablo Alboran - Saturno\n"
                                + "7 - Piso 21 - Te Amo\n8 - Zoé - Labios Rotos (Live)\n";
                        break;
                    case 1:
                        song = "Paulo Londra - Adan y Eva.wav";
                        break;
                    case 2:
                        song = "Paulo Londra - Tal Vez.wav";
                        break;
                    case 3:
                        song = "Jósean Log - Chachachá.wav";
                        break;
                    case 4:
                        song = "Natalia Lafourcade - Nunca Es Suficiente.wav";
                        break;
                    case 5:
                        song = "NICKI NICOLE - Verte.wav";
                        break;
                    case 6:
                        song = "Pablo Alboran - Saturno.wav";
                        break;
                    case 7:
                        song = "Piso 21 - Te Amo.wav";
                        break;
                    case 8:
                        song = "Zoé - Labios Rotos (Live).wav";
                        break;
                    default:
                        song = null;
                        break;
                }
                break;
            default:
                song = null;
                break;
        }
        return song;
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
            //usuarios.add(username);
        }

        model.addElement(username);
        usuarios.add(username);
        System.out.println("Lista actualmente\n********************");
        for (String user : usuarios) {
            System.out.println("usuario: " + user);
        }
        System.out.println("********************");
        lista.setModel(model);
    }

    // Eliminar usuario de lista
    public void removeUser(String username) {
        DefaultListModel model = (DefaultListModel) lista.getModel();
        for (int i = 0; i < lista.getModel().getSize(); i++) {
            Object o = lista.getModel().getElementAt(i);
            if (((String) o).equals(username)) {
                model.remove(i);
            }
        }

        lista.setModel(model);
    }

    class VideoTaker extends Thread {

        @Override
        public void run() {
            while (isOpen) {
                if (cam != null) {
                    try {
                        Image img = cam.getImage();
                        camera.setIcon(new ImageIcon(img));
                        try {
                            Thread.sleep(17);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Ventana.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } catch (Exception e) {
                        //System.out.println("Usuario desconectado");
                    }

                }
            }
            scaleImage("camera.jpg", camera);
        }
    }

    public void scaleImage(String source, javax.swing.JLabel label) {
        URL imageURL = getClass().getResource("/sources/" + source);
        ImageIcon icon = new ImageIcon(imageURL);

        Image img = icon.getImage();
        Image imgScale = img.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon scaleIcon = new ImageIcon(imgScale);
        label.setIcon(scaleIcon);
    }

    public void scaleImage(String source, javax.swing.JButton label) {
        URL imageURL = getClass().getResource("/sources/" + source);
        ImageIcon icon = new ImageIcon(imageURL);

        Image img = icon.getImage();
        Image imgScale = img.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon scaleIcon = new ImageIcon(imgScale);
        label.setIcon(scaleIcon);
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
        cameraLabel = new javax.swing.JLabel();
        toOpenCam = new javax.swing.JButton();
        camera = new javax.swing.JLabel();
        imagen_interacion = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        jLabel1.setText("Chat");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(10, 13, 160, 14);

        jLabel2.setText("Usuarios conectados");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(411, 279, 250, 14);

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
        getContentPane().add(sendData);
        sendData.setBounds(10, 509, 279, 30);

        enviarButton.setText("Enviar");
        enviarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enviarButtonActionPerformed(evt);
            }
        });
        getContentPane().add(enviarButton);
        enviarButton.setBounds(299, 508, 94, 30);

        chat.setEditable(false);
        jScrollPane3.setViewportView(chat);

        getContentPane().add(jScrollPane3);
        jScrollPane3.setBounds(10, 33, 383, 457);

        jScrollPane1.setViewportView(lista);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(411, 311, 120, 179);

        cameraLabel.setText("WebCam");
        getContentPane().add(cameraLabel);
        cameraLabel.setBounds(420, 250, 240, 14);

        toOpenCam.setText("camera");
        toOpenCam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toOpenCamActionPerformed(evt);
            }
        });
        getContentPane().add(toOpenCam);
        toOpenCam.setBounds(650, 250, 80, 23);

        camera.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        camera.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        getContentPane().add(camera);
        camera.setBounds(411, 33, 320, 240);
        getContentPane().add(imagen_interacion);
        imagen_interacion.setBounds(540, 310, 200, 180);

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

        this.applyLogic(doc, "[" + this.username + "]: " + mensaje, style);
        this.sendData.setText("");
    }//GEN-LAST:event_enviarButtonActionPerformed

    private void sendDataKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sendDataKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            enviarButtonActionPerformed(null);
        }
    }//GEN-LAST:event_sendDataKeyPressed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing

    }//GEN-LAST:event_formWindowClosing

    boolean isOpen;
    private void toOpenCamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toOpenCamActionPerformed
        if (isOpen) {
            cam.close();
            isOpen = !isOpen;
            System.out.println("camara cerrada");

            StyledDocument doc = chat.getStyledDocument();
            Style style = chat.addStyle("Style", null);
            StyleConstants.setForeground(style, Color.black);
            addText(doc, "-Se ha cerrado la camara-\n", style);
        } else {
            try {
                if (cam == null) {
                    cam = Webcam.getDefault();
                    cam.close();
                    cam.setViewSize(new Dimension(320, 240));
                }
                cam.open();
                
                StyledDocument doc = chat.getStyledDocument();
                Style style = chat.addStyle("Style", null);
                StyleConstants.setForeground(style, Color.black);
                addText(doc, "-Se ha abierto la camara-\n", style);
                
                isOpen = !isOpen;
                new VideoTaker().start();
                System.out.println("camara abierta");
            } catch (WebcamException e) {
                System.out.println("Camara ocupada");
            }
        }
    }//GEN-LAST:event_toOpenCamActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JLabel camera;
    private javax.swing.JLabel cameraLabel;
    private javax.swing.JTextPane chat;
    private javax.swing.JButton enviarButton;
    private javax.swing.JLabel imagen_interacion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList<String> lista;
    private javax.swing.JTextField sendData;
    private javax.swing.JButton toOpenCam;
    // End of variables declaration//GEN-END:variables
}
