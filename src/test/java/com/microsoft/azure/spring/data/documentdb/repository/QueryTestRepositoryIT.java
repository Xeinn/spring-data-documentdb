/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.documentdb.repository;

import com.microsoft.azure.spring.data.documentdb.common.TestConstants;
import com.microsoft.azure.spring.data.documentdb.common.TestUtils;
import com.microsoft.azure.spring.data.documentdb.domain.Memo;
import com.microsoft.azure.spring.data.documentdb.domain.QueryTest;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestRepositoryConfig.class)
public class QueryTestRepositoryIT {
    private static final String ID_VALUE_1 = "ID1";
    private static final String ID_VALUE_2 = "ID2";
    private static final String ID_VALUE_3 = "ID3";

    private static final String MESSAGE_VALUE_1 = "message 1";
    private static final String MESSAGE_VALUE_2 = "Message 2";
    private static final String MESSAGE_VALUE_3 = "My message 3";

    private static final String MESSAGE_CONTAINING_TEST = "message";
    private static final String MESSAGE_ENDSWITH_TEST = "ge 2";
//    private static final String MESSAGE_STARTSWITH_TEST = "My m";
    
    
    private static Date dateValue1;
    private static Date dateValue2;
    private static Date dateValue3;
    
    private static QueryTest testRecord1;
    private static QueryTest testRecord2;
    private static QueryTest testRecord3;


    @Autowired
    QueryTestRepository repository;

    @BeforeClass
    public static void init() throws ParseException {

        final SimpleDateFormat dateFormat = new SimpleDateFormat(TestConstants.DATE_FORMAT);
        
        dateValue1 = dateFormat.parse("1/1/2000");
        dateValue2 = dateFormat.parse("1/1/2001");
        dateValue3 = dateFormat.parse("1/1/2002");

        testRecord1 = new QueryTest(ID_VALUE_1, MESSAGE_VALUE_1, dateValue1, "a value");
        testRecord2 = new QueryTest(ID_VALUE_2, MESSAGE_VALUE_2, dateValue2, "");
        testRecord3 = new QueryTest(ID_VALUE_3, MESSAGE_VALUE_3, dateValue3, null);
    }

    @Before
    public void setup() {
        repository.save(testRecord1);
        repository.save(testRecord2);
        repository.save(testRecord3);
    }

    @After
    public void cleanup() {
        repository.deleteAll();
    }

    @Test
    public void testAndCondition() {
    }

    @Test
    public void testOrCondition() {
    }

    @Test
    public void testIsEqualForString() {

        final List<QueryTest> result = repository.findQueryTestByMessage(MESSAGE_VALUE_1);
        
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(ID_VALUE_1);
    }

    @Test
    public void testIsNotEqual() {

        final List<QueryTest> result = repository.findQueryTestByMessageNot(MESSAGE_VALUE_1);
        
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getId()).isNotEqualTo(ID_VALUE_1);
        assertThat(result.get(1).getId()).isNotEqualTo(ID_VALUE_1);
    }

    @Test
    public void testIsLessThan() {

        final List<QueryTest> result = repository.findQueryTestByIdLessThan(ID_VALUE_3);
        
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getMessage()).isNotEqualTo(MESSAGE_VALUE_3);
        assertThat(result.get(1).getMessage()).isNotEqualTo(MESSAGE_VALUE_3);
    }

    @Test
    public void testIsLessThanOrEqual() {

        final List<QueryTest> result = repository.findQueryTestByIdLessThanEqual(ID_VALUE_2);
        
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getMessage().equals(MESSAGE_VALUE_1)
                || result.get(0).getMessage().equals(MESSAGE_VALUE_2)).isTrue();
        assertThat(result.get(1).getMessage().equals(MESSAGE_VALUE_1)
                || result.get(1).getMessage().equals(MESSAGE_VALUE_2)).isTrue();
    }

    @Test
    public void testIsGreaterThan() {

        final List<QueryTest> result = repository.findQueryTestByIdGreaterThan(ID_VALUE_1);
        
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getMessage().equals(MESSAGE_VALUE_2)
                || result.get(0).getMessage().equals(MESSAGE_VALUE_3)).isTrue();
        assertThat(result.get(1).getMessage().equals(MESSAGE_VALUE_2)
                || result.get(1).getMessage().equals(MESSAGE_VALUE_3)).isTrue();
    }

    @Test
    public void testIsGreaterThanOrEqual() {

        final List<QueryTest> result = repository.findQueryTestByIdGreaterThanEqual(ID_VALUE_2);
        
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getMessage().equals(MESSAGE_VALUE_2)
                || result.get(0).getMessage().equals(MESSAGE_VALUE_3)).isTrue();
        assertThat(result.get(1).getMessage().equals(MESSAGE_VALUE_2)
                || result.get(1).getMessage().equals(MESSAGE_VALUE_3)).isTrue();
    }

    @Test
    public void testBetween() {

        final List<QueryTest> result = repository.findQueryTestByIdBetween(ID_VALUE_1, ID_VALUE_2);
        
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getMessage().equals(MESSAGE_VALUE_1)
                || result.get(0).getMessage().equals(MESSAGE_VALUE_2)).isTrue();
        assertThat(result.get(1).getMessage().equals(MESSAGE_VALUE_1)
                || result.get(1).getMessage().equals(MESSAGE_VALUE_2)).isTrue();
    }

    @Test
    public void testWithin() {

        final List<QueryTest> result = repository.findQueryTestByMessageWithin("Before" + MESSAGE_VALUE_1 + "After");
        
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getMessage()).isEqualTo(MESSAGE_VALUE_1);
    }

    @Test
    public void testContaining() {

        final List<QueryTest> result = repository.findQueryTestByMessageContaining(MESSAGE_CONTAINING_TEST);
        
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getMessage().equals(MESSAGE_VALUE_1)
                || result.get(0).getMessage().equals(MESSAGE_VALUE_3)).isTrue();
        assertThat(result.get(1).getMessage().equals(MESSAGE_VALUE_1)
                || result.get(1).getMessage().equals(MESSAGE_VALUE_3)).isTrue();
    }

    @Test
    public void testEndingWith() {

        final List<QueryTest> result = repository.findQueryTestByMessageEndingWith(
                MESSAGE_ENDSWITH_TEST);
        
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getMessage()).isEqualTo(MESSAGE_VALUE_2);
    }

//    
//    Requires that the index on the message attribute of the QUeryTest domain class is set to a range query
//    however there appears to be no way to do this at the moment via annotations.  Will seek advice on how
//    best to implement this test
//
//    @Test
//    public void testStartingWith() {
//
//        final List<QueryTest> result = repository.findQueryTestByMessageStartsWith(
//                MESSAGE_STARTSWITH_TEST);
//        
//        assertThat(result).isNotNull();
//        assertThat(result.size()).isEqualTo(1);
//        assertThat(result.get(0).getMessage()).isEqualTo(MESSAGE_VALUE_2);
//    }

    @Test
    public void testExists() {

        final List<QueryTest> result = repository.findQueryTestByMessageExists();
        
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    public void testIsEmpty() {

        final List<QueryTest> result = repository.findQueryTestByTestValueIsEmpty();
        
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getMessage()).isEqualTo(MESSAGE_VALUE_2);
    }

//    @Test
//    public void testIsNotEmpty() {
//
//        final List<QueryTest> result = repository.findQueryTestByTestValueIsNotEmpty();
//        
//        assertThat(result).isNotNull();
//        assertThat(result.size()).isEqualTo(2);
//        assertThat(result.get(0).getMessage().equals(MESSAGE_VALUE_1)
//                || result.get(0).getMessage().equals(MESSAGE_VALUE_3)).isTrue();
//        assertThat(result.get(1).getMessage().equals(MESSAGE_VALUE_1)
//                || result.get(1).getMessage().equals(MESSAGE_VALUE_3)).isTrue();
//    }

    @Test
    public void testIsNull() {

        final List<QueryTest> result = repository.findQueryTestByTestValueIsNull();
        
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getMessage()).isEqualTo(MESSAGE_VALUE_3);
    }

    @Test
    public void testIsNotNull() {

        final List<QueryTest> result = repository.findQueryTestByTestValueIsNotNull();
        
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getMessage().equals(MESSAGE_VALUE_1)
                || result.get(0).getMessage().equals(MESSAGE_VALUE_2)).isTrue();
        assertThat(result.get(1).getMessage().equals(MESSAGE_VALUE_1)
                || result.get(1).getMessage().equals(MESSAGE_VALUE_2)).isTrue();
    }

    @Test
    public void testIn() {

        final List<QueryTest> result = repository.findQueryTestByIdIsIn(Arrays.asList(new String[] {"ID1", "ID2"}));
        
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getMessage().equals(MESSAGE_VALUE_1)
                || result.get(0).getMessage().equals(MESSAGE_VALUE_2)).isTrue();
        assertThat(result.get(1).getMessage().equals(MESSAGE_VALUE_1)
                || result.get(1).getMessage().equals(MESSAGE_VALUE_2)).isTrue();
    }

    @Test
    public void testNotIn() {

        final List<QueryTest> result = repository.findQueryTestByIdIsNotIn(Arrays.asList(new String[] {"ID1", "ID2"}));
        
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getMessage()).isEqualTo(MESSAGE_VALUE_3);
    }

}
