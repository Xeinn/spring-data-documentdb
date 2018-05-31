/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.documentdb.repository.query;

import com.microsoft.azure.spring.data.documentdb.core.mapping.DocumentDbPersistentProperty;
import com.microsoft.azure.spring.data.documentdb.core.query.NewQuery;
import com.microsoft.azure.spring.data.documentdb.core.query.NewCriteria;
import com.microsoft.azure.spring.data.documentdb.core.query.CriteriaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class NewDocumentDbQueryCreator extends AbstractQueryCreator<NewQuery, NewCriteria> {

    private static final Logger logger = LoggerFactory.getLogger(DocumentDbQueryCreator.class);

    private static final Map<Part.Type, CriteriaType> criteriaLookup;
    static {
        final Map<Part.Type, CriteriaType> init = new HashMap<>();
        
        init.put(Part.Type.SIMPLE_PROPERTY,          CriteriaType.IS_EQUAL);
        init.put(Part.Type.NEGATING_SIMPLE_PROPERTY, CriteriaType.IS_NOT_EQUAL);
        init.put(Part.Type.AFTER,                    CriteriaType.IS_GREATER_THAN);
        init.put(Part.Type.BEFORE,                   CriteriaType.IS_LESS_THAN);
        init.put(Part.Type.BETWEEN,                  CriteriaType.BETWEEN);
        init.put(Part.Type.LESS_THAN,                CriteriaType.IS_LESS_THAN);
        init.put(Part.Type.LESS_THAN_EQUAL,          CriteriaType.IS_LESS_THAN_OR_EQUAL);
        init.put(Part.Type.GREATER_THAN,             CriteriaType.IS_GREATER_THAN);
        init.put(Part.Type.GREATER_THAN_EQUAL,       CriteriaType.IS_GREATER_THAN_OR_EQUAL);
        init.put(Part.Type.IS_EMPTY,                 CriteriaType.IS_EMPTY);
        init.put(Part.Type.IS_NOT_EMPTY,             CriteriaType.IS_NOT_EMPTY);
        init.put(Part.Type.IS_NULL,                  CriteriaType.IS_NULL);
        init.put(Part.Type.IS_NOT_NULL,              CriteriaType.IS_NOT_NULL);
        init.put(Part.Type.WITHIN,                   CriteriaType.WITHIN);
        init.put(Part.Type.CONTAINING,               CriteriaType.CONTAINING);
        init.put(Part.Type.STARTING_WITH,            CriteriaType.STARTING_WITH);
        init.put(Part.Type.ENDING_WITH,              CriteriaType.ENDING_WITH);
        init.put(Part.Type.IN,                       CriteriaType.IN);
        init.put(Part.Type.NOT_IN,                   CriteriaType.NOT_IN);
        init.put(Part.Type.EXISTS,                   CriteriaType.EXISTS);
        init.put(Part.Type.LIKE,                     CriteriaType.LIKE);
        init.put(Part.Type.NOT_LIKE,                 CriteriaType.NOT_LIKE);
        init.put(Part.Type.NEAR,                     CriteriaType.NEAR);
        init.put(Part.Type.REGEX,                    CriteriaType.REGEX);
        
        criteriaLookup = Collections.unmodifiableMap(init);
    }  

    private final MappingContext<?, DocumentDbPersistentProperty> mappingContext;

    public NewDocumentDbQueryCreator(PartTree tree,
            DocumentDbParameterAccessor accessor,
            MappingContext<?, DocumentDbPersistentProperty> mappingContext) {

        super(tree, accessor);

        this.mappingContext = mappingContext;
    }

    @Override
    protected NewCriteria create(Part part, Iterator<Object> iterator) {

        logger.debug("Creating criteria from part: {}", part);
        
        return createSingleConditionCriteria(part, iterator);
    }

    @Override
    protected NewCriteria and(Part part, NewCriteria base, Iterator<Object> iterator) {

        logger.debug("Combining existing {} criteria with {} using AND condition", base, part);

        return NewCriteria.and(base, createSingleConditionCriteria(part, iterator));
    }

    @Override
    protected NewCriteria or(NewCriteria base, NewCriteria criteria) {

        logger.debug("Combining existing criteria {} with {} using AND condition", base, criteria);

        return NewCriteria.or(base, criteria);
    }

    @Override
    protected NewQuery complete(NewCriteria criteria, Sort sort) {
        
        return new NewQuery(criteria, sort);
    }
    
    private NewCriteria createSingleConditionCriteria(Part part, Iterator<Object> parameters) {

        final Part.Type type = part.getType();
        
        final String conditionSubject = mappingContext.getPersistentPropertyPath(part.getProperty()).toDotPath();

        switch (type) {

            case FALSE:
                return NewCriteria.value(conditionSubject, CriteriaType.IS_EQUAL,
                        Arrays.asList(new Object[] {Boolean.FALSE}), isCaseSensitiveCondition(part));

            case TRUE:
                return NewCriteria.value(conditionSubject, CriteriaType.IS_EQUAL,
                        Arrays.asList(new Object[] {Boolean.TRUE}), isCaseSensitiveCondition(part));

            default:

                // Copy the method parameters into an array of values
                
                final List<Object> valueList = new ArrayList<>();
                
                for (int i = 0; i < part.getNumberOfArguments(); ++i) {
                    valueList.add(parameters.next());
                }

                if (criteriaLookup.containsKey(type)) {
                
                    return NewCriteria.value(conditionSubject, criteriaLookup.get(type),
                            valueList, isCaseSensitiveCondition(part));
                }
                
                throw new IllegalArgumentException("unsupported keyword: " + type);
        }
    }
    
    private boolean isCaseSensitiveCondition(Part part) {
        switch (part.shouldIgnoreCase()) {
            case NEVER:
                return false;
            case WHEN_POSSIBLE:
                return part.getProperty().getType() == String.class;
            case ALWAYS:
                return true;
            default:
                return false;
        }
    }
}
