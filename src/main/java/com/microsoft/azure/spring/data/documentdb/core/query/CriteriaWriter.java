/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.documentdb.core.query;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CriteriaWriter {

    private static final Map<CriteriaType, String> operatorLookup;
    static {
        final Map<CriteriaType, String> init = new HashMap<>();

        init.put(CriteriaType.IS_EQUAL,                 "r.@?=@@");
        init.put(CriteriaType.IS_NOT_EQUAL,             "r.@?!=@@");
        init.put(CriteriaType.IS_LESS_THAN,             "r.@?<@@");
        init.put(CriteriaType.IS_LESS_THAN_OR_EQUAL,    "r.@?<=@@");
        init.put(CriteriaType.IS_GREATER_THAN,          "r.@?>@@");
        init.put(CriteriaType.IS_GREATER_THAN_OR_EQUAL, "r.@?>=@@");
        init.put(CriteriaType.BETWEEN,                  "r.@? BETWEEN @@ AND @@");
        init.put(CriteriaType.EXISTS,                   "IS_DEFINED(r.@?)");
        init.put(CriteriaType.IS_EMPTY,                 "LENGTH(r.@?)=0");
        init.put(CriteriaType.IS_NOT_EMPTY,             "LENGTH(r.@?)!=0");
        init.put(CriteriaType.IS_NULL,                  "IS_NULL(r.@?)");
        init.put(CriteriaType.IS_NOT_NULL,              "NOT (IS_NULL(r.@?))");
        init.put(CriteriaType.STARTING_WITH,            "STARTSWITH(r.@?,@@)");
        init.put(CriteriaType.ENDING_WITH,              "ENDSWITH(r.@?,@@)");
        init.put(CriteriaType.CONTAINING,               "CONTAINS(r.@?,@@)");
        init.put(CriteriaType.WITHIN,                   "CONTAINS(@@,r.@?)");

        operatorLookup = Collections.unmodifiableMap(init);
    }

    private final Map<String, Object> parameterMap = new HashMap<>();
    private String entityClassIdFieldName = null;
    private String criteriaString = null;
    
    
    public CriteriaWriter() {
        
    }
    
    public CriteriaWriter(Criteria criteria, String idFieldName) {

        parseCriteria(criteria, idFieldName);
    }

    public void parseCriteria(Criteria criteria, String entityClassIdFieldName) {
        
        this.entityClassIdFieldName = entityClassIdFieldName;
        
        parameterMap.clear();
        
        criteriaString = buildCriteriaString(criteria);
    }
    
    public String getCriteriaAsString() {

        return criteriaString;
    }    
    
    public Map<String, Object> getParameters() {
        
        return parameterMap;
    }
    
    private String buildCriteriaString(Criteria criteria) {
        
        switch(criteria.getCriteriaType()) {
        
            case AND_CONDITION:
    
                return buildCriteriaString(criteria.getCriteriaList().get(0))
                        + " AND "
                        + buildCriteriaString(criteria.getCriteriaList().get(1));
                 
            case OR_CONDITION:
    
                return "("
                        + buildCriteriaString(criteria.getCriteriaList().get(0))
                        + ") OR ("
                        + buildCriteriaString(criteria.getCriteriaList().get(1))
                        + ")";
                                   
            case IN:
            case NOT_IN:
            {
    
                if (criteria.getCriteriaValues().size() != 1 ||
                        !(criteria.getCriteriaValues().get(0) instanceof List<?>)) {
    
                    throw new IllegalArgumentException("value provided for IN parameter is not a list value");
                }
    
                final List<?> listItems = (List<?>) criteria.getCriteriaValues().get(0);
    
                String template = "r.@? IN (" + 
                        String.join(",", Collections.nCopies(listItems.size(), "@@")) + ")";

                if (criteria.getCriteriaType() == CriteriaType.NOT_IN) {

                    template = "NOT (" + template + ")";
                }
                
                return processTemplate(template, criteria, listItems);
            }
                
            default:
            {
                
                if (operatorLookup.containsKey(criteria.getCriteriaType())) {
    
                    final String template = operatorLookup.get(criteria.getCriteriaType());
                    
                    final List<Object> values = criteria.getCriteriaValues();
    
                    return processTemplate(template, criteria, values);
                }
            }
        }
    
        throw new IllegalArgumentException("Unsupported condition");
    }
    
    private String processTemplate(String template, Criteria criteria, List<?> values) {

        String workingTemplate = template;
        
        if (criteria.shouldIgnoreCase()) {
            
            workingTemplate = workingTemplate.replaceAll("r\\.@\\?", "LOWER(r\\.@\\?)");
            workingTemplate = workingTemplate.replaceAll("@@", "LOWER(@@)");
        }

        // If the field name used in any of the criteria parameters matches the id field specified for
        // entity (with @Id) then replace the field name with "id"
        
        workingTemplate = workingTemplate.replaceAll("@\\?", 
                criteria.getCriteriaSubject() != null && 
                        criteria.getCriteriaSubject()
                            .equals(entityClassIdFieldName) ? "id" : criteria.getCriteriaSubject());

        int valuesIndex = 0;

        while (workingTemplate.indexOf("@@") != -1 && valuesIndex < values.size()) {

            final int parameterIndex = parameterMap.size();
            final String parameterName = "@p" + (parameterIndex + 1);

            workingTemplate = workingTemplate.replaceFirst("@@", parameterName);

            parameterMap.put(parameterName, values.get(valuesIndex));
            
            ++valuesIndex;
        }
        
        if (workingTemplate.indexOf("@@") != -1 || valuesIndex < values.size()) {
            
            throw new IllegalArgumentException(
                    "incorrect number of values for " + criteria.getCriteriaType() + " operation ");
        }                    

        return workingTemplate;            
    }  

}
