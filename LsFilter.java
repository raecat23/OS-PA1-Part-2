package cs131.pa1.filter.concurrent;
import java.io.File;

public class LsFilter extends ConcurrentFilter{
	int counter;
	File folder;
	File[] flist;
	private boolean isDone;
	public LsFilter() {
		super();
		counter = 0;
		folder = new File(ConcurrentREPL.currentWorkingDirectory);
		flist = folder.listFiles();
	}
	
	@Override
	public void process() {
		if( counter < flist.length && isDone() == false) {
			output.add(processLine(""));
		}
		ConcurrentREPL.moveProcess(this);
	}
	
	@Override
	public String processLine(String line) {
		
		return flist[counter++].getName();
	}
	public boolean isDone(){
		return isDone
	}
}
