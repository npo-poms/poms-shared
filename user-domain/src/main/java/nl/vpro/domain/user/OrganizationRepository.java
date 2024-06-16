/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.util.List;

interface OrganizationRepository {

    <T extends Organization> List<T> findAll(Class<T> clazz);

    /**
     * @return {@code null} if the entity does not exist
     */
    <T extends Organization> T get(String id, Class<T> clazz);

    /**
     * @return {@code null} if the entity cannot be found
     */
    <T extends Organization> T getByProperty(String property, String value, Class<T> clazz);

    /**
     * @return {@code null} if the entity cannot be found
     */
    <T extends Organization> T getByPropertyIgnoreCase(String property, String value, Class<T> clazz);

    <T extends Organization> T merge(T broadcaster);

    <T extends Organization> void delete(T broadcaster);

}
