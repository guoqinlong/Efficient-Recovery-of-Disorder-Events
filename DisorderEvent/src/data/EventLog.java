package data;

import java.util.LinkedList;

/**
 * The Event log
 * 
 * @author qinlongguo
 *
 */
public class EventLog {
	LinkedList<Trace> content;
	
	public EventLog()
	{
		content = new LinkedList<Trace>();
	}
	
	public void addTrace(Trace trace)
	{
		content.add(trace);
	}
}
