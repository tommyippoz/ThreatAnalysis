/**
 * 
 */
package ippoz.irene.threatanalysis.scenario;

import ippoz.irene.threatanalysis.component.Building;
import ippoz.irene.threatanalysis.component.Component;
import ippoz.irene.threatanalysis.component.ComponentType;
import ippoz.irene.threatanalysis.component.Connection;
import ippoz.irene.threatanalysis.threats.Threat;
import ippoz.irene.threatanalysis.threats.ThreatManager;
import ippoz.irene.threatanalysis.utility.AppLogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collections;
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
	
	public int threatCount() {
		int count = 0;
		for(LinkedList<Component> cl : scenarioThreats.keySet()){
			count = count + scenarioThreats.get(cl).size();
		}
		return count;
	}
	
	public int getComponentNumber(){
		return getComponents().size();
	}
	
	public int getOldComponentNumber(){
		if(oldScenario != null)
			return oldScenario.getComponentNumber();
		else return 0;
	}
	
	public int getAddedComponentNumber(){
		return evStep.getAddedComponents().size();
	}
	
	public int getDeletedComponentNumber(){
		return evStep.getDeletedComponents().size();
	}
	
	public int getBuildingsNumber(){
		int count = 0;
		for(Component comp : getComponents()){
			if(comp instanceof Building)
				count++;
		}
		return count;
	}
	
	public int getConnectionsNumber(){
		int count = 0;
		for(Component comp : getComponents()){
			if(comp instanceof Connection)
				count++;
		}
		return count;
	}
	
	public int addedStructuralCount(){
		int count = 0;
		LinkedList<Threat> oldList;
		if(oldScenario == null)
			return structuralCount();
		for(LinkedList<Component> cl : scenarioThreats.keySet()){
			if(cl.size() == 1){
				oldList = oldScenario.getExistingList(cl);
				if(oldList == null)
					count = count + scenarioThreats.get(cl).size();
				else {
					for(Threat t : scenarioThreats.get(cl)){
						if(!oldList.contains(t))
							count++;
						}
				}
			}
		}
		return count;
	}
	
	public int deletedStructuralCount(){
		int count = 0;
		LinkedList<Threat> newList;
		if(oldScenario == null)
			return 0;
		for(LinkedList<Component> cl : oldScenario.getThreats().keySet()){
			if(cl.size() == 1){
				newList = getExistingList(cl);
				if(newList == null){
					count = count + oldScenario.getThreats().get(cl).size();
				} else {
					for(Threat t : oldScenario.getThreats().get(cl)){
						if(!newList.contains(t))
							count++;
					}
				}
			}
		}
		return count;
	}
	
	public int addedEmergingCount(){
		int count = 0;
		LinkedList<Threat> oldList;
		if(oldScenario == null)
			return emergingCount();
		for(LinkedList<Component> cl : scenarioThreats.keySet()){
			if(cl.size() > 1){
				oldList = oldScenario.getExistingList(cl);
				if(oldList == null)
					count = count + scenarioThreats.get(cl).size();
				else {
					for(Threat t : scenarioThreats.get(cl)){
						if(!oldList.contains(t))
							count++;
						}
				}
			}
		}
		return count;
	}
	
	public int deletedEmergingCount(){
		int count = 0;
		LinkedList<Threat> newList;
		if(oldScenario == null)
			return 0;
		for(LinkedList<Component> cl : oldScenario.getThreats().keySet()){
			if(cl.size() > 1){
				newList = getExistingList(cl);
				if(newList == null){
					count = count + oldScenario.getThreats().get(cl).size();
				} else {
					for(Threat t : oldScenario.getThreats().get(cl)){
						if(!newList.contains(t))
							count++;
					}
				}
			}
		}
		return count;
	}
	
	public void threatAnalysis() {
		if(oldScenario != null)
			scenarioThreats = cloneOldThreats();
		else scenarioThreats = new HashMap<LinkedList<Component>, LinkedList<Threat>>();
		for(Component comp : evStep.getDeletedComponents()){
			deleteThreats(comp);
		}
		for(Component comp : evStep.getAddedComponents()){
			addThreats(comp);
		}
		for(LinkedList<Component> cl : scenarioThreats.keySet()){
			Collections.sort(scenarioThreats.get(cl));
		}

	}

	@SuppressWarnings("unchecked")
	private HashMap<LinkedList<Component>, LinkedList<Threat>> cloneOldThreats() {
		HashMap<LinkedList<Component>, LinkedList<Threat>> cloned = new HashMap<LinkedList<Component>, LinkedList<Threat>>();
		HashMap<LinkedList<Component>, LinkedList<Threat>> oldThreats = oldScenario.getThreats();
		for(LinkedList<Component> cl : oldThreats.keySet()){
			cloned.put((LinkedList<Component>)cl.clone(), (LinkedList<Threat>)oldThreats.get(cl).clone());
		}
		return cloned;
	}

	private void addThreats(Component comp) {
		LinkedList<Threat> partialList;
		HashMap<LinkedList<Component>, LinkedList<Threat>> emThreats;
		LinkedList<Component> cList = new LinkedList<Component>();
		cList.add(comp);
		if(scenarioThreats.get(cList) == null)
			scenarioThreats.put(cList, new LinkedList<Threat>());
		if(tManager.getCategoryThreats(comp) != null){
			for(Threat t : tManager.getCategoryThreats(comp)){
				scenarioThreats.get(cList).add(t);
			}
		} else AppLogger.logInfo(getClass(), "Unable to find category threats for " + comp.toString());
		if(tManager.getComponentThreats(comp) != null){
			for(Threat t : tManager.getComponentThreats(comp)){
				scenarioThreats.get(cList).add(t);
			}
		} else AppLogger.logInfo(getClass(), "Unable to find component threats for " + comp.toString());
		for(Component c : evStep.getAddedComponents()){
			emThreats = emergingThreats(c);
			for(LinkedList<Component> cl : emThreats.keySet()){
				partialList = getExistingList(cl);
				if(partialList == null){
					partialList = new LinkedList<Threat>();
					scenarioThreats.put(cl, partialList);
				} 
				for(Threat t : emThreats.get(cl)){
					if(!partialList.contains(t))
						partialList.add(t);
				}
			}
		}
	}

	private LinkedList<Threat> getExistingList(LinkedList<Component> currentList) {
		boolean cont;
		if(scenarioThreats.get(currentList) != null)
			return scenarioThreats.get(currentList);
		for(LinkedList<Component> cl : scenarioThreats.keySet()){
			if(cl.size() == currentList.size()){
				cont = true;
				for(Component c : currentList){
					if(!cl.contains(c)){
						cont = false;
						break;
					}
				}
				if(cont)
					return scenarioThreats.get(cl);
			}
		}
		return null;
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
	
	public int emergingCount(){
		int count = 0;
		for(LinkedList<Component> cl : scenarioThreats.keySet()){
			if(cl.size() > 1)
				count = count + scenarioThreats.get(cl).size();
		}
		return count;
	}
	
	public int structuralCount(){
		int count = 0;
		for(LinkedList<Component> cl : scenarioThreats.keySet()){
			if(cl.size() == 1)
				count = count + scenarioThreats.get(cl).size();
		}
		return count;
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
		printSummary(outFolder);
		printEmerging(outFolder);
	}
	
	private void printSummary(String outFolder){
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(new File(outFolder + "/" + getName() + ".summary")));
			writer.write("* Components\n");
			for(Component comp : getComponents()){
				if(!(comp instanceof Connection))
					writer.write("BLD#" + comp.toString() + "\n");
			}
			for(Component comp : getComponents()){
				if(comp instanceof Connection)
					writer.write("CON#" + comp.toString() + "\n");
			}
			writer.write("\n* Structural Threats\n");
			for(LinkedList<Component> cl : scenarioThreats.keySet()){
				if(cl.size() == 1)
					writer.write("STR#" + cl.toString() + "#" + scenarioThreats.get(cl).toString() + "\n");
			}
			writer.write("\n* Emerging Threats\n");
			for(LinkedList<Component> cl : scenarioThreats.keySet()){
				if(cl.size() > 1)
					writer.write("EM#" + cl.toString() + "#" + scenarioThreats.get(cl).toString() + "\n");
			}
			writer.close();
		} catch(Exception ex){
			AppLogger.logException(getClass(), ex, "Unable to write scenario summary file");
		}
	}
	
	private void printEmerging(String outFolder){
		BufferedWriter writer;
		LinkedList<LinkedList<Component>> matchingCompLists;
		try {
			writer = new BufferedWriter(new FileWriter(new File(outFolder + "/" + getName() + "_emerging.csv")));
			writer.write("Threat,,Occurrences,\n");
			writer.write("Index,Description,Count,List\n");
			for(Threat et : tManager.listEmergingThreats()){
				matchingCompLists = new LinkedList<LinkedList<Component>>();
				writer.write(et.getIndex() + "," + et.getName() + ",");
				for(LinkedList<Component> cl : scenarioThreats.keySet()){
					if(cl.size() > 1 && scenarioThreats.get(cl).contains(et))
						matchingCompLists.add(cl);
				}
				writer.write(matchingCompLists.size() + ",");
				for(LinkedList<Component> cl : matchingCompLists){
					writer.write("(");
					for(Component c : cl){
						writer.write(c.toString() + ";");
					}
					writer.write(") | ");
				}
				writer.write("\n");
			}
			writer.close();
		} catch(Exception ex){
			AppLogger.logException(getClass(), ex, "Unable to write scenario summary file");
		}
	}

}
