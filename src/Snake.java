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
		
		byte direction = 0;
		short speed = 200;
		short score = 0;
		short winningScore = 60;
		short acceleration = speed;
		boolean difficultyHard = false;
		long foodTimeStart = System.currentTimeMillis();
		long foodExpiringTime = 20000;
		byte foodBlinkingOption = 1;

		/*
		 * Setting the Lanterna Terminal (New Console)
		 * https://code.google.com/archive/p/lanterna/wikis/UsingTerminal.wiki
		 * importing library lanterna-2.1.7.jar
		 */
		Terminal terminal = TerminalFacade.createTerminal(System.in, System.out, Charset.forName("UTF8"));
		terminal.enterPrivateMode();
		TerminalSize terminalSize = terminal.getTerminalSize();
		terminal.setCursorVisible(false);
		
		
		// Choose difficulty
		terminal.moveCursor(terminalSize.getColumns() / 2 - 24, terminalSize.getRows() / 2 - 2);
		write("Choose difficulty:", terminal, true);
		terminal.moveCursor(terminalSize.getColumns() / 2 - 4, terminalSize.getRows()  / 2-2);
		terminal.applyForegroundColor(Terminal.Color.GREEN);
		write("e - easy", terminal, true);
		terminal.moveCursor(terminalSize.getColumns() / 2 - 4, terminalSize.getRows() / 2 -1);
		terminal.applyForegroundColor(Terminal.Color.RED);
		write("h - hard", terminal, true);
		while (true) {
			Key pressedKey = terminal.readInput();
			// time delay - CPU friendly
			delay(1);
			if (pressedKey != null) {
				System.out.println(pressedKey);
				if (pressedKey.getCharacter() == 'h') {
					System.out.println("Natisnahte hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
					difficultyHard = true;
					System.out.println(difficultyHard);
					break;
				}
				if (pressedKey.getCharacter() == 'e') {
					difficultyHard = false;
					System.out.println("Lesno NIVO");
					break;
				}
				if (pressedKey.getKind() == Key.Kind.Escape) {
					turnOff(terminal);
					break;
				}
			}
		}
		terminal.clearScreen();	
		
		// Start the game or exit
		terminal.moveCursor(terminalSize.getColumns() / 2 - 11, terminalSize.getRows() / 2 - 2);
		write("Press any key to START ", terminal, true);
		terminal.moveCursor(terminalSize.getColumns() / 2 - 11, terminalSize.getRows() - 2);
		terminal.applyForegroundColor(Terminal.Color.BLUE);
		write("or Press Escape to EXIT", terminal, true);

		while (true) {
			Key pressedKey = terminal.readInput();
			// time delay - CPU friendly
			delay(1);
			if (pressedKey != null) {
				System.out.println(pressedKey);
				if (pressedKey.getKind() == Key.Kind.Escape) {
					turnOff(terminal);
					break;
				} else {
					break;
				}
				
			}
		}
		terminal.clearScreen();
		ArrayList<Position> borderLines = printBorders(terminal, terminalSize, score);

		// Array: coordinates of the four directions
		Position[] directions = new Position[] { 
				new Position(1, 0), // right  index 0
				new Position(-1, 0), // left  index 1
				new Position(0, 1), // down     index 2
				new Position(0, -1), // up  index 3
		};
		byte right = 0;
		byte left = 1;
		byte down = 2;
		byte up = 3;
		
		// Snake body
		Queue<Position> snakeBody = new LinkedList<Position>();
		for (int segment = 2; segment <= 7; segment++) {
			snakeBody.offer(new Position(segment, terminalSize.getRows() / 2));
		}
		Position snakeHead = new Position(7, terminalSize.getRows() / 2);
		printSnakeBody(terminal, snakeBody, snakeHead);
		
		// Print first snakeFood (random position)
		Position snakeFood = printSnakeFood(terminal, terminalSize, borderLines, snakeBody);
		avelableKeys(terminal, terminalSize);
		
		// Game engine info
		infoGameEngine(terminal, terminalSize, snakeFood, snakeBody, snakeHead, speed);
		byte arrowUpCount = 0;
		byte arrowDownCount = 0;
		byte arrowLeftCount = 0;
		byte arrowRightCount = 0;
		while (true) {
			acceleration=speed;
			Key pressedKey = terminal.readInput();
			if (pressedKey != null) {
				System.out.println(pressedKey);
				if (pressedKey.getKind() == Key.Kind.ArrowUp) {
					if (direction != down) {
						direction = up;
						arrowUpCount++;
						arrowDownCount = 0;
						arrowLeftCount = 0;
						arrowRightCount = 0;
					}
				}
				if (pressedKey.getKind() == Key.Kind.ArrowDown) {
					if (direction != up) {
						direction = down;
						arrowUpCount = 0;
						arrowDownCount++;
						arrowLeftCount = 0;
						arrowRightCount = 0;
					}
				}
				if (pressedKey.getKind() == Key.Kind.ArrowLeft) {
					if (direction != right) {
						direction = left;
						arrowUpCount = 0;
						arrowDownCount = 0;
						arrowLeftCount++;
						arrowRightCount = 0;
					}
				}

				if (pressedKey.getKind() == Key.Kind.ArrowRight) {
					if (direction != left) {
						direction = right;
						arrowUpCount = 0;
						arrowDownCount = 0;
						arrowLeftCount = 0;
						arrowRightCount++;
					}
				}
				// acceleration
				if (pressedKey.getKind() == Key.Kind.ArrowUp & direction == up & arrowUpCount > 2) {
					acceleration = 100;
				}
				if (pressedKey.getKind() == Key.Kind.ArrowDown & direction == down & arrowDownCount > 2) {
					acceleration = 100;

				}
				if (pressedKey.getKind() == Key.Kind.ArrowLeft & direction == left & arrowLeftCount > 2) {
					acceleration = 60;
				}

				if (pressedKey.getKind() == Key.Kind.ArrowRight & direction == right & arrowRightCount > 2) {
					acceleration = 60;
				}

				// Pause
				if (pressedKey.getKind() == Key.Kind.Enter) {
					printBorders(terminal, terminalSize, score);
					Toolkit.getDefaultToolkit().beep();
					terminal.moveCursor(terminalSize.getColumns() / 2 - 4, terminalSize.getRows() / 2 - 2);
					terminal.applyForegroundColor(Terminal.Color.WHITE);
					terminal.applyBackgroundColor(Terminal.Color.BLACK);
					write("P A U S E", terminal, false);
					boolean flashingText = true;
					while (true) {
						Key pauseKey = terminal.readInput();
						if (flashingText == false) {
							terminal.applyForegroundColor(Terminal.Color.CYAN);
							terminal.moveCursor(terminalSize.getColumns() / 2 - 12, terminalSize.getRows() / 2);
							write("Press any key to continue", terminal, false);

						} else if (flashingText == true) {
							terminal.moveCursor(terminalSize.getColumns() / 2 - 12, terminalSize.getRows() / 2);
							terminal.applyForegroundColor(Terminal.Color.BLACK);
							write("Press any key to continue", terminal, false);
						}
						if (pauseKey != null) {
							terminal.clearScreen();
							printBorders(terminal, terminalSize, score);
							reDrawSnake(terminal, snakeBody, snakeHead);
							terminal.moveCursor(snakeFood.col, snakeFood.row);
							terminal.applyForegroundColor(Terminal.Color.GREEN);
							terminal.putCharacter('@');
							break;
						}
						delay(500);
						flashingText = !flashingText;
					}
					avelableKeys(terminal, terminalSize);
				}
				// Exit during playing the game
				if (pressedKey.getKind() == Key.Kind.Escape) {
					exitMsg(terminal, terminalSize, score, snakeBody, snakeFood, snakeHead);
				}				
			}
			// This's to stop accumulation of input keys, when you hold a arrow key. 
			if (score>14) {
			Key pressedKeyDisable = terminal.readInput();
			}
			
			// The snake moves
			Position newDirection = directions[direction];
			snakeHead = new Position(snakeHead.col + newDirection.col, snakeHead.row + newDirection.row);
			
			// The snake can die
			boolean snakeSuicide = false;
			boolean crashedIntoWall = false;
			for (Position i : snakeBody) {
				if (snakeHead.row == i.row && snakeHead.col == i.col) {
					snakeSuicide = true;
				}
			}
			for (Position i : borderLines) {
				if (snakeHead.row == i.row && snakeHead.col == i.col) {
					crashedIntoWall = true;
				}
			}
			
			// Game over; Restart/Exit option;
			if (crashedIntoWall || snakeSuicide) {
				Toolkit.getDefaultToolkit().beep();
				gameOver(terminal, terminalSize, snakeHead, borderLines, snakeBody, score);
				gameOverMsg(terminal, terminalSize, score);
			}
			snakeHead = printSnakeBody(terminal, snakeBody, snakeHead);
			Position removeLast = snakeBody.poll();
			terminal.moveCursor(removeLast.col, removeLast.row);
			terminal.putCharacter(' ');

			// Eating
			if (snakeHead.col == snakeFood.col && snakeHead.row == snakeFood.row) {
				//	Turn on for speed grow up (+2)
//				snakeBody.offer(new Position(snakeFood.col, snakeFood.row));
				score++;
				printSnakeBody(terminal, snakeBody, snakeHead);
				snakeFood = printSnakeFood(terminal, terminalSize, borderLines, snakeBody);
				foodTimeStart = System.currentTimeMillis();
				printBorders(terminal, terminalSize, score);
				infoGameEngine(terminal, terminalSize, snakeFood, snakeBody, snakeHead, speed);
				avelableKeys(terminal, terminalSize);
				foodExpiringTime = 14000 - levelPrint(score, terminal)*50;
			}
			speed=speedUp(speed, score);
			
			// difficulty: Hard
			if (difficultyHard) {
				long foodTimeEnd = System.currentTimeMillis();
				if (foodTimeEnd - foodTimeStart > foodExpiringTime - 3000) {
					foodBlinkingOption = foodBlinking(terminal, terminalSize, snakeFood, foodBlinkingOption);
				}
				// Expiring food
				if (foodTimeEnd - foodTimeStart > foodExpiringTime) {
					foodExpiring(terminal, terminalSize, snakeFood);
					snakeFood = printSnakeFood(terminal, terminalSize, borderLines, snakeBody);
					score--;
					printBorders(terminal, terminalSize, score);
					foodTimeStart = System.currentTimeMillis();
				}
			}

			// Change time delay if acceleration is >0
			if (speed==acceleration){
				delay(speed);	
			} else if (speed!=acceleration){
				delay(acceleration);
			}
			
			// Winning score
			if (difficultyHard == false) {
				if (score == winningScore) {
					delay(1500);
					win(terminal, terminalSize);
					break;
				}
			}
		}
	}

	// Prints String to console (terminal)
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
	
	// Prints snake
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

	// Prints food
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
	
	// Display info for some parameters 
	public static void infoGameEngine (Terminal terminal, TerminalSize terminalSize,
			Position snakeFood, Queue<Position> snakeBody, Position snakeHead, short speed) {
		System.out.println("Terminal size: \nColumns - "+terminalSize.getColumns()+", Rows - "+terminalSize.getRows());
		System.out.println("Snake length: "+snakeBody.size());
		System.out.println("Snake head coordinates: "+snakeHead.col+", "+snakeHead.row);
		System.out.println("Snake food coordinates: "+snakeFood.col+", "+snakeFood.row);
		System.out.println("Current speed: " + speed);
	}
	
	//Prints playing field
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
		// Display score
		terminal.moveCursor(4, 0);
		terminal.applyForegroundColor(Terminal.Color.WHITE);
		terminal.applyBackgroundColor(Terminal.Color.BLUE);
		write("Score: " + score, terminal, false);
		levelPrint(score, terminal);
		return borderLines;
	}
	
	// Time interval to refresh the screen
	private static void delay(int speed) {
		try {
			Thread.sleep(speed);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// The snake flashing, when dying
	public static void gameOver(Terminal terminal, TerminalSize terminalSize, Position snakeHead,
			ArrayList<Position> borderLines, Queue<Position> snakeBody, short score) {
		for (int flashTimes = 1; flashTimes < 10; flashTimes++) {
			if (flashTimes % 2 == 0) {
				printBorders(terminal, terminalSize, score);
				printSnakeBody(terminal, snakeBody, snakeHead);
			} else if (flashTimes % 2 != 0) {
				terminal.clearScreen();
				printBorders(terminal, terminalSize, score);
			}
		delay(350);
		}
	}
	
	// Prints massage for Game over
	public static void gameOverMsg (Terminal terminal, TerminalSize terminalSize, short score) {
		String[][] sad = { { "	                                               " },
				{ "	   /██████   /██████  /██      /██ /████████   " },
				{ "	  /██__  ██ /██__  ██| ███    /███| ██_____/   " },
				{ "	 | ██  \\__/| ██  \\ ██| ████  /████| ██         " },
				{ "	 | ██ /████| ████████| ██ ██/██ ██| █████      " },
				{ "	 | ██|_  ██| ██__  ██| ██  ███| ██| ██__/      " },
				{ "	 | ██  \\ ██| ██  | ██| ██\\  █ | ██| ██         " },
				{ "	 |  ██████/| ██  | ██| ██ \\/  | ██| ████████   " },
				{ "	  \\______/ |__/  |__/|__/     |__/|________/   " },

				{ "	                                               " },
				{ "	    /██████  /██    /██ /████████ /███████     " },
				{ "	   /██__  ██| ██   | ██| ██_____/| ██__  ██    " },
				{ "	  | ██  \\ ██| ██   | ██| ██      | ██  \\ ██    " },
				{ "	  | ██  | ██|  ██ / ██/| █████   | ███████/    " },
				{ "	  | ██  | ██ \\  ██ ██/ | ██__/   | ██__  ██    " },
				{ "	  | ██  | ██  \\  ███/  | ██      | ██  \\ ██    " },
				{ "	  |  ██████/   \\  █/   | ████████| ██  | ██    " },
				{ "	   \\______/     \\_/    |________/|__/  |__/    " },
				{ "	                                               " },

		};     
		terminal.clearScreen();
		terminal.applyForegroundColor(Terminal.Color.YELLOW);
		terminal.applyBackgroundColor(Terminal.Color.RED);
		for (int i = 0; i < sad.length; i++) {
			// String rowString = Arrays.toString(sad[i]);
			terminal.moveCursor(terminalSize.getColumns() / 2 - 25, 4 + i);
			write(Arrays.toString(sad[i]), terminal, false);
			// System.out.println(Arrays.toString(sad[i]));}
		}
		terminal.applyForegroundColor(Terminal.Color.WHITE);
		terminal.applyBackgroundColor(Terminal.Color.BLACK);
		terminal.moveCursor(terminalSize.getColumns() / 2 - 5, 3);
		write("Your score: " + score, terminal, true);
		restartOrExitChoise(terminal, terminalSize);
	}
	
	// exit during the play
	public static void exitMsg(Terminal terminal, TerminalSize terminalSize, short score, Queue<Position> snakeBody,
			Position snakeFood, Position snakeHead) {
		terminal.clearScreen();
		terminal.applyForegroundColor(Terminal.Color.WHITE);
		terminal.applyBackgroundColor(Terminal.Color.RED);
		terminal.moveCursor(terminalSize.getColumns() / 2 - 8, terminalSize.getRows() / 2-1);
		write("Press \"y\" to confirm. ", terminal, false);
		while (true) {
			Key pressedKey = terminal.readInput();
			// time delay - CPU friendly
			delay(1);
			if (pressedKey != null) {
				// Hidden quick exit
				if (pressedKey.getKind() == Key.Kind.Escape) {
					terminal.exitPrivateMode();
					System.exit(0);
				}
				System.out.println(pressedKey);
				if (pressedKey.getCharacter() == 'y') {
					terminal.clearScreen();
					terminal.moveCursor(terminalSize.getColumns() / 2 - 8, terminalSize.getRows() / 2-1);
					write("   - exiting -   ", terminal, false);
					long timeStart = System.currentTimeMillis();
					delay(1000);
					int delayMinus = 15;
					for (int i = 1; i <= 30; i++) {
						terminal.clearScreen();
						int randomColumn = (int) (Math.random() * ((terminalSize.getColumns() - 66))) + 24;
						int randomRow = (int) (Math.random() * ((terminalSize.getRows() - 24))) + 12;
						terminal.moveCursor(randomColumn, randomRow);
						if (i%2!=0) {
							terminal.applyBackgroundColor(Terminal.Color.YELLOW);
							terminal.applyForegroundColor(Terminal.Color.RED);
						} else if (i%2==0) {
							terminal.applyBackgroundColor(Terminal.Color.RED);
							terminal.applyForegroundColor(Terminal.Color.WHITE);
						}
						write("   - exiting -   ", terminal, false);
//						terminal.moveCursor(randomColumn, randomRow - 1);
//						write("=================", terminal, false);
//						terminal.moveCursor(randomColumn, randomRow + 1);
//						write("=================", terminal, false);
						delay(600 - (delayMinus));
						if (570 - delayMinus > 5) {
							delayMinus += 30;
						}
						
						}
					terminal.clearScreen();
					terminal.moveCursor(terminalSize.getColumns() / 2 - 8, terminalSize.getRows() / 2-1);
					terminal.applyForegroundColor(Terminal.Color.YELLOW);
					terminal.applyBackgroundColor(Terminal.Color.BLACK);
					long timeEnd = System.currentTimeMillis();
					long totalTime=timeEnd-timeStart;
					System.out.println(totalTime);
					write("   Bye-bye :)   ", terminal, false);
					delay(3000);
					turnOff(terminal);
				} else {
					terminal.clearScreen();
					printBorders(terminal, terminalSize, score);
					reDrawSnake(terminal, snakeBody, snakeHead);
					terminal.moveCursor(snakeFood.col, snakeFood.row);
					terminal.applyForegroundColor(Terminal.Color.GREEN);
					terminal.putCharacter('@');
					break;
				}
				
			}
		}
		
	}
	
	// Turns off the terminal & application
	public static void turnOff (Terminal terminal) {
		terminal.exitPrivateMode();
		System.exit(0);
	}
	
	public static void avelableKeys (Terminal terminal, TerminalSize terminalSize) {
		terminal.moveCursor(terminalSize.getColumns()-55, terminalSize.getRows()-1);
		terminal.applyForegroundColor(Terminal.Color.BLACK);
		terminal.applyBackgroundColor(Terminal.Color.BLUE);
		write("\"Escape\" for Exit.  \"Enter\" to Pause the game.", terminal, false);	
	}
	
	public static Position reDrawSnake(Terminal terminal, Queue<Position> snakeBody,
			Position snakeHead) {
		for (Position segmentOfSnake : snakeBody) {
			terminal.applyForegroundColor(Terminal.Color.YELLOW);
			terminal.applyBackgroundColor(Terminal.Color.BLACK);
			terminal.moveCursor(segmentOfSnake.col, segmentOfSnake.row);
			if (segmentOfSnake.equals(snakeHead) == false) {
				terminal.putCharacter('₪');
			} else {
				terminal.putCharacter('о');
			}
		}
		return snakeHead;
	}
	
	public static void win (Terminal terminal, TerminalSize terminalSize) {
	terminal.clearScreen();
	terminal.applyForegroundColor(Terminal.Color.YELLOW);
	terminal.applyBackgroundColor(Terminal.Color.BLACK);
	terminal.moveCursor(terminalSize.getColumns() / 2 - 12, terminalSize.getRows() / 2-1);
	write("You just won The Game!!!", terminal, true);
	restartOrExitChoise(terminal, terminalSize);
	}
	
	public static void restartOrExitChoise (Terminal terminal, TerminalSize terminalSize) {
		terminal.moveCursor(terminalSize.getColumns() / 2 - 11, terminalSize.getRows()-5);
		write("Press Escape for EXIT", terminal, true);
		terminal.moveCursor(terminalSize.getColumns() / 2 - 1, terminalSize.getRows()-4);
		write("or", terminal, true);
		terminal.moveCursor(terminalSize.getColumns() / 2 - 13, terminalSize.getRows()-3);
		write("Press Enter to play again", terminal, false);
		while (true) {
			Key keyAfterGameOver = terminal.readInput();
			// time delay - CPU friendly
			delay(1);
			if (keyAfterGameOver != null) {
				// Restart
				if (keyAfterGameOver.getKind() == Key.Kind.Enter) {
					terminal.exitPrivateMode();
					new Snake();
					Snake.main(null);
				}
				// Exit
				if (keyAfterGameOver.getKind() == Key.Kind.Escape) {
					terminal.exitPrivateMode();
					System.exit(0);
				}
			}
		}
	}
	public static void escapeKeyExit (Terminal terminal) {
		Key pressed = terminal.readInput();
		if (pressed.getKind() == Key.Kind.Escape) {
			terminal.exitPrivateMode();
			System.exit(0);
		}
	}
	
	private static short speedUp (short speed, short score) {
		short speedUp=speed;
		switch (score) {
		case 5:
			speedUp = 180;
			break;
		case 10:
			speedUp = 160;
			break;
		case 15: speedUp = 140;
		break;
		case 20: speedUp = 120;
		break;
		case 30: speedUp = 100;
		break;
		case 45: speedUp = 80;
		break;
		case 55: speedUp = 60;
		break;
		default:
		}
		return speedUp;
		
	}

	public static byte levelPrint(short score, Terminal terminal) {
		byte level = 1;
		if (score > 4 & score < 10) {
			level = 2;
		} else if (score > 9 & score < 15) {
			level = 3;
		} else if (score > 14 & score < 20) {
			level = 4;
		} else if (score > 19 & score < 30) {
			level = 5;
		} else if (score > 29 & score < 45) {
			level = 6;
		} else if (score > 44 & score < 55) {
			level = 7;
		} else if (score > 54) {
			level=8;
		} 
		terminal.moveCursor(86, 0);
		terminal.applyForegroundColor(Terminal.Color.WHITE);
		terminal.applyBackgroundColor(Terminal.Color.BLUE);
		write("Level: " + level, terminal, false);
		return level;
	}

	public static Key pressedKeys (Terminal terminal) {
		Key pressed = terminal.readInput();
		return pressed;
	}
	
	public static ArrayList<Position> innerWall (Terminal terminal, TerminalSize terminalSize) {
		// Borderlines of the playing field
		ArrayList<Position> innerWall = new ArrayList<Position>();
		return innerWall;
	}
	
	public static void foodExpiring(Terminal terminal, TerminalSize terminalSize, Position snakeFood) {
		terminal.moveCursor(snakeFood.col, snakeFood.row);
		terminal.applyBackgroundColor(Terminal.Color.BLACK);
		terminal.putCharacter(' ');
	}

	public static byte foodBlinking(Terminal terminal, TerminalSize terminalSize, Position snakeFood,
			byte foodBlinking) {
		switch (foodBlinking) {
		case 1:
			terminal.moveCursor(snakeFood.col, snakeFood.row);
			terminal.applyForegroundColor(Terminal.Color.BLACK);
			terminal.putCharacter('@');
			foodBlinking = 0;
			break;
		case 0:
			terminal.moveCursor(snakeFood.col, snakeFood.row);
			terminal.applyForegroundColor(Terminal.Color.RED);
			terminal.putCharacter('@');
			foodBlinking = 1;
			break;
		}
		return foodBlinking;
	}
}
	