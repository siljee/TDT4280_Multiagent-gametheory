package oving3;

import java.util.ArrayList;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public abstract class GeneralAgent extends Agent implements Constants {
	private GlobalItems globalItems;
	private ArrayList<Item> inventoryList;
	private ArrayList<Item> desiredList;
	private int money;
	private String agentType = AGENT_TYPE;
	
	public void setup() {
		System.out.println("Agent " + getLocalName() + " started");
		registerToDF();
		
		// Initialize member variables:
		globalItems = GlobalItems.getInstance();
		money = START_MONEY;
		inventoryList = globalItems.makeInventoryList();
		
		addBehaviour(new sellerBehaviour());
		addBehaviour(new DesiredItemBehaviour());
		
		
		
		// for each desired product in desiredlist. make a new buyer behaviour.
				// each byer thread tries to buy one item or exchange with something it has 
				// need to check if it has enough money. 
				// will always exchange except if the exchanged item is on the agents wishlist
			
			
			// also invoke one thread that constantly listens for sellers
		
	}
	
	/**
	 * Register agent at the DF. All agents in this project are and do the same and therefore 
	 * all agents have the same type defined in the constant AGENT_TYPE.
	 * Agents discover what other agents offer by communicating with them, so this should not be specified.
	 */
	private void registerToDF() {
		// Set Agents ID to a new DF Description object.
		DFAgentDescription dfDescription = new DFAgentDescription();
		dfDescription.setName(getAID());
		
		// Set the agents service, with type as AGENT_TYPE, and add it to dfDescription
		ServiceDescription serviceDescription  = new ServiceDescription();
		serviceDescription.setName(getLocalName());
		serviceDescription.setType(agentType);
		dfDescription.addServices(serviceDescription);
		
		// Register the dfDescription at the DF.
		try {
			DFService.register(this, dfDescription);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
	
	private class DesiredItemBehaviour extends Behaviour {
		boolean isDone = false;

		@Override
		public void action() {
			if (globalItems.getInventoryAgentsCount() >= NUMBER_OF_AGENTS) {
				desiredList = globalItems.makeDesiredList();
			
				for (int i = 0; i < desiredList.size(); i++) {
					addBehaviour(new BuyBehaviour(desiredList.get(i)));
				}
				isDone = true;
			} else {
				// Sleep for a short time to avoid it running many times in vain.
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public boolean done() {
			return isDone;
		}
			
		
	}
	
	private class BuyBehaviour extends Behaviour {
		private int step = 0;
		private Item desiredItem;
		private Agent [] agents;
		private Agent chosenAgent;
		
		public BuyBehaviour(Item desiredItem) {
			super();
			this.desiredItem = desiredItem;
		}
		
		@Override
		public void action() {
			switch (step) {
			case 0: 			// Runs once
				// Find all agents from DF
				
				step = 1;
				
			case 1:				// Runs once
				// send message(CFP?) to all agents in DF asking for the desired item
				// !!! Except of itself
			
				// prepare template to get proposals
				
				step = 2;
				break;
				
			case 2:				// Runs until all agents have answered
				int replyCounter = 0;
				// receive all proposal/refusal from other agents
				
				// choose one proposal(chosenAgent) to negotiate with.
				
				// When all agents replied we move to the next step
				if (replyCounter >= agents.length - 1) {
					step = 3;
				}
			case 3:			// Runs until negotiation is finished
				// negotiate with the chosen agent
				// SPECIALISED AGENT NEGOTIATIO FUNCTION COMES HERE
				
				// When negotiation finished: send the item
				// remember two different messages: either buying or exchanging
				
				// prepare template to get the purchased order reply
				
			case 4: 
				// Receive purchase order reply.
				// The deal is finished
				// update everything (remove from desired and add to inventory. 
				// reduce money or remove exchanged item from inventory. )
				
				// step = 5
			}
		}

		@Override
		public boolean done() {
			// Stop behaviour if no agent hold the desired item or if all steps are executed
			if (step == 3 && chosenAgent == null) {
				System.out.println("No agents hold the item: " + desiredItem.getName());
			}
			return (step == 5 || (step == 3 && chosenAgent == null));
		}
		
	}
	
	private class sellerBehaviour extends CyclicBehaviour {

		@Override
		public void action() {
			// TODO Auto-generated method stub
			
		}
		
	}
}
