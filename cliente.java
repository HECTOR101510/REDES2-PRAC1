import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;

public class cliente {
    public static void main(String[] args) {
        try {
            System.out.println("Cliente conectado...\nRecibiendo archivos de la carpeta abierta...");
            while(true){
            Socket cl = new Socket(InetAddress.getByName("127.0.0.2"), 5050);
            menu(cl);
            System.out.println("\n");
            }
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
            System.out.println("\tEnviar archivos o carpetas: PUT \n\tGET \n\tQUIT");
            System.out.print("Escribe tu eleccion: ");
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
                case "PUT":
                    System.out.print("Escribe el nombre del archivo: ");
                    String nombre=in.readLine();
                    put(nombre,cl);
                    break;
                case "GET":
                    System.out.print("Ingresa el nombre del archivo o carpeta a obtener: ");
                    String archivo=in.readLine();
                    out.println(archivo);
                    get(archivo,cl);
                    break;
                case "QUIT":
                    System.out.println("Saliendo de la aplicacion...");
                    cl.close();
                    System.exit(0);
                    break;
                default:
                    break;
            }
            cl.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private static void put(String nombre, Socket cl){
        try {
            File archivo = new File(nombre);
            if (!archivo.exists()) {
                System.out.println("Error: El archivo no existe.");
                return;
            }
            PrintWriter out = new PrintWriter(cl.getOutputStream(), true);
            out.println(archivo.getName());  // Enviar solo el nombre del archivo
            OutputStream os = cl.getOutputStream();
            byte[] contenido = Files.readAllBytes(archivo.toPath());
            os.write(contenido);
            os.flush();
            System.out.println("Archivo enviado con éxito al servidor.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private static void get(String nm, Socket cl) {
    try {
        PrintWriter out = new PrintWriter(cl.getOutputStream(), true);
        out.println("GET " + nm);

        BufferedReader in = new BufferedReader(new InputStreamReader(cl.getInputStream()));
        String filename = in.readLine();
        if (filename == null || filename.isEmpty()) {
            System.out.println("No se puede obtener el nombre del archivo.");
            return;
        }

        File archivo = new File(dirActual, filename);
        FileOutputStream fos = new FileOutputStream(archivo);

        int bytesRead;
        byte[] buffer = new byte[4096];
        InputStream is = cl.getInputStream();
        while ((bytesRead = is.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }

        fos.close();
        System.out.println("Archivo recibido desde el servidor y guardado como: " + archivo.getName());
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