package cs131.pa1.filter.concurrent;
import java.io.File;

public class LsFilter extends ConcurrentFilter{
	int counter;
	File folder;
	File[] flist;
	
	public LsFilter(int jobNum) {
		super(jobNum);
		counter = 0;
		folder = new File(ConcurrentREPL.currentWorkingDirectory);
		flist = folder.listFiles();
	}
	
	@Override
	public void process() {
		while(counter < flist.length && isDone() == false) {
			output.offer(processLine(""));
		}
		isDone = true;
		
	}
	
	@Override
	public String processLine(String line) {
		return flist[counter++].getName();
	}
	
	
	public String toString() {
		return "ls";
	}
}