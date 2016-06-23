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
						manageComponent(splitted, oldScenario);
					}
				}
			}
			reader.close();
		} catch(IOException ex){
			AppLogger.logException(getClass(), ex, "Unable to load evolution step [" + targetFile.getName() + "]");
		}
	}

	private void manageComponent(String[] splitted, Scenario oldScenario) {
		Component comp = buildComponent(splitted, oldScenario);
		switch(splitted[0]){
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

	private Component buildComponent(String[] splitted, Scenario oldScenario) {
		ComponentType cType = ComponentType.valueOf(splitted[1]);
		if(Component.getCategoryOf(cType).equals(ComponentCategory.CON)){
			return new Connection(splitted[2], cType, getComponent(splitted[3].split(";")[0].trim(), oldScenario), getComponent(splitted[3].split(";")[1].trim(), oldScenario));
		} else return new Building(splitted[2], cType);
	}

	private Component getComponent(String compName, Scenario oldScenario) {
		Component comp = null;
		if(oldScenario != null)
			comp = oldScenario.getComponentByName(compName);
		if(comp == null){
			for(Component ac : addedComponents){
				if(ac.getName().equals(compName)){
					comp = ac;
					break;
				}
			}
		}
		return comp;
	}

}
