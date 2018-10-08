package cs131.pa1.filter.concurrent;

public class PwdFilter extends ConcurrentFilter {
	public boolean isDone;
	
	public PwdFilter() {
		super();
		isDone = false;
	}
	
	public void process() {
		output.add(processLine(""));
		isDone = true;
		ConcurrentREPL.moveProcess(this);
	}
	
	public String processLine(String line) {
		return ConcurrentREPL.currentWorkingDirectory;
	}
	
	public boolean isDone() {
		return isDone;
	}
	
	public String toString() {
		return "pwd";
	}
	
}
