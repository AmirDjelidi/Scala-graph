# Functional Programming in Scala 3 - Functional Graphs

Welcome to the final project for your Functional Programming in Scala 3 class. This project is designed to assess your understanding and application of functional programming principles. The grading will be based on the proper behavior of the implementation, adherence to functional programming principles, test coverage, project organization and the quality and completeness of the documentation.

**Target Skills: [C201], [C202]**

**Project Deadline: Friday, 18 July 2025**

## Project Overview

You will design and implement a graph data structure library with various operations, integrate it into a ZIO 2 application and document your work comprehensively. The project will involve the following key components:

1. **Graph Data Structure**
2. **Graph Operations**
3. **ZIO 2 Application Integration**
4. **Comprehensive Documentation**

## Detailed Instructions

### 1. Graph Data Structure

#### Data Structure Design

A [graph](https://www.geeksforgeeks.org/graph-data-structure-and-algorithms/) is a collection of vertices (nodes) and edges (connections between nodes). A graph can be directed (edges have a direction) or undirected (edges have no direction). Graph can have weights (values) associated with edges, to represent the cost or distance between vertices.

- **Task**: Design an interface to represent a graph, parameterized by type.
- **Examples of Methods to Implement**:
    - Get all vertices
    - Get all edges
    - Get neighbors of a given vertex
    - Add an edge
    - Remove an edge

#### Implementations

Different types of graphs include:

1. **Directed Graph (Digraph)**: Edges have a direction, going from one vertex to another.
1. **Undirected Graph**: Edges have no direction, connecting two vertices bidirectionally.
1. **Weighted Edges**: To simplify the design of this project, edges will always have weights.


- **Task**: Implement the defined interface for the following types of graphs:
    - Directed Graph (Digraph)
    - Undirected Graph

#### JSON Encoding/Decoding

JSON (JavaScript Object Notation) is a lightweight data interchange format. Encoding and decoding involve converting
between graph objects and their JSON representations.

- **Task**: Implement a way to encode and decode graphs from and to JSON.
- **Hint**: Use the `zio-json` library for JSON handling.

#### GraphViz Representation

[GraphViz](https://graphviz.org) is a tool for visualizing graphs. Generating a GraphViz representation involves
creating a textual description of the graph in the DOT language.

- **Task**: Implement a way to generate a GraphViz representation of a graph.
- **Hint**: Use **implicit extensions** and consider the `foldLeft` method in the Scala collections standard library.

#### Testing

Ensure the implemented data structures work as expected by writing unit tests.

- **Task**: Implement unit tests to ensure the correctness of the above implementations.
- **Hint**: Use ScalaTest [FlatSpec](https://www.scalatest.org/user_guide/selecting_a_style) style for writing unit tests.

### 2. Graph Operations

- **Hint**: Make the following implementation independent of the notion of graph from the previous section.

#### Depth First Search (DFS)

DFS is an algorithm for traversing or searching tree or graph data structures. It starts at a source vertex and explores
as far as possible along each branch before backtracking.

- **Task**: Implement and test DFS.
- **Hint**: Provide a tail recursive solution.

#### Breadth First Search (BFS)

BFS is an algorithm for traversing or searching tree or graph data structures. It starts at a source vertex and explores
all the neighbor vertices at the present depth before moving on to vertices at the next depth level.

- **Task**: Implement and test BFS.
- **Hint**: Provide a tail recursive solution.

#### Cycle Detection

Cycle detection in a graph is the process of finding a cycle (a path of edges and vertices wherein a vertex is reachable
from itself).

- **Task**: Use DFS to detect cycles in a graph.
- **Hint**: Provide a tail recursive solution.

#### Dijkstra's Algorithm

Dijkstra’s algorithm is used for finding the shortest path from a single source vertex to all other vertices in a
weighted graph with non-negative weights.

- **Task**: Implement and test Dijkstra's algorithm for shortest paths.

#### Testing

Ensure the implemented operations work as expected by writing unit tests.

- **Task**: Implement unit tests to ensure the correctness of the above implementations.
- **Hint**: Use ScalaTest [FlatSpec](https://www.scalatest.org/user_guide/selecting_a_style) style for writing unit tests.

### 3. ZIO 2 Application Integration

#### Application Design

[ZIO](https://zio.dev/) is a library for asynchronous and concurrent programming in Scala. It simplifies handling side-effects in functional
programming.

- **Task**: Integrate the core library and operations into a terminal-based interactive ZIO 2 application.
- **Hint**: Start with the [Getting Started with ZIO](https://zio.dev/overview/getting-started/) tutorial to learning how to create your first ZIO 2 application.
- **Hint**: Use the previous work to ease graph construction.
- **Hint**: Choose a scenario and a type for your nodes (you can create your own case classes).

## Project Organization

- **Use `sbt` subprojects**: Separate the core library from the ZIO application using [subprojects](https://www.scala-sbt.org/1.x/docs/Multi-Project.html#Build-wide+settings).
- **Scala Worksheets**: Use Scala worksheets in addition to unit tests to experiment and validate implementations.
- **Version Control**: Use Git for version control throughout the project. Regularly commit your changes and use meaningful commit messages.

## Documentation

### README Requirements

Your README should be comprehensive and include:

- **Project Overview**: Brief description of the project and its components.
- **Instructions**: How to build, test and run the application.
- **Design Decisions**: Explanation of key design decisions.
- **Usage Examples**: Examples of how to use the application.
- **Testing**: How to run the tests and an overview of the test coverage.

## Grading Criteria

- **Implementation Behavior**: Correct and expected functionality of the implemented features.
- **Functional Programming Principles**: Adherence to functional programming paradigms.
- **Test Coverage**: Comprehensive unit tests covering all implemented features.
- **Project Organization**: Clear and logical project structure.
- **Documentation Quality**: Completeness and clarity of the documentation.

**Project Deadline: Friday, 18 July 2025**

Good luck with your project! If you have any questions, please don't hesitate to ask.