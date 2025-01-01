import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class proyecto {
    private static final String FILE_PATH = "/Users/aldozamora/Downloads/Gestion de Eventos.csv";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("Elija que desea hacer: i (ver los eventos), + (añadir evento), - (quitar evento), e (editar evento), s (salir)");
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

    private static void mostrarEventos() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                System.out.println(new Event(
                        parts[0],
                        parts[1],
                        LocalDateTime.parse(parts[2], formatter),
                        LocalDateTime.parse(parts[3], formatter),
                        parts[4]
                ));
            }
        } catch (IOException e) {
            System.out.println("Error al leer eventos: " + e.getMessage());
        }
    }

    private static void añadirEvento() {
        try (FileWriter fw = new FileWriter(FILE_PATH, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            System.out.println("Ingrese ID del evento:");
            String id = scanner.nextLine();

            System.out.println("Ingrese título del evento:");
            String title = scanner.nextLine();

            System.out.println("Ingrese fecha y hora de inicio (YYYY-MM-DD HH:mm):");
            LocalDateTime startTime = LocalDateTime.parse(scanner.nextLine(), formatter);

            System.out.println("Ingrese fecha y hora de fin (YYYY-MM-DD HH:mm):");
            LocalDateTime endTime = LocalDateTime.parse(scanner.nextLine(), formatter);

            System.out.println("Ingrese descripción del evento:");
            String description = scanner.nextLine();

            Event newEvent = new Event(id, title, startTime, endTime, description);
            bw.write(String.format("%s,%s,%s,%s,%s%n",
                                   id, title, startTime.format(formatter),
                                   endTime.format(formatter), description));

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

            // Read all lines except the one to remove
            try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.startsWith(idToRemove + ",")) {
                        lines.add(line);
                    }
                }
            }

            // Write back all lines except the removed one
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
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

    private static void editarEvento() {
        try {
            List<String> lines = new ArrayList<>();
            System.out.println("Ingrese el ID del evento a editar:");
            String idToEdit = scanner.nextLine();
            boolean found = false;

            // Read all lines
            try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith(idToEdit + ",")) {
                        found = true;
                        // Get new event details
                        System.out.println("Ingrese nuevo título (o Enter para mantener):");
                        String title = scanner.nextLine();
                        System.out.println("Ingrese nueva fecha y hora de inicio YYYY-MM-DD HH:mm (o Enter para mantener):");
                        String startTime = scanner.nextLine();
                        System.out.println("Ingrese nueva fecha y hora de fin YYYY-MM-DD HH:mm (o Enter para mantener):");
                        String endTime = scanner.nextLine();
                        System.out.println("Ingrese nueva descripción (o Enter para mantener):");
                        String description = scanner.nextLine();

                        String[] parts = line.split(",");
                        // Update only the fields that were changed
                        lines.add(String.format("%s,%s,%s,%s,%s",
                                                idToEdit,
                                                title.isEmpty() ? parts[1] : title,
                                                startTime.isEmpty() ? parts[2] : LocalDateTime.parse(startTime, formatter).format(formatter),
                                                endTime.isEmpty() ? parts[3] : LocalDateTime.parse(endTime, formatter).format(formatter),
                                                description.isEmpty() ? parts[4] : description));
                    } else {
                        lines.add(line);
                    }
                }
            }

            if (!found) {
                System.out.println("Evento no encontrado!");
                return;
            }

            // Write back all lines with the edited event
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
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
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String description;

        public Event(String id, String title, LocalDateTime startTime, LocalDateTime endTime, String description) {
            this.id = id;
            this.title = title;
            this.startTime = startTime;
            this.endTime = endTime;
            this.description = description;
        }

        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return "Event{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", startTime=" + startTime.format(formatter) +
                    ", endTime=" + endTime.format(formatter) +
                    ", description='" + description + '\'' +
                    '}';
        }
    }
}
