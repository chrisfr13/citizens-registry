package gr.hua.dit.service.repository;

import gr.hua.dit.domain.model.Citizen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitizenRepository extends JpaRepository<Citizen, String> {

    @Query("SELECT c FROM Citizen c WHERE " +
           "(:idNumber IS NULL OR c.idNumber = :idNumber) AND " +
           "(:firstName IS NULL OR LOWER(c.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
           "(:lastName IS NULL OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
           "(:gender IS NULL OR c.gender = :gender) AND " +
           "(:birthDate IS NULL OR c.birthDate = :birthDate) AND " +
           "(:afm IS NULL OR c.afm = :afm) AND " +
           "(:address IS NULL OR LOWER(c.address) LIKE LOWER(CONCAT('%', :address, '%')))")
    List<Citizen> search(
            @Param("idNumber") String idNumber,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("gender") String gender,
            @Param("birthDate") String birthDate,
            @Param("afm") String afm,
            @Param("address") String address
    );
}
