/**
 * 
 */
package ippoz.irene.threatanalysis.component;

import ippoz.irene.threatanalysis.threats.Threat;

import java.util.LinkedList;

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

}
