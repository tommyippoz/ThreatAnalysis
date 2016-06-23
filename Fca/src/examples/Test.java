package examples;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
public class Test {

    public static void main (String[] args) {
        Relation rel = new TreeRelation();
        rel.add("chmod", "change");
        rel.add("chmod", "file");
        rel.add("chmod", "mode");
        rel.add("chmod", "permission");
        rel.add("chown", "change");
        rel.add("chown", "file");
        rel.add("chown", "group");
        rel.add("chown", "owner");
        rel.add("fstat", "get");
        rel.add("fstat", "file");
        rel.add("fstat", "status");
        rel.add("fork", "create");
        rel.add("fork", "new");
        rel.add("fork", "process");
        rel.add("chdir", "change");
        rel.add("chdir", "directory");
        rel.add("mkdir", "create");
        rel.add("mkdir", "new");
        rel.add("mkdir", "directory");
        rel.add("open", "create");
        rel.add("open", "file");
        rel.add("open", "open");
        rel.add("open", "read");
        rel.add("open", "write");
        rel.add("read", "file");
        rel.add("read", "input");
        rel.add("read", "read");
        rel.add("rmdir", "directory");
        rel.add("rmdir", "file");
        rel.add("rmdir", "remove");
        rel.add("write", "file");
        rel.add("write", "output");
        rel.add("write", "write");
        rel.add("creat", "create");
        rel.add("creat", "file");
        rel.add("creat", "new");
        rel.add("access", "access");
        rel.add("access", "check");
        rel.add("access", "file");

        Lattice lattice = new HybridLattice(rel);
        //get the iterator
        Iterator<Concept> it = lattice.conceptIterator(Traversal.TOP_OBJSIZE);
        while(it.hasNext()) {
            Concept c = it.next();
            System.out.println(c);
            //System.out.println(c.toString().replace("objects:[", "").replace("], attributes:[", "\t").replace("]", "").replace(",", "") + "\n");
         }
        
  
    }
}
