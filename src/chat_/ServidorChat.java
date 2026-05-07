/*
1.- ServidorChat.java debe ejecutarse primero
2.- ClienteChat.java debe ejecutarse después:
    La clase 'ClienteChat.java' puede ejecutarse las veces que se desee
    para agregar a más de un cliente al chat.
*/

//Se crea el SERVIDOR del chat.
//El SERVIDOR espera a que se conecten los clientes.
//Una vez que el cliente se conecta pregunta por su nombre y después lo manda al CLIENTHANDLER.
//Vuele a esperar a que un nuevo cliente se conecte.
package chat_;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;

public class ServidorChat {
    // Lista para guardar a todos los clientes conectados
    private static ArrayList<ClientHandler> clientes = new ArrayList<>();
    
    private static int puerto = 1234;

    public static void main(String[] args) {
        //Se crea el SERVIDOR en el puerto 1234 de esta pc
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {            
            System.out.println("SERVIDOR iniciado en el puerto " + puerto + "...");
            System.out.println("");            
            
            System.out.println("Indicaciones:");
            System.out.println("Si el CLIENTE se conecta desde la misma PC del SERVIDOR, lo puede hacer con la direccion de 'localhost'");
            System.out.println("");
            
            System.out.println("Si los CLIENTES se conectan desde otra PC,");            
            System.out.println("deberan conectarse a las direcciones disponibles mostradas a continuacion, excepto las de maquinas virtuales");            
            System.out.println("");
            
            System.out.println("Direcciones ipv4 identificadas:");
            obtenerDireccionesIPv4();           

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
    
    public static void obtenerDireccionesIPv4() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();

                // Filtramos interfaces inactivas o de loopback
                if (iface.isLoopback() || !iface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    // Buscamos solo direcciones IPv4 para simplificar
                    if (addr instanceof java.net.Inet4Address) {
                        System.out.println(addr.getHostAddress());
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}