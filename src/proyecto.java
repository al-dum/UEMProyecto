import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class proyecto {
    private static final String archivo = "/Users/aldozamora/Documents/IntellijProjects/UEMProyecto/src/Gestion_de_Eventos.csv";
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Scanner scanner = new Scanner(System.in);

    private static LocalTime parsearHora(String hora) {
        try {
            // Intenta con el formato HH:mm
            return LocalTime.parse(hora, DateTimeFormatter.ofPattern("H:mm"));
        } catch (Exception e) {
            System.out.println("Formato de hora inválido: " + hora);
            throw e;
        }
    }

    private static void mostrarEventos() {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String line;
            // Leer y descartar la línea del encabezado
            br.readLine();
            System.out.println("Eventos registrados:");

            while ((line = br.readLine()) != null) {
                try {
                    String[] parts = line.split(";");
                    if (parts.length >= 7) {
                        System.out.println(new Event(
                                parts[0],  // ID
                                parts[1],  // Nombre
                                LocalDate.parse(parts[2], dateFormatter),  // Fecha
                                LocalTime.parse(parts[3], DateTimeFormatter.ofPattern("H:mm")),  // Hora de inicio
                                LocalTime.parse(parts[4], DateTimeFormatter.ofPattern("H:mm")),  // Hora de fin
                                parts[5],  // Location
                                parts[6],  // Descripción
                                parts[7]   // URL
                        ));
                    }
                } catch (Exception e) {
                    System.out.println("Error al procesar la línea: " + line);
                    System.out.println("Error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer eventos: " + e.getMessage());
        }
    }

    private static void añadirEvento() {
        try (FileWriter fw = new FileWriter(archivo, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            System.out.println("Ingrese ID del evento:");
            String id = scanner.nextLine();

            System.out.println("Ingrese título del evento:");
            String title = scanner.nextLine();

            System.out.println("Ingrese fecha (dd/MM/yyyy):");
            LocalDate date = LocalDate.parse(scanner.nextLine(), dateFormatter);

            System.out.println("Ingrese hora de inicio (H:mm o HH:mm):");
            LocalTime startTime = parsearHora(scanner.nextLine());

            System.out.println("Ingrese hora de fin (H:mm o HH:mm):");
            LocalTime endTime = parsearHora(scanner.nextLine());

            System.out.println("Ingrese lugar:");
            String location = scanner.nextLine();

            System.out.println("Ingrese descripción:");
            String description = scanner.nextLine();

            System.out.println("Ingrese URL del evento (o Enter si no hay):");
            String url = scanner.nextLine();

            Event newEvent = new Event(id, title, date, startTime, endTime, location, description, url);
            bw.write(String.format("%s;%s;%s;%s;%s;%s;%s;%s%n",
                                   id,
                                   title,
                                   date.format(dateFormatter),
                                   startTime.format(DateTimeFormatter.ofPattern("H:mm")),
                                   endTime.format(DateTimeFormatter.ofPattern("H:mm")),
                                   location,
                                   description,
                                   url));

            System.out.println("Evento añadido exitosamente!");
        } catch (IOException e) {
            System.out.println("Error al añadir evento: " + e.getMessage());
        }
    }

    private static void quitarEvento() {
        try {
            List<String> lines = new ArrayList<>();
            System.out.println("Ingrese el ID del evento a eliminar:");
            String idToRemove = scanner.nextLine();

            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.startsWith(idToRemove + ";")) {
                        lines.add(line);
                    }
                }
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
                for (String line : lines) {
                    bw.write(line);
                    bw.newLine();
                }
                System.out.println("Evento eliminado exitosamente!");
            }
        } catch (IOException e) {
            System.out.println("Error al eliminar evento: " + e.getMessage());
        }
    }

    private static void editarEvento() {
        try {
            List<String> lines = new ArrayList<>();
            System.out.println("Ingrese el ID del evento a editar:");
            String idToEdit = scanner.nextLine();
            boolean found = false;

            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith(idToEdit + ";")) {
                        found = true;
                        String[] parts = line.split(";");

                        System.out.println("Ingrese nuevo título (o Enter para mantener):");
                        String title = scanner.nextLine();

                        System.out.println("Ingrese nueva fecha (dd/MM/yyyy, o Enter para mantener):");
                        String date = scanner.nextLine();

                        System.out.println("Ingrese nueva hora de inicio (H:mm o Enter para mantener):");
                        String startTime = scanner.nextLine();

                        System.out.println("Ingrese nueva hora de fin (H:mm o Enter para mantener):");
                        String endTime = scanner.nextLine();

                        System.out.println("Ingrese nuevo lugar (o Enter para mantener):");
                        String location = scanner.nextLine();

                        System.out.println("Ingrese nueva descripción (o Enter para mantener):");
                        String description = scanner.nextLine();

                        System.out.println("Ingrese nueva URL (o Enter para mantener):");
                        String url = scanner.nextLine();

                        lines.add(String.format("%s;%s;%s;%s;%s;%s;%s;%s",
                                                idToEdit,
                                                title.isEmpty() ? parts[1] : title,
                                                date.isEmpty() ? parts[2] : LocalDate.parse(date, dateFormatter).format(dateFormatter),
                                                startTime.isEmpty() ? parts[3] : parsearHora(startTime).format(DateTimeFormatter.ofPattern("H:mm")),
                                                endTime.isEmpty() ? parts[4] : parsearHora(endTime).format(DateTimeFormatter.ofPattern("H:mm")),
                                                location.isEmpty() ? parts[5] : location,
                                                description.isEmpty() ? parts[6] : description,
                                                url.isEmpty() ? parts[7] : url));
                    } else {
                        lines.add(line);
                    }
                }
            }

            if (!found) {
                System.out.println("Evento no encontrado!");
                return;
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
                for (String line : lines) {
                    bw.write(line);
                    bw.newLine();
                }
            }

            System.out.println("Evento editado exitosamente!");
        } catch (IOException e) {
            System.out.println("Error al editar evento: " + e.getMessage());
        }
    }

    public static class Event {
        private String id;
        private String title;
        private LocalDate date;
        private LocalTime startTime;
        private LocalTime endTime;
        private String location;
        private String description;
        private String url;

        public Event(String id, String title, LocalDate date, LocalTime startTime, LocalTime endTime, String location, String description, String url) {
            this.id = id;
            this.title = title;
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;
            this.location = location;
            this.description = description;
            this.url = url;
        }

        @Override
        public String toString() {
            return "Event{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", date=" + date.format(dateFormatter) +
                    ", startTime=" + startTime.format(DateTimeFormatter.ofPattern("H:mm")) +
                    ", endTime=" + endTime.format(DateTimeFormatter.ofPattern("H:mm")) +
                    ", location='" + location + '\'' +
                    ", description='" + description + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println("""
                                        Elija que desea hacer: 
                                        i (ver los eventos)
                                        + (añadir evento) 
                                        - (quitar evento) 
                                        e (editar evento) 
                                        s (salir)"
                                       """
                              );
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "i":
                    mostrarEventos();
                    break;
                case "+":
                    añadirEvento();
                    break;
                case "-":
                    quitarEvento();
                    break;
                case "e":
                    editarEvento();
                    break;
                case "s":
                    System.out.println("¡Hasta luego!");
                    return;
                default:
                    System.out.println("Opción no válida");
            }
        }
    }
}