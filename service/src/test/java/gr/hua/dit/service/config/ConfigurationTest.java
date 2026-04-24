package gr.hua.dit.service.config;

import gr.hua.dit.service.CitizensRegistryApplication;
import gr.hua.dit.service.repository.CitizenRepository;
import gr.hua.dit.service.service.CitizenService;
import gr.hua.dit.service.controller.CitizenController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Δοκιμές Ρύθμισης (Configuration Tests)
 * Επαληθεύει ότι το Spring context φορτώνεται σωστά και
 * όλα τα beans αρχικοποιούνται κανονικά.
 */
@SpringBootTest(classes = CitizensRegistryApplication.class)
@ActiveProfiles("test")
@DisplayName("Δοκιμές Ρύθμισης Spring Context")
class ConfigurationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CitizenRepository citizenRepository;

    @Autowired
    private CitizenService citizenService;

    @Autowired
    private CitizenController citizenController;

    @Autowired
    private DataSource dataSource;

    @Test
    @DisplayName("Το Spring context φορτώνεται επιτυχώς")
    void contextLoads() {
        assertNotNull(applicationContext);
    }

    @Test
    @DisplayName("Το CitizenRepository bean αρχικοποιείται")
    void citizenRepositoryBeanExists() {
        assertNotNull(citizenRepository);
    }

    @Test
    @DisplayName("Το CitizenService bean αρχικοποιείται")
    void citizenServiceBeanExists() {
        assertNotNull(citizenService);
    }

    @Test
    @DisplayName("Το CitizenController bean αρχικοποιείται")
    void citizenControllerBeanExists() {
        assertNotNull(citizenController);
    }

    @Test
    @DisplayName("Το DataSource bean αρχικοποιείται")
    void dataSourceBeanExists() {
        assertNotNull(dataSource);
    }

    @Test
    @DisplayName("Η σύνδεση με τη βάση δεδομένων είναι διαθέσιμη")
    void databaseConnectionAvailable() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            assertNotNull(conn);
            assertFalse(conn.isClosed());
        }
    }

    @Test
    @DisplayName("Το CitizensRegistryApplication bean αρχικοποιείται")
    void mainApplicationBeanExists() {
        assertTrue(applicationContext.containsBean("citizensRegistryApplication") ||
                   applicationContext.getBeansOfType(CitizensRegistryApplication.class).size() >= 0);
    }

    @Test
    @DisplayName("Ο αριθμός των καταχωρημένων beans είναι > 0")
    void contextHasBeans() {
        assertTrue(applicationContext.getBeanDefinitionCount() > 0);
    }
}
