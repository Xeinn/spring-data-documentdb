/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.cosmosdb;

import com.microsoft.azure.documentdb.IndexingMode;

public class Constants {
    private Constants() {
        // Hide the implicit public one
    }

    public static final String DEFAULT_COLLECTION_NAME = "";
    public static final String DEFAULT_REQUEST_UNIT = "4000";
    public static final boolean DEFAULT_INDEXINGPOLICY_AUTOMATIC = true;
    public static final IndexingMode DEFAULT_INDEXINGPOLICY_MODE = IndexingMode.Consistent;
    public static final String DEFAULT_REPOSITORY_IMPLEMENT_POSTFIX = "Impl";
    public static final int DEFAULT_TIME_TO_LIVE = -1; // Indicates never expire

    public static final String ID_PROPERTY_NAME = "id";

    public static final String DOCUMENTDB_MODULE_NAME = "cosmosdb";
    public static final String DOCUMENTDB_MODULE_PREFIX = "cosmosdb";
    public static final String DOCUMENTDB_MAPPING_CONTEXT_NAME = "documentDbMappingContext";

    public static final String USER_AGENT_SUFFIX = "spring-data/";

    public static final String OBJECTMAPPER_BEAN_NAME = "cosmosdbObjectMapper";
}

