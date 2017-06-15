/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

import nl.vpro.domain.media.Person;
import nl.vpro.transfer.extjs.TransferList;

@XmlRootElement(name = "persons")
public class PersonList extends TransferList<PersonView>{

    public PersonList() {
    }

    public PersonList(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static PersonList create(List<Person> fullList) {
        PersonList simpleList = new PersonList();

        for(Person fullPerson : fullList) {
            if(fullPerson != null) {
                simpleList.add(PersonView.create(fullPerson));
            }
        }

        simpleList.success = true;

        return simpleList;
    }
}