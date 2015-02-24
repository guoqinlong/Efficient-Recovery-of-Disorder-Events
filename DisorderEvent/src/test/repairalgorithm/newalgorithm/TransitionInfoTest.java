package test.repairalgorithm.newalgorithm;

import java.io.IOException;
import java.util.HashMap;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Transition;

import repairalgorithm.newalgorithm.transitioninfo.TransitionInfo;
import util.IOUtil;

/**
 * Test for class TransitionInfo
 * @author qinlongguo
 *
 */
public class TransitionInfoTest {
	
	/** 
	 * Test for method calculateTransitionInfos
	 * @throws IOException 
	 */
	public static void testCalculateTransitionInfos() throws IOException
	{
		String filePath = "data/model/Shortloop3.pnml";
		PetriNet petriNet = IOUtil.getPetriNetFromFilePath(filePath);
		HashMap<Transition, TransitionInfo> transitionInfos = TransitionInfo.calculateTransitionInfos(petriNet);
		System.out.println(transitionInfos);
	}

	public static void main(String agrs[]) throws IOException
	{
		testCalculateTransitionInfos();
	}
}
