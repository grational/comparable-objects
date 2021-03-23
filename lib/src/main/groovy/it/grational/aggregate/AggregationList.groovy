package it.grational.aggregate

trait AggregationList {
	
	List list

	def leftShift(def elem) {
		Integer idx = this.list.findIndexOf { it == elem }
		if ( idx >= 0 ) {
			this.list[idx] += elem
		} else {
			this.list += elem
		}
	}

	def plus(AggregationList other) {
		other.list.each { elem ->
			this << elem
		}
	}

}
