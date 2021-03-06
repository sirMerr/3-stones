package TSServer.game;

import java.util.Random;
import org.slf4j.LoggerFactory;

/**
 * Class responsible for the 3-Stones game logic. It will start a new game, 
 * get user input, validate user input, make the computer find the best choice 
 * move a piece and calculate total points.
 *
 * @author Tiffany Le-Nguyen
 * @author Trevor Eames
 * @author Alessandro Ciotola
 *
 */
public class Game{
    private final org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private int piecesPlayed = 0; // max 30 game stops
    private int lastColumn = -1;
    private int lastRow = -1;
    private InnerBoard innerBoard;
    private Slot[][] gameBoard;
    private int playerPoints = 0;
    private int compPoints = 0;

    /**
     * No-param constructor
     */
    public Game(){
        log.debug("Game Constructor");
        innerBoard = new InnerBoard();
        gameBoard = innerBoard.getBoardArray();
    }

    /**
     * Get current amount of pieces played
     * @return amount of pieces
     */
    public byte getPiecesPlayed() {
        return (byte) piecesPlayed;
    }

    /**
     * Get current computer score
     * @return points of score
     */
    public byte getCompPoints() {
        return (byte) compPoints;
    }

    /**
     * Get current player score
     * @return points of player
     */
    public byte getPlayerPoints() {
        return (byte) playerPoints;
    }

    /**
     * Add a piece to the boardArray
     *
     * @param row
     * @param column
     * @param cellState
     */
    public boolean addPiece(int row, int column, Slot cellState) {
        // Either while loop or check with other side
        log.debug("Attempting to add piece at: row ->" + row + " column->" + column);

        // validate, calculate points and add to globals
        log.debug(cellState.name());
        if (cellState == Slot.HUMAN_MOVE) {
            row += 1;
            column += 1;
            if (validatePiece(row, column, lastRow, lastColumn)) {
                innerBoard.add(row, column, cellState);
                lastColumn = column;
                lastRow = row;
                piecesPlayed++;
                playerPoints += calculatePoints(row, column, cellState);
                return true;
            }
        } else if (cellState == Slot.COMPUTER_MOVE) {
            innerBoard.add(row, column, cellState);            
            lastColumn = column;
            lastRow = row;
            piecesPlayed++;
            compPoints += calculatePoints(row, column, cellState);
            return true;
        }

        return false;
    }


    
    /**
     * Validate the move, follows the rules of
     * being within one column and row
     * of the last piece placed. If no available spots are available,
     * random is ok. Slot must be *empty* or *adjacent* for a move to be valid
     *
     * @param row row played
     * @param column column played
     * @param lastColumn last column played
     * @param lastRow last row played
     *
     * @return true if valid, false if not
     */
    public boolean validatePiece(int row, int column, int lastRow, int lastColumn){
        log.debug("Validating piece: row-> " + row + " column-> " + column + "\nlastRow-> " + lastRow + " lastColumn->" + lastColumn);
        //Check if the it is the first move, check if the user placed it in the right spot.
        //Check if the it is the first move, check if the user placed it in the right spot.
        if (piecesPlayed == 0 && gameBoard[row][column] == Slot.NOT_OCCUPIED)
            return true;
        if (piecesPlayed != 0){
            // Slot is empty and either the row or column is the same
            if (gameBoard[row][column] == Slot.NOT_OCCUPIED && (row == lastRow || column == lastColumn))
                return true;
            else {
                // Checks if there is a free adjacent cell that the player
                // Could have placed his piece in. If not, the move is valid.
                for (int i = 0; i < gameBoard.length; i++){
                    if (gameBoard[lastRow][i] == Slot.NOT_OCCUPIED || gameBoard[i][lastColumn] == Slot.NOT_OCCUPIED)
                        return false;
                }
                // User's move was valid because no space around last played piece
                return true;
            }
        }
        return false;
    }

    /**
     * Checks the gameBoard with the new added piece.
     *
     * 1 point = 3 adjacent same colored pieces
     *
     * @param lastRow
     * @param lastColumn
     * @param cellState
     * @return
     */
    public int calculatePoints(int lastRow, int lastColumn, Slot cellState) {
        log.debug("Calculating points...");
        int scoreCounter = 0;

        log.debug("LastRow: " + lastRow);
        log.debug("Last Column: " + lastColumn);

        // Check each lastRow, if full +1 point each
        if (gameBoard[lastRow + 1][lastColumn] == cellState && gameBoard[lastRow - 1][lastColumn] == cellState) {
            scoreCounter++;
        }
        // Check each lastColumn, if full +1 point each
        if (gameBoard[lastRow][lastColumn + 1] == cellState && gameBoard[lastRow][lastColumn - 1] == cellState) {
            scoreCounter++;
        }
        // Check first diagonal, if full +1 point each
        if (gameBoard[lastRow + 1][lastColumn - 1] == cellState && gameBoard[lastRow - 1][lastColumn + 1] == cellState) {
            scoreCounter++;
        }

        // Check second diagonal
        if (gameBoard[lastRow - 1][lastColumn - 1] == cellState && gameBoard[lastRow + 1][lastColumn + 1] == cellState) {
            scoreCounter++;
        }

        // Check at 2nd left and right
        if (gameBoard[lastRow][lastColumn - 1] == cellState && gameBoard[lastRow][lastColumn - 2] == cellState) {
            scoreCounter++;
        }

        if (gameBoard[lastRow][lastColumn + 1] == cellState && gameBoard[lastRow ][lastColumn + 2] == cellState) {
            scoreCounter++;
        }

        // Check 2nd top and bottom
        if (gameBoard[lastRow + 1][lastColumn] == cellState && gameBoard[lastRow + 2][lastColumn] == cellState) {
            scoreCounter++;
        }

        if (gameBoard[lastRow - 1][lastColumn] == cellState && gameBoard[lastRow - 2][lastColumn] == cellState) {
            scoreCounter++;
        }

        // Check 2nd diagonal
        if (gameBoard[lastRow + 1][lastColumn -1] == cellState && gameBoard[lastRow + 2][lastColumn - 2] == cellState) {
            scoreCounter++;
        }

        if (gameBoard[lastRow - 1][lastColumn + 1] == cellState && gameBoard[lastRow - 2][lastColumn + 2] == cellState) {
            scoreCounter++;
        }
        
        if (gameBoard[lastRow + 1][lastColumn + 1] == cellState && gameBoard[lastRow + 2][lastColumn + 2] == cellState) {
            scoreCounter++;
        }

        if (gameBoard[lastRow - 1][lastColumn - 1] == cellState && gameBoard[lastRow - 2][lastColumn - 2] == cellState) {
            scoreCounter++;
        }

        return scoreCounter;
    }

    /**
     * Method which will find the position on the board that is valid and will
     * produce the highest result. This will be the computers move. If no moves
     * are possible, find an empty valid position.
     *
     */
    public byte[] getNextMove(){
        byte[] move = new byte[2];
        int scoreHolder = 0;
        int calculatedHolder = -1;
        int bestRowHolder = -1;
        int bestColumnHolder = -1;
        Slot cellState = Slot.COMPUTER_MOVE;

        log.debug("Getting computer move.");

        for(int i = 1; i < gameBoard.length - 1; i++){
            if (validatePiece(i, lastColumn, lastRow, lastColumn)){
                log.debug("Trying row: [" + i + "] and column: [" + lastColumn + "]");

                calculatedHolder = calculatePoints(i,lastColumn, cellState);
                if (calculatedHolder >= scoreHolder){
                    scoreHolder = calculatedHolder;
                    bestRowHolder = i;
                    bestColumnHolder = lastColumn;
                }
            }

            if (validatePiece(lastRow, i, lastRow, lastColumn)){

                log.debug("Trying row: [" + lastRow + "] and column: [" + i + "]");

                calculatedHolder = calculatePoints(lastRow,i, cellState);
                if (calculatedHolder >= scoreHolder){
                    scoreHolder = calculatedHolder;
                    bestRowHolder = lastRow;
                    bestColumnHolder = i;
                }
            }

        }
        if(bestRowHolder != -1){
            log.debug("Best Row: " + bestRowHolder + " Best Column: " + bestColumnHolder);
            addPiece(bestRowHolder, bestColumnHolder, cellState);
            move[0] = (byte) bestRowHolder;
            move[1] = (byte) bestColumnHolder;
            return move;
        }
        else{
            move = getRandomSpot(cellState);
            return move;
        }
    }

    /**
     * Method which will randomly generate numbers for the row and column. If the
     * number is valid, add the piece to the board.
     *
     * @param cellState
     * @return
     */
    public byte[] getRandomSpot(Slot cellState){
        //Get a random number generator
        Random randomSpace = new Random();
        byte[] move = new byte[2];
        //Make 2 variables to hold the random numbers. I set them to 0 because
        //at position [0,0] the space should always be FORBIDDEN_SPACE.                
        int rdmRowHolder = 0, rdmColumnHolder = 0;

        //Keep finding a random spot until it is valid. Will only exit the loop when
        //The piece is valid.
        while (!validatePiece(rdmRowHolder, rdmColumnHolder, lastRow, lastColumn)){
            //Gets a random space between 0 and 6 (7X7 game board), which will then be validated
            rdmRowHolder = randomSpace.nextInt(7)+1;
            rdmColumnHolder = randomSpace.nextInt(7)+1;
        }
        //Will only arrive here if the random piece is valid, so add to board!
        log.debug("Random Row: " + rdmRowHolder + " Random Column: " + rdmColumnHolder);
        move[0] = (byte) rdmRowHolder;
        move[1] = (byte) rdmColumnHolder;
        addPiece(rdmRowHolder, rdmColumnHolder, cellState);
        return move;
    }
}