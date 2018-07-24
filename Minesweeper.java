import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Minesweeper extends JFrame{
	public int numTilesX = 9;
	public int numTilesY = 9;
	public Tile[][] tiles = new Tile[numTilesX][numTilesY];
	public boolean gameOn = true;
	public boolean gameStarted = false;
	Timer timer = new Timer();
	
	int numMines = 10;
	Tile[] minedTiles = new Tile[numMines];
	int minesLeft = 0;
	int minesFlagged = 0;
	int spacesRevealed = 0;
	int timeSecs = 0;
	int totalTiles = numTilesX * numTilesY;
	JLabel mineCount = new JLabel("Mines Left: " + Integer.toString(numMines));
	JLabel timerText = new JLabel("Time (Sec): 0");
	JButton resetButton = new JButton("", new ImageIcon(getClass().getResource("smile.png")));
	Random rand = new Random();
	int randomMineX;
	int randomMineY;
	
	public Minesweeper () {
		super("Minesweeper");
		this.setSize(numTilesX * 42, numTilesY * 42 + 40);
        this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		
		BorderLayout borderLayout = new BorderLayout();
		this.setLayout(borderLayout);
		JPanel topPanel = new JPanel(new BorderLayout());
		mineCount.setBorder(new EmptyBorder(0,11,0,11));
		timerText.setBorder(new EmptyBorder(0,10,0,10));
		resetButton.setFocusable(false);
		resetButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				reset();
			} 
		});
		
		JPanel tileGrid = new JPanel();
		GridLayout gridLayout = new GridLayout(numTilesX, numTilesY);
		tileGrid.setLayout(gridLayout);
		
		this.add(topPanel, BorderLayout.NORTH);
		this.add(tileGrid, BorderLayout.CENTER);
		topPanel.add(mineCount, BorderLayout.WEST);
		topPanel.add(resetButton, BorderLayout.CENTER);
		topPanel.add(timerText, BorderLayout.EAST);
		
		
		//Add tiles to frame
		for (int i=0; i < numTilesX; i++) {
			for (int j=0; j < numTilesY; j++) {
				Tile thisTile = new Tile(i, j);
				tiles[i][j] = thisTile;

				thisTile.addMouseListener(new MouseAdapter(){
		            boolean leftPressed;
		            boolean rightPressed;

		            public void mousePressed(MouseEvent e) {
		            	if (gameOn) {
		            		if (e.getButton() == MouseEvent.BUTTON1) {
			            		leftPressed = true;
				            }
			            	else if (e.getButton() == MouseEvent.BUTTON3) {
			            		rightPressed = true;
			            	}
		            	}
		            }

		            public void mouseReleased(MouseEvent e) {
		            	if (gameOn) {
		            		if (!gameStarted) {
		            			gameStarted = true;
		            			timer.schedule(new TimerTask() {

		            	            @Override
		            	            public void run() {
		            	            	timeSecs++;
		            	                timerText.setText("Time (Sec): " + timeSecs);
		            	            }
		            	        }, 1, 1000);
		            		}
		            		
			            	if (leftPressed) {
			            		if (!thisTile.flagged) {
				            		if (thisTile.isMined) {
			            				thisTile.reveal();
				            			gameLost();
				            		}
				            		else {
				            			searchTiles(thisTile.x, thisTile.y);
				            		}
				            		leftPressed = false;
			            		}
			            	}
			            	else if (rightPressed) {
			            		if (!thisTile.flagged) {
			            			minesLeft--;
			            		}
			            		else {
			            			minesLeft++;
			            		}
			            		
			            		//Keep track of whether flagged tiles are actually mined or not
			            		if (thisTile.isMined && !thisTile.flagged) {
			            			minesFlagged++;
			            		}
			            		else if (thisTile.isMined && thisTile.flagged) {
			            			minesFlagged--;
			            		}
			            		
			            		thisTile.flag();
			            		mineCount.setText("Mines Left: " + Integer.toString(minesLeft));			            		
			        
			            		rightPressed = false;
			            	}
		            	}
		            	
		            	//Check for win condition
		            	checkWin();
		            }

		            public void mouseExited(MouseEvent e) {
		            	if (leftPressed) {
		            		leftPressed = false;
		            	}
		            	else if (rightPressed) {
		            		rightPressed = false;
		            	}
		            }

		            public void mouseEntered(MouseEvent e) {
		            	if (leftPressed) {
		            		leftPressed = false;
		            	}
		            	else if (rightPressed) {
		            		rightPressed = false;
		            	}
		            }
		        });

		        tileGrid.add(tiles[i][j]);
			}
		}
		
		//Generate mines and add them to game field
		while (minesLeft < 10) {
			randomMineX = rand.nextInt(numTilesX);
			randomMineY = rand.nextInt(numTilesY);
			
			if (!tiles[randomMineX][randomMineY].isMined) {
				tiles[randomMineX][randomMineY].isMined = true;
				minedTiles[minesLeft] = tiles[randomMineX][randomMineY];
				minesLeft++;
			}
		}
		
		this.setVisible(true);
	}

	public static void main(String[] args) {
		new Minesweeper();
	}
	
	public void searchTiles(int x, int y) {
		if (x < 0 || x >= numTilesX || y < 0 || y >= numTilesY || tiles[x][y].revealed) {
			return;
		}
		
		int numMines = 0;
		
		numMines += checkTile(x-1, y);
		numMines += checkTile(x-1, y-1);
		numMines += checkTile(x, y-1);
		numMines += checkTile(x+1, y-1);
		numMines += checkTile(x+1, y);
		numMines += checkTile(x-1, y+1);
		numMines += checkTile(x, y+1);
		numMines += checkTile(x+1, y+1);
		
		tiles[x][y].nearbyMines = numMines;
		tiles[x][y].reveal();
		spacesRevealed++;
		
		//Recursively reveal adjacent tiles if the initial one clicked is blank
		if (numMines == 0) {
			searchTiles(x-1, y);
			searchTiles(x-1, y-1);
			searchTiles(x, y-1);
			searchTiles(x+1, y-1);
			searchTiles(x+1, y);
			searchTiles(x-1, y+1);
			searchTiles(x, y+1);
			searchTiles(x+1, y+1);
		}
	}
	
	public int checkTile(int x, int y) {
		if (x >= 0 && x < numTilesX && y >= 0 && y < numTilesY && tiles[x][y].isMined) {
			return 1;
		}
		return 0;
	}
	
	public void checkWin() {
		if (minesFlagged == numMines && minesLeft == 0 && spacesRevealed == totalTiles - numMines) {
			gameWon();
		}
	}
	
	public void gameLost() {
		gameOn = false;
		resetButton.setIcon(new ImageIcon(getClass().getResource("frown.png")));
		
		for (int i=0; i < numMines; i++) {
			minedTiles[i].reveal();
		}
		
		timer.cancel();
		timer.purge();
	}
	
	public void gameWon() {
		gameOn = false;
		resetButton.setIcon(new ImageIcon(getClass().getResource("trophy.png")));
		timer.cancel();
		timer.purge();
	}
	
	public void reset() {
		this.setVisible(false);
		new Minesweeper();
		this.dispose();
	}
}
