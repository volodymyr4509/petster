package com.petster.app.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.petster.app.domain.Pet;
import com.petster.app.service.PetService;
import com.petster.app.web.rest.util.HeaderUtil;
import com.petster.app.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Pet.
 */
@RestController
@RequestMapping("/api")
public class PetResource {

    private final Logger log = LoggerFactory.getLogger(PetResource.class);
        
    @Inject
    private PetService petService;
    
    /**
     * POST  /pets : Create a new pet.
     *
     * @param pet the pet to create
     * @return the ResponseEntity with status 201 (Created) and with body the new pet, or with status 400 (Bad Request) if the pet has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/pets",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Pet> createPet(@RequestBody Pet pet) throws URISyntaxException {
        log.debug("REST request to save Pet : {}", pet);
        if (pet.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("pet", "idexists", "A new pet cannot already have an ID")).body(null);
        }
        Pet result = petService.save(pet);
        return ResponseEntity.created(new URI("/api/pets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("pet", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /pets : Updates an existing pet.
     *
     * @param pet the pet to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated pet,
     * or with status 400 (Bad Request) if the pet is not valid,
     * or with status 500 (Internal Server Error) if the pet couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/pets",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Pet> updatePet(@RequestBody Pet pet) throws URISyntaxException {
        log.debug("REST request to update Pet : {}", pet);
        if (pet.getId() == null) {
            return createPet(pet);
        }
        Pet result = petService.save(pet);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("pet", pet.getId().toString()))
            .body(result);
    }

    /**
     * GET  /pets : get all the pets.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of pets in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/pets",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Pet>> getAllPets(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Pets");
        Page<Pet> page = petService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/pets");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /pets/:id : get the "id" pet.
     *
     * @param id the id of the pet to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the pet, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/pets/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Pet> getPet(@PathVariable Long id) {
        log.debug("REST request to get Pet : {}", id);
        Pet pet = petService.findOne(id);
        return Optional.ofNullable(pet)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /pets/:id : delete the "id" pet.
     *
     * @param id the id of the pet to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/pets/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        log.debug("REST request to delete Pet : {}", id);
        petService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("pet", id.toString())).build();
    }

}
