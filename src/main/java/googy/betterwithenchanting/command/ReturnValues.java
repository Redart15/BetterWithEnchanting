package googy.betterwithenchanting.command;

public enum ReturnValues {
	CANNOT(-2),
	FAIL(-1),
	OK(0);
	private final int code;

	ReturnValues(int code) {
		this.code = code;
	}

	public static int code(ReturnValues v) {
		return v.code;
	}
}
