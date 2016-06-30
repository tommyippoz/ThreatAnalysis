/**
 * 
 */
package ippoz.irene.threatanalysis.engine;

import ippoz.irene.threatanalysis.scenario.EvolutionStep;
import ippoz.irene.threatanalysis.scenario.Scenario;
import ippoz.irene.threatanalysis.threats.ThreatManager;
import ippoz.irene.threatanalysis.utility.AppLogger;

import java.util.LinkedList;

/**
 * @author Tommy
 *
 */
public class Analyzer {
	
	private String outFolder;
	private FcaBuilder fcaBuilder;
	private ThreatManager tManager;
	private LinkedList<Scenario> scenarioList;
	
	public Analyzer(ThreatManager tManager, String outFolder) {
		this.tManager = tManager;
		this.outFolder = outFolder;
		fcaBuilder = new FcaBuilder();
	}

	public void analyze(LinkedList<EvolutionStep> eSteps){
		scenarioList = new LinkedList<Scenario>();
		AppLogger.logInfo(getClass(), "Analyzing " + eSteps.size() + " evolutionary scenarios ");
		for(EvolutionStep eStep : eSteps){
			if(scenarioList.size() > 0)
				scenarioList.add(new Scenario(scenarioList.getLast(), eStep, tManager));
			else scenarioList.add(new Scenario(eStep, tManager));
			scenarioList.getLast().threatAnalysis();
			fcaBuilder.addEvolution(scenarioList.getLast());
		}
	}

	public void report() {
		AppLogger.logOngoingInfo(getClass(), "Storing Threat Analysis results ");
		for(Scenario scenario : scenarioList){
			scenario.print(outFolder);
			System.out.print(".");
		}
		fcaBuilder.printLattice(scenarioList);
		System.out.println(" Done");
	}

	public void flush() {
		scenarioList.clear();
	}	

}
