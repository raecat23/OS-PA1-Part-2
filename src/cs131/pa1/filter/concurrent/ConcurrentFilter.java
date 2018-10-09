package cs131.pa1.filter.concurrent;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import cs131.pa1.filter.Filter;


public abstract class ConcurrentFilter extends Filter implements Runnable{
	
	protected LinkedBlockingQueue<String> input;
	protected LinkedBlockingQueue<String> output;
	protected boolean isDone;
	public int jobNum;
	
	public ConcurrentFilter(int job) {
		input = new LinkedBlockingQueue<>();
		output = new LinkedBlockingQueue<>();
		isDone = false;
		this.jobNum = job;
	}
	
	@Override
	public void setPrevFilter(Filter prevFilter) {
		prevFilter.setNextFilter(this);
	}
	
	@Override
	public void setNextFilter(Filter nextFilter) {
		if (nextFilter instanceof ConcurrentFilter){
			ConcurrentFilter sequentialNext = (ConcurrentFilter) nextFilter;
			this.next = sequentialNext;
			sequentialNext.prev = this;
			if (this.output == null){
				this.output = new LinkedBlockingQueue<String>();
			}
			sequentialNext.input = this.output;
		} else {
			throw new RuntimeException("Should not attempt to link dissimilar filter types.");
		}
	}
	
	public Filter getNext() {
		return next;
	}
	
	public void process(){
		while (!this.prev.isDone() || !this.input.isEmpty() ){
			String line;
			try {
				line = input.take();
				String processedLine = processLine(line);
				if (processedLine != null){
					output.offer(processedLine);
				}
			} catch (InterruptedException e) {
				//Shouldn't happen
				break;
			}
			
		}
		if(this.prev.isDone()) {
			isDone = true;
		}
	}
	
	@Override
	public boolean isDone() {
		return isDone;
	}
	
	public void setDone() {
		isDone = true;
	}
	
	protected abstract String processLine(String line);
	
	public void run(){
		process();
	}
}
