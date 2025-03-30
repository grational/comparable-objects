package it.grational.aggregate

trait AggregationList<T> implements Cloneable {
	
	List<T> list = []

	def leftShift(elem) {
		int idx = this.list.findIndexOf { it == elem }
		if ( idx >= 0 ) {
			this.list[idx] += elem
		} else {
			this.list.add(elem)
		}
	}

	def plus(AggregationList<T> other) {
		if ( this != other )
			throw new IllegalArgumentException("[${this.class.simpleName}] Cannot add different objects!")

		List temp = this.list
		this.list = this.aggregate(other)
		AggregationList<T> result = this.clone()
		this.list = temp

		return result
	}

	List<T> aggregate(AggregationList<T> other) {
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
		AggregationList<T> cloned = (AggregationList<T>) super.clone()
		// just a first level list deep copy
		cloned.list = new ArrayList<T>(this.list)
		return cloned
	}
}
