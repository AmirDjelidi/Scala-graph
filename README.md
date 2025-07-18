# Functional Graphs in Scala 3 & ZIO

_Final project for the functional programming course produced by Tom Klajn, Clément Tauziède and Amir Djelidi_

Target Skills: [C201], [C202].


## Project Overview

This project consists of creating a functional and immutable graph library and integrating it into an interactive console application developed with ZIO 2.

The project is divided into two main modules:

1.  **`core`**: A library containing the data structures for graphs (directed and undirected), main algorithms (Dijkstra, DFS, BFS), and utilities for JSON serialization and Graphviz export.
    
2.  **`app`**: A console application that uses the `core` library to model a campus map. It allows the user to calculate the shortest paths between buildings or between specific rooms within those buildings.
    

## Usage Instructions

#### Prerequisites

-   Java Development Kit (JDK), version 11 or higher.
    
-   `sbt` (Scala Build Tool), version 1.9.x or higher.
    

#### Build the Project

To compile both modules (`core` and `app`), run the following command at the project's root:

Bash

```
sbt compile

```

#### Run the Tests

To run the unit test suite (mainly for the `core` module), use the command:

Bash

```
sbt test

```

#### Run the Application

To start the interactive console application, execute:

Bash

```
sbt "app/run"

```

This will launch the program and display the main menu.

----------

## Design Decisions

Several key architectural choices were made for this project:

-   **`sbt` Subproject Structure**: The clear separation between `core` and `app` decouples the library's business logic from its application. This makes the `core` module completely independent and reusable.
    
-   **Immutability and Functional Programming**: The `core` library is built upon immutable data structures. Operations like `addEdge` do not modify the existing graph but instead return a new instance, thereby adhering to functional programming principles.
    
-   **Side Effect Management with ZIO 2**: The `app` is built on ZIO for clean, asynchronous, and functional management of side effects (file I/O, console interaction). The use of `ZLayer` (e.g., `GraphService`) allows for clear dependency injection and an excellent separation of concerns.
    
-   **Graph Merging at Startup**: To enable complex pathfinding (e.g., from room A-101 to room B-201), the `GraphService` merges the building graph (inter-building) and the room graph (intra-building) into a single "super-graph". This operation is transparent to the user and greatly simplifies the application logic.
    
-   **Advanced Graphviz Visualization**: The Graphviz export feature was designed not only to display a full graph but also to take a specific path as a parameter and highlight it, providing a powerful visual aid.
    

----------

## Usage Examples

Once the application is launched, a menu will guide you. To visualize the generated Graphviz code, you can use an online tool like **[Graphviz Online](https://dreampuf.github.io/GraphvizOnline/)**.

  
To make your experience easier, **[here](https://docs.google.com/spreadsheets/d/1KmLA951-AyWi5oktZ5oC0mWWmDaErYXbzIY9payL_uc/edit?usp=sharing/)** is a table where you will find all the rooms and buildings

**Example: Find a path from room 101 in building A to room 201 in building B**


1.  Choose option `3`.
    
2.  Enter the details for the starting room (`101`, `A`) and the destination room (`201`, `B`).

3.  The application will display the result in two parts, as described below.


#### Understanding the Path Output

-   **Two-Step Display**: The application first shows a simple text version of the path, indicating the weight (cost) of each segment. Example: `Salle(Accueil,A) ->(5)-> Salle(Accueil,B)`. Afterward, it generates the full Graphviz code to be copied and pasted.
    
-   **A Global View of the Graph**: When searching for a path between rooms, the Graphviz visualization is intentionally large. It displays the **entire merged graph** (all rooms in all buildings) to show how the sub-graphs are connected and to provide a global view of the campus.
    
-   **Path Color Coding**:
    
    -   The **start** node is colored **blue**.
        
    -   The **end** node is colored **green**.
        
    -   The **intermediate** nodes and edges of the path are highlighted in **red**.
        
-   **Weight of 0: The Elevator**: An edge with a weight of `0` between two rooms signifies an **elevator**, allowing for instant travel.
-   **One Accueil Only ?**:
    - Building C, there is only one reception area because it only has one room.
    - Building G represents the Louis Aragon station (imagined departure point).
    

----------

## Testing

The validation of the `core` library is ensured by unit tests written with ScalaTest. They cover graph creation, manipulation, algorithm correctness, and export validity. To run them, use the `sbt test` command.
