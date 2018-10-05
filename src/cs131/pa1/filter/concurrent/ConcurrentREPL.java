package cs131.pa1.filter.concurrent;

import cs131.pa1.filter.Message;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class ConcurrentREPL {
	/* THINGS TO DO
	*  Add method to switch to next thing in queue to call at the end of all the loops
	*  (this will take the head(via take) and add it at the end if it isn't "done")
	
	*  In each command make sure we have isDone reflect if it's actually done
	
	*  Have each command check at the end of each loop to see if there are other thing that needs to run a loop
	*/
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
