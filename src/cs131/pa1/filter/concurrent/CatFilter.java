package cs131.pa1.filter.concurrent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import cs131.pa1.filter.Message;

public class CatFilter extends ConcurrentFilter{
	private Scanner reader;
	private String fileName;
	
	public CatFilter(String line, int jobNum) throws Exception {
		super(jobNum);
		
		//parsing the cat options
		String[] args = line.split(" ");
		//obviously incorrect number of parameters
		if(args.length == 1) {
			System.out.printf(Message.REQUIRES_PARAMETER.toString(), line);
			throw new Exception();
		} else {
			try {
				fileName = args[1];
			} catch (Exception e) {
				System.out.printf(Message.REQUIRES_PARAMETER.toString(), line);
				throw new Exception();
			}
		}
		try {
			reader = new Scanner(new File(fileName));
		} catch (FileNotFoundException e) {
			System.out.printf(Message.FILE_NOT_FOUND.toString(), line);
			throw new FileNotFoundException();
		}
	}

	public void process() {
		while(reader.hasNext() && !isDone()) {
			String processedLine = processLine("");
			if(processedLine != null) {
				output.offer(processedLine);
			}
		
		}
		reader.close();
		isDone = true;
		
	}

	public String processLine(String line) {
		if(reader.hasNextLine()) {
			return reader.nextLine();
		} else {
			return null;
		}
	}
		
	public String toString() {
		return "cat "+fileName;
	}
}