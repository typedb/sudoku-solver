# sudoku-solver

## Solving sudoku
Build with maven: 

`mvn package`

Run: 

`java -jar target/sudoku-solver-0.1.jar [sudoku-path]`

Running the command will solve the sudoku specified in the `sudoku-path`.
The default `sudoku-path` is `src/main/resources/sudoku`. 


## Defining sudoku
Sudokus need to be defined in the file specified in the `sudoku-path`. A sudoku is specified by providing a space 
delimited list of cells, with cells to be filled marked using `0`. Sudokus of size 6x6 are supported.

Example:

To solve:

|   |   | 3 | 6 |   |   |
|---|---|---|---|---|---|
|   | 2 |   |   |   | 4 |
| 5 |   |   |   | 6 |   |
|   | 3 |   |   |   | 5 |
| 3 |   |   |   | 1 |   |
|   |   | 1 | 4 |   |   |
 
The sudoku needs to be defined in the following way:
```
0 0 3 6 0 0
0 2 0 0 0 4
5 0 0 0 6 0
0 3 0 0 0 5
3 0 0 0 1 0
0 0 1 4 0 0
```
