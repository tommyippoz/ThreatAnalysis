/**
 * 
 */
package ippoz.irene.threatanalysis.threats;

import ippoz.irene.threatanalysis.component.Component;
import ippoz.irene.threatanalysis.component.ComponentCategory;
import ippoz.irene.threatanalysis.component.ComponentType;
import ippoz.irene.threatanalysis.utility.AppLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author Tommy
 *
 */
public class ThreatManager {

	private LinkedList<Threat> threatList;
	private LinkedList<Mitigation> mitigationList;
	private HashMap<Threat, LinkedList<Mitigation>> threatMitigation;
	private HashMap<ComponentType, LinkedList<Threat>> componentThreats;
	private HashMap<ComponentCategory, LinkedList<Threat>> categoryThreats;
	private HashMap<LinkedList<ComponentType>, LinkedList<Threat>> emergingThreats;
	
	public ThreatManager(String fileFolder, String threatFilter){
		while(fileFolder.endsWith("/") || fileFolder.endsWith("\\")){
			fileFolder = fileFolder.substring(0, fileFolder.length()-1);
		}
		loadThreatList(new File(fileFolder + "/Threats.csv"), threatFilter);
		loadMitigationList(new File(fileFolder + "/Mitigations.csv"));
		loadThreatMitigations(new File(fileFolder + "/Threat_Mitigations.csv"));
		loadComponentThreats(new File(fileFolder + "/Component_Threat.csv"));
		loadCategoryThreats(new File(fileFolder + "/Category_Threat.csv"));
		loadEmergingThreats(new File(fileFolder + "/Emerging_Threats.csv"));
	}

	private void loadEmergingThreats(File targetFile) {
		String readed;
		String splitted[];
		LinkedList<ComponentType> partial;
		int emRules = 0;
		BufferedReader reader = null;
		try {
			emergingThreats = new HashMap<LinkedList<ComponentType>, LinkedList<Threat>>();
			reader = new BufferedReader(new FileReader(targetFile));
			reader.readLine();
			while(reader.ready()){
				readed = reader.readLine();
				if(readed != null){
					readed = readed.trim();
					if(readed.length() > 0){
						splitted = readed.split(",");
						if(findThreat(Integer.parseInt(splitted[0].trim())) != null){
							partial = new LinkedList<ComponentType>();
							for(String ct : splitted[1].split(";")){
								partial.add(ComponentType.valueOf(ct.trim()));
							}
							if(emergingThreats.get(partial) == null)
								emergingThreats.put(partial, new LinkedList<Threat>());
							emergingThreats.get(partial).add(findThreat(Integer.parseInt(splitted[0].trim())));
							emRules++;
						}
					}
				}
			}
			reader.close();
			AppLogger.logInfo(getClass(), "Emerging rules: " + emRules + " rules");
		} catch(IOException ex){
			AppLogger.logException(getClass(), ex, "Unable to load threats");
		}
	}

	private void loadCategoryThreats(File targetFile) {
		String readed;
		String splitted[];
		ComponentCategory current;
		BufferedReader reader = null;
		try {
			categoryThreats = new HashMap<ComponentCategory, LinkedList<Threat>>();
			reader = new BufferedReader(new FileReader(targetFile));
			reader.readLine();
			while(reader.ready()){
				readed = reader.readLine();
				if(readed != null){
					readed = readed.trim();
					if(readed.length() > 0){
						splitted = readed.split(",");
						current = ComponentCategory.valueOf(splitted[0]);
						if(categoryThreats.get(current) == null)
							categoryThreats.put(current, new LinkedList<Threat>());
						if(findThreat(Integer.parseInt(splitted[1].trim())) != null)
							categoryThreats.get(current).add(findThreat(Integer.parseInt(splitted[1].trim())));
					}
				}
			}
			reader.close();
		} catch(IOException ex){
			AppLogger.logException(getClass(), ex, "Unable to load threats");
		}
	}
	
	private void loadComponentThreats(File targetFile) {
		String readed;
		String splitted[];
		ComponentType current;
		BufferedReader reader = null;
		try {
			componentThreats = new HashMap<ComponentType, LinkedList<Threat>>();
			reader = new BufferedReader(new FileReader(targetFile));
			reader.readLine();
			while(reader.ready()){
				readed = reader.readLine();
				if(readed != null){
					readed = readed.trim();
					if(readed.length() > 0){
						splitted = readed.split(",");
						current = ComponentType.valueOf(splitted[0]);
						if(componentThreats.get(current) == null)
							componentThreats.put(current, new LinkedList<Threat>());
						if(!splitted[2].equals("-") && findThreat(Integer.parseInt(splitted[1].trim())) != null)
							componentThreats.get(current).add(findThreat(Integer.parseInt(splitted[1].trim())));
					}
				}
			}
			reader.close();
		} catch(IOException ex){
			AppLogger.logException(getClass(), ex, "Unable to load threats");
		}
	}

	private void loadThreatMitigations(File targetFile) {
		String readed;
		String splitted[];
		LinkedList<Mitigation> partial;
		BufferedReader reader = null;
		try {
			threatMitigation = new HashMap<Threat, LinkedList<Mitigation>>();
			reader = new BufferedReader(new FileReader(targetFile));
			reader.readLine();
			while(reader.ready()){
				readed = reader.readLine();
				if(readed != null){
					readed = readed.trim();
					if(readed.length() > 0){
						splitted = readed.split(",");
						if(findThreat(Integer.parseInt(splitted[0].trim())) != null){
							partial = new LinkedList<Mitigation>();
							if(!splitted[2].equals("-")){
								for(String mitId : splitted[2].split(";")){
									partial.add(findMitigation(Integer.parseInt(mitId.trim())));
								}
							}
							threatMitigation.put(findThreat(Integer.parseInt(splitted[0].trim())), partial);
						}
					}
				}
			}
			reader.close();
		} catch(IOException ex){
			AppLogger.logException(getClass(), ex, "Unable to load threats");
		}
	}

	private void loadMitigationList(File targetFile) {
		String readed;
		String splitted[];
		BufferedReader reader = null;
		try {
			mitigationList = new LinkedList<Mitigation>();
			reader = new BufferedReader(new FileReader(targetFile));
			reader.readLine();
			while(reader.ready()){
				readed = reader.readLine();
				if(readed != null){
					readed = readed.trim();
					if(readed.length() > 0){
						splitted = readed.split(",");
						mitigationList.add(new Mitigation(Integer.parseInt(splitted[0].trim()), splitted[1].trim(), splitted[2].trim(), splitted[3].trim(), splitted[4].trim()));
					}
				}
			}
			reader.close();
			AppLogger.logInfo(getClass(), "Available Mitigations: " + mitigationList.size() + " mitigations");
		} catch(IOException ex){
			AppLogger.logException(getClass(), ex, "Unable to load threats");
		}
	}

	private void loadThreatList(File targetFile, String threatFilter) {
		String readed;
		String splitted[];
		BufferedReader reader = null;
		try {
			threatList = new LinkedList<Threat>();
			reader = new BufferedReader(new FileReader(targetFile));
			reader.readLine();
			while(reader.ready()){
				readed = reader.readLine();
				if(readed != null){
					readed = readed.trim();
					if(readed.length() > 0){
						splitted = readed.split(",");
						threatList.add(new Threat(Integer.parseInt(splitted[2].trim()), splitted[3].trim(), splitted[4].trim(), ThreatCategory.valueOf(splitted[0].trim())));
					}
				}
			}
			reader.close();
			filterThreats(threatFilter);
			AppLogger.logInfo(getClass(), "Available Threats: " + threatList.size() + " threats");
		} catch(IOException ex){
			AppLogger.logException(getClass(), ex, "Unable to load threats");
		}
	}
	
	private void filterThreats(String threatFilter) {
		String[] splitted;
		LinkedList<Integer> valids;
		LinkedList<Threat> toRemove = new LinkedList<Threat>();
		if(threatFilter != null && threatFilter.length() > 0){
			if(threatFilter.equals("ALL"))
				return;
			splitted = threatFilter.split(",");
			if(splitted.length > 0){
				for(String sub : splitted){
					try {
						if(sub.contains("-")){
							for(Threat t : threatList){
								if(t.getIndex() < Integer.parseInt(sub.split("-")[0].trim()) || t.getIndex() > Integer.parseInt(sub.split("-")[1].trim()))
									toRemove.add(t);
							}
						} else if(sub.contains(";")){
							valids = new LinkedList<Integer>();
							for(String s : sub.split(";")){
								valids.add(Integer.parseInt(s));
							}
							for(Threat t : threatList){
								if(!valids.contains(t.getIndex()))
									toRemove.add(t);
							}
						} else {
							for(Threat t : threatList){
								if(!t.getCategory().equals(ThreatCategory.valueOf(sub)))
									toRemove.add(t);
							}
						}
					}catch(Exception ex){
						AppLogger.logError(getClass(), "FilterException", "Unable to recognize subfilter '" + sub + "'");
					}
				}
			} else AppLogger.logInfo(getClass(), "Unrecognized threat filter. Accepted filters are categories (e.g., CCAT), intervals (e.g., 11-20) or single indexes (e.g., 1;4;10;23) separated by commas");
		} else AppLogger.logInfo(getClass(), "Empty or missing threat filter");
		threatList.removeAll(toRemove);
	}

	private Threat findThreat(int tIndex){
		for(Threat t : threatList){
			if(t.getIndex() == tIndex)
				return t;
		}
		return null;
	}
	
	private Mitigation findMitigation(int mIndex){
		for(Mitigation m : mitigationList){
			if(m.getIndex() == mIndex)
				return m;
		}
		return null;
	}

	public LinkedList<Threat> getCategoryThreats(Component comp) {
		return categoryThreats.get(Component.getCategoryOf(comp.getCompType()));
	}
	
	public LinkedList<Threat> getComponentThreats(Component comp) {
		return componentThreats.get(comp.getCompType());
	}

	public HashMap<LinkedList<ComponentType>, LinkedList<Threat>> getEmergingThreats(Component comp) {
		HashMap<LinkedList<ComponentType>, LinkedList<Threat>> ect = new HashMap<LinkedList<ComponentType>, LinkedList<Threat>>();
		for(LinkedList<ComponentType> ctl : emergingThreats.keySet()){
			if(ctl.contains(comp.getCompType()))
				ect.put(ctl, emergingThreats.get(ctl));
		}
		return ect;		
	}
	
}
