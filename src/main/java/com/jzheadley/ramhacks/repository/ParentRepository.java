package com.jzheadley.ramhacks.repository;

import com.jzheadley.ramhacks.domain.Parent;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Parent entity.
 */
public interface ParentRepository extends JpaRepository<Parent,Long> {

}
