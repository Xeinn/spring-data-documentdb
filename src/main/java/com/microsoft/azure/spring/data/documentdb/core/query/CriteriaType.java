/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.documentdb.core.query;

public enum CriteriaType {
    AND_CONDITION,
    OR_CONDITION,
    IS_EQUAL,
    IS_NOT_EQUAL,
    IS_LESS_THAN,
    IS_LESS_THAN_OR_EQUAL,
    IS_GREATER_THAN,
    IS_GREATER_THAN_OR_EQUAL,
    BETWEEN,
    WITHIN,
    CONTAINING,
    ENDING_WITH,
    EXISTS,
    IS_EMPTY,
    IS_NOT_EMPTY,
    IS_NULL,
    IS_NOT_NULL,
    LIKE,
    NOT_LIKE,
    NEAR,
    REGEX,
    STARTING_WITH,
    IN,
    NOT_IN
}
