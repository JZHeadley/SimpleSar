package com.jzheadley.ramhacks.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.jzheadley.ramhacks.domain.FinancialData;
import com.jzheadley.ramhacks.repository.FinancialDataRepository;
import com.jzheadley.ramhacks.repository.search.FinancialDataSearchRepository;
import com.jzheadley.ramhacks.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing FinancialData.
 */
@RestController
@RequestMapping("/api")
public class FinancialDataResource {

    private final Logger log = LoggerFactory.getLogger(FinancialDataResource.class);
        
    @Inject
    private FinancialDataRepository financialDataRepository;
    
    @Inject
    private FinancialDataSearchRepository financialDataSearchRepository;
    
    /**
     * POST  /financial-data : Create a new financialData.
     *
     * @param financialData the financialData to create
     * @return the ResponseEntity with status 201 (Created) and with body the new financialData, or with status 400 (Bad Request) if the financialData has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/financial-data",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<FinancialData> createFinancialData(@Valid @RequestBody FinancialData financialData) throws URISyntaxException {
        log.debug("REST request to save FinancialData : {}", financialData);
        if (financialData.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("financialData", "idexists", "A new financialData cannot already have an ID")).body(null);
        }
        FinancialData result = financialDataRepository.save(financialData);
        financialDataSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/financial-data/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("financialData", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /financial-data : Updates an existing financialData.
     *
     * @param financialData the financialData to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated financialData,
     * or with status 400 (Bad Request) if the financialData is not valid,
     * or with status 500 (Internal Server Error) if the financialData couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/financial-data",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<FinancialData> updateFinancialData(@Valid @RequestBody FinancialData financialData) throws URISyntaxException {
        log.debug("REST request to update FinancialData : {}", financialData);
        if (financialData.getId() == null) {
            return createFinancialData(financialData);
        }
        FinancialData result = financialDataRepository.save(financialData);
        financialDataSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("financialData", financialData.getId().toString()))
            .body(result);
    }

    /**
     * GET  /financial-data : get all the financialData.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of financialData in body
     */
    @RequestMapping(value = "/financial-data",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<FinancialData> getAllFinancialData() {
        log.debug("REST request to get all FinancialData");
        List<FinancialData> financialData = financialDataRepository.findAll();
        return financialData;
    }

    /**
     * GET  /financial-data/:id : get the "id" financialData.
     *
     * @param id the id of the financialData to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the financialData, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/financial-data/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<FinancialData> getFinancialData(@PathVariable Long id) {
        log.debug("REST request to get FinancialData : {}", id);
        FinancialData financialData = financialDataRepository.findOne(id);
        return Optional.ofNullable(financialData)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /financial-data/:id : delete the "id" financialData.
     *
     * @param id the id of the financialData to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/financial-data/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteFinancialData(@PathVariable Long id) {
        log.debug("REST request to delete FinancialData : {}", id);
        financialDataRepository.delete(id);
        financialDataSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("financialData", id.toString())).build();
    }

    /**
     * SEARCH  /_search/financial-data?query=:query : search for the financialData corresponding
     * to the query.
     *
     * @param query the query of the financialData search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/financial-data",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<FinancialData> searchFinancialData(@RequestParam String query) {
        log.debug("REST request to search FinancialData for query {}", query);
        return StreamSupport
            .stream(financialDataSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
