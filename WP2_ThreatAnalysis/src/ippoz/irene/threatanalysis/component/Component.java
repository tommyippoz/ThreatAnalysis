/**
 * 
 */
package ippoz.irene.threatanalysis.component;

/**
 * @author Tommy
 *
 */
public abstract class Component {
	
	private String name;
	private ComponentType compType;
	
	public Component(String name, ComponentType compType) {
		this.name = name;
		this.compType = compType;
	}

	public String getName() {
		return name;
	}

	public ComponentType getCompType() {
		return compType;
	}

}
