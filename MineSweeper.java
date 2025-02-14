import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

public class MineSweeper extends JFrame implements MouseListener, ActionListener{

	JPanel gridPanel;
	JMenu menu;
	JMenuBar menuBar;
	JMenuItem easy, medium, hard;
	JToggleButton[][] grid;
	JButton reset;
	boolean firstClick = true;
	int numMines = 10;
	int dim = 40;
	int numClicked;
	int currRow = 9;
	int currCol = 9;
	ImageIcon mineIcon, flagIcon, smileIcon, loseIcon, winIcon, waitIcon;
	ImageIcon[] numIcons;
	Font clockFont;
	Timer timer;
	JTextField time;
	int timePassed = 0;
	boolean gameOn = true;

	public MineSweeper(){
		menuBar = new JMenuBar();
		menu = new JMenu("Difficulty: Easy");
		easy = new JMenuItem("Beginner");
		medium = new JMenuItem("Medium");
		hard= new JMenuItem("Hard");

		menu.add(easy);
		menu.add(medium);
		menu.add(hard);
		loadImages();

		reset = new JButton();
		reset.setIcon(smileIcon);
		reset.setFocusable(false);

		easy.addActionListener(this);
		medium.addActionListener(this);
		hard.addActionListener(this);
		reset.addActionListener(this);

		time = new JTextField();
		time.setFont(clockFont.deriveFont(18F));
		time.setBackground(Color.BLACK);
		time.setForeground(Color.GREEN);
		time.setEditable(false);
		time.setText("" + 0);
		time.setHorizontalAlignment(JTextField.CENTER);

		menuBar.add(menu);
		menuBar.add(time);
		menuBar.add(reset);
		menuBar.setLayout(new GridLayout(1, 3));

		setup(currRow, currCol, numMines);

		this.setSize(700, 700);
		this.add(menuBar, BorderLayout.NORTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e){
		firstClick = true;
		if(e.getSource() == easy){
			currRow = 9;
			currCol = 9;
			numMines = 10;
			menu.setText("Difficuly: Easy");
		}
		if(e.getSource() == medium){
			currRow = 16;
			currCol = 16;
			numMines = 40;
			menu.setText("Difficuly: Medium");
		}
		if(e.getSource() == hard){
			currRow = 26;
			currCol = 26;
			numMines = 99;
			menu.setText("Difficuly: Hard");
		}
		gameOn = true;
		setup(currRow, currCol, numMines);
	}

	public void mouseReleased(MouseEvent e)
	{
		int row = (int)(((JToggleButton)e.getComponent()).getClientProperty("row"));
		int col = (int)(((JToggleButton)e.getComponent()).getClientProperty("col"));

		if(gameOn)
		{
			if(e.getButton() == MouseEvent.BUTTON1 && grid[row][col].isEnabled()){
				if(firstClick){
					firstClick = false;
					dropMines(row, col);
					timer = new Timer();
					timer.schedule(new UpdateTimer(), 0, 1000);
				}

				grid[row][col].setSelected(true);
				grid[row][col].setEnabled(false);

				int state = (int)(grid[row][col].getClientProperty("state"));
				if(state == 10){
					timer.cancel();
					gameOn = false;
					grid[row][col].setIcon(mineIcon);
					grid[row][col].setDisabledIcon(mineIcon);
					JOptionPane.showMessageDialog(null, "You are a loser!");
					displayMines();
					disabledButton();
				}
				else{
						expand(row, col);
				}
				if(numClicked == numMines){
					timer.cancel();
					gameOn = false;
					JOptionPane.showMessageDialog(null, "You Won");
					disabledButton();
				}
			}
			if(e.getButton() == MouseEvent.BUTTON3){
				if(!firstClick){
					if(!grid[row][col].isSelected()){
						if(grid[row][col].getIcon()== null){
							grid[row][col].setIcon(flagIcon);
							grid[row][col].setDisabledIcon(flagIcon);
							grid[row][col].setEnabled(false);
						}
						else if(grid[row][col].getIcon()== flagIcon){
							grid[row][col].setIcon(null);
							grid[row][col].setDisabledIcon(null);
							grid[row][col].setEnabled(true);
						}
					}
				}
			}
		}
		else
		{
			if(grid[row][col].isSelected())
			{
				grid[row][col].setSelected(true);
			}
			else{
				grid[row][col].setSelected(true);
			}
		}
	}

	public void disabledButton(){
		for(int r = 0; r < grid.length; r++){
			for(int c = 0; c < grid[0].length; c++){
				if(grid[r][c].getIcon()!= flagIcon){
					grid[r][c].setSelected(true);
					grid[r][c].setEnabled(false);
				}
			}
		}
	}

	public void displayMines(){
		for(int r = 0; r < grid.length; r++){
			for(int c = 0; c < grid[0].length; c++){
				int state = (int)(grid[r][c].getClientProperty("state"));
				if(state == 10 && grid[r][c].getIcon() != flagIcon){
					grid[r][c].setIcon(mineIcon);
					grid[r][c].setDisabledIcon(mineIcon);

				}
			}
		}
	}

	public void expand(int row, int col){
		int state = (int)(grid[row][col].getClientProperty("state"));
		numClicked--;
		if(!grid[row][col].isSelected()){
			grid[row][col].setSelected(true);
			grid[row][col].setEnabled(false);
		}

		if(state > 0){
			grid[row][col].setIcon(numIcons[state-1]);
			grid[row][col].setDisabledIcon(numIcons[state-1]);
		}
		else{
			for(int i = row-1; i <= row+1; i++){
				for(int j = col-1; j <= col+1; j++){
					try{
						if(!grid[i][j].isSelected())
							expand(i, j);
					}catch(ArrayIndexOutOfBoundsException e){}
				}
			}
		}
	}

	public void setup(int rows, int cols, int numMines)
	{
		time.setText(" " + 0);
		timePassed = 0;

		this.numMines = numMines;
		numClicked = rows*cols;

		grid = new JToggleButton[rows][cols];
		if(gridPanel!=null){
			this.remove(gridPanel);
		}
		gridPanel = new JPanel();
		gridPanel.setLayout(new GridLayout(rows, cols));

		for(int r = 0 ; r < rows; r++){
			for(int c = 0; c < cols; c++){
				grid[r][c] = new JToggleButton();
				grid[r][c].putClientProperty("row", r);
				grid[r][c].putClientProperty("col", c);
				grid[r][c].putClientProperty("state", 0);
				grid[r][c].setBorder(BorderFactory.createBevelBorder(0));
				//grid[r][c].setFocusPainted(false);
				grid[r][c].addMouseListener(this);
				gridPanel.add(grid[r][c]);
			}
		}
		this.add(gridPanel);
		this.setSize(cols*dim, rows*dim);
		this.revalidate();
	}

	public void dropMines(int r, int c)
	{
		int count = numMines;
		while(count > 0)
		{
			int row = (int)(Math.random()*grid.length);
			int col = (int)(Math.random()*grid[0].length);

			int state = (int)(grid[row][col].getClientProperty("state"));

			if(state == 0 && (Math.abs(row-r)>1 || Math.abs(col-c)>1))
			{
				grid[row][col].putClientProperty("state", 10);
				count --;
			}
		}

		for(int row = 0; row < grid.length; row++)
		{
			for(int col = 0; col < grid[0].length; col++)
			{
				count = 0;
				int currToggle = (int)(grid[row][col].getClientProperty("state"));
				if(currToggle != 10){
					for(int nR = row-1; nR <= row+1; nR++){
						for(int cR = col-1; cR <= col+1; cR++){
							try{
								int adjacent = (int)(grid[nR][cR].getClientProperty("state"));
								if(adjacent == 10){
									count ++;
								}
							}catch(ArrayIndexOutOfBoundsException e){}
						}
					}
					grid[row][col].putClientProperty("state", count);
				}

			}
		}

	}

	public void loadImages(){
		try{
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			clockFont = Font.createFont(Font.TRUETYPE_FONT, new File("Minesweeper Images\\digital-7.ttf"));
			ge.registerFont(clockFont);
		}catch(IOException|FontFormatException e){

		}

		mineIcon = new ImageIcon("Minesweeper Images/mine0.png");
		mineIcon = new ImageIcon(mineIcon.getImage().getScaledInstance(dim, dim, Image.SCALE_SMOOTH));

		flagIcon = new ImageIcon("Minesweeper Images/flag0.png");
		flagIcon = new ImageIcon(flagIcon.getImage().getScaledInstance(dim, dim, Image.SCALE_SMOOTH));

		smileIcon = new ImageIcon("Minesweeper Images/smile0.png");
		smileIcon = new ImageIcon(smileIcon.getImage().getScaledInstance(dim, dim, Image.SCALE_SMOOTH));

		loseIcon = new ImageIcon("Minesweeper Images/dead0.png");
		loseIcon = new ImageIcon(loseIcon.getImage().getScaledInstance(dim, dim, Image.SCALE_SMOOTH));

		winIcon = new ImageIcon("Minesweeper Images/win0.png");
		winIcon = new ImageIcon(winIcon.getImage().getScaledInstance(dim, dim, Image.SCALE_SMOOTH));

		waitIcon = new ImageIcon("Minesweeper Images/wait0.png");
		waitIcon = new ImageIcon(waitIcon.getImage().getScaledInstance(dim, dim, Image.SCALE_SMOOTH));

		numIcons = new ImageIcon[8];
		for(int x = 0; x< 8; x++){
			numIcons[x] = new ImageIcon("Minesweeper Images/" + (x+1) + ".png");
			numIcons[x] = new ImageIcon(numIcons[x].getImage().getScaledInstance(dim, dim, Image.SCALE_SMOOTH));
		}
	}



	public static void main(String[]args){
		MineSweeper mS = new MineSweeper();
	}

	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mousePressed(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}

	public class UpdateTimer extends TimerTask
	{
		public void run(){
			if(gameOn)
			{
				timePassed++;
				time.setText("" + timePassed);
			}
		}
	}
}