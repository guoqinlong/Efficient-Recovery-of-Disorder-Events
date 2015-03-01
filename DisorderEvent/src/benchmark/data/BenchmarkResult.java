package benchmark.data;

public class BenchmarkResult {
	double accuracy;
	long time;
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Accuracy\t:\t");
		sb.append(accuracy);
		sb.append("\n");
		
		sb.append("Time\t:\t");
		sb.append(time);
		sb.append("\n");
		return sb.toString();
	}

	public double getAccuracy() { 		return accuracy; 	}

	public void setAccuracy(float accuracy) {		this.accuracy = accuracy;	}

	public long getTime() {		return time;	}

	public void setTime(long time) {		this.time = time;	}
}
