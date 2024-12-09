import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class proyecto {
    public static void main(String[] args) {
//hello
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
                    ", startTime=" + startTime +
                    ", endTime=" + endTime +
                    ", description='" + description + '\'' +
                    '}';
        }
    }
}
