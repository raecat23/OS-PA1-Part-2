package cs131.pa1.filter.concurrent;

import cs131.pa1.filter.Message;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class ConcurrentREPL {

	static String currentWorkingDirectory;
	static LinkedBlockingQueue<ConcurrentFilter> processes;
	static List<List<ConcurrentFilter>> jobs;
	
	public static void main(String[] args){
		jobs = new LinkedList<>();
		currentWorkingDirectory = System.getProperty("user.dir");
		processes = new LinkedBlockingQueue<>();
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
				if(withBG(command)) {
					String[] temp = command.split(" ");
					if(command.equals("repl_jobs")) {
						replJobs();
					}else if(temp[0].equals("kill")) {
						kill(Integer.parseInt(temp[1]));
					}else {
						ConcurrentCommandBuilder.createFiltersFromCommand(command.trim().substring(0, command.length()-1));
						//Didn't run the processes here since it wouldn't stop for a new command; the last command to be typed that doesn't have an & will 
						//begin the processing 
					}
				}else {
					//building the filters list from the command
					if(ConcurrentCommandBuilder.createFiltersFromCommand(command)) {
						processes.poll().process();
					}		
				}
			}
		}
		s.close();
		System.out.print(Message.GOODBYE);
	}
	
	public static void moveProcess(ConcurrentFilter f){
		if(!f.isDone()) {
			processes.offer(f);//Adds it to the end of the commands again if it still needs to finish
		}
		if(!processes.isEmpty()) {
			processes.poll().process();//makes sure there are more commands and if there are starts the next burst for the next one
		}
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
	
	private static void replJobs() {
		for(int i = 0; i < jobs.size(); i++) {//Starts the new line for each "job" list 
			System.out.print(i+1 + ". ");
			for(int j = 0; j < jobs.get(i).size(); j++) { //Prints each of the jobs (overwrote the toString for filters to print)
				System.out.print(jobs.get(i).get(j).toString()+" ");
				if(j == jobs.get(i).size()-1) {
					System.out.print("&");//we know it's an & at the end becaues repl jobs was called
				}else {
					System.out.print("| ");
				}
			}
			System.out.println();
		}
	}
	
	private static void kill(int kill) {
		if(kill <= jobs.size()) {
			kill--;//Because we need an index it must be decremented
			List<ConcurrentFilter> killJob = jobs.get(kill);
			int size = processes.size();
			for(int i = 0; i < size; i++) {
				ConcurrentFilter curr = processes.poll();
				if(!killJob.contains(curr)) {
					processes.offer(curr);
				}
			}
		}else {
			System.out.printf(Message.INVALID_PARAMETER.toString(), "kill "+kill);
		}
	}

}