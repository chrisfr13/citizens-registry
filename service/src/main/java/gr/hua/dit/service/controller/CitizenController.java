package gr.hua.dit.service.controller;

import gr.hua.dit.domain.model.Citizen;
import gr.hua.dit.domain.model.UpdateRequest;
import gr.hua.dit.service.exception.CitizenNotFoundException;
import gr.hua.dit.service.service.CitizenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/citizens")
public class CitizenController {

    @Autowired
    private CitizenService citizenService;

    /**
     * POST /api/citizens - Εισαγωγή νέου πολίτη
     */
    @PostMapping
    public ResponseEntity<?> insert(@RequestBody Citizen citizen) {
        try {
            Citizen saved = citizenService.insert(citizen);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (CitizenNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/citizens/{idNumber} - Διαγραφή πολίτη βάσει ΑΤ
     * Επιστρέφει 204 No Content σε επιτυχία, 404 αν δεν βρεθεί, 400 για μη έγκυρα δεδομένα.
     */
    @DeleteMapping("/{idNumber}")
    public ResponseEntity<?> delete(@PathVariable String idNumber) {
        try {
            citizenService.delete(idNumber);
            return ResponseEntity.noContent().build();
        } catch (CitizenNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/citizens/{idNumber} - Ενημέρωση ΑΦΜ και διεύθυνσης
     */
    @PutMapping("/{idNumber}")
    public ResponseEntity<?> update(@PathVariable String idNumber,
                                     @RequestBody UpdateRequest updateRequest) {
        try {
            Citizen updated = citizenService.update(idNumber, updateRequest);
            return ResponseEntity.ok(updated);
        } catch (CitizenNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/citizens/search - Αναζήτηση βάσει οποιουδήποτε πεδίου
     */
    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam(required = false) String idNumber,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String birthDate,
            @RequestParam(required = false) String afm,
            @RequestParam(required = false) String address) {
        try {
            List<Citizen> results = citizenService.search(idNumber, firstName, lastName,
                    gender, birthDate, afm, address);
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/citizens/{idNumber} - Εμφάνιση στοιχείων πολίτη βάσει ΑΤ
     */
    @GetMapping("/{idNumber}")
    public ResponseEntity<?> getById(@PathVariable String idNumber) {
        try {
            Citizen citizen = citizenService.getById(idNumber);
            return ResponseEntity.ok(citizen);
        } catch (CitizenNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
