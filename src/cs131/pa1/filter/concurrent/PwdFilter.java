package cs131.pa1.filter.concurrent;

public class PwdFilter extends ConcurrentFilter {
	public PwdFilter() {
		super();
	}
	
	public void process() {
		output.add(processLine(""));
	}
	
	public String processLine(String line) {
		if(!isDone()){
			//move to the next process
		}
		return ConcurrentREPL.currentWorkingDirectory;
	}
}
