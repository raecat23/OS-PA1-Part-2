package cs131.pa1.filter.concurrent;

import java.util.HashSet;

public class UniqFilter extends ConcurrentFilter{
	private HashSet<String> existingStringSet;
	//This set will record what strings are existing
	private boolean isDone;
	public UniqFilter () throws Exception {
		existingStringSet = new HashSet<String> ();
	}

	public void process(){
		while (!input.isEmpty() && isDone() == false){
			String line = input.poll();
			String processedLine = processLine(line);
			if (processedLine != null){
				output.add(processedLine);
			}
		}	
	}
	public String processLine(String line) {
		if(existingStringSet.contains(line)) {
			return null;
		}else {
			existingStringSet.add(line);
			return line;
		}
	}
	public boolean isDone(){
		return isDone;
	}
}
