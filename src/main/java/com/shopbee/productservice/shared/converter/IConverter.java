package com.shopbee.productservice.shared.converter;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;

/**
 * The interface Converter.
 *
 * @param <S> the type parameter
 * @param <D> the type parameter
 */
public interface IConverter<S, D> {

    /**
     * Convert d.
     *
     * @param source the source
     * @return the d
     */
    D convert(S source);

    /**
     * Convert all list.
     *
     * @param sourceList the source list
     * @return the list
     */
    default List<D> convertAll(Collection<S> sourceList) {
        return CollectionUtils.emptyIfNull(sourceList).stream()
                .map(this::convert)
                .toList();
    }
}
