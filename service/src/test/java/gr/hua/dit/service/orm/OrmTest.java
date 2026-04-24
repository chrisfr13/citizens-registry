package gr.hua.dit.service.orm;

import gr.hua.dit.domain.model.Citizen;
import gr.hua.dit.service.CitizensRegistryApplication;
import gr.hua.dit.service.repository.CitizenRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Δοκιμές ORM (Object-Relational Mapping)
 * Επαληθεύει ότι οι λειτουργίες CRUD μέσω JPA/Hibernate λειτουργούν σωστά.
 */
@SpringBootTest(classes = CitizensRegistryApplication.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("Δοκιμές ORM - JPA Repository")
class OrmTest {

    @Autowired
    private CitizenRepository citizenRepository;

    private Citizen testCitizen;

    @BeforeEach
    void setup() {
        citizenRepository.deleteAll();
        testCitizen = new Citizen("AB123456", "Γιώργος", "Παπαδόπουλος",
                "Άνδρας", "15-06-1990", "123456789", "Αθήνα 1");
    }

    // =====================================================================
    // ΔΟΚΙΜΕΣ ΕΙΣΑΓΩΓΗΣ (CREATE)
    // =====================================================================

    @Nested
    @DisplayName("Δοκιμές Εισαγωγής")
    class InsertTests {

        /**
         * Ελέγχει ότι ο αποθηκευμένος πολίτης επιστρέφεται με ΟΛΑ τα πεδία σωστά.
         * Διόρθωση: προστέθηκαν assertions για όλα τα πεδία (όχι μόνο idNumber).
         */
        @Test
        @DisplayName("Αποθήκευση πολίτη - επιτυχής (όλα τα πεδία)")
        void saveCitizen_Success() {
            Citizen saved = citizenRepository.save(testCitizen);

            assertNotNull(saved);
            assertEquals("AB123456",       saved.getIdNumber());
            assertEquals("Γιώργος",        saved.getFirstName());
            assertEquals("Παπαδόπουλος",   saved.getLastName());
            assertEquals("Άνδρας",         saved.getGender());
            assertEquals("15-06-1990",     saved.getBirthDate());
            assertEquals("123456789",      saved.getAfm());
            assertEquals("Αθήνα 1",        saved.getAddress());
        }

        @Test
        @DisplayName("Αποθήκευση πολίτη - διατήρηση όλων των πεδίων μετά από fetch")
        void saveCitizen_AllFieldsPersisted() {
            citizenRepository.save(testCitizen);
            Optional<Citizen> found = citizenRepository.findById("AB123456");
            assertTrue(found.isPresent());
            Citizen c = found.get();
            assertEquals("Γιώργος",      c.getFirstName());
            assertEquals("Παπαδόπουλος", c.getLastName());
            assertEquals("Άνδρας",       c.getGender());
            assertEquals("15-06-1990",   c.getBirthDate());
            assertEquals("123456789",    c.getAfm());
            assertEquals("Αθήνα 1",      c.getAddress());
        }

        /**
         * Ελέγχει ότι τα υποχρεωτικά πεδία αποθηκεύτηκαν σωστά και τα προαιρετικά είναι null.
         * Διόρθωση: προστέθηκαν assertions για τα υποχρεωτικά πεδία.
         */
        @Test
        @DisplayName("Αποθήκευση πολίτη χωρίς προαιρετικά πεδία - σωστά υποχρεωτικά + null προαιρετικά")
        void saveCitizen_WithoutOptionalFields() {
            Citizen c = new Citizen("XY789012", "Μαρία", "Γεωργίου", "Γυναίκα", "01-01-1985");
            citizenRepository.save(c);
            Optional<Citizen> found = citizenRepository.findById("XY789012");
            assertTrue(found.isPresent());

            Citizen stored = found.get();
            // Υποχρεωτικά πεδία - πρέπει να έχουν αποθηκευτεί σωστά
            assertEquals("XY789012",   stored.getIdNumber());
            assertEquals("Μαρία",      stored.getFirstName());
            assertEquals("Γεωργίου",   stored.getLastName());
            assertEquals("Γυναίκα",    stored.getGender());
            assertEquals("01-01-1985", stored.getBirthDate());
            // Προαιρετικά πεδία - πρέπει να είναι null
            assertNull(stored.getAfm());
            assertNull(stored.getAddress());
        }

        @Test
        @DisplayName("Αύξηση πλήθους εγγραφών μετά από αποθήκευση")
        void saveCitizen_CountIncreases() {
            long before = citizenRepository.count();
            citizenRepository.save(testCitizen);
            long after = citizenRepository.count();
            assertEquals(before + 1, after);
        }
    }

    // =====================================================================
    // ΔΟΚΙΜΕΣ ΑΝΑΖΗΤΗΣΗΣ (READ)
    // =====================================================================

    @Nested
    @DisplayName("Δοκιμές Ανάκτησης")
    class ReadTests {

        @Test
        @DisplayName("Εύρεση πολίτη βάσει υπάρχοντος ΑΤ")
        void findById_Exists_ReturnsOptional() {
            citizenRepository.save(testCitizen);
            Optional<Citizen> found = citizenRepository.findById("AB123456");
            assertTrue(found.isPresent());
        }

        @Test
        @DisplayName("Εύρεση πολίτη βάσει μη υπάρχοντος ΑΤ - κενό Optional")
        void findById_NotExists_EmptyOptional() {
            Optional<Citizen> found = citizenRepository.findById("ZZ000000");
            assertFalse(found.isPresent());
        }

        @Test
        @DisplayName("Εύρεση όλων - επιστρέφει σωστό αριθμό")
        void findAll_ReturnsCorrectCount() {
            citizenRepository.save(testCitizen);
            citizenRepository.save(new Citizen("CD999999", "Νίκος", "Αλεξίου", "Άνδρας", "20-03-1975"));
            List<Citizen> all = citizenRepository.findAll();
            assertEquals(2, all.size());
        }

        @Test
        @DisplayName("existsById - υπάρχων ΑΤ επιστρέφει true")
        void existsById_ExistingId_True() {
            citizenRepository.save(testCitizen);
            assertTrue(citizenRepository.existsById("AB123456"));
        }

        @Test
        @DisplayName("existsById - μη υπάρχων ΑΤ επιστρέφει false")
        void existsById_NonExistingId_False() {
            assertFalse(citizenRepository.existsById("ZZ000000"));
        }
    }

    // =====================================================================
    // ΔΟΚΙΜΕΣ ΕΝΗΜΕΡΩΣΗΣ (UPDATE)
    // =====================================================================

    @Nested
    @DisplayName("Δοκιμές Ενημέρωσης")
    class UpdateTests {

        @Test
        @DisplayName("Ενημέρωση ΑΦΜ - επιτυχής")
        void updateAfm_Success() {
            citizenRepository.save(testCitizen);
            Optional<Citizen> found = citizenRepository.findById("AB123456");
            assertTrue(found.isPresent());
            found.get().setAfm("999888777");
            citizenRepository.save(found.get());

            Optional<Citizen> updated = citizenRepository.findById("AB123456");
            assertTrue(updated.isPresent());
            assertEquals("999888777", updated.get().getAfm());
        }

        @Test
        @DisplayName("Ενημέρωση Διεύθυνσης - επιτυχής")
        void updateAddress_Success() {
            citizenRepository.save(testCitizen);
            Optional<Citizen> found = citizenRepository.findById("AB123456");
            found.get().setAddress("Θεσσαλονίκη 50");
            citizenRepository.save(found.get());

            Optional<Citizen> updated = citizenRepository.findById("AB123456");
            assertEquals("Θεσσαλονίκη 50", updated.get().getAddress());
        }
    }

    // =====================================================================
    // ΔΟΚΙΜΕΣ ΔΙΑΓΡΑΦΗΣ (DELETE)
    // =====================================================================

    @Nested
    @DisplayName("Δοκιμές Διαγραφής")
    class DeleteTests {

        @Test
        @DisplayName("Διαγραφή υπάρχοντος πολίτη - επιτυχής")
        void deleteCitizen_Success() {
            citizenRepository.save(testCitizen);
            assertTrue(citizenRepository.existsById("AB123456"));
            citizenRepository.deleteById("AB123456");
            assertFalse(citizenRepository.existsById("AB123456"));
        }

        @Test
        @DisplayName("Μείωση πλήθους μετά τη διαγραφή")
        void deleteCitizen_CountDecreases() {
            citizenRepository.save(testCitizen);
            long before = citizenRepository.count();
            citizenRepository.deleteById("AB123456");
            assertEquals(before - 1, citizenRepository.count());
        }
    }

    // =====================================================================
    // ΔΟΚΙΜΕΣ ΑΝΑΖΗΤΗΣΗΣ ΜΕΣΩ CUSTOM QUERY
    // =====================================================================

    @Nested
    @DisplayName("Δοκιμές Custom Query αναζήτησης")
    class SearchQueryTests {

        @BeforeEach
        void addData() {
            citizenRepository.save(testCitizen);
            citizenRepository.save(new Citizen("CD999999", "Μαρία", "Γεωργίου",
                    "Γυναίκα", "01-01-1985", "987654321", "Θεσσαλονίκη 5"));
        }

        @Test
        @DisplayName("Αναζήτηση βάσει επιθέτου - εύρεση αποτελέσματος")
        void searchByLastName_Found() {
            List<Citizen> results = citizenRepository.search(null, null, "Παπαδόπουλος",
                    null, null, null, null);
            assertEquals(1, results.size());
            assertEquals("AB123456", results.get(0).getIdNumber());
        }

        @Test
        @DisplayName("Αναζήτηση βάσει φύλου - πολλαπλά αποτελέσματα")
        void searchByGender_MultipleResults() {
            List<Citizen> results = citizenRepository.search(null, null, null,
                    "Γυναίκα", null, null, null);
            assertEquals(1, results.size());
        }

        @Test
        @DisplayName("Αναζήτηση χωρίς κριτήρια - επιστρέφει όλες τις εγγραφές")
        void searchNoParams_ReturnsAll() {
            List<Citizen> results = citizenRepository.search(null, null, null,
                    null, null, null, null);
            assertEquals(2, results.size());
        }

        @Test
        @DisplayName("Αναζήτηση βάσει ΑΦΜ - επιτυχής εύρεση")
        void searchByAfm_Found() {
            List<Citizen> results = citizenRepository.search(null, null, null,
                    null, null, "123456789", null);
            assertEquals(1, results.size());
        }
    }
}
