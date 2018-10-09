package cs131.pa1.filter.concurrent;

import java.util.HashSet;

public class UniqFilter extends ConcurrentFilter{
	private HashSet<String> existingStringSet;
	//This set will record what strings are existing
	
	
	public UniqFilter (int jobNum) throws Exception {
		super(jobNum);
		existingStringSet = new HashSet<String> ();
	}

	public String processLine(String line) {
		if(existingStringSet.contains(line)) {
			return null;
		}else {
			existingStringSet.add(line);
			return line;
		}
	}
	
	public String toString() {
		return "uniq";
	}
}
