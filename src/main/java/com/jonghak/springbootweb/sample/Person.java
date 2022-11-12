package com.jonghak.springbootweb.sample;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Getter @Setter @ToString
public class Person {

    private Long id;

    private String name;

}
