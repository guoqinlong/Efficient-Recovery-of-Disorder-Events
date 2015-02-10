package data;

import java.util.LinkedList;

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
}
