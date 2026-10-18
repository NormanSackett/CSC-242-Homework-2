name: Norman Sackett
email: nsackett@u.rochester.edu

This program runs a breadth first search and an A* search on a sliding tiles puzzle.

The initial board is entered through a file in the format:

3

1 2 5

3 4 8

6 7 0

where '3' is the size and the other nine numbers are the board, with tile 0 being the empty space. The program is run with:

java puzzle --astar/--bfs < file

--astar/--bfs determines which algorithm gets run. The file is the aforementioned file being passed to System.in and is read from a file location rooted at the project's source file.
The program will output a list of moves which were used to find the solution and the number of nodes expanded.

The heuristic I used for the A* search is the number of displaced tiles. A* performed much better than the breadth first search, with the above example board only having 10 nodes expanded by the A* search and 102 nodes expanded by the breadth first search.
