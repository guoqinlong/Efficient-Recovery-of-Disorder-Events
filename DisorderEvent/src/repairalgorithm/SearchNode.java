package repairalgorithm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;

import util.ModelUtil;
import data.Trace;

/**
 * 
 * Node in the search tree.
 * 
 * @author qinlongguo
 *
 */
public class SearchNode implements Comparable<SearchNode>
{
	//==search state==
	protected List<Place> markings;		//state of PetriNet	
	protected int fValue;							//f value		:	estimated value
	protected int realValue;						//real value	:	namely the value of how many change

	protected int tracePos;						//state of trace;
	protected Trace currentTrace;			//current trace.
	
	//==search background==
	protected PetriNet petriNet;
	protected Trace originalTrace;
	public HashMap<String,Transition> transitionNameMap;
	
	
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
	public SearchNode(List<Place> markings, int fValue, int realValue, int tracePos, Trace currentTrace, 
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
	public SearchNode(PetriNet petriNet, Trace originalTrace, HashMap<String,Transition> transitionNameMap)
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
	public int compareTo(SearchNode n) 
	{		
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
	public SearchNode clone()
	{
		SearchNode ret = new SearchNode(this.petriNet, this.originalTrace, this.transitionNameMap);
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

	public String toString()
	{
		StringBuilder ret = new StringBuilder();
		
		ret.append("markings\t:\t");
		ret.append(markings.toString());
		ret.append("\n");
		
		ret.append("fValue\t:\t");
		ret.append(fValue);
		ret.append("\n");
				
		ret.append("realValue\t:\t");
		ret.append(realValue);
		ret.append("\n");
				
		ret.append("tracePos\t:\t");
		ret.append(tracePos);
		ret.append("\n");
				
		ret.append("currentTrace\t:\t");
		ret.append(currentTrace.toString());
		ret.append("\n");				
				
		ret.append("originalTrace\t:\t");
		ret.append(originalTrace.toString());
		ret.append("\n");					
		return ret.toString();
	}
	
	public int getRealValue() { return realValue; }
	public void setRealValue(int realValue) { 	this.realValue = realValue; }

	public int getfValue() { 	return fValue; }
	public void setfValue(int fValue) { this.fValue = fValue; 	}

	public Trace getCurrentTrace() { return currentTrace; 	}
	public void setCurrentTrace(Trace currentTrace) { this.currentTrace = currentTrace; }
	
}
