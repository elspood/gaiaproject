package state;
import java.util.Random;
import java.util.Vector;

public enum RoundScore {
	TERRA(1),
	SCIENCE(1),
	MINE(1),
	FED(1),
	TS3(1),
	TS4(1),
	GMINE3(1),
	GMINE4(1),
	PIA(2);
	
	private final int count;
	
	RoundScore(int count) {
		this.count = count;
	}
	
	public static RoundScore[] randomize(long seed) {
		Vector<RoundScore> pool = new Vector<RoundScore>();
		for (RoundScore rs : RoundScore.values())
			for (int i=0; i < rs.count; i++)
				pool.add(rs);

		Random rand = new Random(seed);
		RoundScore[] tiles = new RoundScore[6];
		for (int i=0; i < 6; i++) {
			int next = rand.nextInt(pool.size());
			tiles[i] = pool.remove(next);
		}
		
		return tiles;
	}
}
