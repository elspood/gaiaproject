package state;
import java.util.EnumMap;
import java.util.Random;
import java.util.Vector;

public class Research {
	
	public static final int[] NAVIGATIONRANGE = {1, 1, 2, 2, 3, 4};
	public static final int[] GAIAFORMINGCOST = {Integer.MAX_VALUE, 6, 6, 4, 3, 3};

	private EnumMap<ScienceTrack,AdvTech> advtech;
	private EnumMap<ScienceTrack,TechTile> techtile;
	private TechTile[] wildcard;
	private EnumMap<ScienceTrack,Integer>[] level;
	// TODO: keep valhalla claimed status up to date
	private EnumMap<ScienceTrack,Boolean> valhallataken;
	private FederationToken bonus;
	
	public Research(EnumMap<ScienceTrack,AdvTech> advtech, EnumMap<ScienceTrack,TechTile> techtile, FederationToken bonus,
			int players) {
		this.advtech = advtech;
		this.techtile = techtile;
		int idx = 0;
		wildcard = new TechTile[TechTile.values().length - techtile.size()];
		for (TechTile t : TechTile.values())
			if (!techtile.containsValue(t))
				wildcard[idx++] = t;
		this.bonus = bonus;
		level = new EnumMap[players];
		for (int i=0; i < players; i++) {
			level[i] = new EnumMap<ScienceTrack,Integer>(ScienceTrack.class);
			for (ScienceTrack t : ScienceTrack.values()) level[i].put(t, 0);
		}
		valhallataken = new EnumMap<ScienceTrack,Boolean>(ScienceTrack.class);
		for (ScienceTrack t : ScienceTrack.values()) valhallataken.put(t, false);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (ScienceTrack t : ScienceTrack.values()) {
			String s = t.toString();
			while (s.length() < 8) s += " ";
			sb.append(s + "|");
		}
		sb.append("\n");
		for (ScienceTrack t : ScienceTrack.values()) {
			String s = advtech.get(t).toString();
			while (s.length() < 8) s += " ";
			sb.append(s + "|");
		}
		sb.append("\n");
		for (ScienceTrack t : ScienceTrack.values()) {
			for (int i=0; i < level.length; i++) {
				sb.append(level[i].get(t) + " ");
			}
			sb.append("|");
		}
		sb.append("\n");
		for (ScienceTrack t : ScienceTrack.values()) {
			String s = techtile.get(t).toString();
			while (s.length() < 8) s += " ";
			sb.append(s + "|");
		}
		sb.append("\n           ");
		for (TechTile t : wildcard) sb.append(t + " ");
		sb.append("\n");
		return sb.toString();
	}
	
	public Vector<Income> income(int player) {
		Vector<Income> income = new Vector<Income>();
		int lvl = level[player].get(ScienceTrack.ECON);
		if ((lvl > 0) && (lvl < 5)) {
			income.add(new Income(ResourceType.CHARGE, lvl));
			income.add(new Income(ResourceType.CREDITS, (lvl == 1) ? 2 : lvl));
			if (lvl > 1)
				income.add(new Income(ResourceType.ORE, lvl / 2));
		}
		lvl = level[player].get(ScienceTrack.SCI);
		if ((lvl > 0) && (lvl < 5))
			income.add(new Income(ResourceType.KNOWLEDGE, lvl));
		return income;
	}
	
	public Vector<Income> progress(int player, ScienceTrack track) {
		int lvl = level[player].get(track);
		if (lvl == 5) throw new IllegalStateException("Player " + (player + 1) + " could not advance in " +
				track + ": already at level 5");
		level[player].put(track, ++lvl);
		Vector<Income> income = new Vector<Income>();
		switch (track) {
		case TERRA:
			if ((lvl == 1) || (lvl == 4)) income.add(new Income(ResourceType.ORE, 2));
			else if (lvl == 5) income.add(new Income(ResourceType.FED, bonus.ordinal()));
			break;
		case NAV:
			if ((lvl == 1) || (lvl == 3)) income.add(new Income(ResourceType.QIC, 1));
			else if (lvl == 5) income.add(new Income(ResourceType.LPLANET, 1));
			break;
		case AI:
			int qic = (int)Math.pow(2, (lvl + 1)/2 - 1);
			income.add(new Income(ResourceType.QIC, qic));
			break;
		case GAIA:
			if ((lvl == 1) || (lvl == 3) || (lvl == 4)) income.add(new Income(ResourceType.GF, 1));
			else if (lvl == 2) income.add(new Income(ResourceType.POWER, 3));
			else if (lvl == 5) income.add(new Income(ResourceType.GVP, 1));
			break;
		case ECON:
			if (lvl == 5) {
				income.add(new Income(ResourceType.ORE, 3));
				income.add(new Income(ResourceType.CREDITS, 6));
				income.add(new Income(ResourceType.CHARGE, 6));
			}
			break;
		case SCI:
			if (lvl == 5) income.add(new Income(ResourceType.KNOWLEDGE, 9));
			break;
		}
		if (lvl == 3) income.add(new Income(ResourceType.CHARGE, 3));
		if (lvl == 5) income.add(new Income(ResourceType.FED, -1));
		return income;
	}
	
	public int navigationRange(int player) {
		return NAVIGATIONRANGE[level[player].get(ScienceTrack.NAV)];
	}
	
	public int gaiaformingCost(int player) {
		return GAIAFORMINGCOST[level[player].get(ScienceTrack.GAIA)];
	}
	
	public CanResearch canResearch(int player) {
		boolean canresearchwithfed = false;
		for (ScienceTrack t : ScienceTrack.values()) {
			CanResearch cr = canResearch(player, t);
			if (cr == CanResearch.YES) return cr;
			if (cr == CanResearch.WITHFED) canresearchwithfed = true;
		}
		if (canresearchwithfed) return CanResearch.WITHFED;
		return CanResearch.NO;
	}
	
	public CanResearch canResearch(int player, ScienceTrack track) {
		int lvl = level[player].get(track);
		if (lvl < 4) return CanResearch.YES;
		if ((lvl == 4) && !valhallataken.get(track)) return CanResearch.WITHFED;
		return CanResearch.NO;
	}
	
	public static Research randomize(int players, long seed) {
		Random rand = new Random(seed);
		
		Vector<AdvTech> pool = new Vector<AdvTech>();
		for (AdvTech t : AdvTech.values()) pool.add(t);
		EnumMap<ScienceTrack,AdvTech> advtech = new EnumMap<ScienceTrack,AdvTech>(ScienceTrack.class);
		for (ScienceTrack t : ScienceTrack.values()) {
			int next = rand.nextInt(pool.size());
			advtech.put(t, pool.remove(next));
		}
		
		Vector<TechTile> pool2 = new Vector<TechTile>();
		for (TechTile t : TechTile.values()) pool2.add(t);
		EnumMap<ScienceTrack,TechTile> techtile = new EnumMap<ScienceTrack,TechTile>(ScienceTrack.class);
		for (ScienceTrack t : ScienceTrack.values()) {
			int next = rand.nextInt(pool2.size());
			techtile.put(t, pool2.remove(next));
		}
		
		Vector<FederationToken> pool3 = new Vector<FederationToken>();
		for (FederationToken t : FederationToken.values()) if (t.inResearchPool()) pool3.add(t);
		FederationToken bonus = pool3.get(rand.nextInt(pool3.size()));
		
		return new Research(advtech, techtile, bonus, players);
	}
	
	public static void main(String[] args) {
		Research r = randomize(4, new Random().nextLong());
		for (int i=0; i < 5; i++) {
			Vector<Income> income = r.progress(0, ScienceTrack.AI);
			System.out.print(i + ": ");
			for (Income inc: income) System.out.print(inc + " ");
			System.out.println();
		}
	}
}
