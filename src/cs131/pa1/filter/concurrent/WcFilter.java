package cs131.pa1.filter.concurrent;

public class WcFilter extends ConcurrentFilter {
	private int linecount;
	private int wordcount;
	private int charcount;
	private boolean isDone;
	
	public WcFilter() {
		super();
		isDone = false;
	}
	
	public void process() {
		if(!input.isEmpty()){
			String line = input.poll();
			String processedLine = processLine(line);
			if (processedLine != null){
				output.add(processedLine);
				isDone = true;
			}
		}else {
			isDone = true;
			output.add(processLine(null));
		}
		ConcurrentREPL.moveProcess(this);
	}
	
	public String processLine(String line) {
		//prints current result if ever passed a null
		if(line == null) {
			return linecount + " " + wordcount + " " + charcount;
		}
		
		if(isDone()) {
			String[] wct = line.split(" ");
			wordcount += wct.length;
			String[] cct = line.split("|");
			charcount += cct.length;
			return ++linecount + " " + wordcount + " " + charcount;
		} else {
			linecount++;
			String[] wct = line.split(" ");
			wordcount += wct.length;
			String[] cct = line.split("|");
			charcount += cct.length;
			return null;
		}
		
	}
	
	public boolean isDone() {
		return isDone;
	}
	
	public String toString() {
		return "wc";
	}
	
}
