/**
 * 
 */
package ippoz.irene.threatanalysis.threats;

/**
 * @author Tommy
 *
 */
public class Mitigation {
	
	private int index;
	private String code;
	private String name;
	private String keyPhrases;
	private String description;
	
	public Mitigation(int index, String code, String name, String keyPhrases, String description) {
		this.index = index;
		this.code = code;
		this.name = name;
		this.keyPhrases = keyPhrases;
		this.description = description;
		
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

	public String getCode() {
		return code;
	}

	public String getKeyPhrases() {
		return keyPhrases;
	}

}
