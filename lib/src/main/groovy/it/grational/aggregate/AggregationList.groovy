package it.grational.aggregate

trait AggregationList {
	
	@Delegate
	List list

	def leftShift(def elem) {
		Integer idx = this.list.findIndexOf { it == elem }
		if ( idx >= 0 ) {
			this.list[idx] += elem
		} else {
			this.list.add(elem)
		}
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

}
