package repairalgorithm.newalgorithm;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;

import data.EventLog;
import data.Trace;
import repairalgorithm.RepairAlgorithm;
import repairalgorithm.SearchNode;
import repairalgorithm.benchmark.Alignment_Astar;
import repairalgorithm.newalgorithm.transitioninfo.TransitionInfo;
import util.ModelUtil;

public class NewAlgorithm extends Alignment_Astar{
	
	

	public Trace repairTrace(PetriNet petriNet, Trace originalTrace) 
	{
		initBestTrace();
		HashMap<String,Transition> transitionNameMap= ModelUtil.getTransitionNameMap(petriNet);
		
		SearchNode sourceNode = new SearchNode(petriNet, originalTrace, transitionNameMap);
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
		return bestTrace;


	}

	
}
