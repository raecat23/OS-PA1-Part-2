package cs131.pa1.filter.concurrent;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import cs131.pa1.filter.Message;

public class ConcurrentCommandBuilder {
	
	public static boolean createFiltersFromCommand(String command, int jobNum) {
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
			ConcurrentFilter filter = constructFilterFromSubCommand(commands[i].trim(), jobNum);
					
			if(filter != null) {
				job.add(filter);
			} else {
				return false;
			}
		}
		
		ConcurrentFilter fin = determineFinalFilter(command, jobNum);
		if(fin == null) {
			return false;
		}

		job.add(fin);
		
		if(linkFilters(job, command) == true ){
			ConcurrentREPL.processes.addAll(job);
			ConcurrentREPL.runNewJobs(job);
			//Adds job list to the list of jobs that are currently running
			return true;
		} else {
			return false;
		}
	}
		
	
	private static ConcurrentFilter determineFinalFilter(String command, int jobNum){
		
		String[] redir = command.split(">");
		if(redir.length == 1) {
			return new PrintFilter(jobNum);
		}
		else {
			try{
				return new RedirectFilter("> " + redir[1], jobNum);
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
	
	private static ConcurrentFilter constructFilterFromSubCommand(String subCommand, int jobNum){
		String[] commandextract = subCommand.split(" ");
		ConcurrentFilter filter;
				
		try {
			switch (commandextract[0]) {
				case "cat":
					filter = new CatFilter(subCommand, jobNum);
					break;
				case "cd":
					filter = new CdFilter(subCommand, jobNum);
					break;
				case "ls":
					filter = new LsFilter(jobNum);
					break;
				case "pwd":
					filter = new PwdFilter(jobNum);
					break;
				case "grep":
					filter = new GrepFilter(subCommand, jobNum);
					break;
				case "wc":
					filter = new WcFilter(jobNum);
					break;
				case "uniq":
					filter = new UniqFilter(jobNum);
					break;
				case ">":
					filter = new RedirectFilter(subCommand, jobNum);
					break;
				/*case "repl_jobs":
					ConcurrentREPL.replJobs();
					filter = null;
					break;
				case "kill":
					ConcurrentREPL.kill(subCommand);
					filter = null;
					break;*/
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