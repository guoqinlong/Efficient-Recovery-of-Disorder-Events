package data;

import java.util.LinkedList;

/**
 * The Event log
 * 
 * @author qinlongguo
 *
 */
public class EventLog {
	LinkedList<LinkedList<String>> content;
	
	public EventLog()
	{
		content = new LinkedList<LinkedList<String>>();
	}
	
	public void addTrace(LinkedList<String> trace)
	{
		content.add(trace);
	}
}
