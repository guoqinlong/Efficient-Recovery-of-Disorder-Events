package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.processmining.framework.log.LogEvent;
import org.processmining.framework.models.ModelGraphVertex;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;
/**
 * 
 * This classes deals with issues related to Model.
 * 
 * @author qinlongguo
 *
 */
public class ModelUtil {
	
	/**
	 * 
	 * Hash Map with name as key  and Transition as value.
	 * 
	 * @param petriNet
	 * @return
	 */
	public static HashMap<String, Transition> getTransitionNameMap(
			PetriNet petriNet) {
		HashMap<String, Transition>  ret = new HashMap<String, Transition>();
		ArrayList<Transition> transitions = petriNet.getTransitions();
		for (Transition transition	:	transitions)
		{
			String name = transition.getIdentifier();
			ret.put(name, transition);
		}
		return ret;
	}
	
	/**
	 * 
	 * according to the petriNet and markings, return the list of transition which are firable.
	 * 
	 * @param petriNet
	 * @param markings
	 * @return
	 */
	public static List<Transition> getFirableTransitions(PetriNet petriNet,
			List<Place> markings) {
		List<Transition> ret = new LinkedList<Transition>();
		for (Place place	:	markings)
		{
			HashSet<Transition> possibleTransitions = (HashSet<Transition>) place.getSuccessors();
			for (Transition transition	:	possibleTransitions)
			{
				if (markings.containsAll(transition.getPredecessors()) && !ret.contains(transition))
					ret.add(transition);
			}
		}
		return ret;
	}
	
	/**
	 * 
	 * Get the Initial Marking, namely a list with a source place.
	 * 
	 * @param petriNet
	 * @return
	 */
	public static List<Place> getIntialMarking(PetriNet petriNet) {
		List<Place> ret= new LinkedList<Place>();
		ret.add((Place) petriNet.getSource());
		return ret;
	}
	
	/**
	 * 
	 * Fire the transition to return the new markings.
	 * 
	 * @param petriNet
	 * @param markings
	 * @param nowTransition
	 * @return
	 */
	public static List<Place> fire(PetriNet petriNet, List<Place> markings,
			Transition nowTransition) {
		List<Place> ret = new LinkedList<Place>();
		HashSet<ModelGraphVertex> prePlaces = nowTransition.getPredecessors();
		HashSet<ModelGraphVertex> postPlaces = nowTransition.getSuccessors();
		
		for (Place p: markings)
		{
			if (!prePlaces.contains(p))
				ret.add(p);
		}
		for (ModelGraphVertex mgv	:	postPlaces)
		{
			ret.add((Place) mgv);
		}				
		return ret;
	}

	/**
	 * On this markings,  whether the execution of petrinet is finished
	 * @param markings
	 * @param petriNet
	 * @return
	 */
	public static boolean isFinished(List<Place> markings, PetriNet petriNet) {
		List<Transition> firableTransitions = ModelUtil.getFirableTransitions(petriNet, markings);
		Place sinkPlace = (Place) petriNet.getSink();
		return (firableTransitions.isEmpty() && markings.contains(sinkPlace));		
	}
	
	
	/**
	 * make the transions in the petrinet visible.
	 * @return
	 */
	public static PetriNet makedTransitionVisible(PetriNet pn)
	{
		PetriNet ret = (PetriNet) pn.clone();
		for (Transition t	:	ret.getTransitions())
		{
			LogEvent le = new LogEvent(t.getIdentifier(), null);
			t.setLogEvent(le);
		}
		return ret;
	}
}
