package View;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.JPanel;

import model.Data;
import model.MyObject;
import model.Obstacle;
import model.Player;


public class PlayerPanel extends JPanel implements Observer{
	private static final long serialVersionUID = 1L;
	public static final double WIDTH = 150;
	Data data;
	Player player;
	private ArrayList<Obstacle> oCandidates = new ArrayList<Obstacle>();
	private Obstacle focusedObstacle = null;
	private Player.PPMouseInputListener mouseInputListener;
	
	public PlayerPanel(){
		data = Data.getInstance();
		player = data.getPlayer();
		player.addObserver(this);
		player.setOCandidates(oCandidates);
		
		try {
			readInObstacles(Project_MAIN.ocFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mouseInputListener = player.getPPMouseInputListener();
		addMouseListener(mouseInputListener);
		addMouseMotionListener(mouseInputListener);
	}
	
	public Obstacle getFocusedObstacle(){
		return focusedObstacle;
	}
	
	private void readInObstacles(String oFile) throws Exception {
		File file = new File(oFile);
		Scanner input = new Scanner(file);

		int numOfObstacle = Integer.parseInt(new StringTokenizer(MyObject.ReadInNext(input)).nextToken());
		for (int i = 0; i < numOfObstacle; i++)
			oCandidates.add(new Obstacle(input));
	}
	
	public Dimension getPreferredSize() {
		return new Dimension((int) WIDTH, (int) GraphPanel.CANVAS);
	}
	
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		
		g.setColor(Color.black);
		g.drawString("Player's Strength:\n" + player.getStrength(), 0, 10);
		
		for(int i=0; i<oCandidates.size(); i++){
			oCandidates.get(i).show(g, true);
		}
		
		/*if(mouseInputListener.getMirrorObstacle() != null){
			mouseInputListener.getMirrorObstacle().show(g, false);
		}*/
		//player.showMirrorObstacle(g);
		mouseInputListener.showMirrorObstacle(g);
	}

	public void update(Observable o, Object arg) {
		repaint();
	}
}
