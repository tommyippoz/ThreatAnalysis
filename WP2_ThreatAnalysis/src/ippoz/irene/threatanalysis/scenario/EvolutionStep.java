/**
 * 
 */
package ippoz.irene.threatanalysis.scenario;

import ippoz.irene.threatanalysis.component.Building;
import ippoz.irene.threatanalysis.component.Component;
import ippoz.irene.threatanalysis.component.ComponentCategory;
import ippoz.irene.threatanalysis.component.ComponentType;
import ippoz.irene.threatanalysis.component.Connection;
import ippoz.irene.threatanalysis.utility.AppLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 * @author Tommy
 *
 */
public class EvolutionStep {

	private File targetFile;
	private LinkedList<Component> addedComponents;
	private LinkedList<Component> deletedComponents;
	
	public EvolutionStep(File targetFile){
		this.targetFile = targetFile;
	}
	
	public LinkedList<Component> getAddedComponents() {
		return addedComponents;
	}
	
	public LinkedList<Component> getDeletedComponents() {
		return deletedComponents;
	}

	public void loadFile(Scenario oldScenario) {
		String readed;
		String splitted[];
		BufferedReader reader = null;
		try {
			addedComponents = new LinkedList<Component>();
			deletedComponents = new LinkedList<Component>();
			reader = new BufferedReader(new FileReader(targetFile));
			while(reader.ready()){
				readed = reader.readLine();
				if(readed != null){
					readed = readed.trim();
					if(readed.length() > 0 && !readed.startsWith("*")){
						splitted = readed.split(",");
						manageComponent(readed, splitted, oldScenario);
					}
				}
			}
			checkCascadingDelete(oldScenario);
			reader.close();
			AppLogger.logInfo(getClass(), "Evolution Step '" + getName() + "' read: " + addedComponents.size() + " added and " + deletedComponents.size() + " deleted components");
		} catch(IOException ex){
			AppLogger.logException(getClass(), ex, "Unable to load evolution step [" + targetFile.getName() + "]");
		}
	}
	
	private void checkCascadingDelete(Scenario oldScenario) {
		LinkedList<Component> cascadingDelete = new LinkedList<Component>();
		for(Component dc : deletedComponents){
			cascadingComponent(oldScenario, dc, cascadingDelete);
		}
		deletedComponents.addAll(cascadingDelete);
	}
	
	@SuppressWarnings("unchecked")
	private void cascadingComponent(Scenario oldScenario, Component dc, LinkedList<Component> cascadingDelete){
		LinkedList<Component> tempList = (LinkedList<Component>) oldScenario.getComponents().clone();
		tempList.addAll(addedComponents);
		for(Component comp : tempList){
			if(comp instanceof Connection){
				if(!deletedComponents.contains(comp) && !cascadingDelete.contains(comp) && comp.relatedTo(dc) && referredBy(oldScenario, comp, cascadingDelete) <= 1){
					cascadingDelete.add(comp);
					cascadingComponent(oldScenario, comp, cascadingDelete);
				}
			}
		}
	}
	
	private int referredBy(Scenario oldScenario, Component comp, LinkedList<Component> cascadingDelete){
		int count = 0;
		for(Component c : oldScenario.getComponents()){
			if(!deletedComponents.contains(c) && !cascadingDelete.contains(c) && c.relatedTo(comp))
				count++;
		} 
		for(Component c : addedComponents){
			if(!deletedComponents.contains(c) && !cascadingDelete.contains(c) && c.relatedTo(comp))
				count++;
		}
		return count;
	}

	private void manageComponent(String readed, String[] splitted, Scenario oldScenario) {
		Component comp; 
		if(splitted != null && (splitted.length == 3 || splitted.length == 4)){
			comp = buildComponent(splitted, oldScenario);
			if(comp != null){
				switch(splitted[0].trim()){
					case "ADD":
						addedComponents.add(comp);
						break;
					case "DEL":
					case "DELETE":
					case "REMOVE":
						deletedComponents.add(comp);
						break;
					default:
						AppLogger.logError(getClass(), "UnrecognizedEvolutionActionException", "unrecognized action: '" + splitted[0] + "'");
				}
			}
		} else AppLogger.logError(getClass(), "UnrecognizdString", "Unable to recognize '" + readed + "': accepted formats are '{ADD/DEL},{PP,H ...},<building_name>' and '{ADD/DEL},{EC,DC,MG},<connection_name>,<from_component>;<to_component>'");
	}

	private Component buildComponent(String[] splitted, Scenario oldScenario) {
		ComponentType cType;
		try {
			cType = ComponentType.valueOf(splitted[1].trim());
			if(getComponent(splitted[2].trim(), oldScenario) != null){
				return getComponent(splitted[2].trim(), oldScenario);
			}
			if(Component.getCategoryOf(cType).equals(ComponentCategory.CON)){
				if(splitted[3] != null && splitted[3].contains(";") && splitted[3].split(";").length == 2) {
					if(getComponent(splitted[3].trim().split(";")[0].trim(), oldScenario) == null) {
						AppLogger.logInfo(getClass(), "Ignored component: " + splitted[2] + " due to unrecognized '"  + splitted[3].split(";")[0].trim() + "' constituent component");
					} else if(getComponent(splitted[3].trim().split(";")[1].trim(), oldScenario) == null){
						AppLogger.logInfo(getClass(), "Ignored component: " + splitted[2] + " due to unrecognized '"  + splitted[3].split(";")[1].trim() + "' constituent component");
					} else return new Connection(splitted[2].trim(), cType, getComponent(splitted[3].trim().split(";")[0].trim(), oldScenario), getComponent(splitted[3].split(";")[1].trim(), oldScenario));
				} else AppLogger.logError(getClass(), "WrongConstituentComponents", "Unable to parse '" + splitted[3] + "': specify 2 component names separated by ;");
			} else return new Building(splitted[2].trim(), cType);
		} catch(Exception ex){
			AppLogger.logError(getClass(), "WrongComponentCategory", "Category '" + splitted[1] + "' is unknown: please check the valid ones");
		}
		return null;
	}

	private Component getComponent(String compName, Scenario oldScenario) {
		Component comp = null;
		if(oldScenario != null)
			comp = oldScenario.getComponentByName(compName);
		if(comp == null) {
			for(Component ac : addedComponents){
				if(ac.getName().equals(compName)){
					return ac;
				}
			}
		} 
		return comp;
	}

	public String getName() {
		return targetFile.getName().substring(0, targetFile.getName().indexOf("."));
	}

}
