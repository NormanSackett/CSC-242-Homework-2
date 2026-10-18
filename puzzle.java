import java.io.*;
import java.util.Queue;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class puzzle {
	private class GameNode {
		GameState state;
		GameNode prev = null; //previous node
		int move = -1; //indicates which move was taken from the previous state to get to the current state
		int cost = 0;
		
		public GameNode(GameState s, GameNode p, int c, int m) {
			state = s;
			prev = p;
			cost = c;
			move = m;
		}
		
		public GameNode(GameState s, GameNode p, int m) {
			state = s;
			prev = p;
			move = m;
		}
		
		public GameNode(BufferedReader br) throws IOException {
			GameState newState = new GameState(br);
			state = newState;
			cost = 0;
		}
		
		public int getPriority() {
			return state.numDisplaced() + cost;
		}
	}
	
	public class GameState {
		int s;
		int[][] board;
		int emptyRow;
		int emptyCol;
		
		public GameState(BufferedReader br) throws IOException {
			s = Integer.valueOf(br.readLine());
			board = new int[s][s];
			for (int i = 0; i < s; i++) {
				String line = br.readLine();
				for (int j = 0; j < s; j++) {
					int nextSpace = line.indexOf(' ');
					int val;
					if (nextSpace != -1) {
						val = Integer.valueOf(line.substring(0, nextSpace));
					} else {
						val = Integer.valueOf(line);
					}
					board[i][j] = val;
					if (val == 0) {
						emptyRow = i;
						emptyCol = j;
					}
					line = line.substring(nextSpace + 1);
				}
			}
		}
		
		public GameState(int size, int[][] givenBoard, int r, int c) {
			s = size;
			board = givenBoard;
			emptyRow = r;
			emptyCol = c;
		}
		
		//returns moves as true or false in [up, down, left, right]
		public boolean[] getMoves() {
			boolean[] moveset = {true, true, true, true};
			if (emptyRow == s - 1)
				moveset[0] = false;
			if (emptyRow == 0)
				moveset[1] = false;
			if (emptyCol == s - 1)
				moveset[2] = false;
			if (emptyCol == 0)
				moveset[3] = false;
			
			return moveset;
		}
		
		//makes a move with 0=up, 1=down, 2=left, 3=right
		public GameState makeMove(int move) {
			int[][] newBoard = new int[s][s];
			for (int i = 0; i < s; i++) {
				for (int j = 0; j < s; j++) {
					newBoard[i][j] = board[i][j];
				}
			}
			switch (move) {
			case 0:
				newBoard[emptyRow][emptyCol] = board[emptyRow + 1][emptyCol];
				newBoard[emptyRow + 1][emptyCol] = 0;
				return new GameState(s, newBoard, emptyRow + 1, emptyCol);
			case 1:
				newBoard[emptyRow][emptyCol] = board[emptyRow - 1][emptyCol];
				newBoard[emptyRow - 1][emptyCol] = 0;
				return new GameState(s, newBoard, emptyRow - 1, emptyCol);
			case 2:
				newBoard[emptyRow][emptyCol] = board[emptyRow][emptyCol + 1];
				newBoard[emptyRow][emptyCol + 1] = 0;
				return new GameState(s, newBoard, emptyRow, emptyCol + 1);
			case 3:
				newBoard[emptyRow][emptyCol] = board[emptyRow][emptyCol - 1];
				newBoard[emptyRow][emptyCol - 1] = 0;
				return new GameState(s, newBoard, emptyRow, emptyCol - 1);
			}
			
			return null;
		}
		
		//heuristic for A* that returns the number of tiles out of place
		public int numDisplaced() {
			int counter = 0;
			int displaced = 0;
			for (int i = 0; i < s; i++) {
				for (int j = 0; j < s; j++) {
					if (board[i][j] != counter)
						displaced++;
					counter++;
				}
			}
			return displaced;
		}
		
		public void printBoard() {
			System.out.print(s);
			for (int i = 0; i < s; i++) {
				System.out.print('\n');
				for (int j = 0; j < s; j++) {
					if (j != 0)
						System.out.print(' ');
					System.out.print(board[i][j]);
				}
			}
			System.out.printf("\nempty location: %d, %d\n", emptyRow, emptyCol);
		}
	}

	public static void main(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try{
			puzzle p = new puzzle();
			GameNode node = p.new GameNode(br);
		
			if (args[0].equals("--astar")) {
				p.Astar(node);
			} else if (args[0].equals("--bfs")) {
				p.BFS(node);
			} else {
				System.out.println("Enter valid search algorithm argument (--astar or --bfs)");
			}
		} catch(IOException e) {
			System.out.println("file read error");
		}
	}
	
	private void Astar(GameNode n) {
		int expandedNodes = 0;
		//create a comparator that performs an integer comparison on the cost and heuristic value of the two states
		Comparator<GameNode> c = (state1, state2) -> Integer.compare(state1.getPriority(), state2.getPriority());
		PriorityQueue<GameNode> pq = new PriorityQueue<>(c);
		pq.add(n);
		
		while (pq.size() != 0) {
			GameNode node = pq.remove();
			if (node.state.numDisplaced() == 0) {
				System.out.println(printMoveList(node));
				System.out.println("Number of expanded nodes: " + expandedNodes);
				return;
			} else {
				boolean[] possibleMoves = node.state.getMoves();
				for (int i = 0; i < 4; i++) {
					if (possibleMoves[i]) {
						pq.add(new GameNode(node.state.makeMove(i), node, node.cost + 1, i));
						expandedNodes++;
					}
				}
			}
		}
	}
	
	private void BFS(GameNode n) {
		int expandedNodes = 0;
		Queue<GameNode> q = new LinkedList<>();
		q.add(n);
		
		while (q.size() != 0) {
			GameNode node = q.remove();
			if (node.state.numDisplaced() == 0) {
				System.out.println(printMoveList(node));
				System.out.println("Number of expanded nodes: " + expandedNodes);
				return;
			} else {
				boolean[] possibleMoves = node.state.getMoves();
				for (int i = 0; i < 4; i++) {
					if (possibleMoves[i]) {
						q.add(new GameNode(node.state.makeMove(i), node, i));
						expandedNodes++;
					}
				}
			}
		}
	}
	
	private String printMoveList(GameNode n) {
		if (n.prev == null) {
			return moveIntToStr(n.move);
		}
		return printMoveList(n.prev) + "\n" + moveIntToStr(n.move);
	}
	
	private String moveIntToStr(int m) {
		switch (m) {
		case 0:
			return "UP";
		case 1:
			return "DOWN";
		case 2:
			return "LEFT";
		case 3:
			return "RIGHT";
		}
		return "";
	}
}
