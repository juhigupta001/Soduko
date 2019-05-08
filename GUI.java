package project;



import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.*;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;


public class GUI extends JFrame implements ActionListener{

    private MyJButton sudokuGrid[][] = new MyJButton[9][9]; 
    static int subCheck[][]=new int[9][9];
   
    // GUI components
    private JPanel grid = new JPanel();
    private JPanel numberOptions = new JPanel();
    private JPanel displayCandidates = new JPanel();
    private JPanel p[][] = new JPanel[3][3];
    private MyJButton[] choices;

    private JTextField field = new JTextField();

    private final String values[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9","","submitted"};      // values for the choices

    private int value;                              // toggles for buttons
    private boolean choiceClickedFirst = false;
    private boolean eraserClicked = false;
    private boolean checkOnFill = false;
    private boolean questionClicked = false;
    private boolean submited=false;

    private Hint hint = new Hint();                 // objects for algorithms created
  

    private int colorChangeIndex = 0;


    public GUI(){
        super("Sudoku");
        getContentPane().setBackground(Color.gray);

        setUpSudokuGrid();              //
        setUpChoiceButtons();
        JPanel container = new JPanel(new BorderLayout());
        container.add(grid, BorderLayout.WEST);
        container.add(numberOptions);
        field.setEditable(false);
        field.setPreferredSize( new Dimension( 300, 24 ) );
        displayCandidates.add(field);
        container.add(displayCandidates, BorderLayout.SOUTH);

        add(container);

        addMenu();  //Adds the menu bar to the window
        setSize( 500, 500 );  //window size
        setVisible( true );
    }

    public void actionPerformed(ActionEvent event) {

        MyJButton click = (MyJButton) event.getSource();

        for(int i = 0; i < values.length-3; ++i){
            choices[i].setForeground(Color.BLACK);
            if(event.getSource() == choices[i]){        // button we clicked was an number choice
                value = choices[i].getValue();
                choiceClickedFirst = true;
                eraserClicked = false;
                questionClicked = false;
                choices[i].setForeground(Color.RED);
                colorChangeIndex = i;

            }
            
         
        }
        choices[colorChangeIndex].setForeground(Color.RED);
   

        if(event.getSource() == choices[9]){        // if eraser was clicked
            value = 0;
            eraserClicked = true;
            choiceClickedFirst = true;
            
        }
       
        if(event.getSource() == choices[10])
        {
        	int count=0;
        	for(int i=0;i<9;i++)
        	{
        		for(int j=0;j<9;j++)
        		{
        			if(subCheck[i][j]!=0)
        			{
        				count++;
        			}
        			
        			
        		}
        	}
        	
        	int t=count*(100/81);
        	
        	
				JOptionPane.showMessageDialog(this,
	                    "your score is "+t,
	                    "Score Board", JOptionPane.PLAIN_MESSAGE);
				
				System.exit(0);
        	
        	
        }

        if(!click.getOriginalPiece() && !click.getChoiceButtons() && choiceClickedFirst) {      // in sudoku grid... not original piece
            if (eraserClicked) {
                eraserWasClicked(click);  //calls erase function
            }
            else if(questionClicked){  //shows candidate list in bottom panel
                field.setText("Candidates: " + String.valueOf(click.getCandidateList()));
            }
            else {
                if(checkOnFill) {   //makes sure num can go at that position
                    ArrayList candidate = click.getCandidateList();
                    if(candidate.contains(value)) {  //makes sure num can go at that position
                        if(click.getValue() != 0){
                            eraserWasClicked(click);
                        }
                        field.setText("");
                        click.setText(Integer.toString(value));
                        click.setValue(value);
                        int i=click.getRow();
                        int j=click.getCol();
                        hint.checkRow(click.getRow(), value, sudokuGrid);
                        hint.checkCol(click.getCol(), value, sudokuGrid);
                        hint.checkGrids(sudokuGrid);
                        hint.checkForWin(sudokuGrid);
                        subCheck[i-1][j-1]=value;
                        System.out.println(i+" "+j+" "+value);
                        
                    }
                    else{
                        //Window displayed when digit can't be place at this position
                    
                        JOptionPane.showMessageDialog(this,
                                "Can't place digit at current position.",
                                "Error", JOptionPane.PLAIN_MESSAGE);
                    }
                }
                else{
                    if(click.getValue() != 0){   //if num is already in square and being overwritten
                        eraserWasClicked(click);  //erase from grid
                    }
                    field.setText("");
                    click.setText(Integer.toString(value));
                    click.setValue(value);
                    hint.checkRow(click.getRow(), value, sudokuGrid);
                    hint.checkCol(click.getCol(), value, sudokuGrid);
                    hint.checkGrids(sudokuGrid);
                    hint.checkForWin(sudokuGrid);
                }
            }
        }
    }

    /*  Creates menu bar and attach it to GUI window
        and adds all buttons like exit, add puzzle,
        and load a puzzle from a file
    */
    private void addMenu() {
        //set up File menu and its menu items
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');

        //set up Load Puzzle submenu item under File
        JMenuItem loadItem = new JMenuItem("Load Puzzle");
        loadItem.setMnemonic('L');
        fileMenu.add(loadItem);
        loadItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JFileChooser fc = new JFileChooser();
                        int i = fc.showOpenDialog(null);
                        if (i == JFileChooser.APPROVE_OPTION) {
                            File f = fc.getSelectedFile();
                            try {
                                BufferedReader br = new BufferedReader(new FileReader(f));
                                String s1 = "";
                                while ((s1 = br.readLine()) != null) {
                                    readfile(f);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
        );

        //set up Store Puzzle submenu item under File
        JMenuItem storeItem = new JMenuItem("Store Puzzle");
        storeItem.setMnemonic('S');
        fileMenu.add(storeItem);
        fileMenu.addSeparator();
        storeItem.addActionListener(
                new ActionListener() {  // anonymous inner class
                    // Loads new puzzle into program
                    public void actionPerformed(ActionEvent event) {

                        final JFileChooser SaveAs = new JFileChooser();
                        int actionDialog = SaveAs.showSaveDialog(null);
                        if (actionDialog != JFileChooser.APPROVE_OPTION) {
                            return;
                        }

                        File f = new File(SaveAs.getSelectedFile() +".txt");
                        try{
                            FileWriter fw = new FileWriter(f);
                            for(int i = 1; i < 10; i++) {       //loops through grid and writes to file
                                for (int j = 1; j < 10; j++) {
                                    if(sudokuGrid[j -1][i - 1].getValue() != 0){
                                        fw.write(Integer.toString(i) + " " + Integer.toString(j) +
                                                " " + Integer.toString(sudokuGrid[j -1][i -1].getValue()) + "\n");
                                    }
                                }
                            }
                            fw.close();  //closes the file
                        }catch(Exception ex){
                        }
                    }
                }  // end anonymous inner class
        ); // end call to addActionListener

        //set up Exit submenu item under File
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('x');
        fileMenu.add(exitItem);
        exitItem.addActionListener(
                new ActionListener(){  // anonymous inner class
                    // terminate application when user clicks exitItem
                    public void actionPerformed(ActionEvent event){
                        System.exit(0);
                    }
                }  // end anonymous inner class
        ); // end call to addActionListener

        JMenu helpMenu = new JMenu("Help");
        fileMenu.setMnemonic('H');

        //set up How to play submenu item under Help
        JMenuItem helpItem = new JMenuItem("How to Play");
        helpItem.setMnemonic('p');
        helpMenu.add(helpItem);
        helpItem.addActionListener(
                new ActionListener(){  // anonymous inner class
                    public void actionPerformed(ActionEvent event){
                        howToPlay();
                    }
                }  // end anonymous inner class
        ); // end call to addActionListener

  

        //set up About submenu item under Help
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setMnemonic('A');
        helpMenu.add(aboutItem);
        aboutItem.addActionListener(
                new ActionListener(){  // anonymous inner class
                    // display message dialog when user selects About...
                    public void actionPerformed(ActionEvent event){
                        JOptionPane.showMessageDialog( GUI.this,
                                "Authors:\n JUHI GUPTA\n" +
                                        "   TANYA GOYAN",
                                "About",JOptionPane.PLAIN_MESSAGE );
                    }
                }  // end anonymous inner class
        ); // end call to addActionListener

        JMenu hintMenu = new JMenu("Hint");
        fileMenu.setMnemonic('h');

        //set up Check on Fill  submenu item under hint
        JCheckBoxMenuItem checkItem = new JCheckBoxMenuItem("Check on Fill");
        checkOnFill = !checkOnFill;
        //checkItem.setMnemonic('c');
        hintMenu.add(checkItem);
        checkItem.addActionListener(
                new ActionListener(){  // anonymous inner class
                    public void actionPerformed(ActionEvent event){
                        checkOnFill = !checkOnFill;
                    }
                }  
        ); // end call to addActionListener\

        

        
        // Makes bar and adds all buttons to it
        JMenuBar bar = new JMenuBar();
        setJMenuBar(bar);
        bar.add(fileMenu);
        bar.add(helpMenu);
        bar.add(hintMenu);
    }

    /*
        Sets up the array of buttons that can be applied
        to the board.
    */
    private void setUpChoiceButtons(){
        choices = new MyJButton[values.length];

        for(int i = 0; i < values.length; ++i){   //adds side buttons to board
            choices[i] = new MyJButton(values[i]);
            choices[i].setValue(i + 1);  //value that button holds
            choices[i].setChoiceButtons(true);
            choices[i].addActionListener(this);
            choices[i].setPreferredSize(new Dimension(55,60));
            numberOptions.add(choices[i]);
        }
//        try { //adds image to question mark button
//            Image img1 = ImageIO.read(getClass().getResource("QMark.png"));
//            choices[values.length -1 ].setIcon(new ImageIcon(img1));
//            choices[values.length -1 ].setHorizontalTextPosition(SwingConstants.CENTER);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try {  //adds image to eraser button
            Image img = ImageIO.read(getClass().getResource("eraser.png"));
            choices[values.length -2 ].setIcon(new ImageIcon(img));
            choices[values.length -2 ].setHorizontalTextPosition(SwingConstants.CENTER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
        Sets up the array of buttons that make
        up the sudoku grid
    */
    private void setUpSudokuGrid(){
        for(int rows = 0; rows <= 8; rows++){       // makes buttons
            for(int col = 0; col <= 8; col++){
                sudokuGrid[col][rows] = new MyJButton(" ", col + 1, rows + 1, 0, false, false);
                sudokuGrid[col][rows].addActionListener(this);
            }
        }

        for(int x = 0; x <= 2; x++){        // make panel for grid to sit on
            for(int y = 0; y <= 2; y++){
                p[x][y] = new JPanel(new GridLayout(3, 3));
            }
        }

        grid.setLayout(new GridLayout(3, 3, 10,10));


        for(int i = 0; i <= 2; i++){            // builds  the board
            for(int u = 0; u <= 2; u++){
                for(int x = 0; x <= 2; x++) {
                    for (int y = 0; y <= 2; y++) {
                        p[u][i].add(sudokuGrid[y + u * 3][x + i * 3]);
                    }
                }
                grid.add(p[u][i]);
            }
        }
    }

    /*
        Clears board and resets all of
        its values
    */
    private void clearBoard(){
        for(int i = 0; i < 9; ++i){
            for(int j = 0; j < 9; ++j){
                sudokuGrid[i][j].setText(" ");
                sudokuGrid[i][j].setValue(0);
                sudokuGrid[i][j].setOriginalPiece(false);
                sudokuGrid[i][j].reinitalizeCandidateList();
            }
        }
    }

    /*
        Describes how to play the board
    */
    private void howToPlay(){
        JOptionPane.showMessageDialog(GUI.this,
                "Sudoku is a puzzle that often uses a 9x9 grid of 81 cells. The grid is divided into rows, columns\n" +
                        "and boxes. The boxes are 3x3 sub-grids of 9 cells. Thus each row, column and box contains 9\n" +
                        "cells. The object is the fill in the numbers from 1 to 9 so that each row, column and box contain\n" +
                        "each number from 1 to 9 only once",
                "How to Play", JOptionPane.PLAIN_MESSAGE);
    }

    /*
        Reads in specified .txt file and uploads
        it to the board
    */
    private void readfile(File filename){         // read file selected .txt file
        String line;
        String[] parts;
        int col, row, value;

        clearBoard();

        try {
            FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                parts = line.split(" ", 3);
                row = Integer.parseInt(parts[0]);
                col = Integer.parseInt(parts[1]);
                value = Integer.parseInt(parts[2]);

                sudokuGrid[col - 1][row - 1].setText(parts[2]);   //updates grid text to new text
                sudokuGrid[col - 1][row - 1].setValue(value);   //updates grid value  to new value
                sudokuGrid[col - 1][row - 1].setOriginalPiece(true);  //updates original button to true

                hint.checkRow(row, value, sudokuGrid);
                hint.checkCol(col, value, sudokuGrid);
                //sudokuGrid[col - 1][row - 1].getCandidateList().clear();
            }
            hint.checkGrids(sudokuGrid);

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" + filename + "'");
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    private void eraserWasClicked(MyJButton click){
        //field.setText("");
        if (click.getValue() != 0) {
            click.setText(" ");  //removes text
            click.setValue(value);   //removes value from button
            for (int i = 1; i < 10; i++) {
                for (int j = 1; j < 10; j++) {
                    sudokuGrid[i - 1][j - 1].reinitalizeCandidateList();  //resets the candidate list
                }
            }
            for (int i = 1; i < 10; i++) {
                for (int j = 1; j < 10; j++) {   //updates candidate list
                    hint.checkRow(i, sudokuGrid[j - 1][i - 1].getValue(), sudokuGrid);
                    hint.checkCol(j, sudokuGrid[j - 1][i - 1].getValue(), sudokuGrid);
                }
            }
            hint.checkGrids(sudokuGrid);
        }
        else{
            //Window displayed when eraser can't be erase at this position
            JOptionPane.showMessageDialog(this,
                    "Can't erase empty cell.",
                    "Error", JOptionPane.PLAIN_MESSAGE);
        }
    }
}