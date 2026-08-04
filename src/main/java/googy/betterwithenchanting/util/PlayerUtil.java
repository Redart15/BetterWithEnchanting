package googy.betterwithenchanting.util;

import net.minecraft.core.entity.player.Player;

public class PlayerUtil {
	private PlayerUtil(){}

	public static int addScore(Player player, int value) {
		int currentScore = player.getScore();
		if(currentScore > Integer.MAX_VALUE - value){
			player.score = Integer.MAX_VALUE;
			return Integer.MAX_VALUE - currentScore;
		}
		player.score += value;
		return value;
	}

	public static int subScore(Player player, int value) {
		int currentScore = player.getScore();
		if(currentScore < Integer.MIN_VALUE + value){
			player.score = Integer.MIN_VALUE;
			return currentScore - Integer.MIN_VALUE + 1;
		}
		player.score -= value;
		return value;
	}

	public static void setScore(Player player, int value){
		player.score = value;
	}
}
