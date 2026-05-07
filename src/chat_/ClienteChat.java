/*
1.- ServidorChat.java debe ejecutarse primero
2.- ClienteChat.java debe ejecutarse después:
    Esta clase 'ClienteChat.java' puede ejecutarse las veces que se desee
    para agregar a más de un cliente al chat.
*/

//Cliente se conecta al SERVIDOR
//Se crea un hilo de este cliente para escuchar al SERVIDOR
//Se mantiene un hilo para mandar mensajes al SERVIDOR
package chat_;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteChat {
    private static String nombre;        
    
    public static void main(String[] args) {                
        //Se apunta a la ip y puerto del SERVIDOR         
        System.out.print("Introduce la IP del servidor (deja en blanco para localhost): ");
        Scanner scan = new Scanner(System.in);
        String ipServidor = scan.nextLine();
        
        if (ipServidor.isEmpty()) {
            ipServidor = "127.0.0.1";
        }
        
        int puerto = 1234;                

        //Se establece conexión al servidor del chat
        try (Socket socket = new Socket(ipServidor, puerto)) {
            System.out.println("Conectado al servidor de chat.");
            System.out.print("Ingresa tu nombre: ");
            nombre = scan.nextLine();

            // Hilo para que ESTE cliente escuche mensajes del SERVIDOR.
            Thread escucharHilo = new Thread(new ReceptorMensajes(socket));
            escucharHilo.start();

            // Hilo principal (main de esta clase) para ENVIAR mensajes al servidor desde este cliente
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);            

            System.out.println("Escribe tu mensaje o 'salir' para salir del chat:");
            System.out.print("\n> ");
            while (true) {
                String mensaje = nombre + ": " + scan.nextLine();
                
                if (mensaje.equalsIgnoreCase(nombre + ": salir")) {
                    break;
                }
                
                /* Enviamos el mensaje de ESTE cliente al SERVIDOR 
                   al hilo independiente que se mantiene escuchando a este cliente 
                   en CLIENTHANDLER.                
                */
                out.println(mensaje); 
            }

        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }
        
}

// Clase interna para manejar la recepción de datos desde el SERVIDOR sin bloquear el teclado/consola para mandar mensajes
class ReceptorMensajes implements Runnable {
    private Socket socket;
    private BufferedReader in;

    public ReceptorMensajes(Socket socket) {
        this.socket = socket;//La conexion hacia el SERVIDOR
    }

    @Override
    public void run() {
        try {
            //Se obtiene el "canal" de entrada de datos de el SERVIDOR de este cliente
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String mensajeServidor;
            
            // in.readLine() se queda esperando datos del SERVIDOR (cambios desde CLIENTHANDLER)
            while ((mensajeServidor = in.readLine()) != null) {
                System.out.println("\n" + mensajeServidor);
                System.out.print("> ");
            }
        } catch (IOException e) {
            //(FIN del hilo de escucha del cliente)
            //Si el cliente termina/[presiona 'salir'] , el flujo de datos del socket se cierra, provocando la excepción de 'in.readLine()' y se llega a esta línea. 
            System.out.println("Conexion cerrada con el servidor.");
        }
    }
}