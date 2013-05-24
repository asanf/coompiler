
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import java.util.Enumeration;
import java.util.Set;

/**
 * Classe che implementa il grafo di ereditarietà
 * 
 *
 */
public class InheritanceGraph extends SimpleDirectedGraph<class_c, DefaultEdge>{

	public InheritanceGraph(){
		super(DefaultEdge.class);
	}
	
	/**
	 * Metodo che cerca una classe in base al nome
	 * @param name nome della classe da cercare
	 * @return Il riferimento alla classe se trovata o null altrimenti
	 */
	public class_c findVertex(AbstractSymbol name){
		Set<class_c> vertices = vertexSet();
		for(class_c v: vertices){
			if(v.name.equals(name))
				return v;
		}
		return null;
	}
}
