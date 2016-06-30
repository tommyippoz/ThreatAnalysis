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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PreferencesManager pManager;
		Analyzer tAnalyzer;
		try {
			pManager = new PreferencesManager("threat_analysis.preferences");
			tAnalyzer = new Analyzer(new ThreatManager(pManager.getPreference(FILE_FOLDER)), pManager.getPreference(OUTPUT_FOLDER));
			tAnalyzer.analyze(loadEvolutionSteps(pManager.getPreference(SCENARIO_FOLDER)));
			tAnalyzer.report();
			tAnalyzer.flush();
		} catch(Exception ex){
			AppLogger.logException(Main.class, ex, "Exception");
		}
	}

	private static LinkedList<EvolutionStep> loadEvolutionSteps(String scenarioFolder) {
		File scenarioFolderFile = new File(scenarioFolder);
		LinkedList<EvolutionStep> eStep = new LinkedList<EvolutionStep>();
		if(scenarioFolderFile.exists()){
			for(File evStepFile : scenarioFolderFile.listFiles()){
				if(evStepFile.getName().endsWith(".scenario")){
					eStep.add(new EvolutionStep(evStepFile));
				}
			}
		}
		return eStep;
	}

}
