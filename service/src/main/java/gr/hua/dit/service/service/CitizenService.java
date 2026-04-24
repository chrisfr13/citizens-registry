package gr.hua.dit.service.service;

import gr.hua.dit.domain.model.Citizen;
import gr.hua.dit.domain.model.UpdateRequest;
import gr.hua.dit.service.exception.CitizenNotFoundException;
import gr.hua.dit.service.repository.CitizenRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CitizenService {

    @Autowired
    private CitizenRepository citizenRepository;

    @Autowired
    private Validator validator;

    // INSERT
    @Transactional
    public Citizen insert(Citizen citizen) {
        validateCitizen(citizen);

        if (citizenRepository.existsById(citizen.getIdNumber())) {
            throw new IllegalArgumentException("Πολίτης με ΑΤ '" + citizen.getIdNumber() + "' υπάρχει ήδη στο μητρώο");
        }

        return citizenRepository.save(citizen);
    }

    // DELETE
    @Transactional
    public void delete(String idNumber) {
        validateIdNumber(idNumber);

        if (!citizenRepository.existsById(idNumber)) {
            throw new CitizenNotFoundException("Δεν βρέθηκε πολίτης με ΑΤ '" + idNumber + "'");
        }

        citizenRepository.deleteById(idNumber);
    }

    // UPDATE (only AFM and address)
    @Transactional
    public Citizen update(String idNumber, UpdateRequest updateRequest) {
        validateIdNumber(idNumber);

        Set<ConstraintViolation<UpdateRequest>> violations = validator.validate(updateRequest);
        if (!violations.isEmpty()) {
            String messages = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException(messages);
        }

        Citizen citizen = citizenRepository.findById(idNumber)
                .orElseThrow(() -> new CitizenNotFoundException("Δεν βρέθηκε πολίτης με ΑΤ '" + idNumber + "'"));

        if (updateRequest.getAfm() != null) {
            citizen.setAfm(updateRequest.getAfm());
        }
        if (updateRequest.getAddress() != null) {
            citizen.setAddress(updateRequest.getAddress());
        }

        return citizenRepository.save(citizen);
    }

    // SEARCH
    @Transactional(readOnly = true)
    public List<Citizen> search(String idNumber, String firstName, String lastName,
                                String gender, String birthDate, String afm, String address) {
        if (idNumber != null && !idNumber.isBlank() && idNumber.length() != 8) {
            throw new IllegalArgumentException("Ο ΑΤ πρέπει να αποτελείται από ακριβώς 8 χαρακτήρες");
        }
        if (birthDate != null && !birthDate.isBlank()) {
            if (!birthDate.matches("^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-\\d{4}$")) {
                throw new IllegalArgumentException("Η ημερομηνία γέννησης πρέπει να είναι της μορφής ΗΗ-ΜΜ-ΕΕΕΕ");
            }
        }
        if (afm != null && !afm.isBlank()) {
            if (!afm.matches("^\\d{9}$")) {
                throw new IllegalArgumentException("Το ΑΦΜ πρέπει να αποτελείται από 9 ψηφία");
            }
        }

        String idNumberParam  = (idNumber  != null && !idNumber.isBlank())  ? idNumber  : null;
        String firstNameParam = (firstName != null && !firstName.isBlank()) ? firstName : null;
        String lastNameParam  = (lastName  != null && !lastName.isBlank())  ? lastName  : null;
        String genderParam    = (gender    != null && !gender.isBlank())    ? gender    : null;
        String birthDateParam = (birthDate != null && !birthDate.isBlank()) ? birthDate : null;
        String afmParam       = (afm       != null && !afm.isBlank())       ? afm       : null;
        String addressParam   = (address   != null && !address.isBlank())   ? address   : null;

        return citizenRepository.search(idNumberParam, firstNameParam, lastNameParam,
                genderParam, birthDateParam, afmParam, addressParam);
    }

    // GET by ID
    @Transactional(readOnly = true)
    public Citizen getById(String idNumber) {
        validateIdNumber(idNumber);

        return citizenRepository.findById(idNumber)
                .orElseThrow(() -> new CitizenNotFoundException("Δεν βρέθηκε πολίτης με ΑΤ '" + idNumber + "'"));
    }

    private void validateCitizen(Citizen citizen) {
        Set<ConstraintViolation<Citizen>> violations = validator.validate(citizen);
        if (!violations.isEmpty()) {
            String messages = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException(messages);
        }
    }

    private void validateIdNumber(String idNumber) {
        if (idNumber == null || idNumber.isBlank()) {
            throw new IllegalArgumentException("Ο ΑΤ δεν μπορεί να είναι κενός");
        }
        if (idNumber.length() != 8) {
            throw new IllegalArgumentException("Ο ΑΤ πρέπει να αποτελείται από ακριβώς 8 χαρακτήρες");
        }
    }
}
