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
	public Iterator<Trace> iterator() 
	{
		return content.iterator();		
	}
	
	@Override
	public String toString()
	{
		if (content == null)
			return null;
		StringBuilder sb = new StringBuilder();
		for (Trace trace	:	content)
		{
			sb.append(trace.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	public int size() {		
		return content.size();
	}
	
}
