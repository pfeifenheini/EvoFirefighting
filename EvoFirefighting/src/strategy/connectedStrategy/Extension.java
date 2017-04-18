package strategy.connectedStrategy;

import java.util.Random;

class Extension implements Cloneable {
	Direction dir;
	boolean extendFront;
	
	public Extension(Random rand) {
		dir = Direction.values()[rand.nextInt(8)];
		extendFront = rand.nextBoolean();
	}
	
	public Extension(Direction dir, boolean extendFront) {
		this.dir = dir;
		this.extendFront = extendFront;
	}
	
	public void copy(Extension toCopy) {
		dir = toCopy.dir;
		extendFront = toCopy.extendFront;
	}
	
	@Override
	public Extension clone() {
		Extension e = null;
		try {
			e = (Extension) super.clone();
			e.dir = dir;
			e.extendFront = extendFront;
		} catch (CloneNotSupportedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return e;
	}
}
