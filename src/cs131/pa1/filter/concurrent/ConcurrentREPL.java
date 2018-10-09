package cs131.pa1.filter.concurrent;

import cs131.pa1.filter.Message;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class ConcurrentREPL {

	static String currentWorkingDirectory;
	static List<ConcurrentFilter> processes;
	static int jobs;
	
	public static void main(String[] args){
		jobs = 0;
		processes = new LinkedList<>();
		currentWorkingDirectory = System.getProperty("user.dir");
		Scanner s = new Scanner(System.in);
		System.out.print(Message.WELCOME);
		
		String command;
		boolean exit = false;
		while(!exit) {
			//obtaining the command from the user
			System.out.print(Message.NEWCOMMAND);
			command = s.nextLine();
			if(command.equals("exit")) {
				exit = true;
			} else if(!command.trim().equals("")) {
				String[] temp = command.split(" ");
				
				if(withBG(command)) {
					if(temp[0].equals("repl_jobs")) {
						replJobs();
					}else if(temp[0].equals("kill")) {
						kill(command);
					}else {
						if(ConcurrentCommandBuilder.createFiltersFromCommand(command.trim().substring(0, command.length()-1), jobs)) {
							jobs++;
						}
					}	
				}else {
					if(temp[0].equals("repl_jobs")) {
						replJobs();
					}else if(temp[0].equals("kill")) {
						kill(command);
					}else {
						if(ConcurrentCommandBuilder.createFiltersFromCommand(command, jobs)) {
							jobs++;
						}
					}
				}
				
			}
		}
		s.close();
		System.out.print(Message.GOODBYE);
	}
	
	/**
	 * Checks if the & command has been used so the program will know to print the > prompt
	 */
	private static boolean withBG(String cmd) {
		String trimmed = cmd.trim();		
		if(trimmed.charAt(trimmed.length()-1) == '&') {
			return true;
		}		
		return false;
	}
	
	public static void replJobs() {
		List<ConcurrentFilter> temp = processes;
		for(int i = 0; i < jobs; i++) {//Starts the new line for each "job" list 
			System.out.print(i+1 + ". ");
			for(ConcurrentFilter curr : temp) {
				if(curr.jobNum == i) {
					System.out.print(curr.toString() + " ");
					if(curr.getNext() != null && !(curr.getNext() instanceof PrintFilter)) {
						System.out.print("| ");
					}else {
						System.out.print("&\n");
					}
					temp.remove(curr);
				}
			}
		}
	}
	
	public static void runNewJobs(List<ConcurrentFilter> jobs) {
		for(ConcurrentFilter job : jobs) {
			job.run();
		}
	}
	
	public static void kill(String killCmd) {
		String[] temp = killCmd.split(" ");
		int kill = Integer.parseInt(temp[1]);
		kill--;
		if(kill <= jobs) {
			for(ConcurrentFilter curr : processes) {
				if(curr.jobNum == kill) {
					curr.setDone();
					processes.remove(curr);
				}
			}	
		}else {
			System.out.printf(Message.INVALID_PARAMETER.toString(), "kill "+kill);
		}
	}

}