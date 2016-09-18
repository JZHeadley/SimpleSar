package com.jzheadley.ramhacks.web.rest;

import com.jzheadley.ramhacks.RamHacksApp;
import com.jzheadley.ramhacks.domain.Parent;
import com.jzheadley.ramhacks.repository.ParentRepository;
import com.jzheadley.ramhacks.repository.search.ParentSearchRepository;

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
 * Test class for the ParentResource REST controller.
 *
 * @see ParentResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RamHacksApp.class)
@WebAppConfiguration
@IntegrationTest
public class ParentResourceIntTest {

    private static final String DEFAULT_LAST_NAME = "AAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBB";
    private static final String DEFAULT_MIDDLE_INITIAL = "AAAAA";
    private static final String UPDATED_MIDDLE_INITIAL = "BBBBB";
    private static final String DEFAULT_SSN = "AAAAA";
    private static final String UPDATED_SSN = "BBBBB";

    @Inject
    private ParentRepository parentRepository;

    @Inject
    private ParentSearchRepository parentSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restParentMockMvc;

    private Parent parent;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ParentResource parentResource = new ParentResource();
        ReflectionTestUtils.setField(parentResource, "parentSearchRepository", parentSearchRepository);
        ReflectionTestUtils.setField(parentResource, "parentRepository", parentRepository);
        this.restParentMockMvc = MockMvcBuilders.standaloneSetup(parentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        parentSearchRepository.deleteAll();
        parent = new Parent();
        parent.setLastName(DEFAULT_LAST_NAME);
        parent.setMiddleInitial(DEFAULT_MIDDLE_INITIAL);
        parent.setSsn(DEFAULT_SSN);
    }

    @Test
    @Transactional
    public void createParent() throws Exception {
        int databaseSizeBeforeCreate = parentRepository.findAll().size();

        // Create the Parent

        restParentMockMvc.perform(post("/api/parents")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(parent)))
                .andExpect(status().isCreated());

        // Validate the Parent in the database
        List<Parent> parents = parentRepository.findAll();
        assertThat(parents).hasSize(databaseSizeBeforeCreate + 1);
        Parent testParent = parents.get(parents.size() - 1);
        assertThat(testParent.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testParent.getMiddleInitial()).isEqualTo(DEFAULT_MIDDLE_INITIAL);
        assertThat(testParent.getSsn()).isEqualTo(DEFAULT_SSN);

        // Validate the Parent in ElasticSearch
        Parent parentEs = parentSearchRepository.findOne(testParent.getId());
        assertThat(parentEs).isEqualToComparingFieldByField(testParent);
    }

    @Test
    @Transactional
    public void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = parentRepository.findAll().size();
        // set the field null
        parent.setLastName(null);

        // Create the Parent, which fails.

        restParentMockMvc.perform(post("/api/parents")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(parent)))
                .andExpect(status().isBadRequest());

        List<Parent> parents = parentRepository.findAll();
        assertThat(parents).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSsnIsRequired() throws Exception {
        int databaseSizeBeforeTest = parentRepository.findAll().size();
        // set the field null
        parent.setSsn(null);

        // Create the Parent, which fails.

        restParentMockMvc.perform(post("/api/parents")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(parent)))
                .andExpect(status().isBadRequest());

        List<Parent> parents = parentRepository.findAll();
        assertThat(parents).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllParents() throws Exception {
        // Initialize the database
        parentRepository.saveAndFlush(parent);

        // Get all the parents
        restParentMockMvc.perform(get("/api/parents?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(parent.getId().intValue())))
                .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME.toString())))
                .andExpect(jsonPath("$.[*].middleInitial").value(hasItem(DEFAULT_MIDDLE_INITIAL.toString())))
                .andExpect(jsonPath("$.[*].ssn").value(hasItem(DEFAULT_SSN.toString())));
    }

    @Test
    @Transactional
    public void getParent() throws Exception {
        // Initialize the database
        parentRepository.saveAndFlush(parent);

        // Get the parent
        restParentMockMvc.perform(get("/api/parents/{id}", parent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(parent.getId().intValue()))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME.toString()))
            .andExpect(jsonPath("$.middleInitial").value(DEFAULT_MIDDLE_INITIAL.toString()))
            .andExpect(jsonPath("$.ssn").value(DEFAULT_SSN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingParent() throws Exception {
        // Get the parent
        restParentMockMvc.perform(get("/api/parents/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateParent() throws Exception {
        // Initialize the database
        parentRepository.saveAndFlush(parent);
        parentSearchRepository.save(parent);
        int databaseSizeBeforeUpdate = parentRepository.findAll().size();

        // Update the parent
        Parent updatedParent = new Parent();
        updatedParent.setId(parent.getId());
        updatedParent.setLastName(UPDATED_LAST_NAME);
        updatedParent.setMiddleInitial(UPDATED_MIDDLE_INITIAL);
        updatedParent.setSsn(UPDATED_SSN);

        restParentMockMvc.perform(put("/api/parents")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedParent)))
                .andExpect(status().isOk());

        // Validate the Parent in the database
        List<Parent> parents = parentRepository.findAll();
        assertThat(parents).hasSize(databaseSizeBeforeUpdate);
        Parent testParent = parents.get(parents.size() - 1);
        assertThat(testParent.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testParent.getMiddleInitial()).isEqualTo(UPDATED_MIDDLE_INITIAL);
        assertThat(testParent.getSsn()).isEqualTo(UPDATED_SSN);

        // Validate the Parent in ElasticSearch
        Parent parentEs = parentSearchRepository.findOne(testParent.getId());
        assertThat(parentEs).isEqualToComparingFieldByField(testParent);
    }

    @Test
    @Transactional
    public void deleteParent() throws Exception {
        // Initialize the database
        parentRepository.saveAndFlush(parent);
        parentSearchRepository.save(parent);
        int databaseSizeBeforeDelete = parentRepository.findAll().size();

        // Get the parent
        restParentMockMvc.perform(delete("/api/parents/{id}", parent.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean parentExistsInEs = parentSearchRepository.exists(parent.getId());
        assertThat(parentExistsInEs).isFalse();

        // Validate the database is empty
        List<Parent> parents = parentRepository.findAll();
        assertThat(parents).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchParent() throws Exception {
        // Initialize the database
        parentRepository.saveAndFlush(parent);
        parentSearchRepository.save(parent);

        // Search the parent
        restParentMockMvc.perform(get("/api/_search/parents?query=id:" + parent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parent.getId().intValue())))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME.toString())))
            .andExpect(jsonPath("$.[*].middleInitial").value(hasItem(DEFAULT_MIDDLE_INITIAL.toString())))
            .andExpect(jsonPath("$.[*].ssn").value(hasItem(DEFAULT_SSN.toString())));
    }
}
