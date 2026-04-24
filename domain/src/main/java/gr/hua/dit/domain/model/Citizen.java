package gr.hua.dit.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "citizens")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Citizen {

    @Id
    @Column(name = "id_number", length = 8, nullable = false, unique = true)
    @NotBlank(message = "ΑΤ είναι υποχρεωτικό")
    @Size(min = 8, max = 8, message = "Ο ΑΤ πρέπει να αποτελείται από ακριβώς 8 χαρακτήρες")
    private String idNumber;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "Όνομα είναι υποχρεωτικό")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "Επίθετο είναι υποχρεωτικό")
    private String lastName;

    @Column(name = "gender", nullable = false)
    @NotBlank(message = "Φύλο είναι υποχρεωτικό")
    private String gender;

    @Column(name = "birth_date", nullable = false)
    @NotBlank(message = "Ημερομηνία γέννησης είναι υποχρεωτική")
    @Pattern(
        regexp = "^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-\\d{4}$",
        message = "Η ημερομηνία γέννησης πρέπει να είναι της μορφής ΗΗ-ΜΜ-ΕΕΕΕ (πχ. 12-11-2008)"
    )
    private String birthDate;

    @Column(name = "afm", length = 9)
    @Pattern(
        regexp = "^\\d{9}$",
        message = "Το ΑΦΜ πρέπει να αποτελείται από 9 ψηφία"
    )
    private String afm;

    @Column(name = "address")
    private String address;

    // Constructors
    public Citizen() {}

    public Citizen(String idNumber, String firstName, String lastName, String gender, String birthDate) {
        this.idNumber = idNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    public Citizen(String idNumber, String firstName, String lastName, String gender,
                   String birthDate, String afm, String address) {
        this.idNumber = idNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.afm = afm;
        this.address = address;
    }

    // Getters and Setters
    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public String getAfm() { return afm; }
    public void setAfm(String afm) { this.afm = afm; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return "Citizen{" +
                "idNumber='" + idNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", afm='" + afm + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Citizen)) return false;
        Citizen citizen = (Citizen) o;
        return idNumber != null && idNumber.equals(citizen.idNumber);
    }

    @Override
    public int hashCode() {
        return idNumber != null ? idNumber.hashCode() : 0;
    }
}
