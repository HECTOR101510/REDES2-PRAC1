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
    private static String dirActual = "/home/hector/Documentos/ESCOM/SEMESTRE 6/REDES 2/REDES2-PRAC1"; //direccion default para las pruebas
    private static void menu(Socket cl){
        try {
            //l.getInputStream: flujo de datos que está llegando desde el cliente a través de la conexión de socket
            //InputStreamReader: convierte de bytes a caracteres
            //BufferedReader:lee las lineas completasen la entrada del socket "cl"
            BufferedReader in=new BufferedReader(new InputStreamReader(cl.getInputStream()));
            //cl.getOutputStream es la conexion con el socket y devuelve un flujo de salida a ese socket
            //osea sirve para enviar datos al cliente
            //PrintWriter es el flujo de salida del socket de caracteres a bytes
            PrintWriter out=new PrintWriter(cl.getOutputStream(),true);
            String op=in.readLine();//sirve para leer una linea completa de texto del flijo de entrada y los guarda en una cadena
            System.out.println(">>>>>Directorio actual: " + dirActual);
            out.println(op);//almacena la cadena de texto y se envia al cliente a traves del flujo de salida del socket
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

    private static void elimina(String namecar){//Funcion para eliminar carpeta o archivo
        File ncarp=new File(dirActual, namecar);//dirActual la direccion, namecar nombre de la carpeta
        if(ncarp.exists()){//si existe la carpeta 
            if(ncarp.isDirectory()){//verifica si existe un directorio
                if(ncarp.delete()){//eliminacion con rmdir
                    System.out.println("Carpeta eliminada con exito "+namecar);
                }else{
                    System.out.println("Error al eliminar la carpeta "+namecar);
                }
            }else{
                if(ncarp.delete()){//eliminacion con rm 
                    System.out.println("Carpeta eliminada con exito "+namecar);
                }else{
                    System.out.println("Error al eliminar la carpeta "+namecar);
                }
            }
        }else{
            System.out.println("Error, carpeta o archivo no existente: "+namecar);
        }
    }

    private static void crear(String namecar){//Funcion para crear carpetas
        File ncarp=new File(dirActual, namecar);
        if(ncarp.mkdir()){//intenta crear la carpeta
            System.out.println("carpeta generada "+namecar +" con exito");
        }else{
            System.out.println("ERROR NO SE PUDO CREAR");
        }

    }

    private static void carpeta(Socket cl){//Para hacer el listado de los archivos que tenemos
        try {
            // Obtener el listado de archivos en la carpeta
            File carpeta = new File(dirActual);
            String[] contenido = carpeta.list();//obtenemos el listado de los nombres de los archivos encontrados
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