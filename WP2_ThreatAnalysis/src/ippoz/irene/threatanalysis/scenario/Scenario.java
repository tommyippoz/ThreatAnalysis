/**
 * 
 */
package ippoz.irene.threatanalysis.scenario;

import ippoz.irene.threatanalysis.component.Component;
import ippoz.irene.threatanalysis.threats.ThreatManager;

import java.util.LinkedList;

/**
 * @author Tommy
 *
 */
public class Scenario {
	
	private EvolutionStep evStep;
	private LinkedList<Component> scenarioComponents;
	private ThreatManager tManager;
	
	public Scenario(Scenario oldScenario, EvolutionStep evStep, ThreatManager tManager){
		this.evStep = evStep;
		this.tManager = tManager;
		scenarioComponents = new LinkedList<Component>();
		if(oldScenario != null)
			scenarioComponents.addAll(oldScenario.getComponents());
		evStep.loadFile(oldScenario);
	}
	
	public Scenario(EvolutionStep eStep, ThreatManager tManager) {
		this(null, eStep, tManager);
	}

	public void threatAnalysis() {
		// TODO Auto-generated method stub
		
	}

	private LinkedList<Component> getComponents() {
		return scenarioComponents;
	}

	public Component getComponentByName(String compName) {
		for(Component c : scenarioComponents){
			if(c.getName().equals(compName)){
				return c;
			}
		}
		return null;
	}

}
