import java.util.ArrayList;
import java.util.Random;

public class PkMatch {
	int current = 0, min = 2, max = 8, deaths = 0;
	long lastJoin = 0, maxWait = 30000;
	int lowRating = 1500, highRating = 1500, averageRating = 1500;
	int maxDiff = 150;
	long oldSeconds;
	int pCount = 0;
	public ArrayList<client> players = new ArrayList<client>(),
			losers = new ArrayList<client>();
	boolean playing = false, gameOver = false;

	public PkMatch(client leader) {
		players.add(leader);
		if (leader.rating < lowRating)
			lowRating = leader.rating;
		if (leader.rating > highRating)
			highRating = leader.rating;
		calcAverage();
		lastJoin = System.currentTimeMillis();
		current++;
	}

	public void calcAverage() {
		int total = 0, num = 0;
		for (client temp : players) {
			num++;
			total += temp.rating;
		}
		if (num == 0)
			averageRating = 1500;
		averageRating = (total / num);
	}

	public boolean canStart() {
		if (!playing
				&& (current % 2 == 0)
				&& (oldSeconds != ((maxWait - (System.currentTimeMillis() - lastJoin) % 60000) / 1000))) {
			sendMessage("[Pk Match] Starting in "
					+ ((maxWait - (System.currentTimeMillis() - lastJoin) % 60000) / 1000)
					+ "seconds.");
			oldSeconds = ((maxWait - (System.currentTimeMillis() - lastJoin) % 60000) / 1000);
		}

		if (!playing && ((System.currentTimeMillis() - lastJoin) >= (maxWait))
				&& (current % 2 == 0))
			return true;

		return false;
	}

	public String getStatus(client p) {
		if (!playing) {
			return "Current players " + current + "/" + min + "";
		} else {
			if (p.matchLives < 0)
				p.matchLives = 0;
			return "Playing rated game (" + p.matchLives + " lives)";
		}
	}

	public boolean hasSpace() {
		if (current < max)
			return true;
		return false;
	}

	public void join(client p) {
		players.add(p);
		calcAverage();
		current++;
		lastJoin = System.currentTimeMillis();
		if (current == 2) {
			sendMessage("Minimum players reached: waiting for extra players...");
		}
	}

	public void notifyDeath(client p) {
		if (p.deathNum > 0)
			return;
		deaths++;
		p.deathNum = deaths;
	}

	public void sendMessage(String message) {
		for (client p : players) {
			if (p == null)
				continue;
			p.sendMessage(message);
		}
	}

	public void start() {
		Random r = new Random();
		for (client p : players) {
			playing = true;
			p.teleportToX = 3105 + r.nextInt(10);
			p.teleportToY = 3933 + r.nextInt(10);
		}
		sendMessage("!!!!!!!!!!!!! [PK Match Began] !!!!!!!!!!!!!");
	}

	public void update() {
		boolean allDead = true;
		int total = players.toArray().length;
		int dead = 0;
		for (client p : players) {
			if ((p == null) || p.disconnected) {
				dead++;
				continue;
			}
			if (p.matchLives > 0) {
				allDead = false;
			} else {
				dead++;
			}
		}
		if (dead + 1 == total) {
			gameOver = true;
		}
		if (gameOver) {
			for (client p2 : players) {
				p2.sendMessage("Game over!");
				p2.teleportToX = 2606;
				p2.teleportToY = 3102;
				p2.ResetAttack();
				p2.matchId = -1;
				p2.matchLives = 2;
				if (p2.deathNum <= (current / 2)) {
					p2.rating -= 15;
				} else {
					p2.rating += 15;
				}
				p2.updateRating();
				p2.deathNum = 0;
			}
		}

	}

	public boolean willAccept(client p) {
		if (Math.abs(p.rating - averageRating) <= maxDiff) {
			return true;
		} else {
			p
					.sendMessage("Sorry! they are way too in pk rating good/bad you can't join");
			p.sendMessage("Try again! type ::pking");
		}
		return false;
	}
}