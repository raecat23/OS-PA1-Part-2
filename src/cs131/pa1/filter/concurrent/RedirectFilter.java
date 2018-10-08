package cs131.pa1.filter.concurrent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import cs131.pa1.filter.Filter;
import cs131.pa1.filter.Message;

public class RedirectFilter extends ConcurrentFilter {
	private FileWriter fw;
	private String line;
	private boolean isDone;
	
	public RedirectFilter(String line) throws Exception {
		super();
		isDone = false;
		String[] param = line.split(">");
		if(param.length > 1) {
			if(param[1].trim().equals("")) {
				System.out.printf(Message.REQUIRES_PARAMETER.toString(), line.trim());
				throw new Exception();
			}
			try {
				this.line = param[1].trim();
				fw = new FileWriter(new File(ConcurrentREPL.currentWorkingDirectory + Filter.FILE_SEPARATOR + param[1].trim()));
			} catch (IOException e) {
				System.out.printf(Message.FILE_NOT_FOUND.toString(), line);	//shouldn't really happen but just in case
				throw new Exception();
			}
		} else {
			System.out.printf(Message.REQUIRES_INPUT.toString(), line);
			throw new Exception();
		}
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
		try {
			fw.append(line + "\n");
			if(isDone()) {
				fw.flush();
				fw.close();
			}
		} catch (IOException e) {
			System.out.printf(Message.FILE_NOT_FOUND.toString(), line);
		}
		return null;
	}
	
	public boolean isDone() {
		return isDone;
	}
	
	public String toString() {
		return "> "+line; 
	}
}
