package Oving2;

import java.util.ArrayList;

import jade.core.*;

public class TaskAdministrator extends Agent{
	
	private String task;
	private int result;
	private ArrayList<Agent> agents;
	
	public TaskAdministrator() {
		result = 0;
		task = "";
	}
	
	public TaskAdministrator(String task) {
		result = 0;
		this.task = task;
	}
	
	public void setNewTask(String task) {
		this.task = task;
	}
	
	public void decomposeTask() {
		
	}

}
