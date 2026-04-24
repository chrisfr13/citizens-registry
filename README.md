# Μητρώο Πολιτών - RESTful Υπηρεσία

Maven multi-module project για διαχείριση μητρώου πολιτών μέσω Spring Boot.

## Δομή Έργου

```
citizens-registry/          ← Root Maven project
├── domain/                 ← Κλάσεις πεδίου (Citizen, UpdateRequest)
├── service/                ← RESTful υπηρεσία + JUnit tests
├── client/                 ← Κώδικας πελάτη (interactive menu)
└── integration-tests/      ← Δοκιμές ενοποίησης με REST-Assured
```

## Τεχνολογίες

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA / Hibernate
- H2 Database (embedded, αρχείο `citizensdb.mv.db`)
- JUnit 5 (unit & ORM tests)
- REST-Assured 5.4.0 (integration tests)
- Maven (multi-module)

## Endpoints

| Μέθοδος | URL | Λειτουργία |
|---------|-----|-----------|
| POST | `/api/citizens` | Εισαγωγή πολίτη |
| DELETE | `/api/citizens/{idNumber}` | Διαγραφή πολίτη |
| PUT | `/api/citizens/{idNumber}` | Ενημέρωση ΑΦΜ/Διεύθυνσης |
| GET | `/api/citizens/search` | Αναζήτηση με κριτήρια |
| GET | `/api/citizens/{idNumber}` | Εμφάνιση πολίτη |

## Εκτέλεση

### 1. Compile
```bash
mvn clean install -DskipTests
```

### 2. Εκκίνηση υπηρεσίας
```bash
cd service
mvn spring-boot:run
```
Η υπηρεσία εκκινεί στο http://localhost:8080  
H2 Console: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:file:./citizensdb`)

### 3. Εκτέλεση unit tests
```bash
cd service
mvn test
```

### 4. Εκτέλεση client (απαιτεί τρέχουσα υπηρεσία)
```bash
cd client
mvn spring-boot:run
```

### 5. Εκτέλεση integration tests (απαιτεί τρέχουσα υπηρεσία)
```bash
cd integration-tests
mvn failsafe:integration-test
```

## Παράδειγμα Χρήσης

### Εισαγωγή πολίτη
```bash
curl -X POST http://localhost:8080/api/citizens \
  -H "Content-Type: application/json" \
  -d '{
    "idNumber": "AB123456",
    "firstName": "Γιώργος",
    "lastName": "Παπαδόπουλος",
    "gender": "Άνδρας",
    "birthDate": "15-06-1990",
    "afm": "123456789",
    "address": "Αθήνα 1"
  }'
```

### Αναζήτηση
```bash
curl "http://localhost:8080/api/citizens/search?lastName=Παπαδόπουλος&gender=Άνδρας"
```

### Ενημέρωση
```bash
curl -X PUT http://localhost:8080/api/citizens/AB123456 \
  -H "Content-Type: application/json" \
  -d '{"afm": "987654321", "address": "Θεσσαλονίκη 5"}'
```

## Κανόνες Επικύρωσης

| Πεδίο | Κανόνας |
|-------|---------|
| ΑΤ | Υποχρεωτικό, ακριβώς 8 χαρακτήρες |
| Όνομα | Υποχρεωτικό |
| Επίθετο | Υποχρεωτικό |
| Φύλο | Υποχρεωτικό |
| Ημ. Γέννησης | Υποχρεωτικό, μορφή ΗΗ-ΜΜ-ΕΕΕΕ |
| ΑΦΜ | Προαιρετικό, ακριβώς 9 ψηφία |
| Διεύθυνση | Προαιρετικό |
