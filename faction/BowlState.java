package faction;

import java.util.Vector;

import state.Income;
import state.ResourceType;

public class BowlState {

	private int b1;
	private int b2;
	private int b3;
	private int brainstone = 0;
	
	public static final int BRAINSTONEVALUE = 3;
	
	// TODO: handle moving the brainstone to the gaia bowl
	
	public BowlState(boolean taklons, int b1, int b2, int b3) {
		if (taklons) brainstone = 1;
		init(b1, b2, b3);
	}
	
	public BowlState(int b1, int b2, int b3) {
		init(b1, b2, b3);
	}
	
	private void init(int b1, int b2, int b3) {
		this.b1 = b1;
		this.b2 = b2;
		this.b3 = b3;
	}
	
	public int income(Income i) {
		if (i.type() == ResourceType.CHARGE) return charge(i.amount());
		if (i.type() == ResourceType.POWER) {
			newPower(i.amount());
			return 0;
		}
		throw new IllegalArgumentException("Illegal power income type: " + i.type());
	}
	
	/*
	 * returns the maximum new power that could be charged into bowl 3 by burning bowl 2 power
	 */
	public int maxBurnIncome() {
		if (b2 < 2) return 0;
		if (brainstone == 2) return (b2 + 1) / 2 + 2;
		return b2 / 2;
	}
	
	/*
	 * returns the maximum new power that could be charged into bowl 3 by burning bowl 2 power
	 * while maintaining the specified minimum total token reserve
	 */
	public int maxBurnIncome(int mintokenreserve) {
		if (b2 < 2) return 0;
		int tokens = b1 + b2 + b3 + brainstone;
		int reservemax = Math.max(0, (tokens - mintokenreserve)/2);
		int max = (brainstone == 2) ? (b2 + 1) / 2 + 2 : b2 / 2;
		return Math.min(reservemax, max);
	}
	
	public int spendablePower() {
		return b3 + ((brainstone == 3) ? BRAINSTONEVALUE : 0);
	}
	
	public void newPower(int amount) {
		if (amount < 1) throw new IllegalArgumentException("New power must be a positive integer");
		// if the brainstone was burned, it IS LOST FOREVER
		b1 += amount;
	}

	/**
	 * @param amount The amount of power to charge
	 * @return The amount of wasted power, if any
	 */
	public int charge(int amount) {
		if ((amount > 0) && (brainstone == 1)) {
			amount--;
			brainstone = 2;
		}
		int b1charge = Math.min(b1, amount);
		if (b1charge > 0) {
			b1 -= b1charge;
			b2 += b1charge;
			amount -= b1charge;
		}

		if ((amount > 0) && (brainstone == 2)) {
			amount--;
			brainstone = 3;
		}
		int b2charge = Math.min(b2, amount);
		if (b2charge > 0) {
			b2 -= b2charge;
			b3 += b2charge;
			amount -= b2charge;
		}
			
		return amount;
	}
	
	public int b1() {
		return b1;
	}
	
	public int b2() {
		return b2;
	}
	
	public int b3() {
		return b3;
	}
	
	public BowlState clone() {
		BowlState s = new BowlState(b1, b2, b3);
		s.brainstone = brainstone;
		return s;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof BowlState)) return false;
		BowlState s = (BowlState)o;
		return ((b1 == s.b1) && (b2 == s.b2) && (b3 == s.b3) && (brainstone == s.brainstone));
	}
	
	public BowlState[] bowlStates(Vector<Income> powerIncome) {
		if (powerIncome.size() == 0)
			throw new IllegalArgumentException("Must have power income to calculate bowl states");
		Vector<BowlState> states = new Vector<BowlState>();
		
		calculatePossibleStates(states, new boolean[powerIncome.size()], powerIncome, this);
		
		int size = states.size();
		if (size < 1) return null;
		return states.toArray(new BowlState[size]);
	}
	
	private static void calculatePossibleStates(Vector<BowlState> states, boolean[] used, Vector<Income> income, BowlState bowls) {
		int i = 0;
		boolean done = true;
		while (i < used.length) {
			if (!used[i]) {
				done = false;
				BowlState newstate = bowls.clone();
				newstate.income(income.get(i));
				used[i] = true;
				calculatePossibleStates(states, used, income, newstate);
				used[i] = false;
			}
			i++;
		}
		if (done) {
			if (!states.contains(bowls)) states.add(bowls);
		}
	}
	
	public String toString() {
		return b1 + (brainstone == 1 ? "*" : "") + "/" +
				b2 + (brainstone == 2 ? "*" : "") + "/" +
				b3 + (brainstone == 3 ? "*" : "") + " pw";
	}
}
