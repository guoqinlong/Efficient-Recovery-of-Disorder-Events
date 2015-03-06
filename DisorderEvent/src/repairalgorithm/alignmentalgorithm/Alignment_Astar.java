package repairalgorithm.alignmentalgorithm;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Transition;

import repairalgorithm.RepairAlgorithm;
import repairalgorithm.RepairResult;
import repairalgorithm.SearchNode;
import util.ModelUtil;
import data.EventLog;
import data.Trace;



/**
 * 
 * Benchmark algorithm	:	 alignment, a* version;
 * 
 * @author qinlongguo
 *
 */
public class Alignment_Astar extends RepairAlgorithm{
	
	/**
	 * Main method, repair the eventlog with petrinet
	 * repair  each trace with method repairTrace
	 * @param petriNet
	 * @param eventLog
	 * @return
	 */
	public RepairResult repair(PetriNet petriNet, EventLog eventLog)
	{
		RepairResult result = new RepairResult();
		EventLog ret = new EventLog();
		HashMap<String,Transition> transitionNameMap= ModelUtil.getTransitionNameMap(petriNet);
		Date d1 = new Date();
		for (Trace originalTrace	:	eventLog)
		{
			Trace repairedTrace;
			repairedTrace = repairTrace(petriNet, originalTrace, transitionNameMap);
			ret.addTrace(repairedTrace);
		}
		Date d2 = new Date();
		result.setEventLog(ret);
		result.setTime(d2.getTime() - d1.getTime());
		return result;
	}	
	
	/**
	 *  repair trace with petrinet
	 * @param petriNet
	 * @param trace
	 * @return
	 */
	public Trace repairTrace(PetriNet petriNet, Trace trace, HashMap<String,Transition> transitionNameMap )
	{								
		initBestTrace();
		
		SearchNode sourceNode = new SearchNode(petriNet, trace, transitionNameMap);
		sourceNode.sourceNode();
		LinkedList<SearchNode> openTable = new LinkedList<SearchNode>();	//OPEN table
		openTable.add(sourceNode);
		HashSet<SearchNode> closeTable = new HashSet<SearchNode>();															//ClOSE table				
		
		while (openTable.size() > 0)
		{
			SearchNode headNode = openTable.poll();
			if (!isFirstTrace && headNode.getRealValue() > bestValue)															//In case of dead-loop. As long as the current real value has exceed the best Value, discard it. 
				continue;
			if (headNode.completeRepiar())
			{
				updateRepair(headNode);
				continue;
			}
			List<SearchNode> childNodes = expand(headNode);
			for (SearchNode childNode	:	childNodes)
			{
				//evaluate the f of childNode
				if (openTable.contains(childNode))
				{
					int originalPos = openTable.indexOf(childNode);
					SearchNode originalNode = openTable.get(originalPos);
					if (originalNode.getfValue() > childNode.getfValue())
					{
						openTable.remove(originalPos);
						openTable.add(childNode);
					}										
				}
				else if (closeTable.contains(childNode))
				{
					continue;
				}
				else
				{
					openTable.add(childNode);
				}
			}
			closeTable.add(headNode);
			Collections.sort(openTable);			
		}				
		System.out.println("CloseTable Size:\t"+closeTable.size());
		//System.out.println(closeTable);
		return bestTrace;
	}

	/**
	 * update the bestTrace
	 * 
	 * @param headNode
	 */
	protected void updateRepair(SearchNode headNode) {
		if (isFirstTrace || bestValue > headNode.getRealValue())
		{
			bestValue = headNode.getRealValue();
			bestTrace= (Trace) headNode.getCurrentTrace().clone();
			isFirstTrace = false;
		}		
	}

	/**
	 * 
	 * Expand the head node, return the next nodes.
	 * 
	 * 3 cases:
	 * 1. both petriNet and trace move.
	 * 2. petriNet move while trace not.
	 * 3. trace move while petriNet not.
	 * 
	 * special case for invisible task:
	 * when petriNet move, we should consider for the invisible task.
	 * 
	 * @param headNode
	 * @return
	 */
	protected List<SearchNode> expand(SearchNode headNode) {
		List<SearchNode> ret = new LinkedList<SearchNode>();			
		Transition nowTransition = headNode.getNowTransition();		
		List<Transition> nowFirableTransitions = headNode.getFirableTransitions();
						
		//case 1: both petriNet and trace move, the nowtransition means it would not be the invisible task.
		if (nowTransition != null && nowFirableTransitions.contains(nowTransition))
		{
			SearchNode newNode = headNode.clone();
			newNode.fire(nowTransition);
			newNode.moveTrace();
			newNode.updateFValue();
			
			ret.add(newNode);
		}
		//case 2: petriNet move and trace not
		if (!nowFirableTransitions.isEmpty())
		{
			for(Transition transition	:	nowFirableTransitions)
			{				
				if (transition.equals(nowTransition))
					continue;
				SearchNode newNode = headNode.clone();
				newNode.fire(transition);
				newNode.increaseRealValue();
				newNode.updateFValue();
				
				ret.add(newNode);
			}
		}
		//case 3: petriNet not move, trace move
		if (nowTransition != null)
		{			
			SearchNode newNode = headNode.clone();
			newNode.moveTrace();
			newNode.increaseRealValue();
			newNode.updateFValue();			
			ret.add(newNode);
		}
		return ret;
	}

}
