package repairalgorithm.newalgorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import data.Trace;
import repairalgorithm.SearchNode;
import repairalgorithm.newalgorithm.transitioninfo.TransitionInfo;

/**
 * Search node used for new algorithm
 * 
 * Compared with the traditional one:
 * 1. Add leftNodes, those nodes means that does  match before.
 * 
 * @author qinlongguo
 *
 */
public class NewAlgorithmSearchNode extends SearchNode {
	Multiset<String> unusedTransitions;
	
	public NewAlgorithmSearchNode(PetriNet petriNet, Trace originalTrace,
			HashMap<String, Transition> transitionNameMap) {
		super(petriNet, originalTrace, transitionNameMap);
		unusedTransitions = HashMultiset.create();
	}
	
	public NewAlgorithmSearchNode clone()
	{
		NewAlgorithmSearchNode ret = new NewAlgorithmSearchNode(this.petriNet, this.originalTrace, this.transitionNameMap);
		ret.markings = new LinkedList<Place>(this.markings);
		ret.fValue = this.fValue;
		ret.realValue = this.realValue;
		ret.tracePos = this.tracePos;
		ret.currentTrace =(Trace) this.currentTrace.clone();
		ret.unusedTransitions = HashMultiset.create(this.unusedTransitions);
		return ret;
	}

	/**
	 *  check whether the transition is ok to fire
	 */
	public boolean isOktoFire(Transition nowTransition,
			HashMap<Transition, TransitionInfo> transitionInfo) 
	{
		String nowTransitionName = nowTransition.getIdentifier();
		Multiset<String> leftTransitions = getLeftTransitions();
		TransitionInfo info = transitionInfo.get(nowTransition);

		//Prune 1 : nowTransition should be in the leftTransitions.
		if (!leftTransitions.contains(nowTransitionName))
			return false;
		
		//Prune 2 : nowTransition.mustFollowTransitions should be in the leftTransitions.
		HashSet<Transition> mustFollowTransitions = info.getMustFollowTransition();
		for (Transition t	:	mustFollowTransitions)
		{
			String name = t.getIdentifier();
			if (!leftTransitions.contains(name))
				return false;				
		}
		
		//Prune 3: for leftTransitions,  if it is in the preSet, the pres should also in the leftTransitions.
		HashMap<Transition, List<Transition>> preSet = info.getPreSet();
		for (String	transitionName	:	leftTransitions)
		{
			Transition leftTransition = transitionNameMap.get(transitionName);
			List<Transition> preTransitions = preSet.get(leftTransition);
			if (preTransitions != null)
			{
				for (Transition preTransition	:	preTransitions)
				{
					String preTransitionName = preTransition.getIdentifier();
					if (!leftTransitions.contains(preTransitionName))
						return false;
				}
			}
		}
				
		return true;
	}

	/**
	 * get all the transitions which have not been matched with petri net.
	 * @return
	 */
	private Multiset<String> getLeftTransitions() {
		Multiset<String> ret = HashMultiset.create();
		ret.addAll(unusedTransitions);
		for (int i=tracePos; i<originalTrace.length(); i++)		
		{
			String eventName = originalTrace.getEvent(i);
			ret.add(eventName);
		}			
		return ret;
	}

	/**
	 * the trace should move according to this transition, there are 3 case:
	 * 1. the transition is in the unusedTransitions, just move it.
	 * 2. the transition is at trace pos, just move trace.
	 * 3. the transition is after trace pos, move the trace pos, and add the within transitions in the leftNodes. 
	 * @param transition
	 */
	public void updateTrace(Transition transition) 
	{
		String transitionName = transition.getIdentifier();
		if (unusedTransitions.contains(transitionName))
		{
			unusedTransitions.remove(transitionName);
		}
		else if (getNowTransition().equals(transition))
		{
			moveTrace();
		}
		else
		{
			int index = originalTrace.indexOf(transitionName, tracePos);
			for (int i=tracePos; i<index; i++)
				unusedTransitions.add(originalTrace.getEvent(i));
			tracePos = index+1;
		}		
	}
	
	@Override
	public String toString()
	{
		StringBuilder ret = new StringBuilder();
		ret.append(super.toString());
		
		ret.append("unusedTransitions\t:\t");
		ret.append(unusedTransitions.toString());
		ret.append("\n");		
		
		return ret.toString();
	}

	/**
	 * to check whether the node represents a complete repair.
	 * 
	 * For new algorithm, which aims to find misorder, only if there is no left node, the complete is over.
	 */
	@Override
	public boolean completeRepiar()
	{
		Multiset<String> leftTransitions = getLeftTransitions();
		return (leftTransitions.size() == 0);
	}
}
