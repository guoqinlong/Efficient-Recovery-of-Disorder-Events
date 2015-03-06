package repairalgorithm.newalgorithm;

import java.awt.Point;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;

import data.Trace;
import repairalgorithm.SearchNode;
import repairalgorithm.newalgorithm.transitioninfo.TransitionInfo;
import util.DataUtil;

/**
 * Search node used for new algorithm
 * 
 * Compared with the traditional one:
 * 1. Add leftNodes, those nodes means that does  match before.
 * 2. Using ~Longest Common SubSequence~ as the cost Function.
 * 
 * @author qinlongguo
 *
 */
public class NewAlgorithmSearchNode extends SearchNode {
	MultiSet<String> unusedEventNames;
	LinkedList<Integer>	 visitedPositions;								//1. visited positions of Trace Events.							
	LinkedList<Point> lis;																//2. longestIncreaseSubsequence, where x means the position, and y means the left count
	HashMap<String, LinkedList<Integer>> eventPositions;			//3. position of each event in the trace, for case of duplicate event name in trace, the position for each name is stored as a list.
																											//3=>This event Positions would be updated by the transitions, when an event is added into the variable current transition, we could update the trace.
	
	public NewAlgorithmSearchNode(PetriNet petriNet, Trace originalTrace,
			HashMap<String, Transition> transitionNameMap) {
		super(petriNet, originalTrace, transitionNameMap);		
		unusedEventNames = new MultiSet<String>();
		lis = new LinkedList<Point>();
		visitedPositions = new LinkedList<Integer>();
		eventPositions = getEventPositions(originalTrace);
	}
	
	/**
	 * 
	 * get position of each event in the trace
	 * 
	 * @param originalTrace
	 * @return
	 */
	private HashMap<String, LinkedList<Integer>> getEventPositions(Trace originalTrace) {
		HashMap<String, LinkedList<Integer>> ret = new HashMap<String, LinkedList<Integer>>();
		for (int i=0; i<originalTrace.length(); i++)
		{
			String event = originalTrace.getEvent(i);
			LinkedList<Integer> positions = ret.get(event);
			if (positions == null)
				positions = new LinkedList<Integer>();
			positions.add(i);
			ret.put(event, positions);
		}			
		return ret;
	}

	public NewAlgorithmSearchNode clone()
	{
		NewAlgorithmSearchNode ret = new NewAlgorithmSearchNode(this.petriNet, this.originalTrace, this.transitionNameMap);
		ret.markings = new LinkedList<Place>(this.markings);
		ret.fValue = this.fValue;
		ret.realValue = this.realValue;
		ret.tracePos = this.tracePos;
		ret.currentTrace =(Trace) this.currentTrace.clone();
		ret.unusedEventNames = (MultiSet<String>) this.unusedEventNames.clone();
		ret.visitedPositions = (LinkedList<Integer>) this.visitedPositions.clone(); 
		ret.lis = (LinkedList<Point>) this.lis.clone();							
		ret.eventPositions = DataUtil.cloneEventPositions(this.eventPositions);		// clone is shallow copy, this method is deep copy
		return ret;
	}

	/**
	 *  check whether the transition is ok to fire
	 *  update for the invisible task
	 */
	public boolean isOktoFire(Transition nowTransition,
			HashMap<Transition, TransitionInfo> transitionInfo) 
	{
		String nowTransitionName = nowTransition.getIdentifier();
		MultiSet<String> leftTransitions = getLeftTransitions();
		TransitionInfo info = transitionInfo.get(nowTransition);

		//Prune 1 : nowTransition should be in the leftTransitions.
		//--->for invisible task, it may be not in the leftTransitions.
		if (!leftTransitions.contains(nowTransitionName) && !nowTransition.isInvisibleTask())
			return false;
		
		//Prune 2 : nowTransition.mustFollowTransitions should be in the leftTransitions.
		//--->for must follow task, the invisible task may be not in the leftTransitions.
		HashSet<Transition> mustFollowTransitions = info.getMustFollowTransition();
		for (Transition t	:	mustFollowTransitions)
		{
			if (t.isInvisibleTask())
				continue;
			String name = t.getIdentifier();
			if (!leftTransitions.contains(name))
				return false;				
		}
		
		//Prune 3: for leftTransitions,  if it is in the preSet, the pres should also in the leftTransitions.
		//--->for invisible task, it cannot be in theleftTransitions.
		HashMap<Transition, List<Transition>> preSet = info.getPreSet();
		for (String	transitionName	:	leftTransitions)
		{
			Transition leftTransition = transitionNameMap.get(transitionName);
			if (leftTransition.isInvisibleTask())
				continue;
			List<Transition> preTransitions = preSet.get(leftTransition);
			if (preTransitions != null)
			{
				for (Transition preTransition	:	preTransitions)
				{
					if (preTransition.isInvisibleTask())
						continue;
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
	private MultiSet<String> getLeftTransitions() {
		MultiSet<String> ret = new MultiSet<String>();
		ret.addAll(unusedEventNames);
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
	 * 
	 *  special case for invisible task: invisible task should not affect the left transitions..., since the left transitions are calculated by event logs.
	 *  
	 * @param transition
	 */
	public void updateTrace(Transition transition) 
	{
		String transitionName = transition.getIdentifier();
		if (unusedEventNames.contains(transitionName))
		{
			unusedEventNames.remove(transitionName);
		}
		else if (getNowTransition().equals(transition))
		{
			moveTrace();
		}
		else
		{
			int index = originalTrace.indexOf(transitionName, tracePos);
			for (int i=tracePos; i<index; i++)
				unusedEventNames.add(originalTrace.getEvent(i));
			tracePos = index+1;
		}		
	}
	
	@Override
	public String toString()
	{
		StringBuilder ret = new StringBuilder();
		ret.append(super.toString());
		
		ret.append("unusedTransitions\t:\t");
		ret.append(unusedEventNames.toString());
		ret.append("\n");		
		
		ret.append("visitedPositions\t:\t");
		ret.append(visitedPositions.toString());
		ret.append("\n");		
		
		ret.append("eventPositions\t:\t");
		ret.append(eventPositions.toString());
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
		MultiSet<String> leftTransitions = getLeftTransitions();
		return leftTransitions.isEmpty();
	}	
	
	/**
	 * update the fValue by using the  visitedPositions, and lis, the ~Longest Common Subsequence~ Method
	 */
	public void updateFValue(Transition lastExcutedTransition) 
	{				
		//1.  get last Fire event and its positions		
		//2.  insert it into the visitedPositions,  
		//3.  update the lis
		//3.1 update position in an existing point
		//3.2 append position to the end. 
		//4.  and calculate the fValue
		
		//1.
		String event = lastExcutedTransition.getIdentifier();		
		LinkedList<Integer> positions = eventPositions.get(event);
		int position = positions.poll();		
		
		//2.
		Iterator<Integer> vIterator = visitedPositions.iterator();
		int pos = 0;
		int left = 0;			// it means how many elements are smaller than 'position' in the visitedPositions ..
		while (vIterator.hasNext())
		{
			Integer value = vIterator.next();			
			if (value > position)
				break;
			left++;
		}
		visitedPositions.add(left, position);
		
		//3.		
		int leftEventSize = getLeftTransitions().size();
		Iterator<Point> lIterator = lis.iterator();
		boolean needAppend = true;
		while (lIterator.hasNext())
		{
			Point point = lIterator.next();
			if (point.x <position)	//	For the position in the left part, the length of possible LCS  should minus 1; 
			{
				point.y = Math.max(point.y-1, 0);				
			}
			else
			{
				point.x = position;				
				point.y = leftEventSize - (position - left);
				needAppend = false;
			}
		}
		if (needAppend)
		{
			Point p = new Point();
			p.x = position;			
			p.y = leftEventSize - (position - left);
			lis.add(p);
		}
		//4.		
		int newFValue = Math.max(leftEventSize, lis.size());		//the lcs is in the leftEvent, or the lcs is now lis 
		lIterator = lis.iterator();		
		int lisLength = 1;
		while (lIterator.hasNext())
		{
			Point p = lIterator.next();
			newFValue = Math.max(p.y+lisLength, newFValue);
			lisLength ++;
		}
		newFValue = originalTrace.length() - newFValue;
		//last Step!
		fValue = newFValue;
	}		
}
