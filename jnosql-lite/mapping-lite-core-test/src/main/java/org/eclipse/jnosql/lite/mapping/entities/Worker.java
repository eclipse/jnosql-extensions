package org.eclipse.jnosql.lite.mapping.entities;

import jakarta.nosql.Entity;
import jakarta.nosql.Id;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Convert;

@Entity
public class Worker extends Person {

    @Column
    @Convert(MoneyConverter.class)
    private Money salary;

    public Money getSalary() {
        return salary;
    }

    public void setSalary(Money salary) {
        this.salary = salary;
    }
}