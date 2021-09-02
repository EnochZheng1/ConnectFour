import java.util.Random;
import java.util.Scanner;
import java.util.Arrays;

public class ConnectFour {

    final static int BOARD_WIDTH = 7;
    final static int BOARD_HEIGHT = 6;
    final static int CONNECT_WIN = 4;
    final static int COMPUTER_PLAYER = 0;
    final static int HUMAN_PLAYER = 1;

    public enum argState {
        NONE, PLAYERS, TEST
    };

    /**
     * Main method takes input from args to determine whether it will run test mode or
     * player mode. An array includes two players. An empty board is generated with a given
     * height and width Once the methods is running, players will be greeted, and have the 
     * ability to enter a number to select the column for where the token is going to be 
     * dropped. Players would not be able to place tokens outside the range of the width 
     * of the board. Game would not stop until one player won the game or the board is filled
     * up. The board is displayed and updated for every turn. Players have an one-turn
     * cycle. When player chose a column to drop token, code will run through the board
     * horizontally, vertically, and diagonally, and count the number of tokens placed by
     * the player that are aligned, and check if the number is equal or exceed the winning
     * amount. If the board is filled up before a player can be declared as winner, it will
     * be a draw.
     * 
     * 
     * @param args used
     */
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int numPlayers = 2;
        boolean testMode = false;
        boolean seedInput = false;
        long seed = 0;

        argState cmdFlag = argState.NONE;
        for (String arg : args) {
            switch (arg) {
                case "-t":
                    cmdFlag = argState.TEST;
                    break;
                case "-p":
                    cmdFlag = argState.PLAYERS;
                    break;
                default:
                    if (cmdFlag == argState.TEST) {
                        seed = Long.parseLong(arg);
                        seedInput = true;
                    } else if (cmdFlag == argState.PLAYERS) {
                        numPlayers = Integer.parseInt(arg);
                    }
                    cmdFlag = argState.NONE;
                    break;
            }
        }
        Random rand;
        if (seedInput) {
            rand = new Random(seed);
        } else {
            rand = new Random();
        }
        int[] players = new int[] {COMPUTER_PLAYER, COMPUTER_PLAYER};
        for (int i = 0; i < numPlayers && i < players.length; i++) {
            players[i] = HUMAN_PLAYER;
        }
        boolean gameOn = true;
        System.out.println(
            "Welcome to Connect Token Game. \nOn your turn, please select a column from 1 to "
                + BOARD_WIDTH + " to drop your token.");
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        for (int i = 0; i < board.length; i++) {
            Arrays.fill(board[i], -1);
        }
        int player = 0;
        printBoard(board);
        while (gameOn) {
            System.out.println("Player " + (player + 1) + " your move:");
            if (players[player] == HUMAN_PLAYER) {
                while (!input.hasNextInt()) {
                    input.next();
                }
                int move = input.nextInt();
                if (move < 1 || move > BOARD_WIDTH || board[BOARD_HEIGHT - 1][move - 1] != -1) {
                    System.out.println("Invalid column: " + move
                        + ". Please select a (non-full) column from 1 to " + BOARD_WIDTH + ".");
                    continue;
                }
                gameOn = !isWinningCol(move - 1, board, player);
                dropToken(move - 1, board, player);
            } else {
                gameOn = !computerPlayerWinning(board, player, rand);
            }
            printBoard(board);
            if (!gameOn) {
                System.out.println("Player " + (player + 1) + " won!");
            }
            player = (player + 1) % 2;
            if (gameOn && checkFullBoard(board)) {
                System.out.println("Game over. We have a draw!");
                gameOn = false;
            }
        }
        System.out.println("Thank you for playing!");
    }

    /**
     * Checks if the game board is full, i.e., if no more tokens can be added. 
     *
     * A game board is not full if any of the top most cells contain the value -1.
     *
     * @param board The game board to verify. It must be of size BOARD_WIDTH * BOARD_HEIGHT.
     * @return true if the game board is not full, false otherwise.
     */
    public static boolean checkFullBoard(int[][] board) {
        for (int i = 0; i < BOARD_WIDTH; i++) {
            if (board[BOARD_HEIGHT - 1][i] == -1)
                return false;
        }
        return true;
    }

    /**
     * Maps the player index to a character.
     *
     * @param player The integer index to map to a character.
     * @return Returns the mapped character:
     *         - 0 is mapped to 'X'
     *         - 1 is mapped to 'O'
     *         - Every other index is mapped to ' '
     */
    public static char getToken(int player) {
        return (player == 0 ? 'X' : (player == 1 ? 'O' : ' '));
    }

    /**
     * Drops a token into the game board at a specified column, col. The token is place at the lowest
     * unfilled cell (value -1) of column col. Specifically, the lowest unfilled cell is set to the player 
     * index.
     *
     * @param col The column where the token is dropped.
     * @param board The game board into which the token is dropped. It must be of size BOARD_WIDTH * BOARD_HEIGHT.
     * @param player The player index. 
     * @return Returns false if the column if full, i.e., the maximum index is not -1. Otherwisem returns true.
     */
    public static boolean dropToken(int col, int[][] board, int player) {
        if (board[BOARD_HEIGHT - 1][col] != -1) {
            return false;
        }
        for (int i = 0; i < board.length; i++) {
            if (board[i][col] == -1) {
                board[i][col] = player;
                break;
            }
        }
        return true;
    }

    /**
     * Checks each column to see if dropping a token at that column would result in a win for 
     * the specified player index.
     *
     * @param board The game board into which the token is dropped. It must be of size BOARD_WIDTH * BOARD_HEIGHT.
     * @param player The player index. 
     * @returns The lowest column index for which the specified player would win by dropping a token. If there is
     *          no such column, -1 is returned.
     */
    public static int findWinningMove(int[][] board, int player) {
        for (int col = 0; col < BOARD_WIDTH; col++) {
            if (isWinningCol(col, board, player)) {
                return col;
            }
        }
        return -1;
    }

    /**
     * Checks if dropping a token at the specified column for the specified player would result in 
     * a win.
     *
     * @param col The column where the token is dropped.
     * @param board The game board into which the token is dropped. It must be of size BOARD_WIDTH * BOARD_HEIGHT.
     * @param player The player index. 
     * @return true if col is a winning column for player. Otherwise, returns false. 
     */
    public static boolean isWinningCol(int col, int[][] board, int player) {
        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            if (isWinningCoord(i, col, board, player)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Counts if the player is winning by having the certain amount of tokens vertically aligned, 
     * horizontally aligned, or diagonally aligned, then return whether the player has won or
     * not.
     * 
     * @param col The column where the token is dropped.
     * @param role The role where the token is dropped.
     * @param board The game board into which the token is dropped. It must be of size BOARD_WIDTH * BOARD_HEIGHT.
     * @param player The player index.
     * @return true if a player has enough tokens horizontally aligned, vertically aligned, or 
     * diagonally aligned in both directions to win. Otherwise, return false.
     */
    public static boolean isWinningCoord(int col, int role, int[][] board, int player) {
        if (col >= BOARD_HEIGHT || col < 0 || role >= BOARD_WIDTH || role < 0 || board[col][role] != -1
            || (col < BOARD_HEIGHT - 1 && board[col + 1][role] != -1) || (col > 0 && board[col - 1][role] == -1)) {
            return false;
        }
        { // Vertical
            int count = 0;
            for (int i = col - 1; i >= 0; i--) {
                if (board[i][role] != player) {
                    break;
                } else {
                    count++;
                }
            }
            if (count >= CONNECT_WIN - 1) {
                return true;
            }
        }
        { // Horizontal
            int count = 0;
            for (int i = role - 1; i >= 0; i--) {
                if (board[col][i] != player) {
                    break;
                } else {
                    count++;
                }
            }
            for (int i = role + 1; i < BOARD_WIDTH; i++) {
                if (board[col][i] != player) {
                    break;
                } else {
                    count++;
                }
            }
            if (count >= CONNECT_WIN - 1) {
                return true;
            }
        }
        { // Diagonals
            {
                int countNegSlope = 0;
                int i = col + 1;
                int j = role - 1;
                while(i < BOARD_HEIGHT && j >= 0)
                {
                    if(board[i][j] != player) {
                        break;
                    } else {
                        countNegSlope++;
                    }
                    i++;
                    j--;
                }
                i = col - 1;
                j = role + 1;
                while (j < BOARD_HEIGHT && i >= 0) {
                    if(board[i][j] != player) {
                        break;
                    } else {
                        countNegSlope++;
                    }
                    i--;
                    j++;
                }
                if (countNegSlope >= CONNECT_WIN - 1) {
                    return true;
                }
                
                
                int countPosSlope = 0;
                i = col + 1;
                j = role + 1;
                while(i < BOARD_HEIGHT && j < BOARD_WIDTH)
                {
                    if(board[i][j] != player) {
                        break;
                    } else {
                        countPosSlope++;
                    }
                    i++;
                    j++;
                }
                i = col - 1;
                j = role - 1;
                while (j >= 0 && i >= 0) {
                    if(board[i][j] != player) {
                        break;
                    } else {
                        countPosSlope++;
                    }
                    i--;
                    j--;
                }
                if (countPosSlope >= CONNECT_WIN - 1) {
                    return true;
                }
            }   
            
            
            
            /*
            int countNegSlope = 0;
            for (int i = move + 1; i < BOARD_HEIGHT; i++) {
                for (int j = col - 1; j >= 0; j--) {
                    if (board[i][j] != player) {
                        break;
                    } else {
                        countNegSlope++;
                    }
                }
            }
            for (int i = move - 1; i >= 0; i--) {
                for (int j = col + 1; j < BOARD_WIDTH; j++) {
                    if (board[i][j] != player) {
                        break;
                    } else {
                        countNegSlope++;
                    }
                }
            }
            if (countNegSlope >= CONNECT_WIN - 1) {
                return true;
            }
            int countPosSlope = 0;
            for (int i = move + 1; i < BOARD_HEIGHT; i++) {
                for (int j = col + 1; j < BOARD_WIDTH; j++) {
                    if (board[i][j] != player) {
                        break;
                    } else {
                        countPosSlope++;
                    }
                }
            }
            for (int i = move - 1; i >= 0; i--) {
                for (int j = col - 1; j >= 0; j--) {
                    if (board[i][j] != player) {
                        break;
                    } else {
                        countPosSlope++;
                    }
                }
            }
            if (countPosSlope >= CONNECT_WIN - 1) {
                return true;
            }*/

        }
        return false;
    }

    /**
     * Checks if computer player is able to win the game and drop token and whether if 
     * human player is able to win and drop token.
     * 
     * @param board The game board into which the token is dropped. It must be of size BOARD_WIDTH * BOARD_HEIGHT.
     * @param computerPlayer The computer player index.
     * @param rand Random number generator.
     * @return true if computer player won the game. Otherwise, return false.
     */
    public static boolean computerPlayerWinning(int[][] board, int computerPlayer, Random rand) {
        int winningMove = findWinningMove(board, computerPlayer);
        if (winningMove != -1) {
            dropToken(winningMove, board, computerPlayer);
            return true;
        }
        winningMove = findWinningMove(board, (computerPlayer + 1) % 2);
        if (winningMove != -1) {
            dropToken(winningMove, board, computerPlayer);
            return false;
        }
        do {
            winningMove = rand.nextInt(BOARD_WIDTH);
        } while (board[BOARD_HEIGHT - 1][winningMove] != -1);
        dropToken(winningMove, board, computerPlayer);
        return false;
    }

    /**
     * Prints out 2D array board in a format of table.
     * 
     * @param board The game board into which the token is dropped. It must be of size BOARD_WIDTH * BOARD_HEIGHT.
     */
    public static void printBoard(int[][] board) {
        for (int i = 0; i < BOARD_WIDTH; i++) {
            System.out.print("--");
        }
        System.out.println("-");
        for (int i = board.length - 1; i >= 0; i--) {
            System.out.print("|");
            for (int j = 0; j < BOARD_WIDTH; j++) {
                System.out.print(getToken(board[i][j]) + "|");
            }
            System.out.println();
        }
        for (int i = 0; i < BOARD_WIDTH; i++) {
            System.out.print("--");
        }
        System.out.println("-");
        System.out.print(" ");
        for (int i = 0; i < BOARD_WIDTH; i++) {
            System.out.print((i + 1) + " ");
        }
        System.out.println();
    }

}
