/**
 * 
 */
package ippoz.irene.threatanalysis.engine;

import ippoz.irene.threatanalysis.scenario.EvolutionStep;
import ippoz.irene.threatanalysis.scenario.Scenario;
import ippoz.irene.threatanalysis.threats.ThreatManager;
import ippoz.irene.threatanalysis.utility.AppLogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
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
		while(outFolder.endsWith("/") || outFolder.endsWith("\\")){
			outFolder = outFolder.substring(0, outFolder.length()-1);
		}
		if(!new File(outFolder).exists()){
			new File(outFolder).mkdirs();
			AppLogger.logInfo(getClass(), "output folder '" + outFolder + "' was created");
		}	
		fcaBuilder = new FcaBuilder(outFolder);
	}

	public void analyze(LinkedList<EvolutionStep> eSteps){
		scenarioList = new LinkedList<Scenario>();
		AppLogger.logInfo(getClass(), "Analyzing " + eSteps.size() + " evolutionary scenarios ");
		for(EvolutionStep eStep : eSteps){
			if(scenarioList.size() > 0)
				scenarioList.add(new Scenario(scenarioList.getLast(), eStep, tManager));
			else scenarioList.add(new Scenario(eStep, tManager));
			scenarioList.getLast().threatAnalysis();
			AppLogger.logInfo(getClass(), "Scenario '" +  scenarioList.getLast().getName() + "': " + scenarioList.getLast().structuralCount() + " structural and " + scenarioList.getLast().emergingCount() + " emerging threats" );
			fcaBuilder.addEvolution(scenarioList.getLast());
		}
	}

	public void report() {
		AppLogger.logOngoingInfo(getClass(), "Storing Threat Analysis results ");
		for(Scenario scenario : scenarioList){
			scenario.print(outFolder);
			System.out.print(".");
		}
		System.out.println(" Done");
		AppLogger.logInfo(getClass(), "Printing Lattice and Stats");
		fcaBuilder.printLattice(scenarioList);
		printStats();
		
	}

	/**
	 * Prints the stats.
	 */
	private void printStats() {
		int i = 0;
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(new File(outFolder + "/analysisStats.csv")));
			writer.write("scenario,,components,,,,buildings,connections,structural threats,,,emerging threats,,,stats,,,\n");
			writer.write("index,name,old,new,deleted,total,,,total,added,removed,total,added,removed,%structural,%emerging,%added,%removed\n");
			for(Scenario scenario : scenarioList){
				writer.write((i++) + "," + scenario.getName() + "," 
						+ scenario.getOldComponentNumber() + "," + scenario.getAddedComponentNumber() + "," + scenario.getDeletedComponentNumber() + "," + scenario.getComponentNumber() + "," 
						+ scenario.getBuildingsNumber() + "," + scenario.getConnectionsNumber() + "," 
						+ scenario.structuralCount() + "," + scenario.addedStructuralCount() + "," + scenario.deletedStructuralCount() + ","
						+ scenario.emergingCount() + "," + scenario.addedEmergingCount() + "," + scenario.deletedEmergingCount() + "," 
						+ (scenario.structuralCount()*100.0/scenario.threatCount()) + "," + (scenario.emergingCount()*100.0/scenario.threatCount()) + "," + ((scenario.addedStructuralCount() + scenario.addedEmergingCount())*100.0/scenario.threatCount()) + "," + ((scenario.deletedStructuralCount() + scenario.deletedEmergingCount())*100.0/scenario.threatCount()) +"\n");
			}
			writer.close();
		} catch(FileNotFoundException ex){
			AppLogger.logException(getClass(), ex, "Unable to find '" + outFolder + "/analysisStats.csv' file");
		} catch (IOException e) {
			AppLogger.logError(getClass(), "BusyFileError", "'" + outFolder + "/analysisStats.csv' is already opened. No stats were written");
		}
	}

	public void flush() {
		scenarioList.clear();
	}	

}
