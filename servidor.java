import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class servidor {
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(5000);
            System.out.println("Servicio iniciado, esperando por cliente...");
            for (;;) {
                Socket cl = ss.accept();
                System.out.println("Cliente conectado " + cl.getInetAddress() + ": " + cl.getPort());
                
                menu(cl);
                // Cerrar la conexión con el cliente
                cl.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void menu(Socket cl){
        try {
            BufferedReader in=new BufferedReader(new InputStreamReader(cl.getInputStream()));
            PrintWriter out=new PrintWriter(cl.getOutputStream(),true);
            String op=in.readLine();

            switch (op) {
                case "LISTAR":
                    carpeta(cl);
                    break;
                case "CREAR":
                    crear(in.readLine());
                    break;
            
                default:
                System.out.println("Operacion no reconocida: "+op);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void crear(String namecar) {
        File ncarp=new File("C:\\Users\\100054436\\Documents\\ESCOM\\SEMESTRE 6\\REDES 2", namecar);
        if(ncarp.mkdir()){
            System.out.println("carpeta generada "+namecar +" con exito");
        }else{
            System.out.println("ERROR NO SE PUDO CREAR");
        }

    }

    private static void carpeta(Socket cl) {
        try {
            // Obtener el listado de archivos en la carpeta
            File carpeta = new File("C:\\Users\\100054436\\Documents\\ESCOM\\SEMESTRE 6\\REDES 2");
            String[] contenido = carpeta.list();
            // Enviar el listado al cliente
            PrintWriter out = new PrintWriter(cl.getOutputStream(), true);
            for (String archivo : contenido) {
                out.println(archivo);
            }
            // Enviar una línea en blanco para indicar el final del listado
            out.println();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}