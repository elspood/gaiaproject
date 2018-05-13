package state;
public class Bank {
	
	public static final int MAXORE = 15;
	public static final int MAXCREDITS = 30;
	public static final int MAXKNOWLEDGE = 15;

	private int ore = 4;
	private int credits = 15;
	private int knowledge = 3;
	private int gaiaformers = 0;
	private int qic = 1;
	private int vp = 10;
	private int b1 = 2;
	private int b2 = 4;
	private int b3 = 0;
	private int bg = 0;
	
	private FederationToken[] feds = new FederationToken[10];
	private boolean[] fedused = new boolean[10];
	
	public Bank() {
		
	}
	
	public Bank(Income[] nonstandard) {
		for (Income i : nonstandard) income(i);
	}
	
	private void gainFederation(int id) {
		FederationToken fed = FederationToken.values()[id];
		int i = 0;
		while (feds[i] != null) i++;
		feds[i] = fed;
		fedused[i] = !fed.spendable();
		for (Income inc : fed.income()) income(inc);
	}
	
	public void income(Income i) {
		switch (i.type()) {
		case ORE: ore = Math.min(ore + i.amount(), MAXORE); break;
		case CREDITS: credits = Math.min(credits + i.amount(), MAXCREDITS); break;
		case KNOWLEDGE: knowledge = Math.min(knowledge + i.amount(), MAXORE); break;
		case VP: vp += i.amount(); break;
		case QIC: qic += i.amount(); break;
		case GF: gaiaformers += i.amount(); break;
		case FED: gainFederation (i.amount()); break;
		case B1POWER: b1 += i.amount(); break;
		case B2POWER: b2 += i.amount(); break;
		default: throw new IllegalArgumentException("Unhandled bank income for " + i.type());
		}
	}
}
