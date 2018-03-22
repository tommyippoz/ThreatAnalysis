/**
 * 
 */
package ippoz.irene.threatanalysis.executable;

import java.io.File;
import java.util.LinkedList;

import ippoz.irene.threatanalysis.engine.Analyzer;
import ippoz.irene.threatanalysis.scenario.EvolutionStep;
import ippoz.irene.threatanalysis.threats.ThreatManager;
import ippoz.irene.threatanalysis.utility.AppLogger;
import ippoz.irene.threatanalysis.utility.PreferencesManager;

/**
 * @author Tommy
 *
 */
public class Main {

	private static final String FILE_FOLDER = "FILE_FOLDER";
	private static final String SCENARIO_FOLDER = "SCENARIO_FOLDER";
	private static final String OUTPUT_FOLDER = "OUTPUT_FOLDER";
	private static final String THREAT_FILTER = "THREAT_FILTER";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PreferencesManager pManager;
		Analyzer tAnalyzer;
		long startTime = System.currentTimeMillis();
		try {
			pManager = new PreferencesManager("threat_analysis.preferences");
			if(checkPreferences(pManager)){
				AppLogger.logInfo(Main.class, "Reading Threat Library");
				tAnalyzer = new Analyzer(new ThreatManager(pManager.getPreference(FILE_FOLDER), pManager.getPreference(THREAT_FILTER)), pManager.getPreference(OUTPUT_FOLDER));
				AppLogger.logInfo(Main.class, "Analyzing evolutions");
				tAnalyzer.analyze(loadEvolutionSteps(pManager.getPreference(SCENARIO_FOLDER)));
				AppLogger.logInfo(Main.class, "Reporting outputs");
				tAnalyzer.report();
				tAnalyzer.flush();
				AppLogger.logInfo(Main.class, "Total Execution time: " + (System.currentTimeMillis() - startTime) + " ms");
			} 
		} catch(Exception ex){
			AppLogger.logException(Main.class, ex, "Generic Exception");
		}
	}
	
	private static boolean checkPreferences(PreferencesManager pManager){
		if(pManager.correctlyRead()){
			if(pManager.getPreference(FILE_FOLDER) == null){
				AppLogger.logError(Main.class, "UnspecifiedProperty", "Need to specify the 'FILE_FOLDER' property");
				return false;
			} if(pManager.getPreference(SCENARIO_FOLDER) == null){
				AppLogger.logError(Main.class, "UnspecifiedProperty", "Need to specify the 'SCENARIO_FOLDER' property");
				return false;
			} if(pManager.getPreference(OUTPUT_FOLDER) == null){
				AppLogger.logError(Main.class, "UnspecifiedProperty", "Need to specify the 'OUTPUT_FOLDER' property");
				return false;
			} if(pManager.getPreference(THREAT_FILTER) == null){
				AppLogger.logError(Main.class, "UnspecifiedProperty", "Need to specify the 'THREAT_FILTER' property (ALL for default");
				return false;
			}
			return true;
		} else AppLogger.logError(Main.class, "PreferencesNotFound", "Unable to find the required 'threat_analysis.preferences' preferences file");
		return false;
	}

	private static LinkedList<EvolutionStep> loadEvolutionSteps(String scenarioFolder) {
		File scenarioFolderFile = new File(scenarioFolder);
		LinkedList<EvolutionStep> eStep = new LinkedList<EvolutionStep>();
		if(scenarioFolderFile.exists()){
			if(scenarioFolderFile != null && scenarioFolderFile.listFiles() != null){
				for(File evStepFile : scenarioFolderFile.listFiles()){
					if(evStepFile != null && evStepFile.getName().endsWith(".scenario")){
						eStep.add(new EvolutionStep(evStepFile));
					}
				}
			}
		}
		return eStep;
	}

}
