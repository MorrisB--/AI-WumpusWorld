import java.util.Random;

public class WumpusBoard {
	public static Tile[][] board;
	public int points = 0;
	public int arrow = 1;
	public boolean deadWumpus = false;

	public static void InitializeBoard() {
		board = new Tile[4][4];
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				board[i][j] = new Tile(i, j);
			}
		}

		// Agent starts at tile 0,0
		// 0,0 is visited, it is OK
		board[0][0].OK = true;
		board[0][0].V = true;
		board[0][0].A = true;

		// Assign tile adjacents
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				// System.out.println("i: " + i + " j: " + j);
				if (j - 1 > -1)
					board[i][j].Adjacents.add(board[i][j - 1]);
				if (j + 1 < board[i].length)
					board[i][j].Adjacents.add(board[i][j + 1]);
				if (i - 1 > -1)
					board[i][j].Adjacents.add(board[i - 1][j]);
				if (i + 1 < board.length)
					board[i][j].Adjacents.add(board[i + 1][j]);
			}
		}
		int wx = 0;
		int wy = 0;
		Random rand = new Random();

		// Place the wumpus
		while ((wx == 0 && wy == 0)) {
			wx = rand.nextInt(3) + 1;
			wy = rand.nextInt(3) + 1;
			if ((wx == 0 && wy == 0) || (wx == 1 && wy == 0) || (wx == 0 && wy == 1) || (wx == 1 && wy == 1)) {
				wx = 0;
				wy = 0;
				continue;
			} else {
				board[wx][wy].W = true;
				board[wx][wy].SpreadStench();
			}
		}
		System.out.println("The wumpus is at: " + wx + ", " + wy);

		int pits = 3;
		int i = 0;
		int px = 0;
		int py = 0;

		// Place pits
		while (i < pits) {
			px = 0;
			py = 0;
			while (px == 0 && py == 0) {
				px = rand.nextInt(3) + 1;
				py = rand.nextInt(3) + 1;
				if ((px == 0 && py == 0) || (px == 1 && py == 0) || (px == 0 && py == 1) || (px == 1 && py == 1)
						|| board[px][py].W || board[px][py].P)
					continue;
				else {
					board[px][py].P = true;
					board[px][py].SpreadBreeze();
					System.out.println("Pit " + (i + 1) + " is at: " + px + ", " + py);
					i++;
				}
			}
		}

		int gx = 0;
		int gy = 0;
		boolean goldPlaced = false;

		// Place the gold
		while (!goldPlaced) {
			gx = rand.nextInt(3) + 1;
			gy = rand.nextInt(3) + 1;
			if ((gx == 0 && gy == 0) || (gx == 1 && gy == 1) || (gx == 1 && gy == 0) || (gx == 0 && gy == 1)
					|| board[gx][gy].W || board[gx][gy].P) {
				continue;
			} else {
				board[gx][gy].G = true;
				goldPlaced = true;
				System.out.println("The gold is at: " + gx + ", " + gy);
			}
		}
	}

	public void PrintBoard() {
		System.out.println();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				System.out.print(board[i][j].toString() + " ");
			}
			System.out.println();
		}
	}

	public void MoveAgent(int x, int y) {
		// Moves agent to the tile at [x,y]. Has to be adjacent to the agent
		// tile.
		// Print out all the moves made by the agent.
		points--;
	}

	public void AgentDied() {
		// This method is called when the agent comes in contact with the wumpus
		// or falls into a pit.
		// Game is over
		points -= 1000;
	}

	public void ShootArrow(int x, int y) {
		// OPTIONAL
		// Shoots the arrow towards the tile at [x,y]. [x,y] should be adjacent
		// to the agent tile.
		// Once the arrow is shot, it will traverse every tile on its path.
		// If it hits the wumpus, deadWumpus becomes true and the agent doesn't
		// have to worry about the W anymore.
		points -= 10;
	}

	public void FlagOK(Tile t) {
		t.OK = true;
	}

	public void FlagVisited(Tile t) {
		t.V = true;
	}

	public boolean Algorithm(int moves) {

		// Finding the agent
		int i = 0, j = 0;
		for (int a = 0; a < board.length; a++)
			for (int b = 0; b < board[a].length; b++)
				if (board[a][b].A == true) {
					i = a;
					j = b;
				}

		// Mark current tile as visited
		board[i][j].V = true;

		/*
		 * If there is a stench or a breeze mark adjacent tiles as D
		 * (dangerous), ONLY IF, they have not been visited AND its not marked
		 * as OK.
		 */

		if (board[i][j].B || board[i][j].S) {

			// Checking the tile above
			if (i - 1 >= 0 && !board[i - 1][j].V && !board[i - 1][j].OK)
				board[i - 1][j].D = true;

			// Checking the tile to the right
			if (j + 1 < board[i].length && !board[i][j + 1].V && !board[i][j + 1].OK)
				board[i][j + 1].D = true;

			// Checking the tile below
			if (i + 1 < board.length && !board[i + 1][j].V && !board[i + 1][j].OK)
				board[i + 1][j].D = true;

			// Checking the tile to the left
			if (j - 1 >= 0 && board[i][j - 1].V && board[i][j - 1].OK)
				board[i][j - 1].D = true;

		}

		/*
		 * If there is not a breeze or a stench then we know that the ones
		 * around (even if we marked them as dangerous as the past, are safe.
		 */
		else {
			// Checking the tile above
			if (i - 1 >= 0 && (!board[i - 1][j].P && !board[i - 1][j].W))
				board[i - 1][j].OK = true;

			// Checking the tile to the right
			if (j + 1 < board[i].length && (!board[i][j + 1].P && !board[i][j + 1].W))
				board[i][j + 1].OK = true;

			// Checking the tile below
			if (i + 1 < board.length && (!board[i + 1][j].P && !board[i + 1][j].W))
				board[i + 1][j].OK = true;

			// Checking the tile to the left
			if (j - 1 >= 0 && (!board[i][j - 1].P && !board[i][j - 1].W))
				board[i][j - 1].OK = true;
		}

		/*
		 * Moving the agent
		 */

		// The Agent can move if the tile is marked as dangerous AND OK
		// Checking the tile above
		if (i - 1 >= 0 && !board[i - 1][j].V && (!board[i - 1][j].D || board[i - 1][j].OK)) {
			board[i][j].A = false;
			board[i - 1][j].A = true;
		}
		// Checking the tile to the right
		else if (j + 1 < board[i].length && !board[i][j + 1].V && (!board[i][j + 1].D || board[i][j + 1].OK)) {
			board[i][j].A = false;
			board[i][j + 1].A = true;
		}
		// Checking the tile below
		else if (i + 1 < board.length && !board[i + 1][j].V && (!board[i + 1][j].D || board[i + 1][j].OK)) {
			board[i][j].A = false;
			board[i + 1][j].A = true;
		}
		// Checking the tile to the left
		else if (j - 1 >= 0 && !board[i][j - 1].V && (!board[i][j - 1].D || board[i][j - 1].OK)) {
			board[i][j].A = false;
			board[i][j - 1].A = true;
		}

		/*
		 * The tiles around the agent are either Dangerous or Visited, time to
		 * move back to a visited Tile or make a dangerous move.
		 * 
		 * If board[i][j] is still true, we know the above if statements have
		 * not been executed, and it is time to move to a dangerous location.
		 */

		// DO DANGEROUS STUFF
		if (moves > 8) {
			if (board[i][j].A) {

				// First move to dangerous tiles that aren't visited

				// Checking the top tile
				if (i - 1 >= 0 && board[i - 1][j].D && !board[i - 1][j].V && Math.random() > 0.3) {
					System.out.println("DANGEROUS MOVE!");
					board[i][j].A = false;
					board[i - 1][j].A = true;
					// Mark OK as true if breeze or stench AND no pit or wumpus
					if ((board[i - 1][j].B || board[i - 1][j].S) && (!board[i - 1][j].P && !board[i - 1][j].W))
						board[i - 1][j].OK = true;
					// If there is a pit or Wumpus, print
					if (board[i - 1][j].W)
						System.out.println("Hit the Wumpus!");
					if (board[i - 1][j].P)
						System.out.println("Fell in a pit!");
				}
				// Checking the right tile
				else if (j + 1 < board.length && board[i][j + 1].D && !board[i][j + 1].V && Math.random() > 0.3) {
					System.out.println("DANGEROUS MOVE!");
					board[i][j].A = false;
					board[i][j + 1].A = true;
					// Mark OK as true if breeze or stench AND no pit or wumpus
					if ((board[i][j + 1].B || board[i][j + 1].S) && (!board[i][j + 1].P && !board[i][j + 1].W))
						board[i][j + 1].OK = true;
					// If there is a pit or Wumpus, print
					if (board[i][j + 1].W)
						System.out.println("Hit the Wumpus!");
					if (board[i][j + 1].P)
						System.out.println("Fell in a pit!");
				}
				// Checking the tile below
				else if (i + 1 < board[i].length && board[i + 1][j].D && !board[i + 1][j].V && Math.random() > 0.3) {
					System.out.println("DANGEROUS MOVE!");
					board[i][j].A = false;
					board[i + 1][j].A = true;
					// Mark OK as true if breeze or stench AND no pit or wumpus
					if ((board[i + 1][j].B || board[i + 1][j].S) && (!board[i + 1][j].P && !board[i + 1][j].W))
						board[i + 1][j].OK = true;
					// If there is a pit or Wumpus, print
					if (board[i + 1][j].W)
						System.out.println("Hit the Wumpus!");
					if (board[i + 1][j].P)
						System.out.println("Fell in a pit!");
				}
				// Checking the tile to the left
				else if (j - 1 >= 0 && board[i][j - 1].D && !board[i][j - 1].V && Math.random() > 0.3) {
					System.out.println("DANGEROUS MOVE!");
					board[i][j].A = false;
					board[i][j - 1].A = true;
					// Mark OK as true if breeze or stench AND no pit or wumpus
					if ((board[i][j - 1].B || board[i][j - 1].S) && (!board[i][j - 1].P && !board[i][j - 1].W))
						board[i][j - 1].OK = true;
					// If there is a pit or Wumpus, print
					if (board[i][j - 1].W)
						System.out.println("Hit the Wumpus!");
					if (board[i][j - 1].P)
						System.out.println("Fell in a pit!");
				}

				// Now check dangerous tiles that are visited

				// Checking the top tile
				else if (i - 1 >= 0 && board[i - 1][j].D && Math.random() > 0.3) {
					System.out.println("DANGEROUS MOVE!");
					board[i][j].A = false;
					board[i - 1][j].A = true;
					// Mark OK as true if breeze or stench AND no pit or wumpus
					if ((board[i - 1][j].B || board[i - 1][j].S) && (!board[i - 1][j].P && !board[i - 1][j].W))
						board[i - 1][j].OK = true;
					// If there is a pit or Wumpus, print
					if (board[i - 1][j].W)
						System.out.println("Hit the Wumpus!");
					if (board[i - 1][j].P)
						System.out.println("Fell in a pit!");
				}
				// Checking the right tile
				else if (j + 1 < board.length && board[i][j + 1].D && Math.random() > 0.3) {
					System.out.println("DANGEROUS MOVE!");
					board[i][j].A = false;
					board[i][j + 1].A = true;
					// Mark OK as true if breeze or stench AND no pit or wumpus
					if ((board[i][j + 1].B || board[i][j + 1].S) && (!board[i][j + 1].P && !board[i][j + 1].W))
						board[i][j + 1].OK = true;
					// If there is a pit or Wumpus, print
					if (board[i][j + 1].W)
						System.out.println("Hit the Wumpus!");
					if (board[i][j + 1].P)
						System.out.println("Fell in a pit!");
				}
				// Checking the tile below
				else if (i + 1 < board[i].length && board[i + 1][j].D && Math.random() > 0.3) {
					System.out.println("DANGEROUS MOVE!");
					board[i][j].A = false;
					board[i + 1][j].A = true;
					// Mark OK as true if breeze or stench AND no pit or wumpus
					if ((board[i + 1][j].B || board[i + 1][j].S) && (!board[i + 1][j].P && !board[i + 1][j].W))
						board[i + 1][j].OK = true;
					// If there is a pit or Wumpus, print
					if (board[i + 1][j].W)
						System.out.println("Hit the Wumpus!");
					if (board[i + 1][j].P)
						System.out.println("Fell in a pit!");
				}
				// Checking the tile to the left
				else if (j - 1 >= 0 && board[i][j - 1].D && Math.random() > 0.3) {
					System.out.println("DANGEROUS MOVE!");
					board[i][j].A = false;
					board[i][j - 1].A = true;
					// Mark OK as true if breeze or stench AND no pit or wumpus
					if ((board[i][j - 1].B || board[i][j - 1].S) && (!board[i][j-1].P && !board[i][j-1].W))
						board[i][j - 1].OK = true;
					// If there is a pit or Wumpus, print
					if (board[i][j - 1].W)
						System.out.println("Hit the Wumpus!");
					if (board[i][j - 1].P)
						System.out.println("Fell in a pit!");
				}

			}
		}

		// DO SAFE STUFF

		else {
			if (board[i][j].A) {

				// Checking the tile above
				if (i - 1 >= 0 && board[i - 1][j].V && (!board[i - 1][j].D || board[i - 1][j].OK)
						&& Math.random() > 0.3) {
					board[i][j].A = false;
					board[i - 1][j].A = true;
				}
				// Checking the tile to the right
				else if (j + 1 < board[i].length && board[i][j + 1].V && (!board[i][j + 1].D || board[i][j + 1].OK)
						&& Math.random() > 0.3) {
					board[i][j].A = false;
					board[i][j + 1].A = true;
				}
				// Checking the tile below
				else if (i + 1 < board.length && board[i + 1][j].V && (!board[i + 1][j].D || board[i + 1][j].OK)
						&& Math.random() > 0.3) {
					board[i][j].A = false;
					board[i + 1][j].A = true;
				}
				// Checking the tile to the left
				else if (j - 1 >= 0 && board[i][j - 1].V && (!board[i][j - 1].D || board[i][j - 1].OK)
						&& Math.random() > 0.3) {
					board[i][j].A = false;
					board[i][j - 1].A = true;

				}
			}
		}

		/*
		 * Return true or false if the agent hit the gold.
		 */

		// Finding the Agent
		for (int a = 0; a < board.length; a++)
			for (int b = 0; b < board[a].length; b++)
				if (board[a][b].A == true) {
					i = a;
					j = b;
				}

		// Checking if Agent hit the gold
		if (board[i][j].G) {
			this.PrintBoard();
			System.out.println("Found the gold!");
			return true;
		}

		return false;
	}

}
