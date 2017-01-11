import java.awt.Toolkit;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;

// new class: to hold the coordinates of each element
class Position {
	public int row, col;

	public Position(int col, int row) {
		this.col = col;
		this.row = row;
	}
}

public class Snake {

	public static void main(String[] args) {
		
		int direction=0;
		int speed = 200;
		short score = 0;
		

		/*
		 * Setting the Lanterna Terminal (New Console)
		 * https://code.google.com/archive/p/lanterna/wikis/UsingTerminal.wiki
		 * importing library lanterna-2.1.7.jar
		 */
		Terminal terminal = TerminalFacade.createTerminal(System.in, System.out, Charset.forName("UTF8"));
		terminal.enterPrivateMode();
		TerminalSize terminalSize = terminal.getTerminalSize();
		terminal.setCursorVisible(false);
		
		System.out.println("Terminal size: \nColumns - "+terminalSize.getColumns()+", Rows - "+terminalSize.getRows());
		
		// Start the game or exit
		terminal.moveCursor(terminalSize.getColumns() / 2 - 11, terminalSize.getRows() / 2 - 2);
		write("Press any key to START ", terminal, true);
		terminal.moveCursor(terminalSize.getColumns() / 2 - 11, terminalSize.getRows() - 2);
		terminal.applyForegroundColor(Terminal.Color.BLUE);
		write("or Press Escape to EXIT", terminal, true);

		while (true) {
			Key pressedKey = terminal.readInput();
			if (pressedKey != null) {
				System.out.println(pressedKey);
				if (pressedKey.getKind() == Key.Kind.Escape) {
					terminal.exitPrivateMode();
					System.exit(0);
					break;
				} else {
					break;
				}
			}
		}
		terminal.clearScreen();

//		// Borderlines of the playing field
//		ArrayList<Position> borderLines = new ArrayList<Position>();
//		// top wall
//		for (int col = 0; col <= terminalSize.getColumns(); col++) {
//			borderLines.add(new Position(col, 0));
//		}
//		// bottom wall
//		for (int col = 0; col <= terminalSize.getColumns(); col++) {
//			borderLines.add(new Position(col, terminalSize.getRows() - 1));
//		}
//		// left wall
//		for (int row = 0; row <= terminalSize.getRows(); row++) {
//			borderLines.add(new Position(0, row));
//		}
//		// right wall
//		for (int row = 0; row <= terminalSize.getRows(); row++) {
//			borderLines.add(new Position(terminalSize.getColumns() - 1, row));
//		}
//		for (Position i : borderLines) {
//			terminal.applyForegroundColor(Terminal.Color.BLUE);
//			terminal.applyBackgroundColor(Terminal.Color.BLUE);
//			terminal.moveCursor(i.col, i.row);
//			terminal.putCharacter('-');
//		}
//
//		terminal.moveCursor(4, 0);
//		terminal.applyForegroundColor(Terminal.Color.WHITE);
//		terminal.applyBackgroundColor(Terminal.Color.BLUE);
//		write("Score: " + score, terminal, false);
//		printBorders(terminal, terminalSize, score);
		ArrayList<Position> borderLines = printBorders(terminal, terminalSize, score);

		// Array: coordinates of the four directions
		Position[] directions = new Position[] { 
				new Position(1, 0), // right  index 0
				new Position(-1, 0), // left  index 1
				new Position(0, 1), // down     index 2
				new Position(0, -1), // up  index 3
		};

		// Snake body
		Queue<Position> snakeBody = new LinkedList<Position>();
		for (int segment = 2; segment <= 7; segment++) {
			snakeBody.offer(new Position(segment, terminalSize.getRows() / 2));
		}
		Position snakeHead = new Position(8, terminalSize.getRows() / 2);
		printSnakeBody(terminal, snakeBody, snakeHead);
		
		// Print first snakeFood (random position)
		Position snakeFood = printSnakeFood(terminal, terminalSize, borderLines, snakeBody);
//		terminal.putCharacter('@');
		
		// Game engine info
		infoGameEngine(terminal, terminalSize, snakeFood, snakeBody, snakeHead);
		while (true) {
			Key pressedKey = terminal.readInput();
			if (pressedKey != null) {
				// System.out.println(pressedKey);
				if (pressedKey.getKind() == Key.Kind.ArrowUp) {
					if (direction != 2)
						direction = 3;
				}
				if (pressedKey.getKind() == Key.Kind.ArrowDown) {
					if (direction != 3)
						direction = 2;
				}
				if (pressedKey.getKind() == Key.Kind.ArrowLeft) {
					if (direction != 0)
						direction = 1;
				}
				if (pressedKey.getKind() == Key.Kind.ArrowRight) {
					if (direction != 1)
						direction = 0;
				}
				// Pause
				if (pressedKey.getKind() == Key.Kind.Enter) {
					Toolkit.getDefaultToolkit().beep();
					terminal.moveCursor(terminalSize.getColumns() / 2 - 4, terminalSize.getRows() / 2 - 2);
					terminal.applyForegroundColor(Terminal.Color.WHITE);
					write("P A U S E", terminal, false);
					boolean flashingText = true;
					while (true) {
						Key pauseKey = terminal.readInput();
						if (flashingText==false) {
							terminal.applyForegroundColor(Terminal.Color.CYAN);
							terminal.moveCursor(terminalSize.getColumns() / 2 - 12, terminalSize.getRows() / 2);
							write("Press any key to continue", terminal, false);
							
						} else if (flashingText==true){
							terminal.moveCursor(terminalSize.getColumns() / 2 - 12, terminalSize.getRows() / 2);
							terminal.applyForegroundColor(Terminal.Color.BLACK);
							write("Press any key to continue", terminal, false);
						}
						if (pauseKey != null) {
							terminal.clearScreen();
							printBorders(terminal, terminalSize, score);
							printSnakeBody(terminal, snakeBody, snakeHead);
							terminal.moveCursor(snakeFood.col, snakeFood.row);
							terminal.applyForegroundColor(Terminal.Color.GREEN);
							terminal.putCharacter('@');
							break;
						}
						try {
							Thread.sleep((int) 500);
						} catch (InterruptedException e) {

							e.printStackTrace();
						}
						flashingText = !flashingText;
						
					}

				}
				
				if (pressedKey.getKind() == Key.Kind.Escape) {
					terminal.exitPrivateMode();
					System.exit(0);
				}
			}
			Position newDirection = directions[direction];
			snakeHead = new Position(snakeHead.col + newDirection.col, snakeHead.row + newDirection.row);
			snakeHead = printSnakeBody(terminal, snakeBody, snakeHead);
			Position removeLast = snakeBody.poll();
			terminal.moveCursor(removeLast.col, removeLast.row);
			terminal.putCharacter(' ');
			delay(speed);
		}
	}

	// Print to console (terminal)
	private static void write(String text, Terminal terminal, boolean infoLengh) {
		boolean printStringLenth = infoLengh;
		char[] stringToChar = text.toCharArray();
		for (int i = 0; i < text.length(); i++) {
			terminal.putCharacter(stringToChar[i]);
		}
		if (printStringLenth) {
			System.out.println("Printed text: " + text + ", (length: " + stringToChar.length + ")");
		}
	}

	public static Position printSnakeBody(Terminal terminal, Queue<Position> snakeBody,
			Position snakeHeadNewPosition) {
		Position snakeHead;
		snakeBody.offer(snakeHeadNewPosition);
		snakeHead = snakeHeadNewPosition;
		for (Position segmentOfSnake : snakeBody) {
			terminal.applyForegroundColor(Terminal.Color.YELLOW);
			terminal.applyBackgroundColor(Terminal.Color.BLACK);
			terminal.moveCursor(segmentOfSnake.col, segmentOfSnake.row);
			if (segmentOfSnake.equals(snakeHeadNewPosition) == false) {
				terminal.putCharacter('₪');
			} else {
				terminal.putCharacter('о');
			}
		}
		return snakeHead;
	}

	public static Position printSnakeFood(Terminal terminal, TerminalSize terminalSize,
			ArrayList<Position> borderLines, Queue<Position> snakeBody) {
		Position foodPosition;
		int foodColumn;
		int foodRow;
		boolean isInSnake;
		do {
			isInSnake = false;
			// from 1 to terminalSize.getColumns() - 1
			foodColumn = (int) (Math.random() * ((terminalSize.getColumns() - 2)))+1;
			// from 1 to terminalSize.getRows() - 1
			foodRow = (int) (Math.random() * ((terminalSize.getRows() - 2)))+1;
			foodPosition = new Position(foodColumn, foodRow);

			for (Position i : snakeBody) {
				if (i.row == foodRow && i.col == foodColumn) {
					isInSnake = true;
				}
			}
		} while (isInSnake == true);
		terminal.moveCursor(foodPosition.col, foodPosition.row);
		terminal.applyForegroundColor(Terminal.Color.GREEN);
		terminal.putCharacter('@');
		return foodPosition;
	}
	public static void infoGameEngine (Terminal terminal, TerminalSize terminalSize,
			Position foodPosition, Queue<Position> snakeBody, Position snakeHead) {
		System.out.println("Terminal size: \nColumns - "+terminalSize.getColumns()+", Rows - "+terminalSize.getRows());
		System.out.println("Snake length: "+snakeBody.size());
		System.out.println("Snake head coordinates: "+snakeHead.col+", "+snakeHead.row);
		System.out.println("Snake food coordinates: "+foodPosition.col+", "+foodPosition.row);

	}
	
	public static ArrayList<Position> printBorders (Terminal terminal, TerminalSize terminalSize, short score) {
		// Borderlines of the playing field

		ArrayList<Position> borderLines = new ArrayList<Position>();
		// top wall
		for (int col = 0; col <= terminalSize.getColumns(); col++) {
			borderLines.add(new Position(col, 0));
		}
		// bottom wall
		for (int col = 0; col <= terminalSize.getColumns(); col++) {
			borderLines.add(new Position(col, terminalSize.getRows() - 1));
		}
		// left wall
		for (int row = 0; row <= terminalSize.getRows(); row++) {
			borderLines.add(new Position(0, row));
		}
		// right wall
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
		write("Score: " + score, terminal, false);
		
		return borderLines;
	}
	private static void delay(int speed) {
		try {
			Thread.sleep(speed);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}