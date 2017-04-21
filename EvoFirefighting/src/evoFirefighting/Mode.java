package evoFirefighting;

/**
 * Different operation modes.
 * @author Martin
 *
 */
public enum Mode {
	EncloseConnected("enclose connected"),
	EncloseScattered("enclose scattered"),
	ProtectConnected("protect connected"),
	ProtectScattered("protect scattered");
	private final String display;
	
	private Mode(String s) {
		display = s;
	}
	
	@Override
	public String toString() {
		return display;
	}
}
