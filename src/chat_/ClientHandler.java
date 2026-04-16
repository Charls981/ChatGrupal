//Hilo independiente del cliente
//El servidor pregunta por el nombre al cliente conectado.
//El hilo independiente hace que el servidor:
//Mantenga la escucha cuando el cliente/socket ha escrito en el chat
//Notifica a los demás sockets un cambio
package chat_;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ArrayList<ClientHandler> clientes;

    public ClientHandler(Socket socket, ArrayList<ClientHandler> clientes) {
        this.socket = socket;
        this.clientes = clientes;        
    }   

    //lee el mensaje que envía el cliente y lo reparte a todos
    @Override
    public void run() {
        try {
            // Configurar entrada de datos del cliente:
            //socket.getInputStream() obtiene bytes de la conexión 'socket' establecida
            //InputStreamReader() traduce los bytes con utf-8 pero lo hace muy lento, conserva métodos que leen caracter por caracter, teniendo que consultar el socket en cada lectura.
            //BufferedReader() obtiene el flujo de datos del socket mediante el cable de red o wifi, tiene el método readLine que obtiene todos los caracteres de una vez visitando solo una vez al socket
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            //Configurar envío de datos al cliente:
            //socket.getOutputStream() empuja los bytes hacia la red
            //PrintWriter es el formateador para traducir los bytes a texto legible añadiendo \n al final del mensaje (ya que readLine() espera un \n para saber que el mensaje terminó)
            //true envía los datos inmediatamente a la red al usar .println()
            out = new PrintWriter(socket.getOutputStream(), true);
            
            String mensaje;
            // Bucle para que el servidor escuche mensajes cuando ESTE cliente los mande (se pausa hasta que el cliente manda datos)
            //readLine() obtiene los datos hasta encontrar \n quien delimita el mensaje
            while ((mensaje = in.readLine()) != null) {
                System.out.println("Mensaje recibido >> " + mensaje);
                // Reenviar a todos los demás
                broadcast(mensaje);
            }
        } catch (IOException e) {
            System.out.println("Cliente desconectado.");
        } finally {
            desconectar();
        }
    }

    //Manda el mensaje a todos los servidores incluyendose a él mismo
    private void broadcast(String mensaje) {
        for (ClientHandler cliente : clientes) {
            // El servidor envía el mensaje a todos los hilos de la lista
            cliente.out.println(mensaje);
        }
    }

    private void desconectar() {
        try {
            clientes.remove(this);//se quita de la lista, la cual es contenida para cada hilo cliente independiente 
            socket.close();//cierra el flujo de datos
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
