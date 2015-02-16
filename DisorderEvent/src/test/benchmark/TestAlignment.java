package test.benchmark;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.processmining.framework.models.petrinet.PetriNet;
import org.xml.sax.SAXException;

import repairalgorithm.benchmark.Alignment_Astar;
import repairalgorithm.benchmark.Alignment_BruteForce;
import util.DataUtil;
import data.EventLog;

/**
 * 
 * Test for Benchmark algorithm;
 * 
 * @author qinlongguo
 *
 */
public class TestAlignment {

	/**
	 * Test for repair method
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public static void testRepair() throws IOException, ParserConfigurationException, SAXException
	{
		String petriNetFilePath ="data/model/Simselect1.pnml";
		String eventLogFilePath ="data/log/Simselect1.mxml";
		PetriNet petriNet = DataUtil.getPetriNetFromFilePath(petriNetFilePath);
		EventLog eventLog = DataUtil.getEventLogFromFilePath(eventLogFilePath);
		Alignment_Astar alignment = new Alignment_Astar();
		
		EventLog retEventLog = alignment.repair(petriNet, eventLog);
		System.out.println(retEventLog.toString());
	}
	
	public static void main(String args[]) throws IOException, ParserConfigurationException, SAXException
	{
		testRepair();
	}	
	
}	
	