/**
 * 
 */
package ippoz.irene.threatanalysis.engine;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import ippoz.irene.threatanalysis.component.Component;
import ippoz.irene.threatanalysis.scenario.Scenario;
import ippoz.irene.threatanalysis.threats.Threat;
import ippoz.irene.threatanalysis.utility.AppLogger;
import colibri.lib.ComparableSet;
import colibri.lib.Relation;
import colibri.lib.TreeRelation;

/**
 * @author Tommy
 *
 */
public class FcaBuilder {

	private Relation relation;
	
	public FcaBuilder(){
		relation = new TreeRelation();
	}
	
	public void addEvolution(Scenario scenario){
		HashMap<LinkedList<Component>, LinkedList<Threat>> threats = null;
		if(scenario != null){
			threats = scenario.getThreats();
			if(threats != null){
				for(LinkedList<Component> cList : threats.keySet()){
					for(Threat t : threats.get(cList)){
						relation.add(scenario.getName(), t.toString() + ":" + cList.toString());
					}
				}
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void printLattice(LinkedList<Scenario> scenarioList){
		HashMap<String, Integer> idAtt = new HashMap<String, Integer>();
		HashMap<Integer, String> inverted = new HashMap<Integer, String>();
	    Iterator<Comparable> atts;
	    PrintWriter writer;
	    ComparableSet allAtts = relation.getAllAttributes();
	    int index=0;
	    for (Object a : allAtts.toArray()){
	    	idAtt.put(a.toString(),index);
	    	inverted.put(index, a.toString());
	    	index++;		    	
	    }
	    try {
		    writer = new PrintWriter("fcaGraphic.cex", "UTF-8");
			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ConceptualSystem><Version MajorNumber=\"1\" MinorNumber=\"0\" />");
			writer.println("<Contexts><Context Identifier=\"0\" Type=\"Binary\"><Attributes>");
			for(int i=0;i<inverted.keySet().size();i++){
				writer.println("<Attribute Identifier=\""+ i + "\"><Name>" + inverted.get(i) +"</Name></Attribute>");
			}
			writer.println("</Attributes><Objects>");
			for(Scenario scenario : scenarioList){
				atts = relation.getAttributes(scenario.getName());
				writer.println("<Object><Name>" + scenario.getName() + "</Name><Intent>");
				while (atts.hasNext()){
					Object element = atts.next(); 
					writer.println("<HasAttribute AttributeIdentifier=\"" + idAtt.get(element) + "\" />");
				}
			    writer.println("</Intent></Object>");
			}				    
		    writer.println("</Objects></Context></Contexts><RecalculationPolicy Value=\"Clear\" /><Lattices /></ConceptualSystem>");
		    writer.close();
		} catch (FileNotFoundException ex) {
			AppLogger.logException(getClass(), ex, "File Not Found");
		} catch (UnsupportedEncodingException ex) {
			AppLogger.logException(getClass(), ex, "Unable to code Lattice");
		}
	}
}
