package cs131.pa1.filter.concurrent;

public class PwdFilter extends ConcurrentFilter {
	
	public PwdFilter() {
		super();
	}
	
	public void process() {
		output.add(processLine(""));
		isDone = true;
	}
	
	public String processLine(String line) {
		return ConcurrentREPL.currentWorkingDirectory;
	}
		
	public String toString() {
		return "pwd";
	}
	
}