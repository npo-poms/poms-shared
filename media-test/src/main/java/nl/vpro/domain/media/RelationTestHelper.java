/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

public class RelationTestHelper {
    public static Relation getValidRelation() {
        final Relation relation = new Relation(new RelationDefinition("AAAA", "a", "a"));
        relation.setId(1L);
        return relation;
    }

}
