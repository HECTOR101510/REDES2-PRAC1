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
    private static String dirActual = "/home/hector/Documentos/ESCOM/SEMESTRE 6/REDES 2/REDES2-PRAC1"; 
    private static void menu(Socket cl){
        try {
            BufferedReader in=new BufferedReader(new InputStreamReader(cl.getInputStream()));
            PrintWriter out=new PrintWriter(cl.getOutputStream(),true);
            String op=in.readLine();
            System.out.println(">>>>>Directorio actual: " + dirActual);
            out.println(op);
            switch (op) {
                case "LISTAR":
                    carpeta(cl);
                    break;
                case "CREAR":
                    crear(in.readLine());
                    break;
                case "ELIMINAR":
                    System.out.println("Escribe el nombre del archivo o carpeta a eliminar: ");
                    elimina(in.readLine());
                    break;
                case "CD":
                    System.out.print("Escribe el nuevo directorio: ");
                    String dir=in.readLine();
                    out.println(dir);
                    dirActual=dir;
                    System.out.println("\n>>>>>Listo direcccion cambiada a: "+dirActual);
                    break;

                default:
                System.out.println("Operacion no reconocida: "+op);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void elimina(String namecar) {
        File ncarp=new File(dirActual, namecar);
        if(ncarp.exists()){
            if(ncarp.isDirectory()){
                if(ncarp.delete()){//eliminacion con rmdir
                    System.out.println("Carpeta eliminada con exito "+namecar);
                }else{
                    System.out.println("Error al eliminar la carpeta "+namecar);
                }
            }else{//eliminacion con rm 
                if(ncarp.delete()){
                    System.out.println("Carpeta eliminada con exito "+namecar);
                }else{
                    System.out.println("Error al eliminar la carpeta "+namecar);
                }
            }
        }else{
            System.out.println("Error, carpeta o archivo no existente: "+namecar);
        }
    }

    private static void crear(String namecar) {
        File ncarp=new File(dirActual, namecar);
        if(ncarp.mkdir()){
            System.out.println("carpeta generada "+namecar +" con exito");
        }else{
            System.out.println("ERROR NO SE PUDO CREAR");
        }

    }

    private static void carpeta(Socket cl) {
        try {
            // Obtener el listado de archivos en la carpeta
            File carpeta = new File(dirActual);
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