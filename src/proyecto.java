import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * Clase principal para gestionar eventos almacenados en un archivo CSV.
 * Permite visualizar, añadir, editar y eliminar eventos.
 */
public class proyecto {
    private static final String archivo = "/Users/aldozamora/Documents/IntellijProjects/UEMProyecto/src/Gestion_de_Eventos.csv";
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Scanner input = new Scanner(System.in);


    /**
     * Convierte una cadena en formato "H:mm" a un objeto LocalTime.
     *
     * @param hora la hora en formato "H:mm".
     * @return el objeto LocalTime correspondiente.
     */
    private static LocalTime parsearHora(String hora) {
        try {
            return LocalTime.parse(hora, DateTimeFormatter.ofPattern("H:mm"));
        } catch (Exception e) {
            System.out.println("Formato de hora inválido: " + hora);
            throw e;
        }
    }

    /**
     * Muestra los eventos almacenados en el archivo CSV.
     */
    private static void mostrarEventos() {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String line;
            br.readLine();
            System.out.println("Eventos registrados:");

            while ((line = br.readLine()) != null) {
                try {
                    String[] parts = line.split(";");
                    if (parts.length >= 7) {
                        System.out.println(new Event(
                                parts[0], parts[1],
                                LocalDate.parse(parts[2], dateFormatter),
                                LocalTime.parse(parts[3], DateTimeFormatter.ofPattern("H:mm")),
                                LocalTime.parse(parts[4], DateTimeFormatter.ofPattern("H:mm")),
                                parts[5], parts[6], parts[7]
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

    /**
     * Añade un nuevo evento al archivo CSV.
     */
    private static void añadirEvento() {
        try (FileWriter fw = new FileWriter(archivo, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            System.out.println("Ingrese ID del evento:");
            String id = input.nextLine();

            System.out.println("Ingrese título del evento:");
            String title = input.nextLine();

            System.out.println("Ingrese fecha (dd/MM/yyyy):");
            LocalDate date = LocalDate.parse(input.nextLine(), dateFormatter);

            System.out.println("Ingrese hora de inicio (H:mm o HH:mm):");
            LocalTime startTime = parsearHora(input.nextLine());

            System.out.println("Ingrese hora de fin (H:mm o HH:mm):");
            LocalTime endTime = parsearHora(input.nextLine());

            System.out.println("Ingrese lugar:");
            String location = input.nextLine();

            System.out.println("Ingrese el grupo de la reunión (1: Estudiantes, 2: Profesores, 3: Comunidad universitaria):");
            int description = Integer.parseInt(input.nextLine());
            if (description < 1 || description > 3) {
                throw new IllegalArgumentException("Descripción debe ser un número entre 1 y 3.");
            }

            System.out.println("Ingrese URL del evento (o Enter si no hay):");
            String url = input.nextLine();

            Event newEvent = new Event(id, title, date, startTime, endTime, location, String.valueOf(description), url);
            bw.write(String.format("%s;%s;%s;%s;%s;%s;%d;%s%n",
                                   id, title, date.format(dateFormatter),
                                   startTime.format(DateTimeFormatter.ofPattern("H:mm")),
                                   endTime.format(DateTimeFormatter.ofPattern("H:mm")),
                                   location, description, url));

            System.out.println("Evento añadido exitosamente!");
        } catch (IOException e) {
            System.out.println("Error al añadir evento: " + e.getMessage());
        }
    }

    /**
     * Elimina un evento del archivo CSV según su ID.
     */
    private static void quitarEvento() {
        try {
            List<String> lines = new ArrayList<>();
            System.out.println("Ingrese el ID del evento a eliminar:");
            String idToRemove = input.nextLine();
            boolean found = false;

            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith(idToRemove + ";")) {
                        found = true;
                    } else {
                        lines.add(line);
                    }
                }
            }

            if (!found) {
                System.out.println("El evento con ID " + idToRemove + " no existe.");
                return;
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
                for (String line : lines) {
                    bw.write(line);
                    bw.newLine();
                }
            }
            System.out.println("Evento eliminado exitosamente!");
        } catch (IOException e) {
            System.out.println("Error al eliminar evento: " + e.getMessage());
        }
    }

    /**
     * Edita un evento existente en el archivo CSV.
     */
    private static void editarEvento() {
        try {
            List<String> lines = new ArrayList<>();
            System.out.println("Ingrese el ID del evento a editar:");
            String idToEdit = input.nextLine();
            boolean found = false;

            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith(idToEdit + ";")) {
                        found = true;
                        String[] parts = line.split(";");

                        System.out.println("Ingrese nuevo título (o Enter para mantener):");
                        String title = input.nextLine();

                        System.out.println("Ingrese nueva fecha (dd/MM/yyyy, o Enter para mantener):");
                        String date = input.nextLine();

                        System.out.println("Ingrese nueva hora de inicio (H:mm o Enter para mantener):");
                        String startTime = input.nextLine();

                        System.out.println("Ingrese nueva hora de fin (H:mm o Enter para mantener):");
                        String endTime = input.nextLine();

                        System.out.println("Ingrese nuevo lugar (o Enter para mantener):");
                        String location = input.nextLine();

                        System.out.println("Ingrese nuevo grupo de la reunión (1, 2, 3 o Enter para mantener):");
                        String descriptionInput = input.nextLine();
                        int newDescription = descriptionInput.isEmpty() ? Integer.parseInt(parts[6]) : Integer.parseInt(descriptionInput);
                        if (newDescription < 1 || newDescription > 3) {
                            throw new IllegalArgumentException("El grupo debe ser 1, 2 o 3.");
                        }

                        System.out.println("Ingrese nueva URL (o Enter para mantener):");
                        String url = input.nextLine();

                        lines.add(String.format("%s;%s;%s;%s;%s;%s;%d;%s",
                                                idToEdit,
                                                title.isEmpty() ? parts[1] : title,
                                                date.isEmpty() ? parts[2] : LocalDate.parse(date, dateFormatter).format(dateFormatter),
                                                startTime.isEmpty() ? parts[3] : parsearHora(startTime).format(DateTimeFormatter.ofPattern("H:mm")),
                                                endTime.isEmpty() ? parts[4] : parsearHora(endTime).format(DateTimeFormatter.ofPattern("H:mm")),
                                                location.isEmpty() ? parts[5] : location,
                                                newDescription,
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

    /**
     * Representa un evento con detalles como título, fecha, hora y lugar.
     */
    public static class Event {
        private final String id;
        private final String title;
        private final LocalDate date;
        private final LocalTime startTime;
        private final LocalTime endTime;
        private final String location;
        private final String description;
        private final String url;

        /**
         * Constructor para inicializar un evento.
         *
         * @param id          Identificador único del evento.
         * @param title       Título del evento.
         * @param date        Fecha del evento.
         * @param startTime   Hora de inicio del evento.
         * @param endTime     Hora de fin del evento.
         * @param location    Lugar del evento.
         * @param description Descripción del evento (grupo al que pertenece).
         * @param url         URL del evento.
         */
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
                    ", description=" + description +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    /**
     * Método principal del programa.
     *
     * @param args Argumentos de línea de comando.
     */
    public static void main(String[] args) {
        while (true) {
            System.out.println("""
                    Elija que desea hacer:
                    i (ver los eventos)
                    + (añadir evento)
                    - (quitar evento)
                    e (editar evento)
                    s (salir)
                    """);
            String choice = input.nextLine().trim();

            switch (choice) {
                case "i" -> mostrarEventos();
                case "+" -> añadirEvento();
                case "-" -> quitarEvento();
                case "e" -> editarEvento();
                case "s" -> {
                    System.out.println("¡Hasta luego!");
                    return;
                }
                default -> System.out.println("Opción no válida");
            }
        }
    }
}