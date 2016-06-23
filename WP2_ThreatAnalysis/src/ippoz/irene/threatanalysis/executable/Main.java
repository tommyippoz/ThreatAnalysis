/**
 * 
 */
package ippoz.irene.threatanalysis.executable;

import ippoz.irene.threatanalysis.threats.ThreatManager;
import ippoz.irene.threatanalysis.utility.AppLogger;
import ippoz.irene.threatanalysis.utility.PreferencesManager;

/**
 * @author Tommy
 *
 */
public class Main {

	private static final String FILE_FOLDER = "FILE_FOLDER";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PreferencesManager pManager;
		ThreatManager tManager;
		try {
			pManager = new PreferencesManager("threat_analysis.preferences");
			tManager = new ThreatManager(pManager.getPreference(FILE_FOLDER));
		} catch(Exception ex){
			AppLogger.logException(Main.class, ex, "Exception");
		}
	}

}
