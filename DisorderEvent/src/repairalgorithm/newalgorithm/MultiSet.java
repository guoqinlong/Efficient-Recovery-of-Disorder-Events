package repairalgorithm.newalgorithm;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class MultiSet<T> implements Collection<T> {
	HashMap<T, Integer> content;
	
	public MultiSet()
	{
		content = new HashMap<T, Integer>();
	}
	
	@Override
	public int size() {
		int ret = 0;
		for (Integer i	:	content.values())
			ret += i;
		return ret;
	}

	@Override
	public boolean isEmpty() {
		return content.isEmpty();		
	}

	@Override
	public boolean contains(Object o) {
		return this.content.containsKey(o);
	}

	@Override
	public Iterator<T> iterator() {
		return content.keySet().iterator();
	}
	
	@Override
	public Object clone()
	{
		MultiSet<T> ret = new MultiSet<T>();
		ret.content = new HashMap<T,Integer>(this.content);
		return ret;
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray(Object[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public  boolean add(T e) {		
		Integer value = this.content.get(e);
		if (value == null)
			value =0;
		this.content.put(e, value+1);
		return true;
	}

	@Override
	public boolean remove(Object o) {
		Integer value = this.content.get(o);
		if (value == null)
			return false;
		value--;
		if (value<=0)
			this.content.remove(o);
		else
			this.content.put((T)o, value);
		return true;
	}

	@Override
	public boolean containsAll(Collection c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection c) {
		if (c == null)
			return false;
		else if (!(c instanceof MultiSet))
			throw new UnsupportedOperationException();
		else
		{
			MultiSet<T> ms = (MultiSet<T>) c;
			for (T	t:	ms.content.keySet())
			{
				Integer msVal = ms.content.get(t);
				Integer val = this.content.get(t);
				if (val == null)
					val = 0;
				this.content.put(t, msVal+val);				
			}
			return true;
		}
	}

	@Override
	public boolean removeAll(Collection c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}
}
