package com.jzheadley.ramhacks.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.jzheadley.ramhacks.domain.Parent;
import com.jzheadley.ramhacks.repository.ParentRepository;
import com.jzheadley.ramhacks.repository.search.ParentSearchRepository;
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
 * REST controller for managing Parent.
 */
@RestController
@RequestMapping("/api")
public class ParentResource {

    private final Logger log = LoggerFactory.getLogger(ParentResource.class);
        
    @Inject
    private ParentRepository parentRepository;
    
    @Inject
    private ParentSearchRepository parentSearchRepository;
    
    /**
     * POST  /parents : Create a new parent.
     *
     * @param parent the parent to create
     * @return the ResponseEntity with status 201 (Created) and with body the new parent, or with status 400 (Bad Request) if the parent has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/parents",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Parent> createParent(@Valid @RequestBody Parent parent) throws URISyntaxException {
        log.debug("REST request to save Parent : {}", parent);
        if (parent.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("parent", "idexists", "A new parent cannot already have an ID")).body(null);
        }
        Parent result = parentRepository.save(parent);
        parentSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/parents/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("parent", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /parents : Updates an existing parent.
     *
     * @param parent the parent to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated parent,
     * or with status 400 (Bad Request) if the parent is not valid,
     * or with status 500 (Internal Server Error) if the parent couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/parents",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Parent> updateParent(@Valid @RequestBody Parent parent) throws URISyntaxException {
        log.debug("REST request to update Parent : {}", parent);
        if (parent.getId() == null) {
            return createParent(parent);
        }
        Parent result = parentRepository.save(parent);
        parentSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("parent", parent.getId().toString()))
            .body(result);
    }

    /**
     * GET  /parents : get all the parents.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of parents in body
     */
    @RequestMapping(value = "/parents",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Parent> getAllParents() {
        log.debug("REST request to get all Parents");
        List<Parent> parents = parentRepository.findAll();
        return parents;
    }

    /**
     * GET  /parents/:id : get the "id" parent.
     *
     * @param id the id of the parent to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the parent, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/parents/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Parent> getParent(@PathVariable Long id) {
        log.debug("REST request to get Parent : {}", id);
        Parent parent = parentRepository.findOne(id);
        return Optional.ofNullable(parent)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /parents/:id : delete the "id" parent.
     *
     * @param id the id of the parent to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/parents/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteParent(@PathVariable Long id) {
        log.debug("REST request to delete Parent : {}", id);
        parentRepository.delete(id);
        parentSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("parent", id.toString())).build();
    }

    /**
     * SEARCH  /_search/parents?query=:query : search for the parent corresponding
     * to the query.
     *
     * @param query the query of the parent search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/parents",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Parent> searchParents(@RequestParam String query) {
        log.debug("REST request to search Parents for query {}", query);
        return StreamSupport
            .stream(parentSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
