package repairalgorithm.newalgorithm;

import java.util.Date;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Transition;

import data.EventLog;
import data.Trace;
import repairalgorithm.RepairResult;
import repairalgorithm.alignmentalgorithm.Alignment_Astar;
import repairalgorithm.newalgorithm.transitioninfo.TransitionInfo;
import util.ModelUtil;

public class NewAlgorithm extends Alignment_Astar{
	
	public Trace repairTrace(PetriNet petriNet, Trace originalTrace, HashMap<Transition, TransitionInfo> transitionInfo, HashMap<String,Transition> transitionNameMap) 
	{

		initBestTrace();				
 		NewAlgorithmSearchNode sourceNode = new NewAlgorithmSearchNode(petriNet, originalTrace, transitionNameMap);
	
		sourceNode.sourceNode();
		LinkedList<NewAlgorithmSearchNode> openTable = new LinkedList<NewAlgorithmSearchNode>();	//OPEN table
		openTable.add(sourceNode);
		HashSet<NewAlgorithmSearchNode> closeTable = new HashSet<NewAlgorithmSearchNode>();															//ClOSE table						
		while (openTable.size() > 0)
		{
			NewAlgorithmSearchNode headNode = openTable.poll();
//			System.out.println(headNode);
			if (!isFirstTrace && headNode.getfValue() > bestValue)															//In case of dead-loop. As long as the current real value has exceed the best Value, discard it. 
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
		System.out.println("CloseTable Size:\t"+closeTable.size());
		return bestTrace;
	}

	/**
		 * Main method, repair the eventlog with petrinet
		 * repair  each trace with method repairTrace
		 * @param petriNet
		 * @param eventLog
		 * @return
		 */
	    @Override
		public RepairResult repair(PetriNet petriNet, EventLog eventLog)
		{
	    	RepairResult result = new RepairResult();
			EventLog ret = new EventLog();
			
			HashMap<Transition, TransitionInfo> transitionInfo = TransitionInfo.calculateTransitionInfos(petriNet);					
			System.out.println(transitionInfo);
			HashMap<String,Transition> transitionNameMap= ModelUtil.getTransitionNameMap(petriNet);
			Date d1 = new Date();
			for (Trace originalTrace	:	eventLog)
			{				
				Trace repairedTrace;				
				repairedTrace = repairTrace(petriNet, originalTrace, transitionInfo, transitionNameMap);				
				ret.addTrace(repairedTrace);								
			}
			Date d2 = new Date();
			result.setTime(d2.getTime()-d1.getTime());
			result.setEventLog(ret);
			return result;
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
//			System.out.println(headNode);
			
			List<NewAlgorithmSearchNode> ret = new LinkedList<NewAlgorithmSearchNode>();			
						
			List<Transition> nowFirableTransitions = headNode.getFirableTransitions();
			
			for (Transition transition	:	nowFirableTransitions)
			{
				if (!headNode.isOktoFire(transition, transitionInfo))		//prune...
					continue;
				NewAlgorithmSearchNode newNode = headNode.clone();
				
				newNode.fire(transition);
				//Special case for invisible task: the invisible transition cannot update trace(invisible task does not appear in the trace!), and therefore it cannot  update FValue(fValue, the  lcs of repaired trace and original trace, only depends on the status of trace)
				if (!transition.isInvisibleTask())
				{
					newNode.updateTrace(transition);
					newNode.updateFValue(transition);
				}
				ret.add(newNode);
			}
			return ret;
		}						
}
