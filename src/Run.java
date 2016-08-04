public class Run {

	@SuppressWarnings("static-access")
	public static void main(String[] args) {

		WumpusBoard wb = new WumpusBoard();
		wb.InitializeBoard();
		wb.PrintBoard();
		
		int moves =0;
		while(wb.Algorithm(moves)){
			moves++;
			if (moves > 9)
				moves = 0;
			wb.PrintBoard();
		}
	}
}
