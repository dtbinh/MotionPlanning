package View;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import model.Configuration;
import model.Data;
import model.MyPolygon;
import model.Obstacle;

class ControlPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	Data data = Data.getInstance();

	public ControlPanel() {
		setLayout(new GridLayout(1, 0, 0, 0));

		JButton jbtStart = new JButton("Start");
		jbtStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!data.isRunning()) {
					data.start();
				}
			}
		});
		add(jbtStart);

		final JButton jbtShowRRF = new JButton("Show RRF");
		jbtShowRRF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (data.getRRF() == null) {
					return;
				}
				if (!Project_MAIN.GP.isShowRRF()) {
					Project_MAIN.GP.setShowRRF(true);
					jbtShowRRF.setText("Hide RRF");
				} else {
					Project_MAIN.GP.setShowRRF(false);
					jbtShowRRF.setText("Show RRF");
				}
			}
		});
		add(jbtShowRRF);

		final JButton jbtShowPath = new JButton("Show Path");
		jbtShowPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (data.getPath() == null) {
					return;
				}
				if (!Project_MAIN.GP.isShowPath()) {
					Project_MAIN.GP.setShowPath(true);
					jbtShowPath.setText("Hide Path");
				} else {
					Project_MAIN.GP.setShowPath(false);
					jbtShowPath.setText("Show Path");
				}
			}
		});
		add(jbtShowPath);

		final JButton jbtAddObstacle = new JButton("Add Obstacle");
		jbtAddObstacle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MyPolygon newPoly = new MyPolygon();
				newPoly.addPoint(-4, 2);
				newPoly.addPoint(-4, -2);
				newPoly.addPoint(4, -2);
				newPoly.addPoint(4, 2);
				Obstacle newObstacle = new Obstacle(new Configuration(85, 100,
						0));
				newObstacle.addPolygon(newPoly);
				data.addObstacle(newObstacle);
				Project_MAIN.GP.repaint();
				jbtAddObstacle.setEnabled(false);
			}
		});
		//add(jbtAddObstacle);

	}
}
