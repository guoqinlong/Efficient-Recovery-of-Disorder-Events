package benchmark;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.processmining.framework.models.petrinet.PetriNet;
import org.xml.sax.SAXException;

import benchmark.data.BenchmarkResult;
import repairalgorithm.RepairAlgorithm;
import data.EventLog;
import data.Trace;
import util.EventLogUtil;
import util.IOUtil;

/**
 * 
 * benchmark
 * 
 * compare the 2 algorithms on 2 aspects:
 * 1. accuracy
 * 2. time cost
 * 
 * @author qinlongguo
 *
 */
public class Benchmark {
	
	static String className[] = {"repairalgorithm.alignmentalgorithm.Alignment_Astar",
												"repairalgorithm.newalgorithm.NewAlgorithm"
			};
	static String modelFolderName = "model";
	static String logFolderName = "log";
	
	public static void test(RepairAlgorithm repairAlgorithm, String modelFolder, String logFolder) throws IOException, ParserConfigurationException, SAXException
	{
		
		List<PetriNet> petriNets = IOUtil.getPetriNetsFromFolderPath(modelFolder);
		List<EventLog> eventLogs = IOUtil.getEventLogsFromFolderPath(logFolder);
		if (petriNets.size() != eventLogs.size())
		{
			System.out.println("Size does not MATCH!");
			return;
		}
		for (int i=0;i<petriNets.size(); i++)
		{
			PetriNet petriNet = petriNets.get(i);
			EventLog eventLog = eventLogs.get(i);
			EventLog wrongEventLog = EventLogUtil.disorder(eventLog);
			
			BenchmarkResult result = test(repairAlgorithm, petriNet, eventLog, wrongEventLog);
			
			System.out.println(petriNet.getName()+"\t:");
			System.out.println(result.toString());			
		}
	}
	
   /**
    * test single event log
    */
	private static BenchmarkResult test(RepairAlgorithm repairAlgorithm,
			PetriNet petriNet, EventLog eventLog, EventLog wrongEventLog) {
		BenchmarkResult result = new BenchmarkResult();
		Date d1 = new Date();
		EventLog repairedEventLog = repairAlgorithm.repair(petriNet, wrongEventLog);
		Date d2 = new Date();
		result.setTime(d2.getTime() - d1.getTime());
		float accuracy = EventLogUtil.calcuateAccuracy(repairedEventLog, eventLog);
		result.setAccuracy(accuracy);
		return result;
	}

}
