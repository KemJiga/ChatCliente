/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatcliente;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Guacha
 */
public class HiloLectura extends Thread{
    private DataInputStream in;
    private Socket socket;
    private Ventana cliente = null;
    
    public HiloLectura(Socket socket, Ventana cliente) {
        this.socket = socket;
        this.cliente = cliente;
        try {
            //Obtener la entrada de datos del socket
            InputStream input = this.socket.getInputStream();
            in = new DataInputStream(input);
        } catch (IOException ex) {
            System.out.println("Error obteniendo stream: " + ex.getMessage());
        }
    }

    //Método que se ejecuta al iniciar el hilo
    @Override
    public void run() {
        
        // Recibir nombres
        String respuesta = "";
        try {
            //Leer nombres hasta que el servidor envie señal de listo
            respuesta = in.readUTF();
            while (!respuesta.equals(";/ready")) {
                cliente.addUser(respuesta);
                respuesta = in.readUTF();
            }
        } catch (IOException ex) {
            Logger.getLogger(HiloLectura.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Ciclo permanente
        while (true) {
            // Leer respuesta
            try {
                respuesta = in.readUTF();
                this.cliente.update(respuesta);
 
            } catch (IOException ex) {
                System.out.println("Error en lectura del servidor: " + ex.getMessage());
                break;
            }
        }
    }
    
    
    
}
