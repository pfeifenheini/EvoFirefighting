
public class Coordinate implements Cloneable {
	public int x;
	public int y;
	
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public Coordinate clone() {
		Coordinate c = null;
		try {
			c = (Coordinate) super.clone();
			c.x = x;
			c.y = y;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}
}
