package com.jzheadley.ramhacks.web.rest;

import com.jzheadley.ramhacks.RamHacksApp;
import com.jzheadley.ramhacks.domain.FinancialData;
import com.jzheadley.ramhacks.repository.FinancialDataRepository;
import com.jzheadley.ramhacks.repository.search.FinancialDataSearchRepository;

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
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the FinancialDataResource REST controller.
 *
 * @see FinancialDataResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RamHacksApp.class)
@WebAppConfiguration
@IntegrationTest
public class FinancialDataResourceIntTest {


    private static final BigDecimal DEFAULT_OUTSTANDING_LOANDS = new BigDecimal(1);
    private static final BigDecimal UPDATED_OUTSTANDING_LOANDS = new BigDecimal(2);

    private static final BigDecimal DEFAULT_EFC_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_EFC_TOTAL = new BigDecimal(2);

    @Inject
    private FinancialDataRepository financialDataRepository;

    @Inject
    private FinancialDataSearchRepository financialDataSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restFinancialDataMockMvc;

    private FinancialData financialData;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FinancialDataResource financialDataResource = new FinancialDataResource();
        ReflectionTestUtils.setField(financialDataResource, "financialDataSearchRepository", financialDataSearchRepository);
        ReflectionTestUtils.setField(financialDataResource, "financialDataRepository", financialDataRepository);
        this.restFinancialDataMockMvc = MockMvcBuilders.standaloneSetup(financialDataResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        financialDataSearchRepository.deleteAll();
        financialData = new FinancialData();
        financialData.setOutstandingLoands(DEFAULT_OUTSTANDING_LOANDS);
        financialData.setEfcTotal(DEFAULT_EFC_TOTAL);
    }

    @Test
    @Transactional
    public void createFinancialData() throws Exception {
        int databaseSizeBeforeCreate = financialDataRepository.findAll().size();

        // Create the FinancialData

        restFinancialDataMockMvc.perform(post("/api/financial-data")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(financialData)))
                .andExpect(status().isCreated());

        // Validate the FinancialData in the database
        List<FinancialData> financialData = financialDataRepository.findAll();
        assertThat(financialData).hasSize(databaseSizeBeforeCreate + 1);
        FinancialData testFinancialData = financialData.get(financialData.size() - 1);
        assertThat(testFinancialData.getOutstandingLoands()).isEqualTo(DEFAULT_OUTSTANDING_LOANDS);
        assertThat(testFinancialData.getEfcTotal()).isEqualTo(DEFAULT_EFC_TOTAL);

        // Validate the FinancialData in ElasticSearch
        FinancialData financialDataEs = financialDataSearchRepository.findOne(testFinancialData.getId());
        assertThat(financialDataEs).isEqualToComparingFieldByField(testFinancialData);
    }

    @Test
    @Transactional
    public void checkOutstandingLoandsIsRequired() throws Exception {
        int databaseSizeBeforeTest = financialDataRepository.findAll().size();
        // set the field null
        financialData.setOutstandingLoands(null);

        // Create the FinancialData, which fails.

        restFinancialDataMockMvc.perform(post("/api/financial-data")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(financialData)))
                .andExpect(status().isBadRequest());

        List<FinancialData> financialData = financialDataRepository.findAll();
        assertThat(financialData).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEfcTotalIsRequired() throws Exception {
        int databaseSizeBeforeTest = financialDataRepository.findAll().size();
        // set the field null
        financialData.setEfcTotal(null);

        // Create the FinancialData, which fails.

        restFinancialDataMockMvc.perform(post("/api/financial-data")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(financialData)))
                .andExpect(status().isBadRequest());

        List<FinancialData> financialData = financialDataRepository.findAll();
        assertThat(financialData).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllFinancialData() throws Exception {
        // Initialize the database
        financialDataRepository.saveAndFlush(financialData);

        // Get all the financialData
        restFinancialDataMockMvc.perform(get("/api/financial-data?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(financialData.getId().intValue())))
                .andExpect(jsonPath("$.[*].outstandingLoands").value(hasItem(DEFAULT_OUTSTANDING_LOANDS.intValue())))
                .andExpect(jsonPath("$.[*].efcTotal").value(hasItem(DEFAULT_EFC_TOTAL.intValue())));
    }

    @Test
    @Transactional
    public void getFinancialData() throws Exception {
        // Initialize the database
        financialDataRepository.saveAndFlush(financialData);

        // Get the financialData
        restFinancialDataMockMvc.perform(get("/api/financial-data/{id}", financialData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(financialData.getId().intValue()))
            .andExpect(jsonPath("$.outstandingLoands").value(DEFAULT_OUTSTANDING_LOANDS.intValue()))
            .andExpect(jsonPath("$.efcTotal").value(DEFAULT_EFC_TOTAL.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingFinancialData() throws Exception {
        // Get the financialData
        restFinancialDataMockMvc.perform(get("/api/financial-data/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFinancialData() throws Exception {
        // Initialize the database
        financialDataRepository.saveAndFlush(financialData);
        financialDataSearchRepository.save(financialData);
        int databaseSizeBeforeUpdate = financialDataRepository.findAll().size();

        // Update the financialData
        FinancialData updatedFinancialData = new FinancialData();
        updatedFinancialData.setId(financialData.getId());
        updatedFinancialData.setOutstandingLoands(UPDATED_OUTSTANDING_LOANDS);
        updatedFinancialData.setEfcTotal(UPDATED_EFC_TOTAL);

        restFinancialDataMockMvc.perform(put("/api/financial-data")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedFinancialData)))
                .andExpect(status().isOk());

        // Validate the FinancialData in the database
        List<FinancialData> financialData = financialDataRepository.findAll();
        assertThat(financialData).hasSize(databaseSizeBeforeUpdate);
        FinancialData testFinancialData = financialData.get(financialData.size() - 1);
        assertThat(testFinancialData.getOutstandingLoands()).isEqualTo(UPDATED_OUTSTANDING_LOANDS);
        assertThat(testFinancialData.getEfcTotal()).isEqualTo(UPDATED_EFC_TOTAL);

        // Validate the FinancialData in ElasticSearch
        FinancialData financialDataEs = financialDataSearchRepository.findOne(testFinancialData.getId());
        assertThat(financialDataEs).isEqualToComparingFieldByField(testFinancialData);
    }

    @Test
    @Transactional
    public void deleteFinancialData() throws Exception {
        // Initialize the database
        financialDataRepository.saveAndFlush(financialData);
        financialDataSearchRepository.save(financialData);
        int databaseSizeBeforeDelete = financialDataRepository.findAll().size();

        // Get the financialData
        restFinancialDataMockMvc.perform(delete("/api/financial-data/{id}", financialData.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean financialDataExistsInEs = financialDataSearchRepository.exists(financialData.getId());
        assertThat(financialDataExistsInEs).isFalse();

        // Validate the database is empty
        List<FinancialData> financialData = financialDataRepository.findAll();
        assertThat(financialData).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchFinancialData() throws Exception {
        // Initialize the database
        financialDataRepository.saveAndFlush(financialData);
        financialDataSearchRepository.save(financialData);

        // Search the financialData
        restFinancialDataMockMvc.perform(get("/api/_search/financial-data?query=id:" + financialData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(financialData.getId().intValue())))
            .andExpect(jsonPath("$.[*].outstandingLoands").value(hasItem(DEFAULT_OUTSTANDING_LOANDS.intValue())))
            .andExpect(jsonPath("$.[*].efcTotal").value(hasItem(DEFAULT_EFC_TOTAL.intValue())));
    }
}
