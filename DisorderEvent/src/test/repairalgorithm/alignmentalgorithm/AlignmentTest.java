package test.repairalgorithm.alignmentalgorithm;

import java.io.IOException;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import org.processmining.framework.models.petrinet.PetriNet;
import org.xml.sax.SAXException;

import repairalgorithm.alignmentalgorithm.Alignment_Astar;
import util.IOUtil;
import data.EventLog;

/**
 * 
 * Test for Benchmark algorithm;
 * 
 * @author qinlongguo
 *
 */
public class AlignmentTest {

	/**
	 * Test for repair method
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public static void testRepair() throws IOException, ParserConfigurationException, SAXException
	{
		String petriNetFilePath ="data/model/Shortloop1.pnml";
		String eventLogFilePath ="data/log/Shortloop1.mxml";
		PetriNet petriNet = IOUtil.getPetriNetFromFilePath(petriNetFilePath);
		EventLog eventLog = IOUtil.getEventLogFromFilePath(eventLogFilePath);		
		Alignment_Astar alignment = new Alignment_Astar();			
		EventLog retEventLog = alignment.repair(petriNet, eventLog);
		System.out.println(retEventLog.toString());
	}
	
	public static void main(String args[]) throws IOException, ParserConfigurationException, SAXException
	{
		testRepair();
	}	
	
}	
	