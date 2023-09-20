/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.page;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.AbstractRelationSearch;

/**
 * @author Michiel Meeuwissen
 * @since 4.2
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pageRelationSearchType", propOrder = {"types", "broadcasters", "values", "uriRefs"})
public class RelationSearch extends AbstractRelationSearch  {


}
