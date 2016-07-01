/**
 * 
 */
package ippoz.irene.threatanalysis.threats;

/**
 * @author Tommy
 *
 */
public class Threat implements Comparable<Threat>{
	
	private int index;
	private String name;
	private String description;
	private ThreatCategory category;
	
	public Threat(int index, String name, String description, ThreatCategory category) {
		this.index = index;
		this.name = name;
		this.description = description;
		this.category = category;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}	
	
	public ThreatCategory getCategory() {
		return category;
	}

	@Override
	public String toString() {
		return String.valueOf(index);
	}

	@Override
	public int compareTo(Threat other) {
		return Integer.compare(index, other.getIndex());
	}

}
