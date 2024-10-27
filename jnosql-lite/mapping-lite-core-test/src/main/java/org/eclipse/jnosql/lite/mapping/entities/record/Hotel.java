package org.eclipse.jnosql.lite.mapping.entities.record;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

import java.util.Map;

@Entity
public record Hotel(@Id String document, @Column Map<String, String> socialMedias, @Column String[] cities) {
}
