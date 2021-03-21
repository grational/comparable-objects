package it.grational.compare

trait ComparableArray implements Comparable<ComparableArray> {
	private static final int PRECEDE = -1
	private static final int EQUAL = 0
	private static final int FOLLOW = 1

	private Object[] array

	void setArray(Object[] rry) {
		this.array = rry
	}

	Object[] getArray() {
		this.array
	}

	int size() {
		this.array.size()
	}

	def get(int i) {
		this.array[i]
	}

	@Override
	int compareTo(ComparableArray other) {
		int comparison = compareCommonElements(this,other)

		if (comparison == EQUAL)
			comparison = comparisonBySize(this,other)

		return comparison
	}

	private int compareCommonElements(ComparableArray a, ComparableArray b) {
		int comparison
		int smallest = smallestCollectionSize(a,b)
		for(int i = 0; i < smallest; i++) {
			comparison = ( a.get(i) <=> b.get(i) )
			if ( elementsAreDifferent(comparison) )
				break
		}
		return comparison
	}

	private int smallestCollectionSize(ComparableArray a, ComparableArray b) {
		return (a.size() < b.size()) ? a.size() : b.size()
	}

	private Boolean elementsAreDifferent(int comparison) {
		return (comparison != 0)
	}

	private int comparisonBySize(ComparableArray a, ComparableArray b) {
		return (a.size() <=> b.size())
	}

}
