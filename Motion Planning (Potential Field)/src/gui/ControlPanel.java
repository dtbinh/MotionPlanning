package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import planner.NF1;
import planner.Smooth;

class ControlPanel extends JPanel {
	private Planner P;
	String[] flagInputRobots = {"robot00", "robot01", "robot02", "robot03",
			"robot04", "robot05", "robot06", "robot07", "thsrr"};
	String[] flagInputObstacles = {"map00", "map01", "map02", "map03", "map04",
			"map05", "map06", "map07", "thsro"};
	InputPanel IP;
	PlannerPanel PP;
	InformationPanel InP;
	JTextArea msg;
	
	public ControlPanel(Planner in_P) {
		P = in_P;
		
		Box box = Box.createVerticalBox();
		add(box);
		
		IP = new InputPanel();
		IP.setBorder(new TitledBorder("Step1: Choose the Input File"));
		box.add(IP);
		
		PP = new PlannerPanel();
		PP.setBorder(new TitledBorder("Step2: Start to Plan!"));
		box.add(PP);

		InP = new InformationPanel();
		InP.setBorder(new TitledBorder("Step3: Get some Information"));
		box.add(InP);
		
		msg = new JTextArea();
		msg.setBorder(new TitledBorder("Messages:"));
		msg.setBackground(null);
		msg.setEditable(false);
		msg.setPreferredSize(new Dimension(200, 100));
		msg.setSize(new Dimension(200, 100));
		msg.setMaximumSize(new Dimension(200, 100));
		msg.setMinimumSize(new Dimension(200, 100));
		box.add(msg);
		
		box.setPreferredSize(new Dimension(200,(int)GraphPanel.CANVAS));
	}
	
	public void setAllEnabled(boolean b){
		IP.jlstInputObstacles.setEnabled(b);
		IP.jlstInputRobots.setEnabled(b);
		IP.jbtReset.setEnabled(b);
		PP.jbtAnimation.setEnabled(b);
		PP.jbtPlanPath.setEnabled(b);
		PP.jbtSmooth.setEnabled(b);
		InP.jbtPotentialField.setEnabled(b);
		InP.jbtShowPath.setEnabled(b);
	}
	
	class InputPanel extends JPanel{
		JList jlstInputRobots;
		JList jlstInputObstacles;
		JPanel list;
		JButton jbtReset;
		
		InputPanel(){
			setLayout(new BorderLayout(0, 0));
			this.setMaximumSize(new Dimension(200,300));

			JPanel list = new JPanel();
			list.setLayout(new GridLayout(0, 2));
			list.setPreferredSize(new Dimension(200,100));
			add(list, BorderLayout.CENTER);
			
			jlstInputRobots = new JList(flagInputRobots);
			jlstInputRobots.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
			jlstInputRobots.setSelectedIndex(0);
			list.add(new JScrollPane(jlstInputRobots));
			
			jlstInputObstacles = new JList(flagInputObstacles);
			jlstInputObstacles.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
			jlstInputObstacles.setSelectedIndex(0);
			list.add(new JScrollPane(jlstInputObstacles));
			
			JButton jbtNextRobot = new JButton("Next Robot");
			//add(jbtNextRobot);
			
			jbtReset = new JButton("Reset");
			add(jbtReset, BorderLayout.SOUTH);
			
			jlstInputRobots.addListSelectionListener(new ListSelectionListener(){
				public void valueChanged(ListSelectionEvent e){
					int index = jlstInputRobots.getSelectedIndex();
					P.rFile = new String("dat/Robots/"+flagInputRobots[index]+".dat");
					try {
						P.readInRobots();
						P.reset();
						P.GP.repaint();
					} catch (Exception e1) {
						e1.printStackTrace();
					}		
				}
			});
			
			jlstInputObstacles.addListSelectionListener(new ListSelectionListener(){
				public void valueChanged(ListSelectionEvent e){
					int index = jlstInputObstacles.getSelectedIndex();
					P.oFile = new String("dat/Obstacles/"+flagInputObstacles[index]+".dat");
					try {
						P.readInObstacles();
						P.reset();
						P.GP.repaint();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});
			
			jbtNextRobot.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(++P.currentRobot>=P.robots.size())
						P.currentRobot = 0;
				}
			});
			
			jbtReset.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					try {
						P.readInRobots();
						P.readInObstacles();
						P.reset();
						P.GP.repaint();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});			
		}

	}
	class PlannerPanel extends JPanel{
		JButton jbtPlanPath ;
		JButton jbtSmooth;
		JButton jbtAnimation;
		Thread T = null;
		Thread T1 = null;
		PlannerPanel(){
			setLayout(new GridLayout(0, 1, 0, 0));
			
			jbtPlanPath = new JButton("Find the Path");
			add(jbtPlanPath);
			
			jbtSmooth = new JButton("Smooth");
			jbtSmooth.setEnabled(false);
			add(jbtSmooth);

			jbtAnimation = new JButton("Animation");
			jbtAnimation.setEnabled(false);
			add(jbtAnimation);

			jbtPlanPath.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setAllEnabled(false);
					P.reset();
					jbtPlanPath.setText("Cancel");
					jbtPlanPath.setEnabled(true);
					InP.jbtPotentialField.setEnabled(true);
					if(T1 == null)
						T1 = P.Plan();
					else{
						T1.interrupt();
						//P.GP.repaint();	
						jbtPlanPath.setText("Find the Path");
						T1 = null;
						setAllEnabled(true);
					}
					
				}
			});

			jbtSmooth.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(P.path == null)
						return;
					int steps_old = P.path.size();
					Smooth smoother = new Smooth(P.robots.get(P.currentRobot), P.path, P.obstacles);
					P.path = smoother.doSmooth();
					int steps_new = P.path.size();
					msg.setText("<Smoothing>\n"+"we devide the path into "+smoother.getDivisor()+" parts\n"+
							    "Reduces "+steps_old+" steps to "+steps_new+" steps");
					P.GP.paintAllPath();
				}
			});
			
			jbtAnimation.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (P.path == null)
						return;
					setAllEnabled(false);
					jbtAnimation.setText("Stop");
					jbtAnimation.setEnabled(true);
					if(T == null)
						T = P.GP.paintPath();
					else{
						T.interrupt();
						P.GP.animation = false;
						P.GP.paintLast = true;
						P.GP.repaint();
						jbtAnimation.setText("Animation");
						P.CP.setAllEnabled(true);
						T = null;
					}
				}
			});
		}
	}
	class InformationPanel extends JPanel{
		JButton jbtShowPath;
		JButton jbtPotentialField;
		
		InformationPanel(){
			setLayout(new GridLayout(0, 1, 0, 0));
			
			jbtPotentialField = new JButton("Potential Field");
			add(jbtPotentialField);
			
			jbtShowPath = new JButton("Hide Path");
			jbtShowPath.setEnabled(false);
			add(jbtShowPath);
			
			jbtShowPath.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (P.path == null)
						return;
					if(P.GP.paintAllPath){
						P.GP.paintAllPath = false;
						P.GP.paintLast = false;
						jbtShowPath.setText("Show Path");
					}
					else{
						P.GP.paintAllPath = true;
						P.GP.paintLast = false;
						jbtShowPath.setText("Hide Path");
					}
					P.GP.repaint();
				}
			});
			
			jbtPotentialField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (P.PF == null)
						P.PF = new NF1(P.robots.get(P.currentRobot), P.obstacles);
					MapView frame2 = new MapView(P.PF);
					frame2.setLocationRelativeTo(null);
					frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					frame2.pack();
					frame2.setVisible(true);
				}
			});
		}
	}
}
