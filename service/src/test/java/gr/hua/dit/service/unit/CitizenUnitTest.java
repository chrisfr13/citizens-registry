package gr.hua.dit.service.unit;

import gr.hua.dit.domain.model.Citizen;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Δοκιμές Μονάδων για την κλάση Citizen
 * Καλύπτει: δομητές, μεθόδους (getters/setters) και επικύρωση δεδομένων
 */
@DisplayName("Δοκιμές Μονάδων - Κλάση Citizen")
class CitizenUnitTest {

    private static Validator validator;
    private Citizen validCitizen;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setup() {
        validCitizen = new Citizen("AB123456", "Γιώργος", "Παπαδόπουλος", "Άνδρας", "15-06-1990");
    }

    // =====================================================================
    // ΔΟΚΙΜΕΣ ΔΟΜΗΤΩΝ
    // =====================================================================

    @Nested
    @DisplayName("Δοκιμές Δομητών")
    class ConstructorTests {

        @Test
        @DisplayName("Προεπιλεγμένος δομητής - δημιουργία κενού αντικειμένου")
        void defaultConstructor_CreatesEmptyObject() {
            Citizen c = new Citizen();
            assertNull(c.getIdNumber());
            assertNull(c.getFirstName());
            assertNull(c.getLastName());
            assertNull(c.getGender());
            assertNull(c.getBirthDate());
            assertNull(c.getAfm());
            assertNull(c.getAddress());
        }

        @Test
        @DisplayName("Δομητής 5 παραμέτρων - σωστή αρχικοποίηση υποχρεωτικών πεδίων")
        void fiveParamConstructor_InitializesCorrectly() {
            Citizen c = new Citizen("AB123456", "Ιωάννης", "Κωνσταντίνου", "Άνδρας", "01-01-1985");
            assertEquals("AB123456", c.getIdNumber());
            assertEquals("Ιωάννης", c.getFirstName());
            assertEquals("Κωνσταντίνου", c.getLastName());
            assertEquals("Άνδρας", c.getGender());
            assertEquals("01-01-1985", c.getBirthDate());
            assertNull(c.getAfm());
            assertNull(c.getAddress());
        }

        @Test
        @DisplayName("Δομητής 7 παραμέτρων - σωστή αρχικοποίηση όλων των πεδίων")
        void sevenParamConstructor_InitializesAllFields() {
            Citizen c = new Citizen("XY789012", "Μαρία", "Γεωργίου", "Γυναίκα", "25-12-1992",
                    "123456789", "Αθήνα 12");
            assertEquals("XY789012", c.getIdNumber());
            assertEquals("Μαρία", c.getFirstName());
            assertEquals("Γεωργίου", c.getLastName());
            assertEquals("Γυναίκα", c.getGender());
            assertEquals("25-12-1992", c.getBirthDate());
            assertEquals("123456789", c.getAfm());
            assertEquals("Αθήνα 12", c.getAddress());
        }
    }

    // =====================================================================
    // ΔΟΚΙΜΕΣ GETTERS & SETTERS
    // =====================================================================

    @Nested
    @DisplayName("Δοκιμές Getters και Setters")
    class GetterSetterTests {

        @Test
        @DisplayName("setIdNumber / getIdNumber - σωστή λειτουργία")
        void setGetIdNumber() {
            validCitizen.setIdNumber("ZZ999999");
            assertEquals("ZZ999999", validCitizen.getIdNumber());
        }

        @Test
        @DisplayName("setFirstName / getFirstName - σωστή λειτουργία")
        void setGetFirstName() {
            validCitizen.setFirstName("Νίκος");
            assertEquals("Νίκος", validCitizen.getFirstName());
        }

        @Test
        @DisplayName("setLastName / getLastName - σωστή λειτουργία")
        void setGetLastName() {
            validCitizen.setLastName("Αλεξίου");
            assertEquals("Αλεξίου", validCitizen.getLastName());
        }

        @Test
        @DisplayName("setGender / getGender - σωστή λειτουργία")
        void setGetGender() {
            validCitizen.setGender("Γυναίκα");
            assertEquals("Γυναίκα", validCitizen.getGender());
        }

        @Test
        @DisplayName("setBirthDate / getBirthDate - σωστή λειτουργία")
        void setGetBirthDate() {
            validCitizen.setBirthDate("31-12-2000");
            assertEquals("31-12-2000", validCitizen.getBirthDate());
        }

        @Test
        @DisplayName("setAfm / getAfm - σωστή λειτουργία")
        void setGetAfm() {
            validCitizen.setAfm("987654321");
            assertEquals("987654321", validCitizen.getAfm());
        }

        @Test
        @DisplayName("setAddress / getAddress - σωστή λειτουργία")
        void setGetAddress() {
            validCitizen.setAddress("Λεωφόρος Αθηνών 100, Αθήνα");
            assertEquals("Λεωφόρος Αθηνών 100, Αθήνα", validCitizen.getAddress());
        }
    }

    // =====================================================================
    // ΔΟΚΙΜΕΣ ΕΠΙΚΥΡΩΣΗΣ - ΑΤ
    // =====================================================================

    @Nested
    @DisplayName("Επικύρωση ΑΤ")
    class IdNumberValidationTests {

        @Test
        @DisplayName("Έγκυρος ΑΤ 8 χαρακτήρων - χωρίς παραβιάσεις")
        void validIdNumber_NoViolations() {
            validCitizen.setIdNumber("AB123456");
            Set<ConstraintViolation<Citizen>> v = validator.validateProperty(validCitizen, "idNumber");
            assertTrue(v.isEmpty());
        }

        @Test
        @DisplayName("ΑΤ null - παραβίαση NotBlank")
        void nullIdNumber_Violation() {
            validCitizen.setIdNumber(null);
            Set<ConstraintViolation<Citizen>> v = validator.validateProperty(validCitizen, "idNumber");
            assertFalse(v.isEmpty());
        }

        @Test
        @DisplayName("Κενός ΑΤ - παραβίαση NotBlank")
        void blankIdNumber_Violation() {
            validCitizen.setIdNumber("");
            Set<ConstraintViolation<Citizen>> v = validator.validateProperty(validCitizen, "idNumber");
            assertFalse(v.isEmpty());
        }

        @ParameterizedTest
        @ValueSource(strings = {"AB12345", "AB1234567", "A", "ABCDEFGHI"})
        @DisplayName("ΑΤ λανθασμένου μήκους - παραβίαση Size")
        void wrongLengthIdNumber_Violation(String id) {
            validCitizen.setIdNumber(id);
            Set<ConstraintViolation<Citizen>> v = validator.validateProperty(validCitizen, "idNumber");
            assertFalse(v.isEmpty());
        }
    }

    // =====================================================================
    // ΔΟΚΙΜΕΣ ΕΠΙΚΥΡΩΣΗΣ - ΥΠΟΧΡΕΩΤΙΚΑ ΠΕΔΙΑ
    // =====================================================================

    @Nested
    @DisplayName("Επικύρωση Υποχρεωτικών Πεδίων")
    class MandatoryFieldsValidationTests {

        @Test
        @DisplayName("Κενό firstName - παραβίαση NotBlank")
        void blankFirstName_Violation() {
            validCitizen.setFirstName("");
            Set<ConstraintViolation<Citizen>> v = validator.validateProperty(validCitizen, "firstName");
            assertFalse(v.isEmpty());
        }

        @Test
        @DisplayName("Null lastName - παραβίαση NotBlank")
        void nullLastName_Violation() {
            validCitizen.setLastName(null);
            Set<ConstraintViolation<Citizen>> v = validator.validateProperty(validCitizen, "lastName");
            assertFalse(v.isEmpty());
        }

        @Test
        @DisplayName("Null gender - παραβίαση NotBlank")
        void nullGender_Violation() {
            validCitizen.setGender(null);
            Set<ConstraintViolation<Citizen>> v = validator.validateProperty(validCitizen, "gender");
            assertFalse(v.isEmpty());
        }

        @Test
        @DisplayName("Έγκυρος πολίτης - χωρίς παραβιάσεις")
        void validCitizen_NoViolations() {
            Set<ConstraintViolation<Citizen>> v = validator.validate(validCitizen);
            assertTrue(v.isEmpty());
        }
    }

    // =====================================================================
    // ΔΟΚΙΜΕΣ ΕΠΙΚΥΡΩΣΗΣ - ΗΜΕΡΟΜΗΝΙΑ ΓΕΝΝΗΣΗΣ
    // =====================================================================

    @Nested
    @DisplayName("Επικύρωση Ημερομηνίας Γέννησης")
    class BirthDateValidationTests {

        @ParameterizedTest
        @ValueSource(strings = {"01-01-2000", "31-12-1990", "15-06-1985", "28-02-2004"})
        @DisplayName("Έγκυρες ημερομηνίες ΗΗ-ΜΜ-ΕΕΕΕ - χωρίς παραβιάσεις")
        void validBirthDates_NoViolations(String date) {
            validCitizen.setBirthDate(date);
            Set<ConstraintViolation<Citizen>> v = validator.validateProperty(validCitizen, "birthDate");
            assertTrue(v.isEmpty());
        }

        @ParameterizedTest
        @ValueSource(strings = {"2000-01-01", "1/1/2000", "32-01-2000", "01-13-2000", "01-01-200"})
        @DisplayName("Μη έγκυρες μορφές ημερομηνίας - παραβίαση Pattern")
        void invalidBirthDates_Violation(String date) {
            validCitizen.setBirthDate(date);
            Set<ConstraintViolation<Citizen>> v = validator.validateProperty(validCitizen, "birthDate");
            assertFalse(v.isEmpty());
        }

        @Test
        @DisplayName("Null ημερομηνία - παραβίαση NotBlank")
        void nullBirthDate_Violation() {
            validCitizen.setBirthDate(null);
            Set<ConstraintViolation<Citizen>> v = validator.validateProperty(validCitizen, "birthDate");
            assertFalse(v.isEmpty());
        }
    }

    // =====================================================================
    // ΔΟΚΙΜΕΣ ΕΠΙΚΥΡΩΣΗΣ - ΑΦΜ
    // =====================================================================

    @Nested
    @DisplayName("Επικύρωση ΑΦΜ")
    class AfmValidationTests {

        @Test
        @DisplayName("Έγκυρο ΑΦΜ 9 ψηφίων - χωρίς παραβιάσεις")
        void validAfm_NoViolations() {
            validCitizen.setAfm("123456789");
            Set<ConstraintViolation<Citizen>> v = validator.validateProperty(validCitizen, "afm");
            assertTrue(v.isEmpty());
        }

        @Test
        @DisplayName("Null ΑΦΜ - επιτρεπτό (μη υποχρεωτικό)")
        void nullAfm_NoViolation() {
            validCitizen.setAfm(null);
            Set<ConstraintViolation<Citizen>> v = validator.validateProperty(validCitizen, "afm");
            assertTrue(v.isEmpty());
        }

        @ParameterizedTest
        @ValueSource(strings = {"12345678", "1234567890", "12345678A", "ABCDEFGHI"})
        @DisplayName("Μη έγκυρο ΑΦΜ - παραβίαση Pattern")
        void invalidAfm_Violation(String afm) {
            validCitizen.setAfm(afm);
            Set<ConstraintViolation<Citizen>> v = validator.validateProperty(validCitizen, "afm");
            assertFalse(v.isEmpty());
        }
    }

    // =====================================================================
    // ΔΟΚΙΜΕΣ equals / hashCode / toString
    // =====================================================================

    @Nested
    @DisplayName("Δοκιμές equals, hashCode, toString")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Δύο πολίτες με ίδιο ΑΤ είναι ίσοι")
        void sameidNumber_AreEqual() {
            Citizen c1 = new Citizen("AB123456", "Γιώργος", "Παπαδόπουλος", "Άνδρας", "15-06-1990");
            Citizen c2 = new Citizen("AB123456", "Άλλος", "Άλλος", "Γυναίκα", "01-01-2000");
            assertEquals(c1, c2);
        }

        @Test
        @DisplayName("Δύο πολίτες με διαφορετικό ΑΤ δεν είναι ίσοι")
        void differentIdNumber_NotEqual() {
            Citizen c1 = new Citizen("AB123456", "Γιώργος", "Παπαδόπουλος", "Άνδρας", "15-06-1990");
            Citizen c2 = new Citizen("XY789012", "Γιώργος", "Παπαδόπουλος", "Άνδρας", "15-06-1990");
            assertNotEquals(c1, c2);
        }

        @Test
        @DisplayName("Ίδιος ΑΤ → ίδιο hashCode")
        void sameIdNumber_SameHashCode() {
            Citizen c1 = new Citizen("AB123456", "Α", "Β", "Άνδρας", "01-01-2000");
            Citizen c2 = new Citizen("AB123456", "Γ", "Δ", "Γυναίκα", "31-12-1999");
            assertEquals(c1.hashCode(), c2.hashCode());
        }

        @Test
        @DisplayName("toString περιέχει τα βασικά πεδία")
        void toString_ContainsKeyFields() {
            String str = validCitizen.toString();
            assertTrue(str.contains("AB123456"));
            assertTrue(str.contains("Γιώργος"));
            assertTrue(str.contains("Παπαδόπουλος"));
        }
    }
}
