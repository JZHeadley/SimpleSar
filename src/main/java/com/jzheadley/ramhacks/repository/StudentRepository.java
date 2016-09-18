package com.jzheadley.ramhacks.repository;

import com.jzheadley.ramhacks.domain.Student;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Student entity.
 */
public interface StudentRepository extends JpaRepository<Student,Long> {

}
