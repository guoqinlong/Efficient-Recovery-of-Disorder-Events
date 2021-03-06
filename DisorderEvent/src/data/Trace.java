package data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.processmining.framework.models.petrinet.Transition;

public class Trace implements Iterable<String> {
	LinkedList<String> traceContent;

	public Trace()
	{
		traceContent = new LinkedList<String>();
	}

	public void addEvent(String name) 
	{
		traceContent.add(name);
	}

	public int length() 
	{		
		return traceContent.size();
	}
	
	public int indexOf(String event, int beginPos)
	{
		return traceContent.subList(beginPos+1, length()).indexOf(event) + beginPos + 1; 
	}

	public String getEvent(int i) 
	{
		if (i >= length())
			return null;
		return traceContent.get(i);		
	}

	public Transition getTransition(
			HashMap<String, Transition> transitionNameMap, int petriNetPos) {
			String event = getEvent(petriNetPos);		
		return event == null ? null:transitionNameMap.get(event);
	}

	public void removeLastEvent() 
	{
		if (!traceContent.isEmpty())
			traceContent.removeLast();
	}
	
	@Override
	public Object clone()
	{
		Trace ret = new Trace();
		for (String  event	:	traceContent)
		{
			ret.addEvent(event);
		}
		return ret;
	}
	
	@Override
	public String toString()
	{		
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		String separator = "";
		for (String event	:	traceContent)
		{
			sb.append(separator+event);
			separator = ",";
		}
		sb.append(']');
		return sb.toString();
	}
	
	@Override
	public Iterator<String> iterator() 
	{		
		return traceContent.iterator();
	}
	
	public String getLastEvent() {
		return traceContent.getLast();		
	}
	
	public LinkedList<String> getTraceContent() { return traceContent; }

	public void setTraceContent(LinkedList<String> traceContent) { this.traceContent = traceContent; }

	

}
