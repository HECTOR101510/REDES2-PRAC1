import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class cliente {
    public static void main(String[] args) {
        try {
            Socket cl = new Socket(InetAddress.getByName("127.0.0.2"), 5000);
            System.out.println("Cliente conectado...\nRecibiendo archivos de la carpeta abierta...");
            
            menu(cl);
            // Cerrar la conexión con el servidor
            cl.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static String dirActual="/";
    private static void menu(Socket cl) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(cl.getOutputStream(), true);
            System.out.println("Hola que quieres hacer \n\tLISTAR \n\tCREAR \n\tELIMINAR \n\tCambiar directorio: CD");
            String op=in.readLine();
            out.println(op);
            switch (op) {
                case "LISTAR":
                    Listado(cl);
                    break;
                case "CREAR":
                    System.out.println("Ingrese el nombre de la carpeta a crear: ");
                    String ncarp = in.readLine();
                    out.println(ncarp);
                    break;
                case "ELIMINAR":
                    System.out.print("Ingrese el nombre del archivo o carpeta a eliminar: ");
                    String elimina = in.readLine();
                    out.println(elimina);
                    break;
                case "CD":
                    System.out.print("Escribe el nuevo directorio: ");
                    String dir=in.readLine();
                    out.println(dir);
                    dirActual=dir;
                    System.out.print("Listo direcccion cambiada a: "+dirActual);
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private static void Listado(Socket cl) {
        try {
            // Recibir el listado del servidor
            BufferedReader in = new BufferedReader(new InputStreamReader(cl.getInputStream()));
            String archivo;
            System.out.println("Archivos de la carpeta abierta:");
            int i=0;
            while ((archivo = in.readLine()) != null && !archivo.isEmpty()) {
                System.out.println("\t"+archivo);
                i++;
            }
            System.out.println("\tSe han leido "+i+" archivos");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}