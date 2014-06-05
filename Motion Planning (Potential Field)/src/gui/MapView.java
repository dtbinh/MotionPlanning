package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import planner.MyBitMap;

public class MapView extends JFrame {
	final private static int SCALE = 2;
	final public static int CANVAS = SCALE*MyBitMap.SIZE;
	
	private MyBitMap bitMap;
	
	public MapView(MyBitMap in_bitMap){
		setTitle("Potential Fields(Bit Map)");
		bitMap = in_bitMap;
		if(bitMap.getNumberOfMaps()>1)
			setLayout(new GridLayout(0, 2, 0, 2));
		for(int m=0; m<bitMap.getNumberOfMaps(); m++)
			add(new MapPanel(m));
	}
	
	class MapPanel extends JPanel {
		private int m=0;
		public MapPanel(int in_m){
			super();
			m=in_m;
		}
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			
			for(int i=MyBitMap.SIZE-1, k=0; i>=0; i--, k++)
				for(int j=0; j<MyBitMap.SIZE; j++){
					g.setColor(new Color(bitMap.getMap(m)[j][i], bitMap.getMap(m)[j][i], bitMap.getMap(m)[j][i]));
					g.fillRect(j*SCALE, k*SCALE, SCALE, SCALE);
				}
		}
		
		public Dimension getPreferredSize(){
			return new Dimension(CANVAS, CANVAS);
		}
	}
}
