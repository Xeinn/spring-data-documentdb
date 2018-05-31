/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.documentdb.core.query;

import java.util.ArrayList;
import java.util.List;

public class CriteriaFinder {

    private final List<NewCriteria> matchedCriteria = new ArrayList<>();
    
    CriteriaFinder() {
    }
    
    public List<NewCriteria> findCriteria(NewCriteria criteria, String fieldName) {

        matchedCriteria.clear();
        
        searchCriteria(criteria, fieldName);
        
        return matchedCriteria;
    }

    private void searchCriteria(NewCriteria criteria, String fieldName) {
        
        if (criteria.getCriteriaSubject() != null && criteria.getCriteriaSubject().equals(fieldName)) {
            matchedCriteria.add(criteria);
        }
        
        for (final NewCriteria val : criteria.getCriteriaList()) {
            
            searchCriteria(val, fieldName);
        }
    }
}
