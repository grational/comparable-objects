package it.grational.aggregate

import spock.lang.*
import groovy.transform.*

/**
 * This Spock specification was auto generated by 'gigawatt'
 * @author d7392
 * @date 21-03-2021 06.07
 */
class AggregationListUSpec extends Specification {

	def "Should be capable of adding one element without any initialization"() {
		given: 'an instance of an AggregationList'
			def aggregator = new Object() as AggregationList
		when:
			aggregator << 1
		then:
			aggregator.list == [1]
	}

	@Unroll
	def "Should be capable of append different elements to the internal list"() {
		given: 'an instance of an AggregationList'
			def aggregator = new Object() as AggregationList
		and: 'initialize it'
			aggregator.list = initialList
		when:
			aggregator << newElement
		then:
			aggregator.list == expected
		where:
			initialList     | newElement || expected
			// numbers
			[1, 2, 3]       | 4          || [1, 2, 3, 4]
			[1, 2, 3]       | 2          || [1, 4, 3]
			// letters
			['a', 'b', 'c'] | 'd'        || ['a', 'b', 'c', 'd']
			['a', 'b', 'c'] | 'b'        || ['a', 'bb', 'c']
	}

	def "Should refuse to add two different object"() {
		given:
			def a = new IdObject(id: 'a')
		and:
			def b = new IdObject(id: 'b')
		when:
			a + b
		then:
			def exception = thrown(IllegalArgumentException)
			exception.message == "[IdObject] Cannot add different objects!"
	}


	@Unroll
	def "Should be capable of summing with another AggregationList"() {
		given:
			def left = new IdObject(id: 'id')
		and:
			def right = new IdObject(id: 'id')
		and: 'initialize it'
			left.list = leftList
			right.list = rightList
		when:
			def result = left + right
		then:
			result.list == expected
		where:
			leftList        | rightList            || expected
			// numbers
			[1, 2, 3]       | [4, 5, 6]            || [1, 2, 3, 4, 5, 6]
			[1, 2, 3]       | [1, 2]               || [2, 4, 3]
			[1, 2, 3]       | [1, 2, 1]            || [3, 4, 3]
			// letters
			['a', 'b', 'c'] | ['d', 'e', 'f']      || ['a', 'b', 'c', 'd', 'e', 'f']
			['a', 'b', 'c'] | ['a', 'b', 'c', 'd'] || ['aa', 'bb', 'cc', 'd']
			['a', 'b', 'c'] | ['a', 'b', 'a', 'd'] || ['aaa', 'bb', 'c', 'd']
	}

	@EqualsAndHashCode(includes='id')
	class IdObject implements AggregationList { String id }

}
