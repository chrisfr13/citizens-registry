package gr.hua.dit.service.exception;

/**
 * Exception που εκτοξεύεται όταν ο ζητούμενος πολίτης δεν βρεθεί στο μητρώο.
 * Αντιστοιχεί σε κωδικό HTTP 404 Not Found.
 */
public class CitizenNotFoundException extends RuntimeException {

    public CitizenNotFoundException(String message) {
        super(message);
    }
}
