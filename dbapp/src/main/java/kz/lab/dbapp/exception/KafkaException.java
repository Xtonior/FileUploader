package kz.lab.dbapp.exception;

public class KafkaException extends Exception {
     public KafkaException(String message) {
        super(message);
    }

    public KafkaException(String message, Throwable cause) {
        super(message, cause);
    }
}
