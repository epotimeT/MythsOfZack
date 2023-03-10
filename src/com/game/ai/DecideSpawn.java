package com.game.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.game.entities.GenericMonster;
import com.game.entities.RockProjectile;

public class DecideSpawn {
	static Slime slime1 = new Slime(slime1stats());
	static Slime slime2 = new Slime(slime2stats());
	static RockSpitter spitter1 = new RockSpitter(rockspitter1stats(), new RockProjectile(2, 50f));

	/**
	 * 
	 * @param monsters
	 * @param distro   needs to add to 100% to work!
	 * @return
	 */
	public static GenericMonster chooseMonster(ArrayList<GenericMonster> monsters, ArrayList<Integer> distro) {
		if (monsters.size() != distro.size()) {
			return null;
		}
		Random d100 = new Random();
		// 0 - 99
		int roll = d100.nextInt(100);
		int index = -1;
		int cumulFreq = 0;
		while (cumulFreq <= roll) {
			index += 1;
			cumulFreq += distro.get(index);
		}
		return monsters.get(index);
	}

	public static ArrayList<GenericMonster> slimeList() {
		ArrayList<GenericMonster> list = new ArrayList<>();
		list.add(slime1);
		return list;
	}

	public static ArrayList<Integer> slimeDistro() {
		ArrayList<Integer> distro = new ArrayList<>();
		distro.add(100);
		return distro;
	}

	public static ArrayList<GenericMonster> rockSpitterList() {
		ArrayList<GenericMonster> list = new ArrayList<>();
		list.add(spitter1);
		return list;
	}

	public static ArrayList<Integer> rockSpitterDistro() {
		ArrayList<Integer> distro = new ArrayList<>();
		distro.add(100);
		return distro;
	}

	public static ArrayList<GenericMonster> easyList() {
		ArrayList<GenericMonster> list = new ArrayList<>();
		list.add(slime1);
		list.add(slime2);
		return list;
	}

	public static ArrayList<GenericMonster> mediumList() {
		ArrayList<GenericMonster> list = new ArrayList<>();
		list.add(slime1);
		list.add(slime2);
		list.add(spitter1);
		return list;
	}

	public static ArrayList<Integer> mediumDistro() {
		ArrayList<Integer> distro = new ArrayList<>();
		distro.add(20);
		distro.add(40);
		distro.add(40);
		return distro;
	}

	public static ArrayList<Integer> easyDistro() {
		ArrayList<Integer> distro = new ArrayList<>();
		distro.add(60);
		distro.add(40);
		return distro;
	}

	private static List<Float> slime1stats() {
		List<Float> stats = Arrays.asList(100f, 50f, 0f, 0f, 0f, 0.2f, 0.8f);
		return stats;
	}

	private static List<Float> slime2stats() {
		List<Float> stats = Arrays.asList(200f, 50f, 0f, 0f, 0f, 0.5f, 1.0f);
		return stats;
	}

	private static List<Float> rockspitter1stats() {
		List<Float> stats = Arrays.asList(50f, 10f, 0f, 0f, 0f, 0.5f, 1f);
		return stats;
	}

	private static List<Float> rockspitter2stats() {
		List<Float> stats = Arrays.asList(300f, 10f, 0f, 0f, 0f, 0.2f, 0.6f);
		return stats;
	}

}
