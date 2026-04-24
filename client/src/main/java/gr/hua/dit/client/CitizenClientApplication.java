package gr.hua.dit.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.hua.dit.domain.model.Citizen;
import gr.hua.dit.domain.model.UpdateRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Scanner;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class CitizenClientApplication implements CommandLineRunner {

    private static final String BASE_URL = "http://localhost:8080/api/citizens";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        SpringApplication.run(CitizenClientApplication.class, args);
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║     ΜΗΤΡΩΟ ΠΟΛΙΤΩΝ - ΔΙΑΧΕΙΡΙΣΤΙΚΟ     ║");
        System.out.println("╚══════════════════════════════════════════╝");

        while (running) {
            printMenu();
            System.out.print("Επιλογή: ");
            String input = scanner.nextLine().trim();

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Μη έγκυρη επιλογή. Τερματισμός προγράμματος.");
                break;
            }

            switch (choice) {
                case 1 -> insertCitizen(scanner);
                case 2 -> deleteCitizen(scanner);
                case 3 -> updateCitizen(scanner);
                case 4 -> searchCitizens(scanner);
                case 5 -> getCitizenById(scanner);
                default -> {
                    System.out.println("❌ Μη έγκυρη επιλογή. Τερματισμός προγράμματος.");
                    running = false;
                }
            }
        }

        System.out.println("\nΑντίο!");
        scanner.close();
    }

    private void printMenu() {
        System.out.println("\n──────────────────────────────────────────");
        System.out.println(" 1. Εισαγωγή νέου πολίτη");
        System.out.println(" 2. Διαγραφή πολίτη");
        System.out.println(" 3. Ενημέρωση πολίτη (ΑΦΜ / Διεύθυνση)");
        System.out.println(" 4. Αναζήτηση πολιτών");
        System.out.println(" 5. Εμφάνιση στοιχείων πολίτη");
        System.out.println(" [Άλλο] Έξοδος");
        System.out.println("──────────────────────────────────────────");
    }

    // ──────────────────────────────────────────────────────────────────────
    // 1. ΕΙΣΑΓΩΓΗ
    // ──────────────────────────────────────────────────────────────────────
    private void insertCitizen(Scanner scanner) {
        System.out.println("\n--- ΕΙΣΑΓΩΓΗ ΝΕΟΥ ΠΟΛΙΤΗ ---");
        try {
            Citizen citizen = new Citizen();

            System.out.print("ΑΤ (8 χαρακτήρες): ");
            citizen.setIdNumber(scanner.nextLine().trim());

            System.out.print("Όνομα: ");
            citizen.setFirstName(scanner.nextLine().trim());

            System.out.print("Επίθετο: ");
            citizen.setLastName(scanner.nextLine().trim());

            System.out.print("Φύλο: ");
            citizen.setGender(scanner.nextLine().trim());

            System.out.print("Ημερομηνία Γέννησης (ΗΗ-ΜΜ-ΕΕΕΕ): ");
            citizen.setBirthDate(scanner.nextLine().trim());

            System.out.print("ΑΦΜ (9 ψηφία, προαιρετικό): ");
            String afm = scanner.nextLine().trim();
            if (!afm.isEmpty()) citizen.setAfm(afm);

            System.out.print("Διεύθυνση (προαιρετικό): ");
            String address = scanner.nextLine().trim();
            if (!address.isEmpty()) citizen.setAddress(address);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Citizen> request = new HttpEntity<>(citizen, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(BASE_URL, request, String.class);
            System.out.println("✅ Επιτυχής εισαγωγή: " + response.getBody());

        } catch (HttpClientErrorException e) {
            System.out.println("❌ Σφάλμα: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("❌ Σφάλμα επικοινωνίας: " + e.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // 2. ΔΙΑΓΡΑΦΗ
    // ──────────────────────────────────────────────────────────────────────
    private void deleteCitizen(Scanner scanner) {
        System.out.println("\n--- ΔΙΑΓΡΑΦΗ ΠΟΛΙΤΗ ---");
        try {
            System.out.print("ΑΤ πολίτη προς διαγραφή: ");
            String idNumber = scanner.nextLine().trim();

            restTemplate.delete(BASE_URL + "/" + idNumber);
            System.out.println("✅ Ο πολίτης με ΑΤ '" + idNumber + "' διαγράφηκε επιτυχώς.");

        } catch (HttpClientErrorException e) {
            System.out.println("❌ Σφάλμα: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("❌ Σφάλμα επικοινωνίας: " + e.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // 3. ΕΝΗΜΕΡΩΣΗ
    // ──────────────────────────────────────────────────────────────────────
    private void updateCitizen(Scanner scanner) {
        System.out.println("\n--- ΕΝΗΜΕΡΩΣΗ ΠΟΛΙΤΗ ---");
        try {
            System.out.print("ΑΤ πολίτη προς ενημέρωση: ");
            String idNumber = scanner.nextLine().trim();

            UpdateRequest updateRequest = new UpdateRequest();

            System.out.print("Νέο ΑΦΜ (9 ψηφία, Enter για παράλειψη): ");
            String afm = scanner.nextLine().trim();
            if (!afm.isEmpty()) updateRequest.setAfm(afm);

            System.out.print("Νέα Διεύθυνση (Enter για παράλειψη): ");
            String address = scanner.nextLine().trim();
            if (!address.isEmpty()) updateRequest.setAddress(address);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<UpdateRequest> request = new HttpEntity<>(updateRequest, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    BASE_URL + "/" + idNumber,
                    HttpMethod.PUT,
                    request,
                    String.class
            );
            System.out.println("✅ Επιτυχής ενημέρωση: " + response.getBody());

        } catch (HttpClientErrorException e) {
            System.out.println("❌ Σφάλμα: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("❌ Σφάλμα επικοινωνίας: " + e.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // 4. ΑΝΑΖΗΤΗΣΗ
    // ──────────────────────────────────────────────────────────────────────
    private void searchCitizens(Scanner scanner) {
        System.out.println("\n--- ΑΝΑΖΗΤΗΣΗ ΠΟΛΙΤΩΝ ---");
        System.out.println("(Αφήστε κενό για παράλειψη κριτηρίου)");
        try {
            System.out.print("ΑΤ: ");
            String idNumber = scanner.nextLine().trim();
            System.out.print("Όνομα: ");
            String firstName = scanner.nextLine().trim();
            System.out.print("Επίθετο: ");
            String lastName = scanner.nextLine().trim();
            System.out.print("Φύλο: ");
            String gender = scanner.nextLine().trim();
            System.out.print("Ημερομηνία Γέννησης (ΗΗ-ΜΜ-ΕΕΕΕ): ");
            String birthDate = scanner.nextLine().trim();
            System.out.print("ΑΦΜ: ");
            String afm = scanner.nextLine().trim();
            System.out.print("Διεύθυνση: ");
            String address = scanner.nextLine().trim();

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/search");
            if (!idNumber.isEmpty()) builder.queryParam("idNumber", idNumber);
            if (!firstName.isEmpty()) builder.queryParam("firstName", firstName);
            if (!lastName.isEmpty()) builder.queryParam("lastName", lastName);
            if (!gender.isEmpty()) builder.queryParam("gender", gender);
            if (!birthDate.isEmpty()) builder.queryParam("birthDate", birthDate);
            if (!afm.isEmpty()) builder.queryParam("afm", afm);
            if (!address.isEmpty()) builder.queryParam("address", address);

            ResponseEntity<String> response = restTemplate.getForEntity(
                    builder.toUriString(), String.class);

            List<Citizen> citizens = objectMapper.readValue(response.getBody(),
                    new TypeReference<List<Citizen>>() {});

            if (citizens.isEmpty()) {
                System.out.println("ℹ️  Δεν βρέθηκαν αποτελέσματα.");
            } else {
                System.out.println("✅ Βρέθηκαν " + citizens.size() + " εγγραφές:");
                citizens.forEach(c -> printCitizen(c));
            }

        } catch (HttpClientErrorException e) {
            System.out.println("❌ Σφάλμα: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("❌ Σφάλμα: " + e.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // 5. ΕΜΦΑΝΙΣΗ
    // ──────────────────────────────────────────────────────────────────────
    private void getCitizenById(Scanner scanner) {
        System.out.println("\n--- ΕΜΦΑΝΙΣΗ ΣΤΟΙΧΕΙΩΝ ΠΟΛΙΤΗ ---");
        try {
            System.out.print("ΑΤ πολίτη: ");
            String idNumber = scanner.nextLine().trim();

            ResponseEntity<Citizen> response = restTemplate.getForEntity(
                    BASE_URL + "/" + idNumber, Citizen.class);

            System.out.println("✅ Στοιχεία πολίτη:");
            printCitizen(response.getBody());

        } catch (HttpClientErrorException e) {
            System.out.println("❌ Σφάλμα: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("❌ Σφάλμα επικοινωνίας: " + e.getMessage());
        }
    }

    private void printCitizen(Citizen c) {
        if (c == null) return;
        System.out.println("  ┌─────────────────────────────────────");
        System.out.println("  │ ΑΤ:              " + c.getIdNumber());
        System.out.println("  │ Όνομα:           " + c.getFirstName());
        System.out.println("  │ Επίθετο:         " + c.getLastName());
        System.out.println("  │ Φύλο:            " + c.getGender());
        System.out.println("  │ Ημ. Γέννησης:    " + c.getBirthDate());
        System.out.println("  │ ΑΦΜ:             " + (c.getAfm() != null ? c.getAfm() : "-"));
        System.out.println("  │ Διεύθυνση:       " + (c.getAddress() != null ? c.getAddress() : "-"));
        System.out.println("  └─────────────────────────────────────");
    }
}
