package googy.betterwithenchanting.util;

import net.minecraft.core.entity.player.Player;

public class PlayerUtil {
	private PlayerUtil(){}

	public static int addScore(Player player, int value) {
		boolean isNeg = (value >> 31 & 1) == 1;
		if (isNeg) {
			return PlayerUtil.addScore(player, Integer.MIN_VALUE, value);
		} else {
			return PlayerUtil.addScore(player, Integer.MAX_VALUE ,value);
		}
	}

	public static int subScore(Player player, int value) {
		return PlayerUtil.addScore(player, value);
	}

	private static int addScore(Player player, int boarder, int value) {
		int currentScore = player.getScore();
		if(boarder - value > currentScore){
			player.score += value;
			return value;
		}else{
			player.score = boarder;
			return boarder - currentScore;
		}
	}

	public static void setScore(Player player, int value){
		player.score = value;
	}
}
