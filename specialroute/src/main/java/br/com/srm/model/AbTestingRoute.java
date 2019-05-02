package br.com.srm.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "abtesting")
public class AbTestingRoute {

    @Id
    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name="active", nullable = false)
    private String active;

    @Column(name = "endpoint", nullable = false)
    private String endpoint;

    @Column(name = "weight", nullable = false)
    private Integer weight;

}
