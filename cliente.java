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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
                    dirActual=dir;
                    out.println(dir);
                    System.out.print("Listo direcccion cambiada a: "+dirActual);
                    break;
                case "PUT":
                    System.out.print("Escribe el nombre del archivo: ");
                    String nombre=in.readLine();
                    put(nombre,cl);
                    break;
                case "GET":
                    System.out.println("Ingresa el nombre del archivo o carpeta a obtener:");
                    String archivo = in.readLine();
                    out.println("GET " + archivo);  // Enviar el comando y el nombre del archivo al servidor
                    get(archivo, cl);
                    break;
                case "QUIT":
                    System.out.println("Saliendo de la aplicacion...");
                    cl.close();
                    System.exit(0);
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private static void put(String nombre, Socket cl) {
        try {
            File archivo = new File(nombre);
            if (!archivo.exists()) {
                System.out.println("Error: El archivo o directorio no existe.");
                return;
            }

            if (archivo.isDirectory()) {
                // Si es un directorio, comprimirlo en un archivo ZIP antes de enviarlo
                String zipNombre = nombre + ".zip";
                zipDirectory(archivo, zipNombre);
                archivo = new File(zipNombre);
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
    private static void zipDirectory(File dir, String zipDirName) {
    try {
        Path p = Files.createFile(Paths.get(zipDirName));
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
            Path pp = Paths.get(dir.getName());
            Files.walk(pp)
              .filter(path -> !Files.isDirectory(path))
              .forEach(path -> {
                  ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                  try {
                      zs.putNextEntry(zipEntry);
                      Files.copy(path, zs);
                      zs.closeEntry();
                  } catch (IOException e) {
                      System.err.println(e);
                  }
              });
        }
    } catch (IOException e) {
        System.err.println("No se pudo comprimir el directorio: " + e);
    }
}
    private static void get(String nm, Socket cl) {
        try {
            PrintWriter out = new PrintWriter(cl.getOutputStream(), true);
            out.println(nm);  // Enviar el nombre del archivo que quieres obtener al servidor

            // Crear un archivo con el nombre original
            File archivo = new File(nm);
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