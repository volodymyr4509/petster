package com.petster.app.repository;

import com.petster.app.domain.Pet;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Pet entity.
 */
@SuppressWarnings("unused")
public interface PetRepository extends JpaRepository<Pet,Long> {

    @Query("select pet from Pet pet where pet.user.login = ?#{principal.username}")
    List<Pet> findByUserIsCurrentUser();

}
