package test.util;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.processmining.framework.models.petrinet.PetriNet;
import org.xml.sax.SAXException;

import data.EventLog;
import util.IOUtil;

/**
 * 
 * Test for class util.DataUtil
 * @author qinlongguo
 *
 */
public class IOUtilTest {
	
	/**
	 * Test for method getPetriNetFromFilePath
	 */
	public static void testGetPetriNetFromFilePath()
	{
		String filePath = "data/model/Simselect1.pnml";
		PetriNet ret = null;
		try {
			ret = IOUtil.getPetriNetFromFilePath(filePath);
		} catch (IOException e) {			
			e.printStackTrace();
		}
		System.out.println(ret);
	}
	
	/**
	 * Test for method getEventLogFromFilePath
	 */
	public static void testGetEventLogFromFilePath()
	{
		String filePath = "data/log/Simselect1.mxml";
		EventLog ret = null;
		try {
			ret = IOUtil.getEventLogFromFilePath(filePath);
		} catch (IOException | ParserConfigurationException | SAXException e) {			
			e.printStackTrace();
		}
		System.out.println(ret);
	}

	public static void main(String args[])
	{
		testGetEventLogFromFilePath();
	}
}
