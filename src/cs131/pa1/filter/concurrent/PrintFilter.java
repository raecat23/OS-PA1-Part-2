package cs131.pa1.filter.concurrent;

public class PrintFilter extends ConcurrentFilter {
	boolean isDone;
	int i;
	
	public PrintFilter(int jobNum) {
		super(jobNum);
		i = 0;
	}
	
	public String processLine(String line) {
		System.out.println(line);
		return null;
	}
}