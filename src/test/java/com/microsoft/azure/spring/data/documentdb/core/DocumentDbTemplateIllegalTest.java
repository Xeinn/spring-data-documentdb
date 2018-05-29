/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.documentdb.core;

import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.documentdb.PartitionKey;
import com.microsoft.azure.spring.data.documentdb.DocumentDbFactory;
import com.microsoft.azure.spring.data.documentdb.common.TestConstants;
import com.microsoft.azure.spring.data.documentdb.core.convert.MappingDocumentDbConverter;
import com.microsoft.azure.spring.data.documentdb.core.query.Query;
import com.microsoft.azure.spring.data.documentdb.domain.Person;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RunWith(MockitoJUnitRunner.class)
public class DocumentDbTemplateIllegalTest {
    private static final String NULL_STR = null;
    private static final String DUMMY_COLL = "dummy";
    private static final String DUMMY_ID = "ID";
    private static final PartitionKey DUMMY_KEY = new PartitionKey("dummy");
    private static final String EMPTY_STR = StringUtils.EMPTY;
    private static final String WHITESPACES_STR = "  ";
    private static final String CHECK_FAILURE_MSG = "Illegal argument is not checked";

    private DocumentDbTemplate dbTemplate;
    private Class dbTemplateClass;

    @Mock
    DocumentClient documentClient;

    @Mock
    MappingDocumentDbConverter dbConverter;

    @Before
    public void setUp() {
        this.dbTemplate = new DocumentDbTemplate(new DocumentDbFactory(documentClient), dbConverter,
                TestConstants.DB_NAME);
        dbTemplateClass = dbTemplate.getClass();
    }

    @Test
    public void deleteIllegalShouldFail() throws NoSuchMethodException {
        final Method method = dbTemplateClass.getMethod("delete", Query.class, Class.class, String.class);

        checkIllegalArgument(method, null, Person.class, DUMMY_COLL);
        checkIllegalArgument(method, new Query(), null, DUMMY_COLL);
        checkIllegalArgument(method, new Query(), Person.class, null);
    }

    @Test
    public void deleteIllegalCollectionShouldFail() throws NoSuchMethodException {
        final Method method = dbTemplateClass.getDeclaredMethod("deleteAll", String.class);

        checkIllegalArgument(method, NULL_STR);
        checkIllegalArgument(method, EMPTY_STR);
        checkIllegalArgument(method, WHITESPACES_STR);
    }

    @Test
    public void deleteByIdIllegalArgsShouldFail() throws NoSuchMethodException {
        final Method method = dbTemplateClass.getDeclaredMethod("deleteById", String.class, Object.class,
                PartitionKey.class);

        // Test argument collectionName
        checkIllegalArgument(method, null, DUMMY_ID, DUMMY_KEY);
        checkIllegalArgument(method, EMPTY_STR, DUMMY_ID, DUMMY_KEY);
        checkIllegalArgument(method, WHITESPACES_STR, DUMMY_ID, DUMMY_KEY);

        // Test argument id
        checkIllegalArgument(method, DUMMY_COLL, null, DUMMY_KEY);
        checkIllegalArgument(method, DUMMY_COLL, EMPTY_STR, DUMMY_KEY);
        checkIllegalArgument(method, DUMMY_COLL, WHITESPACES_STR, DUMMY_KEY);
    }

    @Test
    public void findByIdIllegalArgsShouldFail() throws NoSuchMethodException {
        final Method method = dbTemplateClass.getDeclaredMethod("findById", Object.class, Class.class);

        checkIllegalArgument(method, null, Person.class);
        checkIllegalArgument(method, EMPTY_STR, Person.class);
        checkIllegalArgument(method, WHITESPACES_STR, Person.class);
    }

    @Test
    public void findByCollIdIllegalArgsShouldFail() throws NoSuchMethodException {
        final Method method = dbTemplateClass.getDeclaredMethod("findById", String.class,
                Object.class, Class.class);

        checkIllegalArgument(method, DUMMY_COLL, null, Person.class);
        checkIllegalArgument(method, DUMMY_COLL, EMPTY_STR, Person.class);
        checkIllegalArgument(method, DUMMY_COLL, WHITESPACES_STR, Person.class);
    }

    /**
     * Check IllegalArgumentException is thrown for illegal parameters
     * @param method
     * @param args Method invocation parameters
     */
    private void checkIllegalArgument(Method method, Object... args) {
        try {
            method.invoke(dbTemplate, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            Assert.isTrue(e.getCause() instanceof IllegalArgumentException, CHECK_FAILURE_MSG);
            return; // Test passed
        }

        throw new IllegalStateException(CHECK_FAILURE_MSG, null);
    }
}