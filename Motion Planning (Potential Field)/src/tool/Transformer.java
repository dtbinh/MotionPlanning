package tool;

public class Transformer extends DPair {
	public Transformer(double x, double y, double angle, double dx, double dy) {
		super((Math.cos(angle / 180 * Math.PI) * x
				- Math.sin(angle / 180 * Math.PI) * y + dx), (Math.sin(angle
				/ 180 * Math.PI)
				* x + Math.cos(angle / 180 * Math.PI) * y + dy));
	}
}
