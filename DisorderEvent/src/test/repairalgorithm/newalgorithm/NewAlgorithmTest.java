package test.repairalgorithm.newalgorithm;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.processmining.framework.models.petrinet.PetriNet;
import org.xml.sax.SAXException;

import repairalgorithm.newalgorithm.NewAlgorithm;
import util.IOUtil;
import data.EventLog;

public class NewAlgorithmTest {
	
	/**
	 * Test new algorithm repair method
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public static void testRepair() throws IOException, ParserConfigurationException, SAXException
	{
		String petriNetFilePath ="data/model/Shortloop3.pnml";
		String eventLogFilePath ="data/log/Shortloop3.mxml";
		PetriNet petriNet = IOUtil.getPetriNetFromFilePath(petriNetFilePath);
		EventLog eventLog = IOUtil.getEventLogFromFilePath(eventLogFilePath);
		NewAlgorithm alignment = new NewAlgorithm();
		
		EventLog retEventLog = alignment.repair(petriNet, eventLog);
		System.out.println(retEventLog.toString());
	}
		
	public static void main(String agrs[]) throws IOException, ParserConfigurationException, SAXException
	{
		testRepair();
	}

}
