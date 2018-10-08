package cs131.pa1.filter.concurrent;
import java.io.File;

public class LsFilter extends ConcurrentFilter{
	int counter;
	File folder;
	File[] flist;
	private boolean isDone;
	
	public LsFilter() {
		super();
		isDone = false;
		counter = 0;
		folder = new File(ConcurrentREPL.currentWorkingDirectory);
		flist = folder.listFiles();
	}
	
	@Override
	public void process() {
		while(counter < flist.length && !isDone()) {
			output.add(processLine(""));
		}
	}
	
	@Override
	public String processLine(String line) {
		return flist[counter++].getName();
	}
	
	public boolean isDone(){
		return isDone;
	}
}
