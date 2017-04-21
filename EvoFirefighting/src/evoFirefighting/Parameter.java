package evoFirefighting;

import grid.Coordinate;

/**
 * Central place for all parameters. Defines names, type and default values.
 * @author Martin
 *
 */
public enum Parameter {
	populationSize(20,Type.Integer),
	simulationTime(50,Type.Integer),
	initialAccount(2.0,Type.Double),
	budget(2.0,Type.Double),
	mutationRate(2.5,Type.Double),
	startOffset(new Coordinate(0,-1),Type.Coordinate),
	highwayDistance(20,Type.Integer)
	;

	private Type type;
	private String defaultValueString;
	private int defaultIntValue;
	private double defaultDoubleValue;
	private Coordinate defaultCoordinateValue;
	
	private Parameter(Type type) {
		this.type = type;
		defaultIntValue = -1;
		defaultDoubleValue = -1.0;
		defaultCoordinateValue = null;
	}
	
	private Parameter(int defaultValue, Type type) {
		this(type);
		defaultIntValue = defaultValue;
		defaultValueString = Integer.toString(defaultValue);
	}
	
	private Parameter(double defaultValue, Type type) {
		this(type);
		defaultDoubleValue = defaultValue;
		defaultValueString = Double.toString(defaultValue);
	}
	
	private Parameter(Coordinate defaultValue, Type type) {
		this(type);
		defaultCoordinateValue = defaultValue;
		defaultValueString = defaultValue.toString();
	}
	
	public Object getDefaultValue() {
		switch (type) {
		case Coordinate:
			return defaultCoordinateValue;
		case Double:
			return defaultDoubleValue;
		case Integer:
			return defaultIntValue;
		}
		return null;
	}
	
	public String getDefaultValueString() {
		return defaultValueString;
	}
}
