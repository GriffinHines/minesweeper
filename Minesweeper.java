import java.util.Scanner;
import java.io.*;

/**                                                                             
 * This class represents a Minesweeper game.                                    
 *                                                                              
 * @author Griffin Hines  <griffinhines@uga.edu>                                
 */
public class Minesweeper {
    int cols;
    int rows;
    int[][] field;
    int mines;
    int[][] mineField;
    /**      
     * Constructs an object instance of the {@link Minesweeper} class using the
     
     * information provided in <code>seedFile</code>. Documentation about the   
     * format of seed files can be found in the project's <code>README.md</code\
>                                                                               
* file.                                                                    
*                                                                          
* @param seedFile the seed file used to construct the game                 
* @see            <a href="https://github.com/mepcotterell-cs1302/cs1302-m\
inesweeper-alpha/blob/master/README.md#seed-files">README.md#seed-files</a>     
    */
    public Minesweeper(File seedFile) {
        boolean seedFileError = false;

        try{
            FileReader fileReader = new FileReader(seedFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            //Read field size                                       
	    String fieldSize = "";
            try{
		fieldSize = bufferedReader.readLine().trim();
	    }
	    catch(IOException e){
	    }
	    for(int i = 0; i<fieldSize.length(); i++){
                if((fieldSize.charAt(i) != 32) && ((fieldSize.charAt(i) -48) < 0 || (fieldSize.charAt(i)-48 > 9))){
                    seedFileError = true;
                }
            }
            if(!seedFileError){
                String columnsString = fieldSize.substring(0, fieldSize.indexOf(" "));
                String rowsString = fieldSize.substring(fieldSize.lastIndexOf(" ") +1);
                rows = Integer.parseInt(rowsString);
                cols = Integer.parseInt(columnsString);
            }
	    field = new int[cols][rows];

            //Read number of mines                                                                                                                                                                          
	    String numberOfMines = "";
	    try{
		numberOfMines = bufferedReader.readLine().replaceAll("\\s","");
	    }
	    catch(IOException e){
	    }

            for(int i = 0; i<numberOfMines.length(); i++){
                if(numberOfMines.charAt(i)-48 < 0 || numberOfMines.charAt(i)-48 > 9){
                    seedFileError = true;
                }
            }

            if(!seedFileError){
                mines = Integer.parseInt(numberOfMines);
            }
            if (mines > rows * cols){
                mines = rows*cols;
            }

            //Read mine locations and place mines on seperate array                                                                                                                                         
            int mineCol = 0;
            int mineRow = 0;
            String mineLocation = "";
            mineField = new int[cols][rows];
            for(int i = 0; i<mines; i++){
		try
		    {
			mineLocation = bufferedReader.readLine().replaceAll("\\s","");
		    }
		catch(IOException e){
		}

                if(mineLocation.length() != 2 || (mineLocation.charAt(0)-48 <0 || mineLocation.charAt(0)-48 > rows-1) || (mineLocation.charAt(1)-48 < 0 || mineLocation.charAt(1)-48 > cols)){
                    seedFileError = true;
                }
                if((mineCol< 0 || mineCol > cols) || (mineRow < 0 || mineRow > rows)){
                    seedFileError = true;
                }
                if(!seedFileError){
                    mineCol = (int)mineLocation.charAt(0)-48;
                    mineRow = (int)mineLocation.charAt(1)-48;
                    mineField[mineCol][mineRow] = 1;
                }
            }
            if(seedFileError){
                System.out.println("Cannot create game with FILENAME, because it is not formatted correctly.");
                System.exit(0);
            }
        }
        catch(FileNotFoundException ex){
            System.out.println("Seed file error file not found");
            System.out.println("Cannot create game with FILENAME, because it is not formatted correctly.");
            System.exit(0);
        }
        System.out.println("rows: " + rows + " cols: " + cols + " Mines: " + mines);
    } // Minesweeper                                                                                                                                                                                        


    /**                                                                                                                                                                                                     
     * Constructs an object instance of the {@link Minesweeper} class using the                                                                                                                             
     * <code>rows</code> and <code>cols</code> values as the game grid's number                                                                                                                             
     * of rows and columns respectively. Additionally, One quarter (rounded up)                                                                                                                             
     * of the squares in the grid will will be assigned mines, randomly.                                                                                                                                    
     *                                                                                                                                                                                                      
     * @param rows the number of rows in the game grid                                                                                                                                                      
     * @param cols the number of cols in the game grid                                                                                                                                                      
     */
    public Minesweeper(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;

        if( (rows<0 || rows>10) || (cols<0  || cols>10) ){
            System.out.println("ಠ_ಠ says, \"Cannot create a mine field with that many rows and/or columns!\"");
            System.exit(0);
        }
        field = new int[cols][rows];

        //Place mines on seperate array                                                                                                                                                                     
        mines = (int) Math.ceil(rows * cols * 0.25);
        mineField = new int[cols][rows];
        int mineCol;
        int mineRow;

        for(int i = 0; i < mines; i++){
            mineCol = (int)(Math.random()*cols);
            mineRow = (int)(Math.random()*rows);
            if(mineField[mineCol][mineRow] == 1){
                i--;
            }
            else{
                mineField[mineCol][mineRow] = 1;
            }
        }


    } // Minesweeper                                                                                                                                                                                        


    /**                                                                                                                                                                                                     
     * Starts the game and execute the game loop.                                                                                                                                                           
     */
    public void run() {
        Scanner input = new Scanner(System.in);
        boolean gameOver = false;
        boolean noFog = false;
        int round = 0;
        int marks = 0;
        if(rows < 1 || rows > 10 || cols < 1 || cols > 10){
            System.out.println("Invalid number of rows/cols");
        }

        System.out.println("        _");
        System.out.println("  /\\/\\ (_)_ __   ___  _____      _____  ___ _ __   ___ _ __");
        System.out.println(" /    \\| | '_ \\ / _ \\/ __\\ \\ /\\ / / _ \\/ _ \\ '_ \\ / _ \\ '__|");
        System.out.println("/ /\\/\\ \\ | | | |  __/\\__ \\\\ V  V /  __/  __/ |_) |  __/ |");
        System.out.println("\\/    \\/_|_| |_|\\___||___/ \\_/\\_/ \\___|\\___| .__/ \\___|_|");
        System.out.println("                             ALPHA EDITION |_| v2017.f");

        while(gameOver == false){
            System.out.println("\nRounds Completed: " + round + "\n");
            //Print grid                                                                                                                                                                                    
            for(int i=0; i<rows; i++){
                System.out.print(" " + i + " |");
                for(int j=0; j<cols; j++){
                    if(noFog && mineField[j][i] == 1){
                        System.out.print('<');
                    }
                    else{
                        System.out.print(' ');
                    }
                    if(field[j][i] == 9){
                        System.out.print(0);
                    }
                    else if(field[j][i] > 0){
                        System.out.print(field[j][i]);
                    }
                    else if(field[j][i] == 0){
                        System.out.print(' ');
                    }
                    else if(field[j][i] == -1){
                        System.out.print('F');
                    }
                    else if(field[j][i] == -2){
                        System.out.print('?');
                    }
                    if(noFog && mineField[j][i] == 1){
                        System.out.print('>');
                    }
                    else{
                        System.out.print(' ');
                    }

                    System.out.print("|");
                }
                System.out.println();
            }
            System.out.print("  ");
            for(int k =0; k<cols; k++){
                System.out.print("   " + k);
            }
            System.out.println();

            //Input command                                                                                                                                                                                 
            String command = input.nextLine().replaceAll("\\s","");

            boolean commandError = false;
            int row;
            int col;

            //Reveal                                                                                                                                                                                        
            if((command.length() == 8 && command.substring(0,6).equals("reveal")) || (command.length() == 3 && command.charAt(0) == 'r')){
                int i;

                if(command.length() ==8){
                    i = 6;
                }
                else{
                    i = 1;
                }
                row = command.charAt(i) - 48;
                col = command.charAt(i+1) - 48;
                if( (row < 0 || row > 9) || (col < 0 || col > 9) ){
                    commandError = true;
                }
                //check for mine                                                                                                                                                                            
                if(!commandError && mineField[col][row] == 1){
                    gameOver = true;
                }
                //scan surroundings for mines                 
                int count = 0;
                for(int k = row-1; k<=row+1; k++){
                    for(int j = col-1; j<=col+1; j++){
                        if((j < 0 || j > cols) || (k < 0 || k > cols)){
                            continue;
                        }
                        if (mineField[j][k] == 1){
                            count++;
                        }
                    }
                }
                //in case of no surrounding mines                                                                                                                                                           
                if(count == 0){
                    count = 9;
                }
                if(commandError == false && gameOver == false){
                    field[col][row] = count;
                }
            }

            //Mark                                                                                                                                                                                          
            else if((command.length() == 6 && command.substring(0,4).equals("mark")) || (command.length() == 3 && command.charAt(0) == 'm')){
                int i;

                if(command.length() == 6){
                    i = 4;
                }
                else{
                    i = 1;
                }
                row = command.charAt(i) - 48;
                col = command.charAt(i+1) - 48;
                if( (row < 0 || row > 9) || (col < 0 || col > 9) ){
                    commandError = true;
                }
                //unmark                                                                                                                                                                                    
                if(commandError == false && field[col][row] == -1){
                    marks--;
                    field[col][row] = 0;
                }
                //mark                                                                                                                                                                                      
                else if(commandError == false){
                    marks++;
                    field[col][row] = -1;
                }
                System.out.println("marks: " + marks);
            }

            //Guess                                                                                                                                                                                         
            else if((command.length() == 7 && command.substring(0,5).equals("guess")) || (command.length() == 3 && command.charAt(0) == 'g')){

                int i;

                if(command.length() == 7){
                    i = 5;
                }
                else{
                    i = 1;
                }
                row = command.charAt(i) - 48;
                col = command.charAt(i+1) - 48;
                if( (row < 0 || row > 9) || (col < 0 || col > 9) ){
                    commandError  = true;
                }
                //unguess                                                                                                                                                                                   
                if(commandError == false && field[col][row] == -2){
                    field[col][row] = 0;
                }
		//guess                                                                                                                                                                                     
                else if (commandError == false){
                    field[col][row] = -2;
                }
            }

            //Help                                                                                                                                                                                          
            else if((command.length() == 4 && command.substring(0, 4).equals("help")) || (command.length() == 1 && command.charAt(0) == 'h') ){
                System.out.println("Commands Available...\n - Reveal: r/reveal row col\n  -  Mark: m/mark   row col\n -  Guess: g/guess  row col\n -   Help: h/help\n -   Quit: q/quit\n");
            }

            //Quit                                                                                                                                                                                          
            else if((command.length() == 4 && command.substring(0,4).equals("quit")) || (command.length() == 1 && command.charAt(0) == 'q')) {
                System.out.println("\nლ(ಠ_ಠლ)\nY U NO PLAY MORE?\nBye!\n");
                System.exit(0);
            }

            //Nofog                                                                                                                                                                                         
            else if (command.length() == 5 && command.substring(0,5).equals("noFog")){
                noFog = !noFog;
            }

            else{
                commandError = true;
            }

            //Check for errors                                                                                                                                                                              
            if(commandError){
                System.out.println("\nಠ_ಠ says, \"Command not recognized!\"");
                commandError = false;
            }
            //Game Over                 
            if(gameOver){
                System.out.println("\n Snake? Snake! SNAAAAAAKE!");
                System.out.println("  __ _  __ _ _ __ ___   ___    _____   _____ _ __");
                System.out.println(" / _` |/ _` | '_ ` _ \\ / _ \\  / _ \\ \\ / / _ \\ '__|");
                System.out.println("| (_| | (_| | | | | | |  __/ | (_) \\ V /  __/ | ");
                System.out.println(" \\__, |\\__,_|_| |_| |_|\\___|  \\___/ \\_/ \\___|_|");
                System.out.println(" |___/");
                System.out.println();
                System.exit(0);
            }

            //Check for victory                                                                                                                                                                             
            int correct = 0;
            for(int i = 0; i<rows; i++){
                for(int j = 0; j<cols; j++){
                    if(mineField[j][i] == 1 && field[j][i] == -1)
                        correct++;
                }
            }
            //Win                                           
            if(correct == mines && marks == mines){
                int score = rows * cols - mines - round;
                System.out.println();
                System.out.println(" ░░░░░░░░░▄░░░░░░░░░░░░░░▄░░░░ \"So Doge\"");
                System.out.println(" ░░░░░░░░▌▒█░░░░░░░░░░░▄▀▒▌░░░");
                System.out.println(" ░░░░░░░░▌▒▒█░░░░░░░░▄▀▒▒▒▐░░░ \"Such Score\"");
                System.out.println(" ░░░░░░░▐▄▀▒▒▀▀▀▀▄▄▄▀▒▒▒▒▒▐░░░");
                System.out.println(" ░░░░░▄▄▀▒░▒▒▒▒▒▒▒▒▒█▒▒▄█▒▐░░░ \"Much Minesweeping\"");
                System.out.println(" ░░░▄▀▒▒▒░░░▒▒▒░░░▒▒▒▀██▀▒▌░░░");
                System.out.println(" ░░▐▒▒▒▄▄▒▒▒▒░░░▒▒▒▒▒▒▒▀▄▒▒▌░░ \"Wow\"");
                System.out.println(" ░░▌░░▌█▀▒▒▒▒▒▄▀█▄▒▒▒▒▒▒▒█▒▐░░");
                System.out.println(" ░▐░░░▒▒▒▒▒▒▒▒▌██▀▒▒░░░▒▒▒▀▄▌░");
                System.out.println(" ░▌░▒▄██▄▒▒▒▒▒▒▒▒▒░░░░░░▒▒▒▒▌░");
                System.out.println(" ▀▒▀▐▄█▄█▌▄░▀▒▒░░░░░░░░░░▒▒▒▐░");
                System.out.println(" ▐▒▒▐▀▐▀▒░▄▄▒▄▒▒▒▒▒▒░▒░▒░▒▒▒▒▌▌");
                System.out.println(" ▐▒▒▒▀▀▄▄▒▒▒▄▒▒▒▒▒▒▒▒░▒░▒░▒▒▐░");
                System.out.println(" ░▌▒▒▒▒▒▒▀▀▀▒▒▒▒▒▒░▒░▒░▒░▒▒▒▌░");
                System.out.println(" ░▐▒▒▒▒▒▒▒▒▒▒▒▒▒▒░▒░▒░▒▒▄▒▒▐░░");
                System.out.println(" ░░▀▄▒▒▒▒▒▒▒▒▒▒▒░▒░▒░▒▄▒▒▒▒▌░░");
                System.out.println(" ░░░░▀▄▒▒▒▒▒▒▒▒▒▒▄▄▄▀▒▒▒▒▄▀░░░ CONGRATULATIONS!");
                System.out.println(" ░░░░░░▀▄▄▄▄▄▄▀▀▀▒▒▒▒▒▄▄▀░░░░░ YOU HAVE WON!");
                System.out.println(" ░░░░░░░░░▒▒▒▒▒▒▒▒▒▒▀▀░░░░░░░░ SCORE: " + score);
                System.out.println();
                System.exit(0);
            }

            round++;

        }







    }

    /**                                                                                                                                                                                                     
     * The entry point into the program. This main method does implement some                                                                                                                               
     * logic for handling command line arguments. If two integers are provided                                                                                                                              
     * as arguments, then a Minesweeper game is created and started with a                                                                                                                                  
     * grid size corresponding to the integers provided and with 10% (rounded                                                                                                                               
     * up) of the squares containing mines, placed randomly. If a single word                                                                                                                               
     * string is provided as an argument then it is treated as a seed file and                                                                                                                              
     * a Minesweeper game is created and started using the information contained                                                                                                                            
     * in the seed file. If none of the above applies, then a usage statement                                                                                                                               
     * is displayed and the program exits gracefully.                                                                                                                                                       
     *                                                                                                                                                                                                      
     * @param args the shell arguments provided to the program                                                                                                                                              
     */
    public static void main(String[] args) {

        /*                                                                                                                                                                                                  
          The following switch statement has been designed in such a way that if                                                                                                                            
          errors occur within the first two cases, the default case still gets                                                                                                                              
          executed. This was accomplished by special placement of the break                                                                                                                                 
          statements.                                                                                                                                                                                       
	*/

        Minesweeper game = null;

        switch (args.length) {

            // random game                                                                                                                                                                                  
        case 2:

            int rows, cols;

            // try to parse the arguments and create a game     
            try {
                rows = Integer.parseInt(args[0]);
                cols = Integer.parseInt(args[1]);
                game = new Minesweeper(rows, cols);
                break;
            } catch (NumberFormatException nfe) {
                // line intentionally left blank                                                                                                                                                            
            } // try                                                                                                                                                                                        

            // seed file game                                                                                                                                                                               
        case 1:

            String filename = args[0];
            File file = new File(filename);

            if (file.isFile()) {
                game = new Minesweeper(file);
                break;
            } // if                                                                                                                                                                                         

            // display usage statement                                                                                                                                                                      
        default:

            System.out.println("Usage: java Minesweeper [FILE]");
            System.out.println("Usage: java Minesweeper [ROWS] [COLS]");
            System.exit(0);

        } // switch                                                                                                                                                                                         

        // if all is good, then run the game                                                                                                                                                                
        game.run();

    } // main                                                                                                                                                                                               


} // Minesweeper                                                                                                                                                                                           