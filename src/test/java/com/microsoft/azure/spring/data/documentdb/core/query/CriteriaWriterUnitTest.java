/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.documentdb.core.query;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.microsoft.azure.spring.data.documentdb.common.TestConstants;

public class CriteriaWriterUnitTest {

    private static final String FIELD_NAME = "field";
    private static final String KEY_NAME = "key";
    private static final String TEST_VALUE1 = "test value";
    private static final String TEST_VALUE2 = "example value";
    private static final String UPPERCASE_TEST_VALUE1 = "TEST VALUE";

    
    private final List<Object> noCriteriaValues = Arrays.asList(new Object[] {});
    private final List<Object> singleCriteriaValue = Arrays.asList(new Object[] {TEST_VALUE1});
    private final List<Object> twoCriteriaValue = Arrays.asList(new Object[] {TEST_VALUE1, TEST_VALUE2});
    

    @Test
    public void testKeyFieldReplacement() {

        final Criteria criteria = Criteria.value(KEY_NAME, CriteriaType.IS_EQUAL, singleCriteriaValue);

        final CriteriaWriter writer = new CriteriaWriter(criteria, KEY_NAME);

        assertThat(writer.getCriteriaAsString()).isEqualTo("r.id=@p1");
        assertThat(writer.getParameters().size()).isEqualTo(1);
        assertThat(writer.getParameters().get("@p1")).isEqualTo(TEST_VALUE1);
    }    
    
    @Test
    public void testSimpleAndCriteria() {

        final Criteria criteria = Criteria.and(
                Criteria.value(FIELD_NAME,   CriteriaType.IS_EQUAL, singleCriteriaValue),
                Criteria.value(FIELD_NAME,   CriteriaType.IS_EQUAL, singleCriteriaValue)
        );

        final CriteriaWriter writer = new CriteriaWriter(criteria, KEY_NAME);

        assertThat(writer.getCriteriaAsString()).isEqualTo("r.field=@p1 AND r.field=@p2");
        assertThat(writer.getParameters().size()).isEqualTo(2);
        assertThat(writer.getParameters().get("@p1")).isEqualTo(TEST_VALUE1);
        assertThat(writer.getParameters().get("@p2")).isEqualTo(TEST_VALUE1);
    }

    @Test
    public void testSimpleOrCriteria() {

        final Criteria criteria = Criteria.or(
                Criteria.value(FIELD_NAME,   CriteriaType.IS_EQUAL, singleCriteriaValue),
                Criteria.value(FIELD_NAME,   CriteriaType.IS_EQUAL, singleCriteriaValue)
        );

        final CriteriaWriter writer = new CriteriaWriter(criteria, KEY_NAME);

        assertThat(writer.getCriteriaAsString()).isEqualTo("(r.field=@p1) OR (r.field=@p2)");
        assertThat(writer.getParameters().size()).isEqualTo(2);
        assertThat(writer.getParameters().get("@p1")).isEqualTo(TEST_VALUE1);
        assertThat(writer.getParameters().get("@p2")).isEqualTo(TEST_VALUE1);
    }

    @Test
    public void testSimpleEqualCriteria() {

        final Criteria criteria = Criteria.value(FIELD_NAME, CriteriaType.IS_EQUAL, singleCriteriaValue);

        final CriteriaWriter writer = new CriteriaWriter(criteria, KEY_NAME);

        assertThat(writer.getCriteriaAsString()).isEqualTo("r.field=@p1");
        assertThat(writer.getParameters().size()).isEqualTo(1);
        assertThat(writer.getParameters().get("@p1")).isEqualTo(TEST_VALUE1);
    }
    
    @Test
    public void testSimpleNotEqualCriteria() {

        final Criteria criteria = Criteria.value(FIELD_NAME, CriteriaType.IS_NOT_EQUAL, singleCriteriaValue);

        final CriteriaWriter writer = new CriteriaWriter(criteria, KEY_NAME);

        assertThat(writer.getCriteriaAsString()).isEqualTo("r.field!=@p1");
        assertThat(writer.getParameters().size()).isEqualTo(1);
        assertThat(writer.getParameters().get("@p1")).isEqualTo(TEST_VALUE1);
    }
    
    @Test
    public void testSimpleLessThanCriteria() {

        final Criteria criteria = Criteria.value(FIELD_NAME, CriteriaType.IS_LESS_THAN, singleCriteriaValue);

        final CriteriaWriter writer = new CriteriaWriter(criteria, KEY_NAME);

        assertThat(writer.getCriteriaAsString()).isEqualTo("r.field<@p1");
        assertThat(writer.getParameters().size()).isEqualTo(1);
        assertThat(writer.getParameters().get("@p1")).isEqualTo(TEST_VALUE1);
    }
    
    @Test
    public void testSimpleLessThanOrEqualCriteria() {

        final Criteria criteria = Criteria.value(FIELD_NAME, CriteriaType.IS_LESS_THAN_OR_EQUAL, singleCriteriaValue);

        final CriteriaWriter writer = new CriteriaWriter(criteria, KEY_NAME);

        assertThat(writer.getCriteriaAsString()).isEqualTo("r.field<=@p1");
        assertThat(writer.getParameters().size()).isEqualTo(1);
        assertThat(writer.getParameters().get("@p1")).isEqualTo(TEST_VALUE1);
    }    

    
    @Test
    public void testSimpleGreaterThanCriteria() {

        final Criteria criteria = Criteria.value(FIELD_NAME, CriteriaType.IS_GREATER_THAN, singleCriteriaValue);

        final CriteriaWriter writer = new CriteriaWriter(criteria, KEY_NAME);

        assertThat(writer.getCriteriaAsString()).isEqualTo("r.field>@p1");
        assertThat(writer.getParameters().size()).isEqualTo(1);
        assertThat(writer.getParameters().get("@p1")).isEqualTo(TEST_VALUE1);
    }   
    
    @Test
    public void testSimpleGreaterThanOrEqualCriteria() {

        final Criteria criteria = Criteria.value(
                FIELD_NAME, CriteriaType.IS_GREATER_THAN_OR_EQUAL, singleCriteriaValue);

        final CriteriaWriter writer = new CriteriaWriter(criteria, KEY_NAME);

        assertThat(writer.getCriteriaAsString()).isEqualTo("r.field>=@p1");
        assertThat(writer.getParameters().size()).isEqualTo(1);
        assertThat(writer.getParameters().get("@p1")).isEqualTo(TEST_VALUE1);
    } 
    
    @Test
    public void testSimpleBetweenCriteria() {

        final Criteria criteria = Criteria.value(
                FIELD_NAME, CriteriaType.BETWEEN, twoCriteriaValue);

        final CriteriaWriter writer = new CriteriaWriter(criteria, KEY_NAME);

        assertThat(writer.getCriteriaAsString()).isEqualTo("r.field BETWEEN @p1 AND @p2");
        assertThat(writer.getParameters().size()).isEqualTo(2);
        assertThat(writer.getParameters().get("@p1")).isEqualTo(TEST_VALUE1);
        assertThat(writer.getParameters().get("@p2")).isEqualTo(TEST_VALUE2);
    } 

    @Test
    public void testSimpleWithinCriteria() {

        final Criteria criteria = Criteria.value(
                FIELD_NAME, CriteriaType.WITHIN, singleCriteriaValue);

        final CriteriaWriter writer = new CriteriaWriter(criteria, KEY_NAME);

        assertThat(writer.getCriteriaAsString()).isEqualTo("CONTAINS(@p1,r.field)");
        assertThat(writer.getParameters().size()).isEqualTo(1);
        assertThat(writer.getParameters().get("@p1")).isEqualTo(TEST_VALUE1);
    } 

    @Test
    public void testSimpleContainingCriteria() {

        final Criteria criteria = Criteria.value(
                FIELD_NAME, CriteriaType.CONTAINING, singleCriteriaValue);

        final CriteriaWriter writer = new CriteriaWriter(criteria, KEY_NAME);

        assertThat(writer.getCriteriaAsString()).isEqualTo("CONTAINS(r.field,@p1)");
        assertThat(writer.getParameters().size()).isEqualTo(1);
        assertThat(writer.getParameters().get("@p1")).isEqualTo(TEST_VALUE1);
    } 

    @Test
    public void testSimpleEndingWithCriteria() {

        final Criteria criteria = Criteria.value(
                FIELD_NAME, CriteriaType.ENDING_WITH, singleCriteriaValue);

        final CriteriaWriter writer = new CriteriaWriter(criteria, KEY_NAME);

        assertThat(writer.getCriteriaAsString()).isEqualTo("ENDSWITH(r.field,@p1)");
        assertThat(writer.getParameters().size()).isEqualTo(1);
        assertThat(writer.getParameters().get("@p1")).isEqualTo(TEST_VALUE1);
    } 

    @Test
    public void testSimpleStartingWithCriteria() {

        final Criteria criteria = Criteria.value(
                FIELD_NAME, CriteriaType.STARTING_WITH, singleCriteriaValue);

        final CriteriaWriter writer = new CriteriaWriter(criteria, KEY_NAME);

        assertThat(writer.getCriteriaAsString()).isEqualTo("STARTSWITH(r.field,@p1)");
        assertThat(writer.getParameters().size()).isEqualTo(1);
        assertThat(writer.getParameters().get("@p1")).isEqualTo(TEST_VALUE1);
    } 

    @Test
    public void testSimpleExistsCriteria() {

        final Criteria criteria = Criteria.value(
                FIELD_NAME, CriteriaType.EXISTS, noCriteriaValues);

        final CriteriaWriter writer = new CriteriaWriter(criteria, KEY_NAME);

        assertThat(writer.getCriteriaAsString()).isEqualTo("IS_DEFINED(r.field)");
        assertThat(writer.getParameters().size()).isEqualTo(0);
    } 

    @Test
    public void testSimpleIsEmptyCriteria() {

        final Criteria criteria = Criteria.value(
                FIELD_NAME, CriteriaType.IS_EMPTY, noCriteriaValues);

        final CriteriaWriter writer = new CriteriaWriter(criteria, KEY_NAME);

        assertThat(writer.getCriteriaAsString()).isEqualTo("LENGTH(r.field)=0");
        assertThat(writer.getParameters().size()).isEqualTo(0);
    } 

    @Test
    public void testSimpleIsNotEmptyCriteria() {

        final Criteria criteria = Criteria.value(
                FIELD_NAME, CriteriaType.IS_NOT_EMPTY, noCriteriaValues);

        final CriteriaWriter writer = new CriteriaWriter(criteria, KEY_NAME);

        assertThat(writer.getCriteriaAsString()).isEqualTo("LENGTH(r.field)!=0");
        assertThat(writer.getParameters().size()).isEqualTo(0);
    } 

    @Test
    public void testSimpleIsNullCriteria() {

        final Criteria criteria = Criteria.value(
                FIELD_NAME, CriteriaType.IS_NULL, noCriteriaValues);

        final CriteriaWriter writer = new CriteriaWriter(criteria, KEY_NAME);

        assertThat(writer.getCriteriaAsString()).isEqualTo("IS_NULL(r.field)");
        assertThat(writer.getParameters().size()).isEqualTo(0);
    } 

    @Test
    public void testSimpleIsNotNullCriteria() {

        final Criteria criteria = Criteria.value(
                FIELD_NAME, CriteriaType.IS_NOT_NULL, noCriteriaValues);

        final CriteriaWriter writer = new CriteriaWriter(criteria, KEY_NAME);

        assertThat(writer.getCriteriaAsString()).isEqualTo("NOT (IS_NULL(r.field))");
        assertThat(writer.getParameters().size()).isEqualTo(0);
    } 
}
