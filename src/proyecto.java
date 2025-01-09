import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/*
ideas:
poner una excepcion en las fechas y los url para que no crasheen
cambiar el id a un raondomizador
 */
/**
 * Clase principal para gestionar eventos almacenados en un archivo CSV.
 * Permite visualizar, añadir, editar y eliminar eventos.
 */
public class proyecto {
    private static final String archivo = "/Users/aldozamora/Documents/IntellijProjects/UEMProyecto/src/Gestion_de_Eventos.csv";
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Scanner input = new Scanner(System.in);


    /**
     * Valida y parsea una fecha en formato dd/MM/yyyy.
     *
     * @param dateStr la fecha en formato String
     * @return LocalDate objeto si la fecha es válida
     * @throws DateTimeParseException si la fecha no tiene el formato correcto
     * @throws IllegalArgumentException si la fecha es del pasado
     */
    private static LocalDate validarFecha(String dateStr) throws DateTimeParseException, IllegalArgumentException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha no puede estar vacía");
        }

        try {
            LocalDate date = LocalDate.parse(dateStr, dateFormatter);
            LocalDate today = LocalDate.now();

            if (date.isBefore(today)) {
                throw new IllegalArgumentException("La fecha no puede ser del pasado");
            }

            return date;
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException(
                    "Formato de fecha inválido. Use dd/MM/yyyy (ejemplo: 25/12/2024)",
                    dateStr,
                    e.getErrorIndex()
            );
        }
    }

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

    //hacer el id que se haga solo con un randomizador de 6 digitos

    /**
     * Muestra los eventos almacenados en el archivo CSV.
     */
    private static void mostrarEventos() {
        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
            String line;
            // Saltar header si exise
            line = lector.readLine();
            if (line == null) {
                System.out.println("El archivo está vacío.");
                return;
            }

            System.out.println("\nEventos registrados:");
            System.out.println("-------------------");
            int eventCount = 0;
            int lineNumber = 1;  // Saltar la primera linea (header)

            while ((line = lector.readLine()) != null) {
                lineNumber++;
                try {
                    String[] parts = line.split(";");
                    if (parts.length < 8) {
                        System.out.printf("Advertencia: Línea %d mal formateada (faltan campos) - saltando...\n", lineNumber);
                        continue;
                    }

                    // Validate and parse each field before creating the Event
                    String id = parts[0].trim();
                    String title = parts[1].trim();
                    LocalDate date;
                    LocalTime startTime;
                    LocalTime endTime;

                    try {
                        date = LocalDate.parse(parts[2].trim(), dateFormatter);
                    } catch (DateTimeParseException e) {
                        System.out.printf("Advertencia: Fecha inválida en línea %d - saltando...\n", lineNumber);
                        continue;
                    }

                    try {
                        startTime = LocalTime.parse(parts[3].trim(), DateTimeFormatter.ofPattern("H:mm"));
                        endTime = LocalTime.parse(parts[4].trim(), DateTimeFormatter.ofPattern("H:mm"));
                    } catch (DateTimeParseException e) {
                        System.out.printf("Advertencia: Hora inválida en línea %d - saltando...\n", lineNumber);
                        continue;
                    }

                    String location = parts[5].trim();
                    String description = parts[6].trim();
                    String url = parts[7].trim();

                    // Create and display the event
                    Event event = new Event(id, title, date, startTime, endTime, location, description, url);
                    System.out.println(event);
                    eventCount++;

                } catch (Exception e) {
                    System.out.printf("Advertencia: Error al procesar línea %d: %s\n", lineNumber, line);
                }
            }

            System.out.println("\n-------------------");
            System.out.printf("Total de eventos mostrados: %d\n", eventCount);

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
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

            LocalDate date = null;
            while (date == null) {
                try {
                    System.out.println("Ingrese fecha (dd/MM/yyyy):");
                    date = validarFecha(input.nextLine());
                } catch (DateTimeParseException | IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }

            LocalTime startTime = null;
            while (startTime == null) {
                try {
                    System.out.println("Ingrese hora de inicio (H:mm o HH:mm):");
                    startTime = parsearHora(input.nextLine());
                } catch (DateTimeParseException | IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }

            LocalTime endTime = null;
            while (endTime == null) {
                try {
                    System.out.println("Ingrese hora de fin (H:mm o HH:mm):");
                    String endTimeStr = input.nextLine();
                    LocalTime tempEndTime = parsearHora(endTimeStr);

                    // Validate that end time is after start time
                    if (tempEndTime.isBefore(startTime)) {
                        System.out.println("Error: La hora de fin debe ser posterior a la hora de inicio");
                        continue;
                    }

                    endTime = tempEndTime;
                } catch (DateTimeParseException | IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }

            System.out.println("Ingrese lugar:");
            String location = input.nextLine();

            int description = 0;
            while (description < 1 || description > 3) {
                try {
                    System.out.println("Ingrese el grupo de la reunión (1: Estudiantes, 2: Profesores, 3: Comunidad universitaria):");
                    description = Integer.parseInt(input.nextLine());
                    if (description < 1 || description > 3) {
                        throw new IllegalArgumentException("El grupo debe ser 1, 2 o 3.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Debe ingresar un número válido (1, 2 o 3)");
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                }
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

            try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
                String line;
                while ((line = lector.readLine()) != null) {
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

            try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
                String line;
                while ((line = lector.readLine()) != null) {
                    if (line.startsWith(idToEdit + ";")) {
                        found = true;
                        String[] parts = line.split(";");

                        System.out.println("Ingrese nuevo título (o Enter para mantener):");
                        String title = input.nextLine();

                        LocalDate date = null;
                        while (true) {
                            System.out.println("Ingrese nueva fecha (dd/MM/yyyy, o Enter para mantener):");
                            String dateStr = input.nextLine();
                            if (dateStr.isEmpty()) {
                                date = LocalDate.parse(parts[2], dateFormatter);
                                break;
                            }
                            try {
                                date = validarFecha(dateStr);
                                break;
                            } catch (DateTimeParseException | IllegalArgumentException e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                        }

                        LocalTime startTime = null;
                        while (true) {
                            System.out.println("Ingrese nueva hora de inicio (H:mm o Enter para mantener):");
                            String timeStr = input.nextLine();
                            if (timeStr.isEmpty()) {
                                startTime = LocalTime.parse(parts[3], DateTimeFormatter.ofPattern("H:mm"));
                                break;
                            }
                            try {
                                startTime = parsearHora(timeStr);
                                break;
                            } catch (DateTimeParseException | IllegalArgumentException e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                        }

                        LocalTime endTime = null;
                        while (true) {
                            System.out.println("Ingrese nueva hora de fin (H:mm o Enter para mantener):");
                            String timeStr = input.nextLine();
                            if (timeStr.isEmpty()) {
                                endTime = LocalTime.parse(parts[4], DateTimeFormatter.ofPattern("H:mm"));
                                break;
                            }
                            try {
                                endTime = parsearHora(timeStr);
                                if (endTime.isBefore(startTime)) {
                                    throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
                                }
                                break;
                            } catch (DateTimeParseException | IllegalArgumentException e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                        }

                        System.out.println("Ingrese nuevo lugar (o Enter para mantener):");
                        String location = input.nextLine();

                        int newDescription = Integer.parseInt(parts[6]);
                        while (true) {
                            System.out.println("Ingrese nuevo grupo de la reunión (1, 2, 3 o Enter para mantener):");
                            String descriptionInput = input.nextLine();
                            if (descriptionInput.isEmpty()) {
                                break;
                            }
                            try {
                                newDescription = Integer.parseInt(descriptionInput);
                                if (newDescription < 1 || newDescription > 3) {
                                    throw new IllegalArgumentException("El grupo debe ser 1, 2 o 3.");
                                }
                                break;
                            } catch (NumberFormatException e) {
                                System.out.println("Error: Debe ingresar un número válido (1, 2 o 3)");
                            } catch (IllegalArgumentException e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                        }

                        System.out.println("Ingrese nueva URL (o Enter para mantener):");
                        String url = input.nextLine();

                        lines.add(String.format("%s;%s;%s;%s;%s;%s;%d;%s",
                                                idToEdit,
                                                title.isEmpty() ? parts[1] : title,
                                                date.format(dateFormatter),
                                                startTime.format(DateTimeFormatter.ofPattern("H:mm")),
                                                endTime.format(DateTimeFormatter.ofPattern("H:mm")),
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
            return String.format("""
                    
                    ID: %s
                    Título: %s
                    Fecha: %s
                    Hora inicio: %s
                    Hora fin: %s
                    Lugar: %s
                    Grupo: %s
                    URL: %s
                    """,
                                 id,
                                 title,
                                 date.format(dateFormatter),
                                 startTime.format(DateTimeFormatter.ofPattern("H:mm")),
                                 endTime.format(DateTimeFormatter.ofPattern("H:mm")),
                                 location,
                                 description,
                                 url.isEmpty() ? "(Sin URL)" : url
            );
        }
    }



    /**
     * Clase auxiliar para almacenar la fecha y el conteo de eventos
     */
    private static class EventCount {
        LocalDate date;
        int count;

        EventCount(LocalDate date) {
            this.date = date;
            this.count = 1;
        }
    }

    /**
     * Analiza los eventos para encontrar los días con más y menos eventos
     * usando Arrays y Lists en lugar de HashMap.
     */
    private static void bonus() {
        List<EventCount> eventCounts = new ArrayList<>();

        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
            String line;
            // ignorar primera linea
            lector.readLine();

            while ((line = lector.readLine()) != null) {
                try {
                    String[] parts = line.split(";");
                    if (parts.length >= 3) {
                        LocalDate date = LocalDate.parse(parts[2], dateFormatter);

                        // Buscar si ya existe la fecha
                        boolean found = false;
                        for (EventCount ec : eventCounts) {
                            if (ec.date.equals(date)) {
                                ec.count++;
                                found = true;
                                break;
                            }
                        }

                        // Si no existe, añadir nueva fecha
                        if (!found) {
                            eventCounts.add(new EventCount(date));
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error al procesar la línea: " + line);
                }
            }

            if (eventCounts.isEmpty()) {
                System.out.println("No hay eventos registrados.");
                return;
            }

            // Ordenar por fecha
            eventCounts.sort((ec1, ec2) -> ec1.date.compareTo(ec2.date));

            // Encontrar días con máximo y mínimo eventos
            EventCount maxEvents = eventCounts.get(0);
            EventCount minEvents = eventCounts.get(0);

            for (EventCount ec : eventCounts) {
                if (ec.count > maxEvents.count) {
                    maxEvents = ec;
                }
                if (ec.count < minEvents.count) {
                    minEvents = ec;
                }
            }

            // Calcular promedio
            int totalEvents = 0;
            for (EventCount ec : eventCounts) {
                totalEvents += ec.count;
            }
            double avgEvents = (double) totalEvents / eventCounts.size();

            // Mostrar resultados
            System.out.println("\n=== Análisis de Eventos ===");

            System.out.println("\nDía con más eventos:");
            System.out.printf("- %s: %d eventos%n",
                              maxEvents.date.format(dateFormatter),
                              maxEvents.count);

            System.out.println("\nDía con menos eventos:");
            System.out.printf("- %s: %d eventos%n",
                              minEvents.date.format(dateFormatter),
                              minEvents.count);

            System.out.println("\nDistribución completa de eventos:");
            for (EventCount ec : eventCounts) {
                System.out.printf("- %s: %d eventos%n",
                                  ec.date.format(dateFormatter),
                                  ec.count);
            }

            System.out.println("\nEstadísticas:");
            System.out.printf("- Total de días con eventos: %d%n", eventCounts.size());
            System.out.printf("- Promedio de eventos por día: %.2f%n", avgEvents);

        } catch (IOException e) {
            System.out.println("Error al leer eventos: " + e.getMessage());
        }
    }


    /**
     * Muestra las opciones que se pueden hacer en el programa
     */
    private static void mostrarMenu() {
        System.out.println("""
                
                === MENÚ DE GESTIÓN DE EVENTOS ===
                1. Ver eventos
                2. Ver análisis de eventos (bonus)
                3. Añadir evento
                4. Quitar evento
                5. Editar evento
                6. Salir
                
                Seleccione una opción (1-6):""");
    }



    /**
     * Método principal del programa.
     *
     * @param args Argumentos de línea de comando.
     */
    public static void main(String[] args) {
        while (true) {
            mostrarMenu();
            String choice = input.nextLine().trim();

            switch (choice) {
                case "1" -> mostrarEventos();
                case "2" -> bonus();
                case "3" -> añadirEvento();
                case "4" -> quitarEvento();
                case "5" -> editarEvento();
                case "6" -> {
                    System.out.println("¡Hasta luego!");
                    return;
                }
                default -> System.out.println("Opción no válida. Por favor, seleccione una opción entre 1 y 6.");
            }
        }
    }
}