package repairalgorithm.alignmentalgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;

import repairalgorithm.RepairAlgorithm;
import repairalgorithm.RepairResult;
import util.ModelUtil;
import data.EventLog;
import data.Trace;

/**
 * 
 * Benchmark algorithm	:	 alignment, brute_force version;
 * 
 * @author qinlongguo
 *
 */
public class Alignment_BruteForce extends RepairAlgorithm {
		
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
		Date d1 = new Date();
		for (Trace originalTrace	:	eventLog)
		{
			Trace repairedTrace;
			repairedTrace = repairTrace(petriNet, originalTrace);
			ret.addTrace(repairedTrace);
		}
		Date d2 = new Date();
		result.setTime(d2.getTime() - d1.getTime());
		result.setEventLog(ret);
		return result;
	}	
	
	/**
	 *  repair trace with petrinet
	 * @param petriNet
	 * @param trace
	 * @return
	 */
	public Trace repairTrace(PetriNet petriNet, Trace trace)
	{		
		Trace currentTrace = new Trace();			
		
		List<Place> markings = ModelUtil.getIntialMarking(petriNet);		
		HashMap<String,Transition> transitionNameMap= ModelUtil.getTransitionNameMap(petriNet);
		
		initBestTrace();
		search(petriNet, trace, currentTrace, markings, 0,0, transitionNameMap, 0);
		return bestTrace;
	}

	/**
	 * A * algorithm to match petriNet with trace.
	 * There are 3 kind of move:
	 * 1. both petriNet and trace move.
	 * 2. petriNet move while trace not.
	 * 3. trace move while petriNet not.
	 */
	private void search(PetriNet petriNet, Trace originalTrace, Trace currentTrace, List<Place> markings, int petriNetPos, int retPos, HashMap<String, Transition> transitionNameMap, int currentValue) 
	{
		List<Transition> firableTransitions = ModelUtil.getFirableTransitions(petriNet, markings);
		
		if (firableTransitions.size() == 0 && retPos == originalTrace.length())		//both petriNet and trace have been searched.
		{
			checkBestTrace(currentTrace, currentValue);
		}
		if (firableTransitions.size() == 0)																		//petriNet has been searched, then the trace should be complemented.
		{
			for (int i=retPos; i<originalTrace.length(); i++)
			{
//				String event = originalTrace.getEvent(i);
//				currentTrace.addEvent(event);				
			}
			checkBestTrace(currentTrace, currentValue+(originalTrace.length() - retPos));
		}																	
				
		String eventName = originalTrace.getEvent(retPos);
		Transition nowTransition = originalTrace.getTransition(transitionNameMap, retPos);
		
		//case 1:both move.
		if (nowTransition != null && firableTransitions.contains(nowTransition))		
		{
			currentTrace.addEvent(eventName);
			List<Place> firedMarkings = ModelUtil.fire(petriNet, markings, nowTransition);
			search(petriNet, originalTrace, currentTrace, firedMarkings, petriNetPos+1, retPos +1, transitionNameMap, currentValue);
			currentTrace.removeLastEvent();
		}
		//case 2: petriNet move while trace not.
		if (!firableTransitions.isEmpty())
		{
			for (Transition transition	:	firableTransitions)
			{
				currentTrace.addEvent(transition.getIdentifier());
				List<Place> firedMarkings = ModelUtil.fire(petriNet, markings, transition);
				search(petriNet, originalTrace, currentTrace, firedMarkings, petriNetPos+1, retPos, transitionNameMap, currentValue+1);
				currentTrace.removeLastEvent();
			}
		}
		//case 3:  trace move while petriNet not.
		if (nowTransition != null)
		{			
			search(petriNet, originalTrace, currentTrace, markings, petriNetPos, retPos+1, transitionNameMap, currentValue+1);			
		}
	}
}
