package com.petster.app.service;

import com.petster.app.domain.Pet;
import com.petster.app.repository.PetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Service Implementation for managing Pet.
 */
@Service
@Transactional
public class PetService {

    private final Logger log = LoggerFactory.getLogger(PetService.class);
    
    @Inject
    private PetRepository petRepository;
    
    /**
     * Save a pet.
     * 
     * @param pet the entity to save
     * @return the persisted entity
     */
    public Pet save(Pet pet) {
        log.debug("Request to save Pet : {}", pet);
        Pet result = petRepository.save(pet);
        return result;
    }

    /**
     *  Get all the pets.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Pet> findAll(Pageable pageable) {
        log.debug("Request to get all Pets");
        Page<Pet> result = petRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one pet by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Pet findOne(Long id) {
        log.debug("Request to get Pet : {}", id);
        Pet pet = petRepository.findOne(id);
        return pet;
    }

    /**
     *  Delete the  pet by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Pet : {}", id);
        petRepository.delete(id);
    }
}
