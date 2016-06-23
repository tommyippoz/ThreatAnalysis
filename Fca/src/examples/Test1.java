package examples;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.xml.sax.SAXException;

import colibri.io.lattice.ConceptWriterString;
import colibri.io.lattice.LatticeWriterDot;
import colibri.io.lattice.LatticeWriterXmlEdges;
import colibri.io.relation.RelationReaderCON;
import colibri.io.relation.RelationReaderXML;
import colibri.lib.Concept;
import colibri.lib.Edge;
import colibri.lib.HashRelation;
import colibri.lib.HybridLattice;
import colibri.lib.Lattice;
import colibri.lib.RawLattice;
import colibri.lib.Relation;
import colibri.lib.Traversal;
import colibri.lib.TreeRelation;
                            
/**
 * Imports a binary relation from a .con or .xml file and
 * outputs the edges of the corresponding lattice or
 * the edges returned by the violation iterator.
 * @author Daniel N. Goetzmann
 * @version 1.0
 */
public class Test1 {

    public static void main (String[] args) {
        Relation rel = new TreeRelation();
        //rel.add("cnn", "House");
        //rel.add("cnn", "war");
        //rel.add("wall street journal", "Congress");
        //rel.add("wall street journal", "Budget");
        //rel.add("wall street journal", "Bill");
        //rel.add("bbc", "Domocrat");
        //rel.add("bbc", "Congress");
        //rel.add("wall street journal", "South Korea");
        //rel.add("wall street journal", "Business");
        //rel.add("time", "South Korea");
        //rel.add("time", "bulldozer");

        rel.add("scenario 1", 1);
        rel.add("scenario 1", 3);
        rel.add("scenario 2", 1);
        rel.add("scenario 2", 3);
        rel.add("scenario 3", 1);
        rel.add("scenario 3", 3);
        rel.add("scenario 3", 5);
        rel.add("scenario 4", 1);
        rel.add("scenario 4", 3);
        rel.add("scenario 5", 1);
        rel.add("scenario 5", 2);
        rel.add("scenario 5", 4);
        rel.add("scenario 6", 1);
        rel.add("scenario 6", 4);
        
        
        Lattice lattice = new HybridLattice(rel);
        //get the iterator
        Iterator<Concept> it = lattice.conceptIterator(Traversal.TOP_OBJSIZE);
        while(it.hasNext()) {
            Concept c = it.next();
            System.out.println(c);
            //System.out.println(c.toString().replace("objects:[", "").replace("], attributes:[", "\t").replace("]", "").replace(",", "") + "\n");
         }
        
        
        System.out.println("RICERCA");
        Collection<Comparable> atts = new ArrayList();
        atts.add(1);
        Concept cOut = lattice.conceptFromAttributes(atts);
        System.out.println(cOut);
        System.out.println("RICERCA2");
        atts.add(3);
        cOut = lattice.conceptFromAttributes(atts);
        System.out.println(cOut);
        System.out.println("RICERCA3");
        
        System.out.println(cOut);
    }
    
    
}



