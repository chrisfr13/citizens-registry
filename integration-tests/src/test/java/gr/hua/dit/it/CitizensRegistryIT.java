package gr.hua.dit.it;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Δοκιμές Ενοποίησης (Integration Tests) με REST Assured
 * Καλύπτει και τις 5 μεθόδους της RESTful υπηρεσίας:
 *   1. POST   /api/citizens         - Εισαγωγή
 *   2. DELETE /api/citizens/{id}    - Διαγραφή
 *   3. PUT    /api/citizens/{id}    - Ενημέρωση
 *   4. GET    /api/citizens/search  - Αναζήτηση
 *   5. GET    /api/citizens/{id}    - Εμφάνιση
 *
 * ΣΗΜΑΝΤΙΚΟ: Αυτές οι δοκιμές απαιτούν η υπηρεσία να τρέχει στο localhost:8080
 *            Εκτελέστε πρώτα: cd service && mvn spring-boot:run
 *            Έπειτα:          cd integration-tests && mvn failsafe:integration-test
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Δοκιμές Ενοποίησης RESTful Υπηρεσίας")
class CitizensRegistryIT {

    private static final String BASE_PATH = "/api/citizens";
    private static final String TEST_ID   = "IT000001";
    private static final String TEST_ID_2 = "IT000002";

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port    = 8080;
        RestAssured.basePath = "";
    }

    @AfterAll
    static void cleanup() {
        try { given().when().delete(BASE_PATH + "/" + TEST_ID);   } catch (Exception ignored) {}
        try { given().when().delete(BASE_PATH + "/" + TEST_ID_2); } catch (Exception ignored) {}
    }

    // =====================================================================
    // 1. ΔΟΚΙΜΕΣ ΕΙΣΑΓΩΓΗΣ (POST)
    // =====================================================================

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("1. Εισαγωγή Πολίτη (POST)")
    class InsertTests {

        /**
         * Διόρθωση: ελέγχονται πλέον ΟΛΑ τα πεδία της απόκρισης, όχι μόνο τα 3.
         */
        @Test
        @Order(1)
        @DisplayName("Εισαγωγή έγκυρου πολίτη - 201 Created")
        void insertValidCitizen_Returns201() {
            Map<String, String> citizen = createValidCitizenMap(TEST_ID);

            given()
                .contentType(ContentType.JSON)
                .body(citizen)
            .when()
                .post(BASE_PATH)
            .then()
                .statusCode(201)
                .body("idNumber",  equalTo(TEST_ID))
                .body("firstName", equalTo("Γιώργος"))
                .body("lastName",  equalTo("Παπαδόπουλος"))
                .body("gender",    equalTo("Άνδρας"))
                .body("birthDate", equalTo("15-06-1990"))
                .body("afm",       equalTo("123456789"))
                .body("address",   equalTo("Αθήνα 1"));
        }

        @Test
        @Order(2)
        @DisplayName("Εισαγωγή ήδη υπάρχοντος πολίτη - 400 Bad Request")
        void insertDuplicateCitizen_Returns400() {
            Map<String, String> citizen = createValidCitizenMap(TEST_ID);

            given()
                .contentType(ContentType.JSON)
                .body(citizen)
            .when()
                .post(BASE_PATH)
            .then()
                .statusCode(400)
                .body("error", containsString("υπάρχει ήδη"));
        }

        @Test
        @Order(3)
        @DisplayName("Εισαγωγή χωρίς υποχρεωτικό πεδίο (firstName) - 400 Bad Request")
        void insertMissingFirstName_Returns400() {
            Map<String, String> citizen = new HashMap<>();
            citizen.put("idNumber",  "IT999999");
            citizen.put("lastName",  "Τεστ");
            citizen.put("gender",    "Άνδρας");
            citizen.put("birthDate", "01-01-1990");

            given()
                .contentType(ContentType.JSON)
                .body(citizen)
            .when()
                .post(BASE_PATH)
            .then()
                .statusCode(400)
                .body("error", notNullValue());
        }

        @Test
        @Order(4)
        @DisplayName("Εισαγωγή με λανθασμένο ΑΤ (λιγότεροι χαρακτήρες) - 400 Bad Request")
        void insertInvalidIdNumber_Returns400() {
            Map<String, String> citizen = new HashMap<>();
            citizen.put("idNumber",  "AB123");   // μόνο 5 χαρακτήρες
            citizen.put("firstName", "Τεστ");
            citizen.put("lastName",  "Τεστ");
            citizen.put("gender",    "Άνδρας");
            citizen.put("birthDate", "01-01-1990");

            given()
                .contentType(ContentType.JSON)
                .body(citizen)
            .when()
                .post(BASE_PATH)
            .then()
                .statusCode(400);
        }

        @Test
        @Order(5)
        @DisplayName("Εισαγωγή με λανθασμένη ημερομηνία - 400 Bad Request")
        void insertInvalidBirthDate_Returns400() {
            Map<String, String> citizen = new HashMap<>();
            citizen.put("idNumber",  "IT888888");
            citizen.put("firstName", "Τεστ");
            citizen.put("lastName",  "Τεστ");
            citizen.put("gender",    "Άνδρας");
            citizen.put("birthDate", "1990-01-01");  // λάθος μορφή

            given()
                .contentType(ContentType.JSON)
                .body(citizen)
            .when()
                .post(BASE_PATH)
            .then()
                .statusCode(400)
                .body("error", containsString("ημερομηνία"));
        }

        @Test
        @Order(6)
        @DisplayName("Εισαγωγή δεύτερου έγκυρου πολίτη (για αναζήτηση) - 201 Created")
        void insertSecondCitizen_Returns201() {
            Map<String, String> citizen = new HashMap<>();
            citizen.put("idNumber",  TEST_ID_2);
            citizen.put("firstName", "Μαρία");
            citizen.put("lastName",  "Γεωργίου");
            citizen.put("gender",    "Γυναίκα");
            citizen.put("birthDate", "25-12-1992");
            citizen.put("afm",       "987654321");

            given()
                .contentType(ContentType.JSON)
                .body(citizen)
            .when()
                .post(BASE_PATH)
            .then()
                .statusCode(201)
                .body("idNumber", equalTo(TEST_ID_2));
        }
    }

    // =====================================================================
    // 2. ΔΟΚΙΜΕΣ ΕΜΦΑΝΙΣΗΣ (GET by ID)
    // =====================================================================

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("2. Εμφάνιση Πολίτη (GET /{id})")
    class GetByIdTests {

        @Test
        @Order(1)
        @DisplayName("Εμφάνιση υπάρχοντος πολίτη - 200 OK")
        void getExistingCitizen_Returns200() {
            given()
            .when()
                .get(BASE_PATH + "/" + TEST_ID)
            .then()
                .statusCode(200)
                .body("idNumber",  equalTo(TEST_ID))
                .body("firstName", equalTo("Γιώργος"))
                .body("lastName",  equalTo("Παπαδόπουλος"))
                .body("gender",    equalTo("Άνδρας"))
                .body("birthDate", equalTo("15-06-1990"));
        }

        /**
         * Διόρθωση: πολίτης που δεν βρίσκεται → 404 Not Found (όχι 400).
         */
        @Test
        @Order(2)
        @DisplayName("Εμφάνιση μη υπάρχοντος πολίτη - 404 Not Found")
        void getNonExistingCitizen_Returns404() {
            given()
            .when()
                .get(BASE_PATH + "/ZZ000000")
            .then()
                .statusCode(404)
                .body("error", containsString("Δεν βρέθηκε"));
        }

        @Test
        @Order(3)
        @DisplayName("Εμφάνιση με λανθασμένο ΑΤ (λιγότεροι χαρακτήρες) - 400 Bad Request")
        void getInvalidIdNumber_Returns400() {
            given()
            .when()
                .get(BASE_PATH + "/AB1")
            .then()
                .statusCode(400)
                .body("error", notNullValue());
        }
    }

    // =====================================================================
    // 3. ΔΟΚΙΜΕΣ ΑΝΑΖΗΤΗΣΗΣ (GET search)
    // =====================================================================

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("3. Αναζήτηση Πολιτών (GET /search)")
    class SearchTests {

        @Test
        @Order(1)
        @DisplayName("Αναζήτηση βάσει επιθέτου - επιτυχής εύρεση")
        void searchByLastName_Found() {
            given()
                .queryParam("lastName", "Παπαδόπουλος")
            .when()
                .get(BASE_PATH + "/search")
            .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(1)))
                .body("[0].lastName", equalTo("Παπαδόπουλος"));
        }

        @Test
        @Order(2)
        @DisplayName("Αναζήτηση βάσει φύλου - επιστρέφει αποτελέσματα")
        void searchByGender_ReturnsResults() {
            given()
                .queryParam("gender", "Γυναίκα")
            .when()
                .get(BASE_PATH + "/search")
            .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(1)));
        }

        @Test
        @Order(3)
        @DisplayName("Αναζήτηση βάσει ΑΤ - συγκεκριμένο αποτέλεσμα")
        void searchByIdNumber_SpecificResult() {
            given()
                .queryParam("idNumber", TEST_ID)
            .when()
                .get(BASE_PATH + "/search")
            .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].idNumber", equalTo(TEST_ID));
        }

        @Test
        @Order(4)
        @DisplayName("Αναζήτηση με λανθασμένο ΑΦΜ - 400 Bad Request")
        void searchInvalidAfm_Returns400() {
            given()
                .queryParam("afm", "123ABC")   // μη έγκυρο ΑΦΜ
            .when()
                .get(BASE_PATH + "/search")
            .then()
                .statusCode(400)
                .body("error", containsString("ΑΦΜ"));
        }

        @Test
        @Order(5)
        @DisplayName("Αναζήτηση συνδυασμού πεδίων - επιτυχής")
        void searchByCombinedFields_Found() {
            given()
                .queryParam("firstName", "Γιώργος")
                .queryParam("gender",    "Άνδρας")
            .when()
                .get(BASE_PATH + "/search")
            .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(1)));
        }
    }

    // =====================================================================
    // 4. ΔΟΚΙΜΕΣ ΕΝΗΜΕΡΩΣΗΣ (PUT)
    // =====================================================================

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("4. Ενημέρωση Πολίτη (PUT /{id})")
    class UpdateTests {

        @Test
        @Order(1)
        @DisplayName("Ενημέρωση ΑΦΜ υπάρχοντος πολίτη - 200 OK")
        void updateAfm_Returns200() {
            Map<String, String> updateRequest = new HashMap<>();
            updateRequest.put("afm", "111222333");

            given()
                .contentType(ContentType.JSON)
                .body(updateRequest)
            .when()
                .put(BASE_PATH + "/" + TEST_ID)
            .then()
                .statusCode(200)
                .body("afm",      equalTo("111222333"))
                .body("idNumber", equalTo(TEST_ID));
        }

        @Test
        @Order(2)
        @DisplayName("Ενημέρωση Διεύθυνσης - 200 OK")
        void updateAddress_Returns200() {
            Map<String, String> updateRequest = new HashMap<>();
            updateRequest.put("address", "Λεωφόρος Αθηνών 100, Αθήνα");

            given()
                .contentType(ContentType.JSON)
                .body(updateRequest)
            .when()
                .put(BASE_PATH + "/" + TEST_ID)
            .then()
                .statusCode(200)
                .body("address", equalTo("Λεωφόρος Αθηνών 100, Αθήνα"));
        }

        @Test
        @Order(3)
        @DisplayName("Ενημέρωση με λανθασμένο ΑΦΜ - 400 Bad Request")
        void updateInvalidAfm_Returns400() {
            Map<String, String> updateRequest = new HashMap<>();
            updateRequest.put("afm", "12345");   // λιγότερα από 9 ψηφία

            given()
                .contentType(ContentType.JSON)
                .body(updateRequest)
            .when()
                .put(BASE_PATH + "/" + TEST_ID)
            .then()
                .statusCode(400)
                .body("error", containsString("ΑΦΜ"));
        }

        /**
         * Διόρθωση: ενημέρωση μη υπάρχοντος πολίτη → 404 Not Found (όχι 400).
         */
        @Test
        @Order(4)
        @DisplayName("Ενημέρωση μη υπάρχοντος πολίτη - 404 Not Found")
        void updateNonExisting_Returns404() {
            Map<String, String> updateRequest = new HashMap<>();
            updateRequest.put("afm", "111222333");

            given()
                .contentType(ContentType.JSON)
                .body(updateRequest)
            .when()
                .put(BASE_PATH + "/ZZ000000")
            .then()
                .statusCode(404)
                .body("error", containsString("Δεν βρέθηκε"));
        }
    }

    // =====================================================================
    // 5. ΔΟΚΙΜΕΣ ΔΙΑΓΡΑΦΗΣ (DELETE)
    // =====================================================================

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("5. Διαγραφή Πολίτη (DELETE /{id})")
    class DeleteTests {

        /**
         * Διόρθωση: επιτυχής διαγραφή → 204 No Content (όχι 200 με body).
         */
        @Test
        @Order(1)
        @DisplayName("Διαγραφή υπάρχοντος πολίτη - 204 No Content")
        void deleteExistingCitizen_Returns204() {
            // Πρώτα εισάγουμε πολίτη για διαγραφή
            Map<String, String> citizen = createValidCitizenMap("DL000001");
            given()
                .contentType(ContentType.JSON)
                .body(citizen)
            .when()
                .post(BASE_PATH);

            // Διαγραφή → 204 No Content (κενό σώμα απόκρισης)
            given()
            .when()
                .delete(BASE_PATH + "/DL000001")
            .then()
                .statusCode(204);
        }

        /**
         * Διόρθωση: διαγραφή μη υπάρχοντος πολίτη → 404 Not Found (όχι 400).
         */
        @Test
        @Order(2)
        @DisplayName("Διαγραφή μη υπάρχοντος πολίτη - 404 Not Found")
        void deleteNonExisting_Returns404() {
            given()
            .when()
                .delete(BASE_PATH + "/ZZ000000")
            .then()
                .statusCode(404)
                .body("error", containsString("Δεν βρέθηκε"));
        }

        @Test
        @Order(3)
        @DisplayName("Διαγραφή με λανθασμένο ΑΤ - 400 Bad Request")
        void deleteInvalidId_Returns400() {
            given()
            .when()
                .delete(BASE_PATH + "/AB1")   // μόνο 3 χαρακτήρες
            .then()
                .statusCode(400)
                .body("error", notNullValue());
        }
    }

    // =====================================================================
    // Helper Methods
    // =====================================================================

    private Map<String, String> createValidCitizenMap(String idNumber) {
        Map<String, String> citizen = new HashMap<>();
        citizen.put("idNumber",  idNumber);
        citizen.put("firstName", "Γιώργος");
        citizen.put("lastName",  "Παπαδόπουλος");
        citizen.put("gender",    "Άνδρας");
        citizen.put("birthDate", "15-06-1990");
        citizen.put("afm",       "123456789");
        citizen.put("address",   "Αθήνα 1");
        return citizen;
    }
}
