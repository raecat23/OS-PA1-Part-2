package cs131.pa1.filter.concurrent;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import cs131.pa1.filter.Message;

public class ConcurrentCommandBuilder {
	
	public static boolean createFiltersFromCommand(String command) {
		//adding whitespace so that string splitting doesn't bug
		command = " " + command + " ";
		
		List<ConcurrentFilter> job = new LinkedList<>();
		
		//removing the final filter here
		String truncCommand = adjustCommandToRemoveFinalFilter(command);
		
		if(truncCommand == null) {
			return false;
		}
		
		//for all the commands, split them by pipes, construct each filter, and add them to the filters list.
		String[] commands = truncCommand.split("\\|");
				
		for(int i = 0; i < commands.length; i++) {
			ConcurrentFilter filter = constructFilterFromSubCommand(commands[i].trim());
					
			if(filter != null) {
				//Adds to it's individual job list as well as the overall queue
				job.add(filter);
				ConcurrentREPL.processes.offer(filter);
			} else {
				return false;
			}
		}
		
		ConcurrentFilter fin = determineFinalFilter(command);
		if(fin == null) {
			return false;
		}
		//Adds to it's individual job list as well as the overall queue
		job.add(fin);
		ConcurrentREPL.processes.offer(fin);
		
		if(linkFilters(job, command) == true && !ConcurrentREPL.processes.isEmpty()){
			ConcurrentREPL.jobs.add(job);
			//Adds job list to the array of job lists that are currently running
			return true;
		} else {
			return false;
		}
	}
		
	
	private static ConcurrentFilter determineFinalFilter(String command){
		String[] redir = command.split(">");
		if(redir.length == 1) {
			return new PrintFilter();
		} else {
			try{
				return new RedirectFilter("> " + redir[1]);
			} catch (Exception e) {
				return null;
			}
		}
	}
	
	private static String adjustCommandToRemoveFinalFilter(String command){
		String[] removeRedir = command.split(">");
		
		//checking for error cases here. If there is a redirection...
		if(removeRedir.length > 1) {
			//if the redirection does not have an input, then output an error
			if(removeRedir[0].trim().equals("")) {
				System.out.printf(Message.REQUIRES_INPUT.toString(), (">" + removeRedir[1]).trim());
				return null;
			}
			//if redirection is attempted to be piped, output an error
			if(removeRedir[1].contains("|")) {
				System.out.printf(Message.CANNOT_HAVE_OUTPUT.toString(), ">" + removeRedir[1].substring(0, removeRedir[1].indexOf("|")));
				return null;
			}
			//if multiple redirections are in the command, output an error
			if(removeRedir.length > 2) {
				System.out.printf(Message.CANNOT_HAVE_OUTPUT.toString(), removeRedir[1].trim());
				return null;
			}
		}
		return removeRedir[0];
	}
	
	private static ConcurrentFilter constructFilterFromSubCommand(String subCommand){
		String[] commandextract = subCommand.split(" ");
		ConcurrentFilter filter;
				
		try {
			switch (commandextract[0]) {
				case "cat":
					filter = new CatFilter(subCommand);
					break;
				case "cd":
					filter = new CdFilter(subCommand);
					break;
				case "ls":
					filter = new LsFilter();
					break;
				case "pwd":
					filter = new PwdFilter();
					break;
				case "grep":
					filter = new GrepFilter(subCommand);
					break;
				case "wc":
					filter = new WcFilter();
					break;
				case "uniq":
					filter = new UniqFilter();
					break;
				default:
					System.out.printf(Message.COMMAND_NOT_FOUND.toString(), subCommand);
					return null;
			}
		} catch (Exception e) {
			return null;
		}
		return filter;
	}

	private static boolean linkFilters(List<ConcurrentFilter> filters, String command){
		Iterator<ConcurrentFilter> iter = filters.iterator();
		ConcurrentFilter prev;
		ConcurrentFilter curr = iter.next();
		String[] cmdlist = command.split("\\|");	//command is brought in so we can output proper error messages
		int cmdindex = 0;
				
		//check to make sure grep and wc are not the first filters
		if(curr instanceof GrepFilter || curr instanceof WcFilter) {
			System.out.printf(Message.REQUIRES_INPUT.toString(),cmdlist[cmdindex].trim());
			return false;
		}
		
		while(iter.hasNext()) {
			prev = curr;
			curr = iter.next();
			cmdindex++;
			
			//additional checks
			if(curr instanceof CdFilter || curr instanceof CatFilter || curr instanceof LsFilter || curr instanceof PwdFilter) {
				System.out.printf(Message.CANNOT_HAVE_INPUT.toString(), cmdlist[cmdindex].trim());
				return false;
			}
			if(prev instanceof CdFilter && !(curr instanceof PrintFilter)) {
				System.out.printf(Message.CANNOT_HAVE_OUTPUT.toString(), cmdlist[cmdindex-1].trim());
				return false;
			}
			
			prev.setNextFilter(curr);
		}
		return true;
	}
}
