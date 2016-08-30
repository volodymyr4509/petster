package com.petster.app.web.rest;

import com.petster.app.FourthApp;
import com.petster.app.domain.Pet;
import com.petster.app.repository.PetRepository;
import com.petster.app.service.PetService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the PetResource REST controller.
 *
 * @see PetResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FourthApp.class)
@WebAppConfiguration
@IntegrationTest
public class PetResourceIntTest {

    private static final String DEFAULT_BREED = "AAAAA";
    private static final String UPDATED_BREED = "BBBBB";

    private static final Integer DEFAULT_AGE = 1;
    private static final Integer UPDATED_AGE = 2;

    @Inject
    private PetRepository petRepository;

    @Inject
    private PetService petService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restPetMockMvc;

    private Pet pet;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PetResource petResource = new PetResource();
        ReflectionTestUtils.setField(petResource, "petService", petService);
        this.restPetMockMvc = MockMvcBuilders.standaloneSetup(petResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        pet = new Pet();
        pet.setBreed(DEFAULT_BREED);
        pet.setAge(DEFAULT_AGE);
    }

    @Test
    @Transactional
    public void createPet() throws Exception {
        int databaseSizeBeforeCreate = petRepository.findAll().size();

        // Create the Pet

        restPetMockMvc.perform(post("/api/pets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pet)))
                .andExpect(status().isCreated());

        // Validate the Pet in the database
        List<Pet> pets = petRepository.findAll();
        assertThat(pets).hasSize(databaseSizeBeforeCreate + 1);
        Pet testPet = pets.get(pets.size() - 1);
        assertThat(testPet.getBreed()).isEqualTo(DEFAULT_BREED);
        assertThat(testPet.getAge()).isEqualTo(DEFAULT_AGE);
    }

    @Test
    @Transactional
    public void getAllPets() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get all the pets
        restPetMockMvc.perform(get("/api/pets?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(pet.getId().intValue())))
                .andExpect(jsonPath("$.[*].breed").value(hasItem(DEFAULT_BREED.toString())))
                .andExpect(jsonPath("$.[*].age").value(hasItem(DEFAULT_AGE)));
    }

    @Test
    @Transactional
    public void getPet() throws Exception {
        // Initialize the database
        petRepository.saveAndFlush(pet);

        // Get the pet
        restPetMockMvc.perform(get("/api/pets/{id}", pet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(pet.getId().intValue()))
            .andExpect(jsonPath("$.breed").value(DEFAULT_BREED.toString()))
            .andExpect(jsonPath("$.age").value(DEFAULT_AGE));
    }

    @Test
    @Transactional
    public void getNonExistingPet() throws Exception {
        // Get the pet
        restPetMockMvc.perform(get("/api/pets/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePet() throws Exception {
        // Initialize the database
        petService.save(pet);

        int databaseSizeBeforeUpdate = petRepository.findAll().size();

        // Update the pet
        Pet updatedPet = new Pet();
        updatedPet.setId(pet.getId());
        updatedPet.setBreed(UPDATED_BREED);
        updatedPet.setAge(UPDATED_AGE);

        restPetMockMvc.perform(put("/api/pets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedPet)))
                .andExpect(status().isOk());

        // Validate the Pet in the database
        List<Pet> pets = petRepository.findAll();
        assertThat(pets).hasSize(databaseSizeBeforeUpdate);
        Pet testPet = pets.get(pets.size() - 1);
        assertThat(testPet.getBreed()).isEqualTo(UPDATED_BREED);
        assertThat(testPet.getAge()).isEqualTo(UPDATED_AGE);
    }

    @Test
    @Transactional
    public void deletePet() throws Exception {
        // Initialize the database
        petService.save(pet);

        int databaseSizeBeforeDelete = petRepository.findAll().size();

        // Get the pet
        restPetMockMvc.perform(delete("/api/pets/{id}", pet.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Pet> pets = petRepository.findAll();
        assertThat(pets).hasSize(databaseSizeBeforeDelete - 1);
    }
}
