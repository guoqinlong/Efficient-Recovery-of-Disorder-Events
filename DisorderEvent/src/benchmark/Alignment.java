package benchmark;

import org.processmining.framework.models.petrinet.PetriNet;

import data.EventLog;
import data.Trace;

/**
 * 
 * Benchmark algorithm	:	 alignment;
 * 
 * @author qinlongguo
 *
 */
public class Alignment {
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
		Trace ret = new Trace();
		return ret;
	}

}
