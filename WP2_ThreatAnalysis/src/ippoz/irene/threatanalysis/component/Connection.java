/**
 * 
 */
package ippoz.irene.threatanalysis.component;

/**
 * @author Tommy
 *
 */
public class Connection extends Component {
	
	private Component from;
	private Component to;

	public Connection(String name, ComponentType compType, Component from, Component to) {
		super(name, compType);
		this.from = from;
		this.to = to;
	}

	public Component getFrom() {
		return from;
	}

	public Component getTo() {
		return to;
	}
	
	@Override
	public boolean relatedTo(Component dc) {
		if(dc instanceof Connection){
			return from == dc || to == dc || ((Connection) dc).getFrom().equals(this) || ((Connection) dc).getTo().equals(this);
		} else return from == dc || to == dc;
	}

	@Override
	public String toString() {
		return getName() + "@[" + from.toString() + "-" + to.toString() + "]";
	}

}
