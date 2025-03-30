# Comparable Objects

A library of Groovy traits and classes to simplify object comparison, aggregation, and manipulation.

## Overview

This library provides a set of powerful tools for working with collections of objects in Groovy, focusing on:

- **Comparison**: Making objects easily comparable using natural ordering
- **Aggregation**: Combining and manipulating collections of objects with same-type merging
- **Summation**: Supporting custom addition logic between objects

## Installation

### Gradle

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.grational:comparable-objects:latest.release'
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.grational</groupId>
        <artifactId>comparable-objects</artifactId>
        <version>latest.release</version>
    </dependency>
</dependencies>
```

## Components

### ComparableArray

A trait that makes it easy to implement the `Comparable` interface for objects that contain an array or list of elements. Implements natural ordering based on the contained elements.

#### Features

- Automatically compares arrays element-by-element
- Falls back to size comparison when common elements are equal
- Simple to integrate by implementing the trait and setting the array

#### Example

```groovy
import it.grational.compare.ComparableArray

// Create a class that implements ComparableArray
class Version implements ComparableArray {
    private final String version
    
    Version(String version) {
        this.version = version
        this.array = version.split('\\.').collect { it as Integer } as Object[]
    }
    
    @Override
    String toString() {
        return version
    }
}

// Usage example
def v1 = new Version("1.0.0")
def v2 = new Version("1.2.0")
def v3 = new Version("2.0.0")
def v4 = new Version("1.0.0.1")

assert v1 < v2
assert v2 < v3
assert v1 < v4  // Same prefix, but v4 has more elements
assert [v3, v1, v4, v2].sort() == [v1, v2, v4, v3]
```

### Summable Interface

A simple interface for objects that can be added together using the `+` operator.

#### Features

- Defines the contract for objects that can be summed
- Enables custom addition behavior between same-type objects

#### Example

```groovy
import it.grational.aggregate.Summable

class Counter implements Summable<Counter> {
    int count
    
    Counter(int count) {
        this.count = count
    }
    
    @Override
    Counter plus(Counter other) {
        return new Counter(this.count + other.count)
    }
    
    @Override
    String toString() {
        return "Counter($count)"
    }
}

def c1 = new Counter(5)
def c2 = new Counter(3)
def sum = c1 + c2
assert sum.count == 8
```

### AggregationList Trait

A trait that enables collections to intelligently aggregate elements by merging identical objects.

#### Features

- Adds elements with the `<<` operator
- Combines duplicate elements by calling their `+` method
- Supports full list merge with the `+` operator
- Enables left join with the `leftJoin` method

#### Example

```groovy
import it.grational.aggregate.AggregationList
import it.grational.aggregate.Summable

// Custom value class that can be summed
class Revenue implements Summable<Revenue> {
    String department
    BigDecimal amount
    
    Revenue(String department, BigDecimal amount) {
        this.department = department
        this.amount = amount
    }
    
    @Override
    Revenue plus(Revenue other) {
        if (department != other.department) {
            throw new IllegalArgumentException("Cannot add revenues from different departments")
        }
        return new Revenue(department, amount + other.amount)
    }
    
    @Override
    boolean equals(Object obj) {
        if (!(obj instanceof Revenue)) return false
        return department == obj.department
    }
    
    @Override
    int hashCode() {
        return department.hashCode()
    }
    
    @Override
    String toString() {
        return "$department: \$${amount}"
    }
}

// Create a class that implements AggregationList
class RevenueReport implements AggregationList<Revenue> {
    String id
    
    RevenueReport(String id) {
        this.id = id
    }
    
    @Override
    boolean equals(Object obj) {
        if (!(obj instanceof RevenueReport)) return false
        return id == obj.id
    }
    
    @Override
    int hashCode() {
        return id.hashCode()
    }
}

// Usage example
def q1 = new RevenueReport("2023")
q1 << new Revenue("Sales", 100000.00)
q1 << new Revenue("Marketing", 50000.00)

def q2 = new RevenueReport("2023")
q2 << new Revenue("Sales", 120000.00)
q2 << new Revenue("IT", 75000.00)

// Adding a duplicate element - will be merged
q1 << new Revenue("Sales", 5000.00)
assert q1.list[0].amount == 105000.00

// Full join using + operator
def combined = q1 + q2
assert combined.list.size() == 3
assert combined.list.any { it.department == "Sales" && it.amount == 225000.00 }
assert combined.list.any { it.department == "Marketing" && it.amount == 50000.00 }
assert combined.list.any { it.department == "IT" && it.amount == 75000.00 }

// Left join - only keeps elements from the left side
def leftJoined = q1.leftJoin(q2)
assert leftJoined.list.size() == 2
assert leftJoined.list.any { it.department == "Sales" && it.amount == 225000.00 }
assert leftJoined.list.any { it.department == "Marketing" && it.amount == 50000.00 }
```

### Aggregator Class

A ready-to-use implementation of the AggregationList trait that can be used directly without creating a custom class.

#### Features

- Fully implemented AggregationList functionality
- Built-in identity management
- Simplified creation and management of aggregated lists

#### Example

```groovy
import it.grational.aggregate.Aggregator
import it.grational.aggregate.Summable

class DataPoint implements Summable<DataPoint> {
    String key
    double value
    
    DataPoint(String key, double value) {
        this.key = key
        this.value = value
    }
    
    @Override
    DataPoint plus(DataPoint other) {
        if (key != other.key) {
            throw new IllegalArgumentException("Cannot add data points with different keys")
        }
        return new DataPoint(key, value + other.value)
    }
    
    @Override
    boolean equals(Object obj) {
        if (!(obj instanceof DataPoint)) return false
        return key == obj.key
    }
    
    @Override
    int hashCode() {
        return key.hashCode()
    }
    
    @Override
    String toString() {
        return "$key: $value"
    }
}

// Create Aggregators with different IDs
def seriesA = new Aggregator<DataPoint>("series-a")
def seriesB = new Aggregator<DataPoint>("series-b")
def seriesADuplicate = new Aggregator<DataPoint>("series-a")

// Add data points
seriesA << new DataPoint("temp", 22.5)
seriesA << new DataPoint("humidity", 45.0)

seriesB << new DataPoint("temp", 23.1)
seriesB << new DataPoint("pressure", 1013.2)

seriesADuplicate << new DataPoint("temp", 21.8)
seriesADuplicate << new DataPoint("humidity", 47.5)

// Can add aggregators with the same ID
def combinedA = seriesA + seriesADuplicate
assert combinedA.list.size() == 2
assert combinedA.list.find { it.key == "temp" }.value == 44.3 // 22.5 + 21.8

// Cannot add aggregators with different IDs
try {
    seriesA + seriesB
    assert false // Should not reach here
} catch (IllegalArgumentException e) {
    assert e.message.contains("Cannot add different objects")
}

// Left join - keeps only elements from the left side
def leftJoined = seriesA.leftJoin(seriesADuplicate)
assert leftJoined.list.size() == 2
assert leftJoined.list.find { it.key == "temp" }.value == 44.3
assert leftJoined.list.find { it.key == "humidity" }.value == 92.5
```

## Advanced Usage

### Custom Equality for Aggregation

For the AggregationList to correctly merge identical objects, you need to override `equals()` and `hashCode()` methods in your classes to define what makes objects equal for aggregation purposes.

```groovy
class Product implements Summable<Product> {
    String sku
    String name
    int quantity
    
    @Override
    boolean equals(Object obj) {
        if (!(obj instanceof Product)) return false
        return sku == obj.sku  // Products are equal if they have the same SKU
    }
    
    @Override
    int hashCode() {
        return sku.hashCode()
    }
    
    @Override
    Product plus(Product other) {
        if (sku != other.sku) {
            throw new IllegalArgumentException("Cannot add products with different SKUs")
        }
        return new Product(sku: sku, name: name, quantity: quantity + other.quantity)
    }
}
```

### Type-Safe Aggregations

The library is fully generic-aware, allowing you to create type-safe aggregations:

```groovy
import it.grational.aggregate.Aggregator

// Type-safe aggregator for integers
def intAggregator = new Aggregator<Integer>("numbers")
intAggregator << 1
intAggregator << 2
intAggregator << 1  // Will be summed with the existing 1

assert intAggregator.list == [2, 2]  // The first 1 was increased to 2

// Type-safe aggregator for strings
def stringAggregator = new Aggregator<String>("words")
stringAggregator << "hello"
stringAggregator << "world"
stringAggregator << "hello"  // Will be concatenated with the existing "hello"

assert stringAggregator.list == ["hellohello", "world"]
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.