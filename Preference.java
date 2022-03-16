package finalyearproject;

public class Preference {
	int week;
	boolean granted;
	int day;
	int order;
	int score;
	boolean modelAsHard;
	
		
	public Preference(int week, int day, int order, int bank, boolean modelAsHard) {
		this.week = week;
		this.day = day;
		this.granted = false;
		this.order = order;
		this.score = calculateScore(bank);
		this.modelAsHard = modelAsHard;
	}
	
	public static Preference copy(Preference r) {
		Preference x = new Preference(r.week, r.day, r.order, 0, r.modelAsHard);
		x.score = r.score;
		x.granted = r.granted;
		return x;
	}

	public int calculateScore(int bank) {
		int score = (int)( (Constants.SCORE_MULTIPLIER * (float)bank ) / 10);
		return score;
	}
}