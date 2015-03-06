package repairalgorithm;

import data.EventLog;

public class RepairResult {
	EventLog eventLog;
	long time;
	public EventLog getEventLog() {
		return eventLog;
	}
	public void setEventLog(EventLog eventLog) {
		this.eventLog = eventLog;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
}
