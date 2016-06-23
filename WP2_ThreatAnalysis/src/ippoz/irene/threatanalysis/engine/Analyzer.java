/**
 * 
 */
package ippoz.irene.threatanalysis.engine;

import ippoz.irene.threatanalysis.scenario.EvolutionStep;
import ippoz.irene.threatanalysis.scenario.Scenario;
import ippoz.irene.threatanalysis.threats.ThreatManager;
import ippoz.irene.threatanalysis.utility.AppLogger;
import ippoz.irene.threatanalysis.utility.PreferencesManager;

import java.util.LinkedList;

/**
 * @author Tommy
 *
 */
public class Analyzer {
	
	private PreferencesManager pManager;
	private ThreatManager tManager;
	private LinkedList<Scenario> scenarioList;
	
	public Analyzer(PreferencesManager pManager, ThreatManager tManager) {
		this.pManager = pManager;
		this.tManager = tManager;
	}

	public void analyze(LinkedList<EvolutionStep> eSteps){
		scenarioList = new LinkedList<Scenario>();
		AppLogger.logOngoingInfo(getClass(), "Analyzing " + eSteps.size() + " evolutionary scenarios ");
		for(EvolutionStep eStep : eSteps){
			if(scenarioList.size() > 0)
				scenarioList.add(new Scenario(scenarioList.getLast(), eStep, tManager));
			else scenarioList.add(new Scenario(eStep, tManager));
			scenarioList.getLast().threatAnalysis();
			System.out.print(".");
		}
		System.out.println(" Done");
	}

	public void report() {
		AppLogger.logOngoingInfo(getClass(), "Storing Threat Analysis results ");
		for(Scenario scenario : scenarioList){
			// TODO
			System.out.print(".");
		}
		System.out.println(" Done");
	}

	public void flush() {
		scenarioList.clear();
	}	

}
