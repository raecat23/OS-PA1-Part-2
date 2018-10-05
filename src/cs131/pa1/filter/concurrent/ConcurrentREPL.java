package cs131.pa1.filter.concurrent;

import cs131.pa1.filter.Message;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class ConcurrentREPL {

	static String currentWorkingDirectory;
	static LinkedBlockingQueue<ConcurrentFilter> processes;
	
	public static void main(String[] args){
		currentWorkingDirectory = System.getProperty("user.dir");
		processes = new LinkedBlockingQueue<>();
		
		Scanner s = new Scanner(System.in);
		System.out.print(Message.WELCOME);
		String command;
		while(true) {
			//obtaining the command from the user
			System.out.print(Message.NEWCOMMAND);
			command = s.nextLine();
			if(command.equals("exit")) {
				break;
			} else if(!command.trim().equals("")) {
				//building the filters list from the command
				ConcurrentFilter filterlist = ConcurrentCommandBuilder.createFiltersFromCommand(command);
				while(filterlist != null) {
					filterlist.process();
					filterlist = (ConcurrentFilter) filterlist.getNext();
				}
			}
		}
		s.close();
		System.out.print(Message.GOODBYE);
	}

}
