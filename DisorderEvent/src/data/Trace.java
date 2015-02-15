package data;

import java.util.HashMap;
import java.util.LinkedList;

import org.processmining.framework.models.petrinet.Transition;

public class Trace {
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
}
