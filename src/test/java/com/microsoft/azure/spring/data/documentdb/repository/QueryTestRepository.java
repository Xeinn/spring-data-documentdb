/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.documentdb.repository;

import com.microsoft.azure.spring.data.documentdb.domain.QueryTest;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface QueryTestRepository extends DocumentDbRepository<QueryTest, String> {

    List<QueryTest> findQueryTestByMessage(String msg);
    List<QueryTest> findQueryTestByMessageNot(String msg);
    List<QueryTest> findQueryTestByIdLessThan(String msg);
    List<QueryTest> findQueryTestByIdLessThanEqual(String msg);
    List<QueryTest> findQueryTestByIdGreaterThan(String msg);
    List<QueryTest> findQueryTestByIdGreaterThanEqual(String msg);
    List<QueryTest> findQueryTestByIdBetween(String start, String end);
    List<QueryTest> findQueryTestByIdIsIn(List<String> values);
    List<QueryTest> findQueryTestByIdIsNotIn(List<String> values);

    
    List<QueryTest> findQueryTestByMessageWithin(String val);
    List<QueryTest> findQueryTestByMessageContaining(String val);
    List<QueryTest> findQueryTestByMessageEndingWith(String val);
    List<QueryTest> findQueryTestByMessageStartsWith(String val);
    List<QueryTest> findQueryTestByMessageExists();

    List<QueryTest> findQueryTestByTestValueIsEmpty();
    List<QueryTest> findQueryTestByTestValueIsNotEmpty();
    List<QueryTest> findQueryTestByTestValueIsNull();
    List<QueryTest> findQueryTestByTestValueIsNotNull();
    
}
