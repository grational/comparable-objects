package it.grational.aggregate

import spock.lang.*
import groovy.transform.*

class AggregatorUSpec extends Specification {

	def "Should create an empty aggregator with default constructor"() {
		when: 'creating an aggregator with default constructor'
			def aggregator = new Aggregator<Integer>()
		then: 'it should have an empty list'
			aggregator.list == []
		and: 'no id'
			aggregator.id == Aggregator.DEFAULT_ID
	}

	def "Should create an aggregator with an initial list and a default id"() {
		given: 'an initial list'
			def initialList = [1, 2, 3]
		when: 'creating an aggregator with initial list'
			def aggregator = new Aggregator<Integer>(initialList)
		then: 'it should have the provided list'
			aggregator.list == initialList
		and: 'no id'
			aggregator.id == Aggregator.DEFAULT_ID
	}

	def "Should create an aggregator with id and initial list"() {
		given: 'an id and initial list'
			def id = "test-id"
			def initialList = [1, 2, 3]
		when: 'creating an aggregator with id and initial list'
			def aggregator = new Aggregator<Integer>(id, initialList)
		then: 'it should have the provided list'
			aggregator.list == initialList
		and: 'the provided id'
			aggregator.id == id
	}

	def "Should handle null initial list as empty list"() {
		when: 'creating an aggregator with null list'
			def aggregator = new Aggregator<Integer>(null)
		then: 'it should have an empty list'
			aggregator.list == []
	}

	def "Should add elements with leftShift operator"() {
		given:
			def aggregator = new Aggregator<Integer>()
		when:
			aggregator << 1
			aggregator << 2
			aggregator << 3
		then: 'the list should contain all added elements'
			aggregator.list == [1, 2, 3]
	}

	@Unroll
	def "Should append or aggregate elements based on equality (#scenario)"() {
		given: 'an aggregator with initial list'
			def aggregator = new Aggregator(initialList)
		when: 'adding a new element'
			aggregator << newElement
		then: 'the list should be updated correctly'
			aggregator.list == expectedList
		where:
			scenario              | initialList     | newElement || expectedList
			'unique element'      | [1, 2, 3]       | 4          || [1, 2, 3, 4]
			'duplicate number'    | [1, 2, 3]       | 2          || [1, 4, 3]
			'unique string'       | ['a', 'b', 'c'] | 'd'        || ['a', 'b', 'c', 'd']
			'duplicate string'    | ['a', 'b', 'c'] | 'b'        || ['a', 'bb', 'c']
	}

	def "Should merge lists with plus operator"() {
		given: 'two aggregators with initial lists'
			def agg1 = new Aggregator<Integer>([1, 2, 3])
			def agg2 = new Aggregator<Integer>([4, 5, 6])
		when: 'adding them together'
			def result = agg1 + agg2
		then: 'the result should contain all elements'
			result.list == [1, 2, 3, 4, 5, 6]
		and: 'the original aggregators should remain unchanged'
			agg1.list == [1, 2, 3]
			agg2.list == [4, 5, 6]
	}

	def "Should aggregate kin elements when adding aggregators due to default id"() {
		given: 'two aggregators with overlapping elements'
			def agg1 = new Aggregator<Integer>([1, 2, 3])
			def agg2 = new Aggregator<Integer>([1, 2, 4])
		when: 'adding them together'
			def result = agg1 + agg2
		then: 'the result should have aggregated duplicates'
			result.list == [2, 4, 3, 4]
	}

	def "Should throw exception when adding aggregators with different ids"() {
		given: 'two aggregators with different ids'
			def agg1 = new Aggregator<Integer>("id1", [1, 2, 3])
			def agg2 = new Aggregator<Integer>("id2", [4, 5, 6])
		when: 'adding them together'
			agg1 + agg2
		then: 'an exception should be thrown'
			def exception = thrown(IllegalArgumentException)
			exception.message == "[Aggregator] Cannot add different objects!"
	}

	def "Should allow adding aggregators with the same id"() {
		given: 'two aggregators with the same id'
			def agg1 = new Aggregator<Integer>("same-id", [1, 2, 3])
			def agg2 = new Aggregator<Integer>("same-id", [4, 5, 6])
		when: 'adding them together'
			def result = agg1 + agg2
		then: 'they should be added successfully'
			result.list == [1, 2, 3, 4, 5, 6]
	}

	def "Should consider aggregators equal if they have the same id"() {
		given: 'two aggregators with same id but different lists'
			def agg1 = new Aggregator<Integer>("id", [1, 2, 3])
			def agg2 = new Aggregator<Integer>("id", [4, 5, 6])
		expect: 'they should be considered equal'
			agg1 == agg2
			agg1.hashCode() == agg2.hashCode()
	}

	def "Should not consider an aggregator equal to another type of object"() {
		given: 'an aggregator and another type of object'
			def aggregator = new Aggregator<Integer>([1, 2, 3])
			def otherObject = "not an aggregator"
		expect: 'they should not be considered equal'
			aggregator != otherObject
	}

	def "Should provide a meaningful toString representation"() {
		given: 'an aggregator with id and list'
			def aggregator = new Aggregator<Integer>("test-id", [1, 2, 3])
		when: 'calling toString'
			def string = aggregator.toString()
		then: 'it should include the id and list'
			string == "Aggregator(id: test-id, list: [1, 2, 3])"
	}

	def "Should handle toString when id is null"() {
		given: 'an aggregator without id'
			def aggregator = new Aggregator<Integer>([1, 2, 3])
		when: 'calling toString'
			def string = aggregator.toString()
		then: 'it should indicate that there is no id'
			string == "Aggregator(id: ${Aggregator.DEFAULT_ID}, list: [1, 2, 3])"
	}

	def "Should properly clone the aggregator"() {
		given:
			def original = new Aggregator<Integer>([1, 2, 3])

		when:
			def cloned = original.clone()

		then: 'the clone should have the same properties'
			cloned.id == original.id
			cloned.list == original.list

		and: 'modifying the clone should not affect the original'
			when: 'modifying the clone'
				cloned.list << 4
			then: 'the original should be unchanged'
				original.list == [1, 2, 3]
				cloned.list == [1, 2, 3, 4]
	}

	def "Should work with Summable objects"() {
		given: 'a custom Summable class'
			def sum1 = new SummableValue(value: 5)
			def sum2 = new SummableValue(value: 10)
			def sum3 = new SummableValue(value: 15)
			
		and: 'an aggregator with Summable objects'
			def aggregator = new Aggregator<SummableValue>([sum1, sum2])
			
		when: 'adding another summable element'
			aggregator << sum3
			
		then: 'it should be added to the list'
			aggregator.list.size() == 3
			aggregator.list == [sum1, sum2, sum3]
			
		when: 'adding an element equal to an existing one'
			def sum2Duplicate = new SummableValue(value: 10)
			aggregator << sum2Duplicate
			
		then: 'the values should be summed'
			aggregator.list.size() == 3
			aggregator.list[1].value == 20 // 10 + 10
	}
	
	@ToString(includeFields = true, includePackage = false)
	@EqualsAndHashCode(includes = ['value'])
	class SummableValue implements Summable<SummableValue> {
		int value
		
		SummableValue plus(SummableValue other) {
			return new SummableValue(value: this.value + other.value)
		}
	}
}
