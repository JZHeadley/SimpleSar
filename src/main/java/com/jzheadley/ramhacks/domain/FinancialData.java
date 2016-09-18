package com.jzheadley.ramhacks.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A FinancialData.
 */
@Entity
@Table(name = "financial_data")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "financialdata")
public class FinancialData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "outstanding_loands", precision=10, scale=2, nullable = false)
    private BigDecimal outstandingLoands;

    @NotNull
    @Column(name = "efc_total", precision=10, scale=2, nullable = false)
    private BigDecimal efcTotal;

    @OneToOne
    @JoinColumn(unique = true)
    private Student student;

    @OneToOne
    @JoinColumn(unique = true)
    private Parent parent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getOutstandingLoands() {
        return outstandingLoands;
    }

    public void setOutstandingLoands(BigDecimal outstandingLoands) {
        this.outstandingLoands = outstandingLoands;
    }

    public BigDecimal getEfcTotal() {
        return efcTotal;
    }

    public void setEfcTotal(BigDecimal efcTotal) {
        this.efcTotal = efcTotal;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FinancialData financialData = (FinancialData) o;
        if(financialData.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, financialData.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "FinancialData{" +
            "id=" + id +
            ", outstandingLoands='" + outstandingLoands + "'" +
            ", efcTotal='" + efcTotal + "'" +
            '}';
    }
}
