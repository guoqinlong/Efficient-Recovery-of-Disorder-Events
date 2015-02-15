package test.benchmark;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;
import org.xml.sax.SAXException;

import repairalgorithm.benchmark.Alignment_BruteForce;
import util.DataUtil;
import util.ModelUtil;
import data.EventLog;
import data.Trace;

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
		Alignment_BruteForce alignment = new Alignment_BruteForce();
		
		EventLog retEventLog = alignment.repair(petriNet, eventLog);
		System.out.println(retEventLog.toString());
	}
	
	public static void main(String args[]) throws IOException, ParserConfigurationException, SAXException
	{
		testRepair();
	}	
	
}	
	