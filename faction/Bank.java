package faction;

import java.util.Vector;

import state.FederationToken;
import state.Income;

public class Bank {
	
	public static final int MAXORE = 15;
	public static final int MAXCREDITS = 30;
	public static final int MAXKNOWLEDGE = 15;

	private int ore = 4;
	private int credits = 15;
	private int knowledge = 3;
	private int qic = 1;
	private int vp = 10;
	private BowlState bowls = new BowlState(2, 4, 0);
	private int gaiabowl = 0;
	
	int federations = 0;
	private FederationToken[] feds = new FederationToken[10];
	int unusedfeds = 0;
	private boolean[] fedused = new boolean[10];
	
	public Bank() {
		
	}
	
	public Bank(Income[] nonstandard) {
		for (Income i : nonstandard) income(i);
	}
	
	public Bank(BowlState nonstandard) {
		bowls = nonstandard;
	}
	
	public void setPower(BowlState power) {
		bowls = power;
	}
	
	private void gainFederation(int id) {
		FederationToken fed = FederationToken.values()[id];
		feds[federations] = fed;
		fedused[federations] = !fed.spendable();
		if (!fedused[federations]) unusedfeds++;
		for (Income inc : fed.income()) income(inc);
		federations++;
	}
	
	public int federations() {
		return federations;
	}
	
	public int emptyGaiaBowl() {
		int power = gaiabowl;
		gaiabowl = 0;
		return power;
	}
	
	/**
	 * @param powerIncome A vector of atomic power income actions (charge and new tokens)
	 * @return A vector of possible bowl states after charging in various orders;
	 * 			If only one state is possible, the bank is updated with the new state and null is returned
	 */
	public BowlState[] powerIncome(Vector<Income> powerIncome) {
		if (powerIncome.size() == 0) return null;
		BowlState[] states = bowls.bowlStates(powerIncome);
		if (states.length > 1) return states;
		if (states.length == 1) {
			bowls = states[0];
		}
		return null;
	}
	
	public void income(Income i) {
		switch (i.type()) {
		case ORE: ore = Math.min(ore + i.amount(), MAXORE); break;
		case CREDITS: credits = Math.min(credits + i.amount(), MAXCREDITS); break;
		case KNOWLEDGE: knowledge = Math.min(knowledge + i.amount(), MAXORE); break;
		case VP: vp += i.amount(); break;
		case QIC: qic += i.amount(); break;
		case FED: gainFederation(i.amount()); break;
		case POWER: bowls.newPower(i.amount()); break;
		default: throw new IllegalArgumentException("Unhandled bank income for " + i.type());
		}
	}
	
	public int credits() {
		return credits;
	}
	
	public int ore() {
		return ore;
	}
	
	public int qic() {
		return qic;
	}
	
	public int knowledge() {
		return knowledge;
	}
	
	public int gaiabowl() {
		return gaiabowl;
	}
	
	public int maxBurnIncome() {
		return bowls.maxBurnIncome();
	}
	
	public int spendablePower() {
		return bowls.spendablePower();
	}
	
	public int unusedFederations() {
		// TODO: make sure used federation tile tracker is kept current
		return unusedfeds;
	}
	
	public String toString() {
		String s = "VP: " + vp + " " + credits + "c " + ore + "o " + knowledge + "K " + qic + "q " +
				bowls + " " + gaiabowl + "\nFederations: ";
		for (int i=0; i < feds.length; i++) {
			if (feds[i] == null) break;
			s += feds[i];
		}
		s += "\n";
		return s;
	}
}
