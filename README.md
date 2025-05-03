# Complex-Graph-traversal

# GraphWorkFlow: Complex Graph Traversal

## Overview
This Java program simulates a workflow system represented as a directed graph. Each node in the graph represents a task that prints its name. The traversal of nodes must satisfy:
- A node executes **only after all its parent nodes have completed**.
- If a node has multiple children, they should be processed **in parallel** (using multithreading).
- Each node must execute **only once**.

The input is read from the console:
- First, the number of nodes (vertices).
- Then, node definitions in the form `id:Node-Name`.
- Next, the number of edges.
- Then, each edge in the form `parent:child`.

The starting node is always `1`.

---

## Approach & Design Decisions

### 1. Data Structures
- `nodeNames (Map<Integer, String>)`: Stores node id → node name.
- `adjacencyList (Map<Integer, List<Integer>>)` : Directed graph structure.
- `dependencyCount (Map<Integer, Integer>)`: Keeps track of how many parent nodes each node has (i.e., in-degree).
- `executedNodes (Set<Integer>)`: Tracks which nodes have been executed to avoid re-execution.
- `threadPool (ExecutorService)`: Used to parallelize child node execution with `CompletableFuture`.

### 2. Graph Traversal
- Start from node 1 using `CompletableFuture.runAsync()`.
- For each node:
  - Print its name.
  - For each child node:
    - Decrement its dependency count.
    - If all parents are done (`dependencyCount == 0`), execute it asynchronously.

### 3. Threading/Concurrency
- Uses **`CompletableFuture` with `Executors.newCachedThreadPool()`** to allow non-blocking parallel execution of child nodes.
- **Thread safety** is handled by:
  - Using `ConcurrentHashMap.newKeySet()` for `executedNodes`.
  - Synchronizing access to `dependencyCount` when updating.

### 4. Execution Order
- Child nodes of a node are sorted to maintain a consistent execution order when eligible.
- Each node is executed only once due to the `executedNodes` check.

---

## Assumptions
- Input format is strictly followed.
- Node 1 is always the root and will have no incoming edges.
- The graph may converge (multiple parents) or diverge (multiple children), but forms a **tree-like DAG**.
- Cycles are not present in the graph (assumed by design).

---

## Libraries & Tools Used
- **Java Core Libraries** only:
  - `java.util` for data structures
  - `java.util.concurrent` for multithreading (`CompletableFuture`, `ExecutorService`, etc.)

No external libraries are used.

---

## Sample Input
```
5
1:Node-1
2:Node-2
3:Node-3
4:Node-4
5:Node-5
5
1:2
1:3
2:4
2:5
3:5
```

## Sample Output
```
Node-1
Node-2
Node-4
Node-3
Node-5
5
```

---

## Limitations & Notes
- The program assumes valid input with no cycles.
- For true parallelism and better performance, `.join()` could be replaced with `CompletableFuture.allOf(...).join()` to wait for all children concurrently.
- The design balances correctness and thread-safety using minimal synchronization.
