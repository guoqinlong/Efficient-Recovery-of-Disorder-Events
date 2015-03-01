package repairalgorithm;
import org.processmining.framework.models.petrinet.PetriNet;

import data.EventLog;
import data.Trace;


public abstract  class RepairAlgorithm {
	protected static boolean isFirstTrace;
	protected static Trace bestTrace;
	protected static int bestValue;
	
	/**
	 * initialize the best trace.
	 */
	protected void initBestTrace() {
		isFirstTrace = true;
		bestTrace = null;
		bestValue = 0;		
	}
	
	/**
	 *
	 * check the best Trace
	 * 
	 * @param currentTrace
	 * @param currentValue
	 */
	protected void checkBestTrace(Trace currentTrace, int currentValue) {
		if (isFirstTrace || currentValue < bestValue)
		{
			bestTrace = (Trace) currentTrace.clone();
			bestValue = currentValue;
			isFirstTrace =false;
			return;
		}		
	}
	
	/**
	 * 
	 * main method repair.
	 * 
	 * @param petriNet
	 * @param eventLog
	 * @return
	 */
	public abstract RepairResult repair(PetriNet petriNet, EventLog eventLog);	
}
