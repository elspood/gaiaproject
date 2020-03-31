package state;
import java.util.Random;

public enum FinalScore {
	BUILDFED,
	BUILDINGS,
	UNIQUES,
	GAIA,
	SECTORS,
	SATELLITES;
	
	public static FinalScore[] randomize(long seed) {
		Random rand = new Random(seed);
		
		FinalScore[] vals = FinalScore.values();
		
		int first = rand.nextInt(vals.length);
		int second = first;
		while (second == first) {
			second = rand.nextInt(vals.length);
		}
		FinalScore[] fs = new FinalScore[2];
		fs[0] = vals[first];
		fs[1] = vals[second];
		
		return fs;
	}
}
