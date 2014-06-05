package View;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;

import model.Configuration;
import model.Data;
import model.MyPolygon;
import model.Obstacle;
import model.RRForest;

class ControlPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	Data data = Data.getInstance();
	PlannerPanel PP;
	
	public ControlPanel() {
		Box box = Box.createVerticalBox();
		add(box);
		
		PP = new PlannerPanel();
		//PP.setBorder(new TitledBorder("Step2: Start to Plan!"));
		box.add(PP);
		
		box.setPreferredSize(new Dimension(200,(int)GraphPanel.CANVAS));
	}

	class PlannerPanel extends JPanel{
		private static final long serialVersionUID = 1L;
		//JButton jbtInitialize ;
		JButton jbtStart;
		JButton jbtShowRRF;
		JButton jbtShowPath;
		JButton jbtAddObstacle;
		
		Thread T = null;
		Thread T1 = null;
		PlannerPanel(){
			setLayout(new GridLayout(0, 1, 0, 0));
			/*
			jbtInitialize = new JButton("Initialize");
			add(jbtInitialize);
			*/
			jbtStart = new JButton("Start");
			add(jbtStart);

			jbtShowRRF = new JButton("Show RRF");
			add(jbtShowRRF);
			
			jbtShowPath = new JButton("Show Path");
			add(jbtShowPath);
			
			jbtAddObstacle = new JButton("Add Obstacle");
			add(jbtAddObstacle);
			
			
/*
			jbtInitialize.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(data.rrf == null){
						data.rrf = new RRForest();
					}
					data.setPath(data.rrf.RRF_FirstCONNECT(data.robot.getInitialConfig(), data.robot.getGoalConfig(), 1000));
					jbtInitialize.setEnabled(false);
				}
			});*/

			jbtStart.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(!data.isRunning()){
						data.start();
						//jbtStart.setText("Stop");
					}
					/*else{
						data.stop();
						jbtStart.setText("Start");
					}*/
				}
			});
			
			jbtShowRRF.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(data.rrf == null){
						return;
					}	
					if(!Project_MAIN.GP.isShowRRF()){
						Project_MAIN.GP.setShowRRF(true);
						jbtShowRRF.setText("Hide RRF");
					}
					else{
						Project_MAIN.GP.setShowRRF(false);
						jbtShowRRF.setText("Show RRF");
					}
				}
			});
			
			jbtShowPath.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(data.path == null){
						return;
					}
					if(!Project_MAIN.GP.isShowPath()){
						Project_MAIN.GP.setShowPath(true);
						jbtShowPath.setText("Hide Path");
					}
					else{
						Project_MAIN.GP.setShowPath(false);
						jbtShowPath.setText("Show Path");
					}
				}
			});
			
			jbtAddObstacle.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					MyPolygon newPoly = new MyPolygon();
					newPoly.addPoint(-4, 2);
					newPoly.addPoint(-4, -2);
					newPoly.addPoint(4, -2);
					newPoly.addPoint(4, 2);
					Obstacle newObstacle = new Obstacle(new Configuration(85,100,0));
					newObstacle.addPolygon(newPoly);
					data.addObstacle(newObstacle);
					Project_MAIN.GP.repaint();
					jbtAddObstacle.setEnabled(false);
				}
			});
		}
	}
	
}
