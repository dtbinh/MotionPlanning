package util;

import java.util.ArrayList;

import model.MyObject;
import model.MyPolygon;

public class PlannerObject {
	/*this object contains the information of the specific object in the planner coordinates*/
	private boolean referenceChanged;
	protected boolean polygonChanged;
	private MyObject object;
	private ArrayList<MyPolygon> referencePolygons;
	
	public PlannerObject(MyObject object){
		this.object = object;
		setPlannerReferencePolygons();
	}
	
	public MyObject getMyObject(){
		return object;
	}
	
	public ArrayList<MyPolygon> getPlannerReferencePolygons(){
		if(referenceChanged || polygonChanged){
			setPlannerReferencePolygons();
		}
		return referencePolygons;
	}
	
	public void setReferenceChanged(){
		referenceChanged = true;
	}
	
	public void setPolygonChanged(){
		polygonChanged = true;
	}
	
	private void setPlannerReferencePolygons() {
		this.referencePolygons = new ArrayList<MyPolygon>();
		for (int i = 0; i < object.getMyPolygon().size(); i++) {
			referencePolygons.add(new MyPolygon());
			for (int j = 0; j < object.getMyPolygon().get(i).npoints; j++) {
				Point pair = Transformer.LocalToPlanner(object.getMyPolygon().get(i).xpoints[j],
						object.getMyPolygon().get(i).ypoints[j],
						object.getReferenceConfig().theta,
						object.getReferenceConfig().x,
						object.getReferenceConfig().y);
				referencePolygons.get(i).addPoint(pair.x, pair.y);
			}
		}
		referenceChanged = false;
		polygonChanged = false;
	}	
}
