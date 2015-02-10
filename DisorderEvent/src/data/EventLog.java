package data;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * The Event log
 * 
 * @author qinlongguo
 *
 */
public class EventLog implements Iterable<Trace>{
	LinkedList<Trace> content;
	
	public EventLog()
	{
		content = new LinkedList<Trace>();
	}
	
	public void addTrace(Trace trace)
	{
		content.add(trace);
	}

	@Override
	public Iterator<Trace> iterator() {
		return content.iterator();		
	}
}
