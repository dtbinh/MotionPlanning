package View;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import model.Data;
import model.Obstacle;
import model.Obstacle.ObstacleType;

public class RobotPanel extends JPanel implements Observer{
	private static final long serialVersionUID = 1L;
	Data data;
	
	public RobotPanel(){
		data = Data.getInstance();
		data.addObserver(this);
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(150, (int) GraphPanel.CANVAS);
	}
	
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		
		g.setColor(Color.black);
		g.drawString("Robot's Strength:" + data.getRobot().getStrength(), 0, 10);
		
		g.drawString("Obstacle Types:", 0, 30);
		ObstacleType[] types = Obstacle.ObstacleType.values();
		for(int i=0; i<types.length; i++){
			g.setColor(Obstacle.ObstacleColor[i]);
			g.fillRect(0, 40+15*i, 15, 15);
			g.setColor(Color.black);
			g.drawString(types[i].toString(), 15, 40+15*(i+1));
		}
	}

	public void update(Observable o, Object arg) {
		repaint();
	}
}
