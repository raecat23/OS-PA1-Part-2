package cs131.pa1.filter.concurrent;

public class PrintFilter extends ConcurrentFilter {
	boolean isDone;
	int i;
	public PrintFilter() {
		super();
		i = 0;
		isDone = false;
	}
	
	public void process() {
		if(!this.input.isEmpty()) {
			processLine(input.poll());
		}else {
			isDone = true;
		}
		ConcurrentREPL.moveProcess(this);
	}
	
	public String processLine(String line) {
		System.out.println(line);
		return null;
	}
	
	public boolean isDone() {
		return isDone;
	}
}