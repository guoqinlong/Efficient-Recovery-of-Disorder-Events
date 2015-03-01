package repairalgorithm.newalgorithm.transitioninfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.processmining.framework.log.LogEvent;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;

import util.ModelUtil;

/**
 * 
 * information related to a transition
 * 
 * @author qinlongguo
 *
 */
public class TransitionInfo 
{
	HashSet<Transition> mustFollowTransition;				//Transitions that must be followed by this transition.
	HashSet<Transition> mayFollowTransition;					//Transitions that may be followed by this transition.
	HashMap<Transition, List<Transition>> preSet;	//Transitions that one have to  execute before list<Transition>
	boolean isInLoop;														//whether this transition is in loop
	boolean isToEnd;														//whether this transition is to end
	
	public TransitionInfo()
	{
		isInLoop = false;
		isToEnd = false;
		mustFollowTransition = null;
		mayFollowTransition = new HashSet<Transition>();
		preSet = new HashMap<Transition, List<Transition>>();		
	}
	
	public void updateMayFollowTransition(List<Transition> visitedTransitions,
			int beginPos)
	{
		for (int i = beginPos; i < visitedTransitions.size(); i++)
		{
			Transition t = visitedTransitions.get(i);
			if (!mayFollowTransition.contains(t))
				mayFollowTransition.add(t);
		}		
	}

	public void updateMustFollowTransition(List<Transition> visitedTransitions,
			int beginPos)
	{		
		if (mustFollowTransition == null)		//firstTime
		{
			mustFollowTransition = new HashSet<Transition>();
			for (int i = beginPos; i < visitedTransitions.size(); i++)
			{
				Transition t = visitedTransitions.get(i);
				mustFollowTransition.add(t);
			}
		}
		else
		{
			HashSet<Transition> newMust = new HashSet<Transition>();
			for (int i = beginPos; i < visitedTransitions.size(); i++)
			{
				Transition t = visitedTransitions.get(i);
				if (mustFollowTransition.contains(t))
					newMust.add(t);				
			}
			mustFollowTransition = newMust;
		}
	}
	
	/**
	 * 
	 * calculate the Transition Info for a petri net.
	 * 
	 * @param petriNet
	 */
	public static HashMap<Transition, TransitionInfo> calculateTransitionInfos(PetriNet petriNet)
	{				 
		//1. initialize ret (Transition Info)
		HashMap<Transition, TransitionInfo> ret = new HashMap<Transition, TransitionInfo>();
		for (Transition t	:	petriNet.getTransitions())
		{
			if (!t.getIdentifier().equals("T2"))
			{
				LogEvent le = new LogEvent(t.getIdentifier(), null);
				t.setLogEvent(le);
			}
			System.out.println(t.getIdentifier()+"\t"+t.isInvisibleTask());
			TransitionInfo info = new TransitionInfo();
			ret.put(t,info);
		}				
		
		List<Place> initialMarking = ModelUtil.getIntialMarking(petriNet);
		HashSet<Transition> toEndTransitions = new HashSet<Transition>();
		HashSet<Transition> inLoopTransitions = new HashSet<Transition>();
		HashMap<Transition, Transition> loopLookUpTransitions = new HashMap<Transition, Transition>();
		List<Transition> visitedTransition = new LinkedList<Transition>();
		searchTransitionInfo(petriNet, initialMarking, toEndTransitions, visitedTransition, ret, inLoopTransitions, loopLookUpTransitions , true);
		updateToEndInfo(ret, petriNet, toEndTransitions);
		searchTransitionInfo(petriNet, initialMarking, toEndTransitions, visitedTransition, ret, inLoopTransitions, loopLookUpTransitions, false);
		updateLoopInfo(ret, petriNet, inLoopTransitions, loopLookUpTransitions);
		expandMayFollowInfo(petriNet, ret);
		return ret;
	}
	
	/**
	 * expand the may follow info in the ret
	 * 
	 * In the previous step, the mayFollowInfo may not be complete, as in the nested loop.(shortloop3.pnml)
	 * if the outer loop is detected early than the inner one, it cannot find C may follow F
	 * 
	 * @param petriNet
	 * @param ret
	 */
	private static void expandMayFollowInfo(PetriNet petriNet,
			HashMap<Transition, TransitionInfo> ret) 
	{
		HashSet<Transition> closeTransitions = new HashSet<Transition>();
		List<Place> initialMarkings = ModelUtil.getIntialMarking(petriNet);		
		for (Transition	t:	petriNet.getTransitions())
		{
			List<Transition> visitedTransitions = new LinkedList<Transition>();
			
			if (!closeTransitions.contains(t))
				dfsExpand(petriNet,initialMarkings, closeTransitions, ret, t, visitedTransitions, 0);
		}
	}

	/**
	 *  expand by using Deep-first Search
	 * @param petriNet
	 * @param initialMarking
	 * @param closeTransitions
	 * @param ret
	 */
	private static int dfsExpand(PetriNet petriNet,
			List<Place> markings, HashSet<Transition> closeTransitions,
			HashMap<Transition, TransitionInfo> ret, Transition t, List<Transition> visitedTransitions, int nowPos) 
	{
		if (closeTransitions.contains(t))
			return -1;
		int minPos = -1;
		
		List<Place> firedMarkings = ModelUtil.fire(petriNet, markings, t);
		List<Transition> availableTransitions = ModelUtil.getFirableTransitions(petriNet, firedMarkings);
		
		for (Transition transition	:	availableTransitions)
		{
			int index;
			if (visitedTransitions.contains(transition))
			{
				 index = visitedTransitions.indexOf(transition);
			}
			else
			{
				visitedTransitions.add(transition);
				index = dfsExpand(petriNet, firedMarkings, closeTransitions, ret, transition, visitedTransitions, nowPos+1);
				visitedTransitions.remove(transition);
			}
			if (minPos == -1 || index < minPos)
				minPos = index;
			
			//update
			copyMayFollowInfo(t,transition,ret);
		}
		if (minPos == -1 || minPos >= nowPos)
			closeTransitions.add(t);
			
		return minPos;		
	}

	/**
	 * 
	 * copy mayFollowTransitions from b to a
	 * @param ret 
	 * 
	 */
	private static void copyMayFollowInfo(Transition a, Transition b, HashMap<Transition, TransitionInfo> ret) {
		TransitionInfo aInfo = ret.get(a);
		TransitionInfo bInfo = ret.get(b);
		HashSet<Transition> bMayFollowTransitions =bInfo.mayFollowTransition;
		HashSet<Transition> aMayFollowTransitions =aInfo.mayFollowTransition;
		HashSet<Transition> aMustFollowTransitions =aInfo.mustFollowTransition;
		for (Transition bMayFollowTransition	:	bMayFollowTransitions)
		{		
			if (!aMayFollowTransitions.contains(bMayFollowTransition))				
				aMayFollowTransitions.add(bMayFollowTransition);
			if (aMustFollowTransitions.contains(bMayFollowTransition))		//if aMustFollow contains bMayFollowTransition, there is no need to update its preset
				continue;			
			List<Transition> preTransitions = new LinkedList<Transition>();
			if (!bMayFollowTransition.equals(a) && !aInfo.preSet.containsKey(bMayFollowTransition))	//1.for oneself, we don't consider it's preset, 2. if it has it's preset, we don't need to update 
			{
				if (!aMustFollowTransitions.contains(b))			//if a must follow b, then there is no need for b as the pre.
					preTransitions.add(b);
				List<Transition> bPreTransitions = bInfo.preSet.get(bMayFollowTransition);
				if (bPreTransitions !=null)
				{
					preTransitions.addAll(bPreTransitions);
				}
				aInfo.preSet.put(bMayFollowTransition, preTransitions);
			}
		}
		
	}

	/**
	 * 
	 * update transtions'loop attribute
	 * 
	 * @param ret
	 * @param petriNet
	 * @param loopLookUpTransitions
	 */
	private static void updateLoopInfo(HashMap<Transition, TransitionInfo> ret,
			PetriNet petriNet, HashSet<Transition> inLoopTransitions,
			HashMap<Transition, Transition> loopLookUpTransitions) 
	{
		for (Transition transition	:	inLoopTransitions)
		{
			TransitionInfo info = ret.get(transition);
			info.isInLoop = true;
		}
		for(Transition inLoopTransition	:	loopLookUpTransitions.keySet())
		{
			Transition lookUpTransition = loopLookUpTransitions.get(inLoopTransition);
			TransitionInfo lookUpInfo = ret.get(lookUpTransition);
			TransitionInfo inLoopInfo = ret.get(inLoopTransition);
			inLoopInfo.mustFollowTransition = (HashSet<Transition>) lookUpInfo.mustFollowTransition.clone();
			inLoopInfo.mustFollowTransition.add(lookUpTransition);
			inLoopInfo.mayFollowTransition = (HashSet<Transition>) lookUpInfo.mayFollowTransition.clone();
			inLoopInfo.mayFollowTransition.add(lookUpTransition);			
		}
	}

	/**
	 * 
	 * update each transition's toEnd attribute
	 * 
	 * @param ret
	 * @param petriNet
	 * @param toEndTransitions
	 */
	private static void updateToEndInfo(HashMap<Transition, TransitionInfo> ret,
			PetriNet petriNet, HashSet<Transition> toEndTransitions) {
		for (Transition transition	:	toEndTransitions)
		{
			TransitionInfo info = ret.get(transition);
			info.isToEnd = true;
		}		
	}

	/**
	 *  DFS the petriNet and add the information on transitionInfo
	 *  
	 *  两遍算法?
	 * @param petriNet
	 * @param markings
	 * @param toEndTransitions
	 * @param visitedTransitions
	 * @param ret
	 */
	private static void searchTransitionInfo(PetriNet petriNet,
			List<Place> markings, HashSet<Transition> toEndTransitions,
			List<Transition> visitedTransitions, HashMap<Transition, TransitionInfo> ret, HashSet<Transition> inLoopTransitions,  HashMap<Transition, Transition> loopLookUpTransitions, boolean isFirstTime) 
	{
		List<Transition> firableTransitions = ModelUtil.getFirableTransitions(petriNet, markings);
		
		if (ModelUtil.isFinished(markings, petriNet))
		{
			if (isFirstTime)
			{
				updateToEndInfo(ret, toEndTransitions, visitedTransitions);
			}
			return;
		}
		for (Transition transition	:	firableTransitions)
		{
			if (visitedTransitions.contains(transition) )
			{
				if (!isFirstTime)
					updateLoopInfo(ret, visitedTransitions, inLoopTransitions,  loopLookUpTransitions, transition);
				continue;
			}
			List<Place> newMarkings = ModelUtil.fire(petriNet, markings, transition);
			visitedTransitions.add(transition);
			searchTransitionInfo(petriNet,newMarkings, toEndTransitions, visitedTransitions, ret, inLoopTransitions, loopLookUpTransitions, isFirstTime);
			visitedTransitions.remove(transition);
		}						
	}

	/**
	 * 
	 * update In loop Transitions
	 * 
	 * @param ret
	 * @param visitedTransitions
	 * @param inLoopTransitions
	 * @param loopLookUpTransitions
	 * @param loopTransition 
	 */
	private static void updateLoopInfo(HashMap<Transition, TransitionInfo> ret,
			List<Transition> visitedTransitions,
			HashSet<Transition> inLoopTransitions,
			HashMap<Transition, Transition> loopLookUpTransitions, Transition loopTransition) 
	{
		//1. find transitions that in loop, namely find part A, B, C
		/*
		 *                  <-
		 *                    A
		 *            --------------
		 *            |               |
		 * -----------------------
		 *     B     D      C
		 *     ->            ->
		 * 
		 */
		//2. update B.mayfollow transitions
		//3. update C.mayfollow trantisions
		//4. update A lookup transitions.
		//5.preset?
		//6. update inLoopTransitions
		
		List<Transition> A = new LinkedList<Transition>();
		List<Transition> B = new LinkedList<Transition>();
		List<Transition> C = new LinkedList<Transition>();
		Transition D = loopTransition;
		boolean isInLoop = false;
		for(Transition t	:	visitedTransitions)
		{									
				if (!isInLoop && D != t)			//not in loop...
				{
					B.add(t);					
				}
				if (D == t || isInLoop)			//D == t: first time in loop, or in loop
				{
					if (D == t)
						isInLoop = true;
					TransitionInfo info = ret.get(t);
					if (info.isToEnd)
						C.add(t);
					else
						A.add(t);
				}
		}
		//2.
		for (Transition b	:	B)
		{
			TransitionInfo bInfo = ret.get(b);
			for (Transition a		:	A)
				bInfo.mayFollowTransition.add(a);
		}
		//3.
		//3.1 c->a
		for (Transition c		:	C)
		{
			TransitionInfo cInfo = ret.get(c);
			for (Transition a		:	A)
				cInfo.mayFollowTransition.add(a);
		}
		//3.2 c->c
		for (Transition c	:	C)
		{
			TransitionInfo cInfo = ret.get(c);
			for(Transition cPre	:	C)
			{				
				cInfo.mayFollowTransition.add(cPre);
				if (c.equals(cPre))
					break;
			}				
		}
		//4.
		for(Transition a	:	A)
			loopLookUpTransitions.put(a, loopTransition);
		//5.
		//5.1 for B
		for(Transition b	:	B)
		{
			TransitionInfo bInfo = ret.get(b); 
			for(int i=0; i<A.size(); i++)
			{
				Transition a = A.get(i);				
				List<Transition> preTransitions;				
				preTransitions = new LinkedList<Transition>(A.subList(0, i));				//CAUTION!!! list.sublist() doen't clone a new list....
				bInfo.preSet.put(a, preTransitions);
			}
		}
		//5.2 for C
		for(Transition c	:	C)
		{
			TransitionInfo cInfo = ret.get(c);
			for(int i=0; i<A.size(); i++)
			{
				Transition a = A.get(i);
				List<Transition> preTransitions;				
				preTransitions = new LinkedList<Transition>(A.subList(0, i));
				cInfo.preSet.put(a, preTransitions);
			}
			for (int i=0; i<C.size(); i++)
			{
				Transition cPre = C.get(i);	
				if (cPre.equals(c))
					break;
				List<Transition> preTransitions = new LinkedList<Transition>(C.subList(0, i));
				preTransitions.addAll(A);
				cInfo.preSet.put(cPre, preTransitions);				
			}
		}		
		
		//6.		
		for (Transition a		:	A)
			if (!inLoopTransitions.contains(a))
				inLoopTransitions.add(a);
		for (Transition c		:	C)
			if (!inLoopTransitions.contains(c))
				inLoopTransitions.add(c);
		if (!inLoopTransitions.contains(loopTransition))
			inLoopTransitions.add(loopTransition);
	}

	/**
	 * 
	 * @param ret
	 * @param toEndTransitions
	 * @param visitedTransitions
	 */
	private static void updateToEndInfo(HashMap<Transition, TransitionInfo> ret,
			HashSet<Transition> toEndTransitions,
			List<Transition> visitedTransitions) 
	{
		//1. update toEndTransitions
		for(Transition t	:	visitedTransitions)
		{
			toEndTransitions.add(t);
		}
		
		for (int i=0; i<visitedTransitions.size(); i++)
		{
			Transition nowTransition = visitedTransitions.get(i);
			TransitionInfo nowTransitionInfo = ret.get(nowTransition);
			nowTransitionInfo.updateMayFollowTransition(visitedTransitions, i+1);
			nowTransitionInfo.updateMustFollowTransition(visitedTransitions, i+1);
		}
	}
	
	@Override
	public String toString()
	{
		StringBuilder ret = new StringBuilder();
		ret.append("isInLoop\t:\t");
		ret.append(isInLoop);
		ret.append("\n");
		
		ret.append("isToEnd\t:\t");
		ret.append(isToEnd);
		ret.append("\n");
		
		ret.append("mayFollowTransitions\t:\t");
		ret.append(mayFollowTransition);
		ret.append("\n");
		
		ret.append("mustFollowTransitons\t:\t");
		ret.append(mustFollowTransition);
		ret.append("\n");
		
		ret.append("PreSet\t:\t");
		ret.append(preSet);
		ret.append("\n");
		
		return ret.toString();		
	}

	public HashSet<Transition> getMustFollowTransition() { return mustFollowTransition; }

	public void setMustFollowTransition(HashSet<Transition> mustFollowTransition) { this.mustFollowTransition = mustFollowTransition; }

	public HashSet<Transition> getMayFollowTransition() { return mayFollowTransition; }

	public void setMayFollowTransition(HashSet<Transition> mayFollowTransition) { this.mayFollowTransition = mayFollowTransition; }

	public HashMap<Transition, List<Transition>> getPreSet() { return preSet; }

	public void setPreSet(HashMap<Transition, List<Transition>> preSet) { this.preSet = preSet; }

	public boolean isInLoop() { return isInLoop; }

	public void setInLoop(boolean isInLoop) { this.isInLoop = isInLoop; }

	public boolean isToEnd() { return isToEnd; }

	public void setToEnd(boolean isToEnd) { this.isToEnd = isToEnd; }

}
