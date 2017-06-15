/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.Person;
import nl.vpro.domain.media.RoleType;
import nl.vpro.transfer.extjs.ExtRecord;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "id",
        "givenName",
        "familyName",
        "role"
        })
public class PersonView extends ExtRecord {

    private Long id;

    private String givenName;

    private String familyName;

    private String role;

    private PersonView() {
    }

    private PersonView(Long id, String givenName, String familyName, String role) {
        this.id = id;
        this.givenName = givenName;
        this.familyName = familyName;
        this.role = role;
    }

    public static PersonView create(Person fullPerson) {
        return new PersonView(
                fullPerson.getId(),
                fullPerson.getGivenName(),
                fullPerson.getFamilyName(),
                fullPerson.getRole().toString()
        );
    }

    public Person toPerson() {
        Person person = new Person();
        person.setId(this.id);
        return updateTo(person);
    }

    public Person updateTo(Person fullPerson) {
        fullPerson.setGivenName(this.givenName);
        fullPerson.setFamilyName(this.familyName);
        fullPerson.setRole(RoleType.fromToString(this.role));

        return fullPerson;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
