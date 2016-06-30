/**
 * 
 */
package ippoz.irene.threatanalysis.scenario;

import ippoz.irene.threatanalysis.component.Component;
import ippoz.irene.threatanalysis.component.ComponentType;
import ippoz.irene.threatanalysis.component.Connection;
import ippoz.irene.threatanalysis.threats.Threat;
import ippoz.irene.threatanalysis.threats.ThreatManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author Tommy
 *
 */
public class Scenario {
	
	private EvolutionStep evStep;
	private Scenario oldScenario;
	private LinkedList<Component> scenarioComponents;
	private ThreatManager tManager;
	private HashMap<LinkedList<Component>, LinkedList<Threat>> scenarioThreats;
	
	public Scenario(Scenario oldScenario, EvolutionStep evStep, ThreatManager tManager){
		this.evStep = evStep;
		this.tManager = tManager;
		this.oldScenario = oldScenario;
		scenarioComponents = new LinkedList<Component>();
		if(oldScenario != null)
			scenarioComponents.addAll(oldScenario.getComponents());
		evStep.loadFile(oldScenario);
	}
	
	public Scenario(EvolutionStep eStep, ThreatManager tManager) {
		this(null, eStep, tManager);
	}

	public void threatAnalysis() {
		if(oldScenario != null)
			scenarioThreats = oldScenario.getThreats();
		else scenarioThreats = new HashMap<LinkedList<Component>, LinkedList<Threat>>();
		for(Component comp : evStep.getDeletedComponents()){
			deleteThreats(comp);
		}
		for(Component comp : evStep.getAddedComponents()){
			addThreats(comp);
		}

	}

	private void addThreats(Component comp) {
		HashMap<LinkedList<Component>, LinkedList<Threat>> emThreats;
		LinkedList<Component> cList = new LinkedList<Component>();
		cList.add(comp);
		if(scenarioThreats.get(cList) == null)
			scenarioThreats.put(cList, new LinkedList<Threat>());
		for(Threat t : tManager.getCategoryThreats(comp)){
			scenarioThreats.get(cList).add(t);
		}
		for(Threat t : tManager.getComponentThreats(comp)){
			scenarioThreats.get(cList).add(t);
		}
		for(Component c : evStep.getAddedComponents()){
			emThreats = emergingThreats(c);
			for(LinkedList<Component> cl : emThreats.keySet()){
				if(scenarioThreats.get(cl) == null)
					scenarioThreats.put(cl, new LinkedList<Threat>());
				scenarioThreats.get(cl).addAll(emThreats.get(cl));
			}
		}
	}

	private HashMap<LinkedList<Component>, LinkedList<Threat>> emergingThreats(Component comp) {
		HashMap<LinkedList<Component>, LinkedList<Threat>> emThreats = new HashMap<LinkedList<Component>, LinkedList<Threat>>();
		LinkedList<Component> connectedComponents = new LinkedList<Component>();
		HashMap<LinkedList<ComponentType>, LinkedList<Threat>> validThreats = tManager.getEmergingThreats(comp);
		getConnectedComponents(comp, connectedComponents);
		for(LinkedList<ComponentType> ctl : validThreats.keySet()){
			for(LinkedList<Component> cl : getComponentsCombinations(comp, connectedComponents, ctl)){
				emThreats.put(cl, validThreats.get(ctl));
			}
		}
		return emThreats;
	}

	@SuppressWarnings("unchecked")
	private LinkedList<LinkedList<Component>> getComponentsCombinations(Component comp, LinkedList<Component> connectedComponents, LinkedList<ComponentType> ctl) {
		LinkedList<LinkedList<Component>> outList = new LinkedList<LinkedList<Component>>();
		HashMap<ComponentType, LinkedList<Component>> connectedCat = new HashMap<ComponentType, LinkedList<Component>>();
		LinkedList<ComponentType> otherTypes = (LinkedList<ComponentType>) ctl.clone();
		otherTypes.remove(comp.getCompType());
		for(ComponentType ct : otherTypes){
			connectedCat.put(ct, new LinkedList<Component>());
			for(Component cc : connectedComponents){
				if(cc.getCompType().equals(ct))
					connectedCat.get(ct).add(cc);
			}
			if(connectedCat.get(ct).size() == 0)
				return outList;
		}
		outList = combinationsOf(connectedCat, otherTypes);
		for(LinkedList<Component> cl : outList){
			if(cl.contains(comp))
				return new LinkedList<LinkedList<Component>>(); 
			cl.add(comp);
		}
		return outList;
	}

	@SuppressWarnings("unchecked")
	private LinkedList<LinkedList<Component>> combinationsOf(HashMap<ComponentType, LinkedList<Component>> connectedCat, LinkedList<ComponentType> validTypes) {
		LinkedList<LinkedList<Component>> baseList; 
		LinkedList<LinkedList<Component>> newList; 
		ComponentType cType = validTypes.removeFirst();
		if(validTypes.size() == 0){
			baseList = new LinkedList<LinkedList<Component>>();
			baseList.add(connectedCat.get(cType));
			return baseList;
		} else {
			baseList = combinationsOf(connectedCat, validTypes);
			newList = new LinkedList<LinkedList<Component>>();
			for(Component c : connectedCat.get(cType)){
				for(LinkedList<Component> bcl : baseList){
					newList.add((LinkedList<Component>) bcl.clone());
					newList.getLast().add(c);
				}
			}
			return newList;
		}
		
	}

	private void getConnectedComponents(Component comp, LinkedList<Component> list) {
		LinkedList<Component> newlyAdded = new LinkedList<Component>();
		for(Component c : getComponents()){
			if(!list.contains(c)){
				if(comp instanceof Connection){
					if(((Connection) comp).relatedTo(c))
						newlyAdded.add(c);
				} else {
					if(c instanceof Connection){
						if(((Connection) c).relatedTo(comp))
							newlyAdded.add(c);
					}
				}
			}
		}
		list.addAll(newlyAdded);
		for(Component c : newlyAdded){
			getConnectedComponents(c, list);
		}
	}

	private void deleteThreats(Component comp) {
		LinkedList<Component> cList = new LinkedList<Component>();
		LinkedList<LinkedList<Component>> toRemove = new LinkedList<LinkedList<Component>>();
		cList.add(comp);
		scenarioThreats.remove(cList);
		for(LinkedList<Component> cl : scenarioThreats.keySet()){
			if(cl.contains(comp))
				toRemove.add(cl);
		}
		for(LinkedList<Component> cl : toRemove){
			scenarioThreats.remove(cl);
		}
	}

	public LinkedList<Component> getComponents() {
		LinkedList<Component> completeList = new LinkedList<Component>();
		completeList.addAll(scenarioComponents);
		completeList.addAll(evStep.getAddedComponents());
		completeList.removeAll(evStep.getDeletedComponents());
		return completeList;
	}

	public Component getComponentByName(String compName) {
		for(Component c : getComponents()){
			if(c.getName().equals(compName)){
				return c;
			}
		}
		return null;
	}

	public HashMap<LinkedList<Component>, LinkedList<Threat>> getThreats() {
		return scenarioThreats;
	}

	public String getName() {
		return evStep.getName();
	}
	
	public void print(String outFolder) {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(new File(outFolder + "/" + getName() + ".summary")));
			writer.write("* Components\n");
			for(Component comp : getComponents()){
				writer.write(comp.toString() + "\n");
			}
			writer.write("\n* Threats\n");
			for(LinkedList<Component> cl : scenarioThreats.keySet()){
				if(cl.size() > 1)
					writer.write("EM#");
				else writer.write("STR#");
				writer.write(cl.toString() + "#" + scenarioThreats.get(cl).toString() + "\n");
			}
			writer.close();
		} catch(Exception ex){
			
		}
	}

}
