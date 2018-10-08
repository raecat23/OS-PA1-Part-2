package cs131.pa1.filter.concurrent;

import cs131.pa1.filter.Message;

public class GrepFilter extends ConcurrentFilter {
	private String toFind;
	private boolean isDone;
	
	public GrepFilter(String line) throws Exception {
		super();
		isDone = false;
		String[] param = line.split(" ");
		if(param.length > 1) {
			toFind = param[1];
		} else {
			System.out.printf(Message.REQUIRES_PARAMETER.toString(), line);
			throw new Exception();
		}
	}
	
	public void process(){
		if(!this.input.isEmpty() && isDone() == false){
			String line = input.poll();
			String processedLine = processLine(line);
			if (processedLine != null){
				output.add(processedLine);
			}
		}else {
			isDone = true;
		}
		ConcurrentREPL.moveProcess(this);
	}
	
	public String processLine(String line) {
		if(line.contains(toFind)) {
				return line;
			} else {
				return null;
		}
	}
	
	public boolean isDone(){
		return isDone;
	}
	
	public String toString() {
		return "grep "+toFind;
	}
}
