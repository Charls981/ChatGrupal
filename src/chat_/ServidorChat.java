//Se crea el SERVIDOR del chat.
//El SERVIDOR espera a que se conecten los clientes.
//Una vez que el cliente se conecta pregunta por su nombre y después lo manda al CLIENTHANDLER.
//Vuele a esperar a que un nuevo cliente se conecte.
package chat_;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServidorChat {
    // Lista para guardar a todos los clientes conectados
    private static ArrayList<ClientHandler> clientes = new ArrayList<>();

    public static void main(String[] args) {
        //Se crear el SERVIDOR en el puerto 1234 de esta pc
        try (ServerSocket serverSocket = new ServerSocket(1234)) {            
            System.out.println("SERVIDOR iniciado en el puerto 1234...");

            while (true) {
                //El SERVIDOR espera la conexión de un cliente
                Socket socket = serverSocket.accept();                                
                System.out.println("Nuevo cliente conectado desde: " + socket.getInetAddress());
                                               
                // Creamos un hilo independiente para este cliente/socket
                //Se pasa la lista de clientes a los que este socket tendrá que notificar un cambio
                ClientHandler clienteHilo = new ClientHandler(socket, clientes);
                clientes.add(clienteHilo);
                //Corre el hilo independiente
                new Thread(clienteHilo).start();
            }
        } catch (IOException e) {
            //Si se tienen problemas creando el SERVIDOR
            e.printStackTrace();
        }
    }
}