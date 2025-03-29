package it.grational.aggregate

trait AggregationList implements Cloneable {
	
	List list = []

	def leftShift(elem) {
		Integer idx = this.list.findIndexOf { it == elem }
		if ( idx >= 0 ) {
			this.list[idx] += elem
		} else {
			this.list.add(elem)
		}
	}

	def plus(AggregationList other) {
		if ( this != other )
			throw new IllegalArgumentException("[${this.class.simpleName}] Cannot add different objects!")

		List temp = this.list
		this.list = this.aggregate(other)
		AggregationList result = this.clone()
		this.list = temp

		return result
	}

	List aggregate(AggregationList other) {
		def result = []
		this.list.eachWithIndex { elem, idx -> 
			result[idx] = elem
			other.list.grep { it == elem }?.each { result[idx] += it }
		}
		result.addAll(other.list - this.list)
		return result
	}

	@Override
	Object clone() {
		return super.clone()
	}
}
