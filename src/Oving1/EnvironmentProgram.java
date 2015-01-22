package Oving1;

public class EnvironmentProgram {
	
	private Environment environment;
	
	public void init() {
		environment = new Environment();
	}
	
	public void run() {
		environment.playNRounds(10);
		environment.printFScores(10);
		environment.resetEnvironment();
		environment.playNRounds(20);
		environment.printFScores(20);
		environment.resetEnvironment();
		environment.playNRounds(30);
		environment.printFScores(30);
	}
	
	public static void main(String[] args) {
		EnvironmentProgram program = new EnvironmentProgram();
		program.init();
		program.run();
	}

}
