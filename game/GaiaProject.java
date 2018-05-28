package game;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import faction.Faction;
import faction.HadschHallas;
import faction.Taklons;
import faction.Terran;
import faction.Xenos;
import state.Action;
import state.ActionType;
import state.FinalScore;
import state.GameState;
import state.Map;
import state.Research;
import state.RoundBooster;
import state.RoundScore;

public class GaiaProject {
	
	private static void usage() {
		System.err.println("Usage: GaiaProject players [seed]");
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			usage();
			return;
		}
		int players = 0;
		try {
			players = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			usage();
			return;
		}
		Random rand = new Random();
		long seed = rand.nextLong();
		if (args.length > 1) {
			try {
				seed = Long.parseLong(args[0]);
				System.out.println("Randomizer initialized with seed " + seed);
			} catch (NumberFormatException e) {
				usage();
				return;
			}
		}
		
		Map map = new Map();
		RoundScore[] rs = RoundScore.randomize(seed);
		FinalScore[] fs = FinalScore.randomize(seed);
		Vector<RoundBooster> boosters = RoundBooster.randomize(seed, players);
		Research tech = Research.randomize(players, seed);
		/*
		Beginner map conditions:
		HADSCH HALLAS:	H7, L4
		TAKLONS:		H6, R6
		TERRAN:			E3, M8
		XENOS:			J2, N8, Q5
		*/
		
		GameState state = new GameState(4, map, rs, fs, boosters, tech);

		Action[] choices;
		Scanner in = new Scanner(System.in);
		while ((choices = state.getActionChoices()) != null) {
			System.out.println(state);
			ActionType type = null;
			int length = 0;
			for (int i=0; i < choices.length; i++) {
				if (choices[i].type() != type) {
					type = choices[i].type();
					System.out.print("\n" + type + ":");
					length = 0;
				}
				if (length > 60) {
					System.out.print("\n ");
					for (int j=0; j < type.toString().length(); j++) System.out.print(" ");
					length = 0;
				}
				String text = choices[i].text();
				System.out.print(" [" + i + "] " + text);
				length += text.length();
			}
			System.out.println();
			while (true) {
				System.out.print(state.playerDisplayName(choices[0].player()) + " action choice: ");
				try {
					String line = in.nextLine();
					int choice = (line.length() == 0) ? 0 : Integer.parseInt(line);
					state.takeAction(choices[choice]);
					break;
				} catch (ArrayIndexOutOfBoundsException e) {}
				catch (InputMismatchException e) {}
				catch (NoSuchElementException e) {}
				catch (NumberFormatException e) {}
			}
		}
		in.close();
		
		System.out.println(state);
		System.out.println("Game over");
	}

}
