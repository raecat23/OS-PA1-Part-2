package cs131.pa1.filter.concurrent;

public class WcFilter extends ConcurrentFilter {
	private int linecount;
	private int wordcount;
	private int charcount;
	
	public WcFilter(int jobNum) {
		super(jobNum);
	}
	
	public void process() {
		if(this.prev.isDone() && this.input.isEmpty()) {
			output.offer(processLine(null));
		}
		while (!this.prev.isDone() || !this.input.isEmpty() ){
			String line;
			try {
				line = input.take();
				if(input.isEmpty() && this.prev.isDone()) {
					isDone = true;
				}
				String processedLine = processLine(line);
				if (processedLine != null){
					output.offer(processedLine);
				}
			} catch (InterruptedException e) {
				//Shouldn't happen
				break;
			}
			
		}
		if(this.prev.isDone()) {
			isDone = true;
		}
	}
	
	public String processLine(String line) {
		//prints current result if ever passed a null
		if(line == null) {
			isDone = true;
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
	
	public String toString() {
		return "wc";
	}
	
}