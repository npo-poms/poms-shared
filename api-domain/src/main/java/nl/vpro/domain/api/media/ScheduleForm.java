/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import java.util.function.Predicate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.Form;
import nl.vpro.domain.media.MediaObject;

/**
 *
 * @author Michiel Meeuwissen
 * @since 4.2
 */
@SuppressWarnings("WSReferenceInspection")         // Intellij warnings are incorrect since parent class is @XmlTransient
@XmlRootElement(name = "scheduleForm")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "scheduleFormType",
    propOrder = {
        "searches"
    })
public class ScheduleForm extends AbstractMediaForm implements Form, Predicate<MediaObject> {

    public static ScheduleForm from(MediaForm form) {
        ScheduleForm scheduleForm = new ScheduleForm();
        scheduleForm.setSearches(form.getSearches());
        scheduleForm.setHighlight(form.isHighlight());
        return scheduleForm;
    }


    @Override
    public boolean isFaceted() {
        return false;

    }
}
