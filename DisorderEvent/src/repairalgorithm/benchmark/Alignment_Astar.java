package repairalgorithm.benchmark;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;

import repairalgorithm.RepairAlgorithm;
import util.ModelUtil;
import data.EventLog;
import data.Trace;

/**
 * 
 * Node in the search tree.
 * 
 * @author qinlongguo
 *
 */
class Node implements Comparable
{
	//==search state==
	List<Place> markings;		//state of PetriNet	
	int fValue;							//f value		:	estimated value
	int realValue;						//real value	:	namely the value of how many change
	int tracePos;						//state of trace;
	Trace currentTrace;			//current trace.
	
	//==search background==
	PetriNet petriNet;
	Trace originalTrace;
	HashMap<String,Transition> transitionNameMap;
	
	
	/**
	 * construct a new Node with all information provided
	 * @param markings
	 * @param fValue
	 * @param tracePos
	 * @param currentTrace
	 * @param petriNet
	 * @param originalTrace
	 * @param transitionNameMap
	 */
	public Node(List<Place> markings, int fValue, int realValue, int tracePos, Trace currentTrace, 
			PetriNet petriNet, Trace originalTrace, HashMap<String, Transition> transitionNameMap)
	{
		this.markings = markings;
		this.fValue = fValue;
		this.realValue = realValue;
		this.tracePos = tracePos;
		this.currentTrace = currentTrace;
		
		this.petriNet = petriNet;
		this.originalTrace = originalTrace;
		this.transitionNameMap = transitionNameMap;
				
	}
	
	/**
	 *  construct a new blank Node as a  node.
	 * @param petriNet
	 */
	public Node(PetriNet petriNet, Trace originalTrace, HashMap<String,Transition> transitionNameMap)
	{				
		initializeState();
		this.petriNet = petriNet;
		this.originalTrace= originalTrace;
		this.transitionNameMap = transitionNameMap;
	}
	
	public void initializeState()
	{
		markings = new LinkedList<Place>();
		fValue = 0;
		realValue = 0;
		tracePos = 0;
		currentTrace = new Trace();
	}
	
	/**
	 * make this node as SourceNode;
	 * 1.initialize the search state
	 * 2.add the markings.
	 */
	public void sourceNode()
	{
		initializeState();
		markings = ModelUtil.getIntialMarking(petriNet);
	}
	
	public List<Transition> getFirableTransitions()
	{
		return ModelUtil.getFirableTransitions(petriNet, markings);
	}
	
	@Override
	public int compareTo(Object o) 
	{
		Node n = (Node) o;
		return (int) Math.signum(this.fValue-n.fValue);		
	}
	
	public Transition getNowTransition()
	{
		String eventName = originalTrace.getEvent(tracePos);
		if (eventName == null)
			return null;
		Transition ret = transitionNameMap.get(eventName);
		return ret;
	}
	
	@Override
	public Node clone()
	{
		Node ret = new Node(this.petriNet, this.originalTrace, this.transitionNameMap);
		ret.markings = new LinkedList<Place>(this.markings);
		ret.fValue = this.fValue;
		ret.realValue = this.realValue;
		ret.tracePos = this.tracePos;
		ret.currentTrace =(Trace) this.currentTrace.clone();
		return ret;
	}
	
	/**
	 * to check whether the node represents a complete repair, namely the firable transition is empty
	 * @return
	 */
	public boolean completeRepiar() 
	{
		List<Transition> firableTransitions = getFirableTransitions();
		return (firableTransitions.size() == 0);
	}

	/**
	 * 
	 * Fire a transition in the node
	 * 
	 * @param nowTransition
	 */
	public void fire(Transition nowTransition) {
		markings =ModelUtil.fire(petriNet, markings, nowTransition);
		currentTrace.addEvent(nowTransition.getIdentifier());
	}

	public void moveTrace() {
		tracePos++;		
	}

	public void updateFValue() {				
		int gValue = realValue + tracePos;		
		int hValue = originalTrace.length() - tracePos;
		
		fValue = gValue + hValue;
	}

	public void increaseRealValue() {
		realValue++;		
	}
	
}

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
	public EventLog repair(PetriNet petriNet, EventLog eventLog)
	{
		EventLog ret = new EventLog();
		for (Trace originalTrace	:	eventLog)
		{
			Trace repairedTrace;
			repairedTrace = repairTrace(petriNet, originalTrace);
			ret.addTrace(repairedTrace);
		}
		return ret;
	}	
	
	/**
	 *  repair trace with petrinet
	 * @param petriNet
	 * @param trace
	 * @return
	 */
	public Trace repairTrace(PetriNet petriNet, Trace trace)
	{								
		initBestTrace();
		HashMap<String,Transition> transitionNameMap= ModelUtil.getTransitionNameMap(petriNet);
		
		Node sourceNode = new Node(petriNet, trace, transitionNameMap);
		LinkedList<Node> openTable = new LinkedList<Node>();	//OPEN table
		openTable.add(sourceNode);
		HashSet<Node> closeTable = new HashSet<Node>();															//ClOSE table				
		
		while (openTable.size() > 0)
		{
			Node headNode = openTable.poll();
			if (headNode.completeRepiar())
			{
				updateRepair(headNode);
				continue;
			}
			List<Node> childNodes = expand(headNode);
			for (Node childNode	:	childNodes)
			{
				//evaluate the f of childNode
				if (openTable.contains(childNode))
				{
					int originalPos = openTable.indexOf(childNode);
					Node originalNode = openTable.get(originalPos);
					if (originalNode.fValue > childNode.fValue)
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
	 * update the bestTrace
	 * 
	 * @param headNode
	 */
	private void updateRepair(Node headNode) {
		if (isFirstTrace || bestValue > headNode.realValue)
		{
			bestValue = headNode.fValue;
			bestTrace= (Trace) headNode.currentTrace.clone();			
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
	 * @param headNode
	 * @return
	 */
	private List<Node> expand(Node headNode) {
		List<Node> ret = new LinkedList<Node>();
		List<Place> nowMarkings = headNode.markings;		
		Transition nowTransition = headNode.getNowTransition();
		int nowTracePos = headNode.tracePos;
		int nowRealValue = headNode.realValue;
		List<Transition> nowFirableTransitions = headNode.getFirableTransitions();
		Trace nowCurrentTrace = headNode.currentTrace;
		
		PetriNet petriNet = headNode.petriNet;
		Trace originalTrace = headNode.originalTrace;
		HashMap<String,Transition> transitionNameMap = headNode.transitionNameMap;
		
		//case 1: both petriNet and trace move
		if (nowTransition != null && nowFirableTransitions.contains(nowTransition))
		{
			Node newNode = headNode.clone();
			newNode.fire(nowTransition);
			newNode.moveTrace();
			newNode.updateFValue();
			
//			List<Place> newMarkings = ModelUtil.fire(petriNet, nowMarkings, nowTransition);		//markings
//			int newRealValue = nowRealValue;																									//both move does not increase the realValue			
//			int newTracePos = nowTracePos +1;																									//state of trace;
//			Trace newCurrentTrace = (Trace) nowCurrentTrace.clone();														//current trace.
//			newCurrentTrace.addEvent(nowTransition.getIdentifier());
			
//			int newFValue = calculateFValue(newRealValue, newTracePos, originalTrace);							//f value
//			Node newNode = new Node(newMarkings, newFValue, newRealValue, newTracePos, newCurrentTrace, 
//												petriNet, originalTrace, transitionNameMap);
			ret.add(newNode);
		}
		//case 2: petriNet move and trace not
		if (!nowFirableTransitions.isEmpty())
		{
			for(Transition transition	:	nowFirableTransitions)
			{				
				Node newNode = headNode.clone();
				newNode.fire(transition);
				newNode.increaseRealValue();
				newNode.updateFValue();
//				List<Place> newMarkings = ModelUtil.fire(petriNet, nowMarkings, transition);		//markings
//				int newRealValue = nowRealValue+1;																									//both move does not increase the realValue			
//				int newTracePos = nowTracePos;																									//state of trace;
//				Trace newCurrentTrace = (Trace) nowCurrentTrace.clone();														//current trace.
//				newCurrentTrace.addEvent(transition.getIdentifier());
//				
//				int newFValue = calculateFValue(newRealValue, newTracePos, originalTrace);							//f value
//				Node newNode = new Node(newMarkings, newFValue, newRealValue, newTracePos, newCurrentTrace, 
//													petriNet, originalTrace, transitionNameMap);
				ret.add(newNode);
			}
		}
		//case 3: petriNet not move, trace move
		if (nowTransition != null)
		{
			
			Node newNode = headNode.clone();
			newNode.moveTrace();
			newNode.increaseRealValue();
			newNode.updateFValue();
			
//			List<Place> newMarkings = nowMarkings;		//markings
//			int newRealValue = nowRealValue+1;																									//both move does not increase the realValue			
//			int newTracePos = nowTracePos +1;																									//state of trace;
//			Trace newCurrentTrace = nowCurrentTrace;
//			
//			int newFValue = calculateFValue(newRealValue, newTracePos, originalTrace);							//f value
//			Node newNode = new Node(newMarkings, newFValue, newRealValue, newTracePos, newCurrentTrace, 
//												petriNet, originalTrace, transitionNameMap);
			ret.add(newNode);
		}
		return ret;
	}

	/**
	 * calculate the fValue
	 * @param newRealValue
	 * @param newTracePos
	 * @param originalTrace
	 * @return
	 */
	private int calculateFValue(int newRealValue, int newTracePos, Trace originalTrace) {
		int gValue;
		int hValue;
		int fValue;
		
		gValue = newRealValue + newTracePos;		
		hValue = originalTrace.length() - newTracePos;
		
		fValue = gValue + hValue;
		return fValue;		
	}
}
