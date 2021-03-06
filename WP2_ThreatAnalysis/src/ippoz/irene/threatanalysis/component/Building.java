/**
 * 
 */
package ippoz.irene.threatanalysis.component;

/**
 * @author Tommy
 *
 */
public class Building extends Component {

	public Building(String name, ComponentType compType) {
		super(name, compType);
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean relatedTo(Component dc) {
		if(dc instanceof Connection && ((Connection)dc).relatedTo(this))
			return true;
		else return false;
	}

}
