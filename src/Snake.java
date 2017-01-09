import java.nio.charset.Charset;
import java.util.ArrayList;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;

class Position {
	public int row, col;
	public Position(int col, int row) {
		this.col = col;
		this.row = row;
	}
}

public class Snake {

	public static void main(String[] args) {

		int score = 0;

		/*
		 * Стандартната конзола на JAVA е недъгава, затова декларираме нова. За
		 * целта използваме LANTERNA:
		 * https://code.google.com/archive/p/lanterna/wikis/UsingTerminal.wiki
		 * импортираме lanterna-2.1.7.jar
		 */
		Terminal terminal = TerminalFacade.createTerminal(System.in, System.out, Charset.forName("UTF8"));
		terminal.enterPrivateMode();
		TerminalSize terminalSize = terminal.getTerminalSize();
		terminal.setCursorVisible(false);
		
		// Старт на играта или изход
		terminal.moveCursor(terminalSize.getColumns() / 2 - 11, terminalSize.getRows() / 2 - 2);
		write("Press any key to START ", terminal);
		terminal.moveCursor(terminalSize.getColumns() / 2 - 11, terminalSize.getRows() - 2);
		terminal.applyForegroundColor(Terminal.Color.BLUE);
		write("or Press Escape to EXIT", terminal);
		
		while (true) {
			Key p = terminal.readInput();
			if (p != null) {
				System.out.println(p);
				if (p.getKind() == Key.Kind.Escape) {
					terminal.exitPrivateMode();
					System.exit(0);
					break;
				} else {
					break;
				}
			}
		}
		terminal.clearScreen();

		// Очертаваме граница на игралното поле
		ArrayList<Position> borderLines = new ArrayList<Position>();
		// горна стена
		for (int col = 0; col <= terminalSize.getColumns(); col++) {
			borderLines.add(new Position(col, 0));
		}
		// долана стена
		for (int col = 0; col <= terminalSize.getColumns(); col++) {
			borderLines.add(new Position(col, terminalSize.getRows() - 1));
		}
		// лява стена
		for (int row = 0; row <= terminalSize.getRows(); row++) {
			borderLines.add(new Position(0, row));
		}
		// дясна стена
		for (int row = 0; row <= terminalSize.getRows(); row++) {
			borderLines.add(new Position(terminalSize.getColumns() - 1, row));
		}
		for (Position i : borderLines) {
			terminal.applyForegroundColor(Terminal.Color.BLUE);
			terminal.applyBackgroundColor(Terminal.Color.BLUE);
			terminal.moveCursor(i.col, i.row);
			terminal.putCharacter('█');
		}

		terminal.moveCursor(4, 0);
		terminal.applyForegroundColor(Terminal.Color.WHITE);
		terminal.applyBackgroundColor(Terminal.Color.BLUE);
		write("Score: " + score, terminal);

		Position[] directions = new Position[] { 
				new Position(1, 0), // дясно
				new Position(-1, 0), // ляво
				new Position(0, 1), // нагоре
				new Position(0, -1), // надолу
		}; 
		
	}
	// метод за отпечатване на конзолата
	private static void write(String text, Terminal terminal) {
		char[] stringToChar = text.toCharArray();
		for (int i = 0; i < text.length(); i++) {
			terminal.putCharacter(stringToChar[i]);
		}
	}

}