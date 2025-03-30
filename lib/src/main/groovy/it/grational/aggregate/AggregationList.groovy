package it.grational.aggregate

trait AggregationList<T> implements Cloneable {
	
	// Delegating to List for basic collection operations
	@Delegate(methodAnnotations=true, interfaces=false)
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

	def leftJoin(AggregationList<T> other) {
		if ( this != other )
			throw new IllegalArgumentException("[${this.class.simpleName}] Cannot join different objects!")

		List temp = this.list
		this.list = this.leftAggregate(other)
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

	List<T> leftAggregate(AggregationList<T> other) {
		def result = []
		this.list.eachWithIndex { elem, idx -> 
			result[idx] = elem
			other.list.grep { it == elem }?.each { result[idx] += it }
		}
		return result
	}

	@Override
	Object clone() {
		AggregationList<T> cloned = (AggregationList<T>) super.clone()
		// just a first level list deep copy
		cloned.list = new ArrayList<T>(this.list)
		return cloned
	}
	
	// NOTE: explicitly delegate to the list for these groovy extension methods
	// to avoid conflicts with the trait methods
	def sum() { list.sum() }
	def max() { list.max() }
	def min() { list.min() }
	
	def count(Closure closure) { 
		list.count(closure) 
	}
	def collect(Closure closure) {
		list.collect(closure)
	}
	def findAll(Closure closure) {
		list.findAll(closure)
	}
	def any(Closure closure) {
		list.any(closure)
	}
	def every(Closure closure) {
		list.every(closure)
	}
	def none(Closure closure) {
		!any(closure)
	}
}
