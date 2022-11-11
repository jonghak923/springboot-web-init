package com.jonghak.springbootweb.sample;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class PersonEntity {

    @Id @GeneratedValue
    private Long id;

    private String name;
}
