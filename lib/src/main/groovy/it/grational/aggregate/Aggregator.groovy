package it.grational.aggregate

trait Aggregator {
	
	List list

	def leftShift(elem) {
		Integer idx = this.list.findIndexOf { it == elem }
		if ( idx > 0 ) {
			this.list[idx] += elem
		} else {
			this.list += elem
		}
	}

}
