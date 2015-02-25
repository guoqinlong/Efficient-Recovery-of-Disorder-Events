package repairalgorithm.newalgorithm;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Transition;

import data.EventLog;
import data.Trace;
import repairalgorithm.alignmentalgorithm.Alignment_Astar;
import repairalgorithm.newalgorithm.transitioninfo.TransitionInfo;
import util.ModelUtil;

public class NewAlgorithm extends Alignment_Astar{
	
	

	public Trace repairTrace(PetriNet petriNet, Trace originalTrace, HashMap<Transition, TransitionInfo> transitionInfo) 
	{
		initBestTrace();
		HashMap<String,Transition> transitionNameMap= ModelUtil.getTransitionNameMap(petriNet);
		
		NewAlgorithmSearchNode sourceNode = new NewAlgorithmSearchNode(petriNet, originalTrace, transitionNameMap);
		sourceNode.sourceNode();
		LinkedList<NewAlgorithmSearchNode> openTable = new LinkedList<NewAlgorithmSearchNode>();	//OPEN table
		openTable.add(sourceNode);
		HashSet<NewAlgorithmSearchNode> closeTable = new HashSet<NewAlgorithmSearchNode>();															//ClOSE table				
		
		while (openTable.size() > 0)
		{
			NewAlgorithmSearchNode headNode = openTable.poll();
			
			if (!isFirstTrace && headNode.getRealValue() > bestValue)															//In case of dead-loop. As long as the current real value has exceed the best Value, discard it. 
				continue;
			if (headNode.completeRepiar())
			{
				updateRepair(headNode);
				continue;
			}
			List<NewAlgorithmSearchNode> childNodes = expand(headNode, transitionInfo);
			for (NewAlgorithmSearchNode childNode	:	childNodes)
			{
				//evaluate the f of childNode
				if (openTable.contains(childNode))
				{
					int originalPos = openTable.indexOf(childNode);
					NewAlgorithmSearchNode originalNode = openTable.get(originalPos);
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

	/**
		 * Main method, repair the eventlog with petrinet
		 * repair  each trace with method repairTrace
		 * @param petriNet
		 * @param eventLog
		 * @return
		 */
		public EventLog repair(PetriNet petriNet, EventLog eventLog)
		{
			EventLog ret = new EventLog();
			HashMap<Transition, TransitionInfo> transitionInfo = TransitionInfo.calculateTransitionInfos(petriNet);
			for (Trace originalTrace	:	eventLog)
			{
				Trace repairedTrace;
				repairedTrace = repairTrace(petriNet, originalTrace, transitionInfo);
				ret.addTrace(repairedTrace);
			}
			return ret;
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
		 * @param headNode
		 * @return
		 */
		protected List<NewAlgorithmSearchNode> expand(NewAlgorithmSearchNode headNode, HashMap<Transition, TransitionInfo> transitionInfo) {
			List<NewAlgorithmSearchNode> ret = new LinkedList<NewAlgorithmSearchNode>();			
						
			List<Transition> nowFirableTransitions = headNode.getFirableTransitions();
			
			for (Transition transition	:	nowFirableTransitions)
			{
				if (!headNode.isOktoFire(transition, transitionInfo))		//prune...
					continue;
				NewAlgorithmSearchNode newNode = headNode.clone();
				
				newNode.fire(transition);
				newNode.updateTrace(transition);
				newNode.updateFValue();

				ret.add(newNode);
			}
			return ret;
		}						
}
