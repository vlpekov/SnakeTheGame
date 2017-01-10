import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

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
			terminal.putCharacter('-');
		}

		terminal.moveCursor(4, 0);
		terminal.applyForegroundColor(Terminal.Color.WHITE);
		terminal.applyBackgroundColor(Terminal.Color.BLUE);
		write("Score: " + score, terminal);

		Position[] directions = new Position[] { new Position(1, 0), // right
				new Position(-1, 0), // left
				new Position(0, 1), // up
				new Position(0, -1), // down
		};

		// Snake body
		Queue<Position> snakeBody = new LinkedList<Position>();
		for (int segment = 2; segment <= 7; segment++) {
			snakeBody.offer(new Position(segment, terminalSize.getRows() / 2));
		}
		Position snakeHead = new Position(8, terminalSize.getRows() / 2);
		printSnakeBody(terminal, snakeBody, snakeHead);
	}

	// Print to console (terminal)
	private static void write(String text, Terminal terminal) {
		char[] stringToChar = text.toCharArray();
		for (int i = 0; i < text.length(); i++) {
			terminal.putCharacter(stringToChar[i]);
		}
	}

	private static Position printSnakeBody(Terminal terminal, Queue<Position> snakeBody, Position snakeHeadNewPosition) {
		Position snakeHead;
		snakeBody.offer(snakeHeadNewPosition);
		snakeHead=snakeHeadNewPosition;
		for (Position segmentOfSnake : snakeBody) {
			terminal.applyForegroundColor(Terminal.Color.YELLOW);
			terminal.applyBackgroundColor(Terminal.Color.BLACK);
			terminal.moveCursor(segmentOfSnake.col, segmentOfSnake.row);
			if (segmentOfSnake.equals(snakeHeadNewPosition) == false) {
				terminal.putCharacter('₪');
			} else {
				terminal.putCharacter('o');
			}
		}
		return snakeHead;
	}

}