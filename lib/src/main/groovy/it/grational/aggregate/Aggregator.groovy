package it.grational.aggregate

import groovy.transform.ToString
import groovy.transform.EqualsAndHashCode

/**
 * Standard implementation of the AggregationList trait.
 * This class provides a ready-to-use container for aggregating elements
 * without requiring custom implementation classes.
 */
@ToString (
	includeFields = true,
	includePackage = false
)
@EqualsAndHashCode(includeFields = true, includes='id')
class Aggregator implements AggregationList {
	
	private final String id
	final static String DEFAULT_ID = 'default'
	
	/**
	 * Constructor with initial list
	 * 
	 * @param initialList The initial list of elements
	 */
	Aggregator(List ls = []) {
		this (
			DEFAULT_ID,
			ls
		)
	}
	
	/**
	 * Constructor with id and initial list
	 * 
	 * @param id Identifier for this aggregator
	 * @param initialList The initial list of elements
	 */
	Aggregator (
		String id = DEFAULT_ID,
		List ls = []
	) {
		this.id = id ?: DEFAULT_ID
		this.list = ls ?: []
	}
	
	/**
	 * Creates a string representation of this aggregator
	 * 
	 * @return String representation
	 */
	@Override
	String toString() {
		return "Aggregator(id: ${id}, list: ${list})"
	}
	
}
