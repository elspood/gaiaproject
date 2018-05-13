package state;

public enum Building {
	
	MINE('M'), TS('T'), PI('P'), LAB('L'), ACAD('A');
	
	private char letter;
	
	private Building(char letter) {
		this.letter = letter;
	}
	
	public String toString() {
		return letter + "";
	}
}
