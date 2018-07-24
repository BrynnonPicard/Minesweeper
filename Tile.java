import java.awt.Color;

import javax.swing.*;

public class Tile extends JButton {
	public boolean isMined = false;
	public int nearbyMines = 0;
	public boolean revealed = false;
	public boolean flagged= false;
	public int x;
	public int y;
	Icon flagIcon = new ImageIcon(getClass().getResource("flag.png"));
	Icon mineIcon = new ImageIcon(getClass().getResource("mine.png"));
	
	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
		this.setFocusable(false);
	}
	
	public void reveal() {
		this.setEnabled (false);
		
		if (isMined) {
			this.setIcon(mineIcon);
			this.setBackground(Color.RED);
			this.setOpaque(true);
		}
		else {
			if (nearbyMines == 0) {
				this.setText("");
			}
			else {
				this.setText(Integer.toString(nearbyMines));
			}
		}
		revealed = true;
	}
	
	public void flag() {
		if (!revealed) {
			if (flagged) {
				this.setText("");
				this.setIcon(null);
				flagged = false;
			}
			else {
				this.setIcon(flagIcon);
				flagged = true;
			}
		}
	}
}
