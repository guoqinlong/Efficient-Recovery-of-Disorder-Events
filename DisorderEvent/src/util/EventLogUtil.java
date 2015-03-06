package util;

import java.util.ArrayList;
import java.util.Iterator;

import data.EventLog;
import data.Trace;

/**
 * Util for data.EventLog
 * 
 * @author qinlongguo
 *
 */
public class EventLogUtil {

	/**
	 * 
	 * disorder an eventLog
	 * 
	 * @param eventLog
	 * @return
	 */
	public static EventLog disorder(EventLog eventLog) {
		EventLog ret = new EventLog();
		for (Trace trace	:	eventLog)
		{
			Trace disorderTrace = disorderTrace(trace);
			ret.addTrace(disorderTrace);
		}
		return ret;
	}
	
	/**
	 * 
	 * disorder a trace
	 * 
	 * @param trace
	 * @return
	 */
	private static Trace disorderTrace(Trace trace) 
	{
		ArrayList<String> content = new ArrayList<String>(trace.getTraceContent());
		Trace ret = new Trace();
		int size = content.size();
		while(size > 0)
		{
//			int pos = (int) (Math.random() * size);
//			int pos = size-1;
			int pos = 0;
			String event = content.get(pos);
			content.remove(pos);
			ret.addEvent(event);			
			size --;
		}
		return ret;
	}

	/**
	 * 
	 * calculate the accuracy of eventlog
	 * 
	 * @param repairedEventLog
	 * @param eventLog
	 * @return
	 */
	public static float calcuateAccuracy(EventLog repairedEventLog,
			EventLog eventLog) {
		float ret = 0;			
		int traceNum = eventLog.size();
		Iterator<Trace> eventLogIterator = eventLog.iterator();
		Iterator<Trace> repairedEventLogIterator = repairedEventLog.iterator();
		
		while (eventLogIterator.hasNext() && repairedEventLogIterator.hasNext())
		{
			Trace trace = eventLogIterator.next();
			Trace repairedTrace = repairedEventLogIterator.next();
			double singleResult = calculateAccuracy(trace, repairedTrace);
			ret += singleResult;
		}
		ret = ret /traceNum;
		return ret;
	}

	/**
	 * 
	 * calculate single accuracy:
	 * 
	 * match(a,b)/maxlen(a,b)	 
	 * 
	 * @param trace
	 * @param repairedTrace
	 * @return
	 */
	private static double calculateAccuracy(Trace trace, Trace repairedTrace) 
	{
		int length = Math.max(trace.length(), repairedTrace.length());
		Iterator<String> traceIterator = trace.iterator();
		Iterator<String> repairedTraceIterator = repairedTrace.iterator();
		int match = 0;
		while (traceIterator.hasNext() && repairedTraceIterator.hasNext())
		{
			String event = traceIterator.next();
			String repairedEvent = repairedTraceIterator.next();
			if (event.equals(repairedEvent))
				match ++;			
		}		
		return 1.0*match / length;
	}

	/**
	 * 
	 * duplicate the event log
	 * 
	 * @param eventLog
	 * @param i
	 * @return
	 */
	public static EventLog duplicate(EventLog eventLog, int coefficient) {
		EventLog ret = new EventLog();
		for (Trace trace	:	eventLog)
		{
			Trace newTrace = (Trace) trace.clone();
			for (int i=0; i<coefficient; i++)
				ret.addTrace(newTrace);
		}
		return ret;
	}

}
