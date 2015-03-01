package test.util;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.processmining.framework.models.petrinet.PetriNet;
import org.xml.sax.SAXException;

import data.EventLog;
import util.IOUtil;
import util.ModelUtil;

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
	
	/**
	 * make the petri net visible
	 * @param folderPath
	 * @throws IOException 
	 */
	public static void makePetriNetVisible(String folderPath) throws IOException
	{
		File folder = new File(folderPath);
		File[] files = folder.listFiles();
		for(File file	:	files)
		{
			String filePath = file.getAbsolutePath();
			PetriNet pn = IOUtil.getPetriNetFromFilePath(filePath);
			PetriNet newPN = ModelUtil.makedTransitionVisible(pn);
			IOUtil.savePetriNetToFile(filePath+"1", pn);
		}
	}

	public static void main(String args[]) throws IOException
	{
		makePetriNetVisible("data/model");
	}
}
