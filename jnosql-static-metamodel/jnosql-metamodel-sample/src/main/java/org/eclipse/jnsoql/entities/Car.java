package org.eclipse.jnsoql.entities;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

@Entity
public class Car {

    @Id
    private String id;

    @Column
    private Person driver;
}
