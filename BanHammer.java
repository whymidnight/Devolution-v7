import java.io.DataInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class BanHammer {
	public static ArrayList<String> ips = new ArrayList<String>();

	public static boolean found(String host) {
		for (String h : ips) {
			if (host.equals(h)) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		// System.out.println("Done!");
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec("/bin/sh ./ban " + args[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String openPage(String pageName) {
		try {
			URL page = new URL(pageName);
			URLConnection conn = page.openConnection();
			DataInputStream in = new DataInputStream(conn.getInputStream());
			String source, pageSource = "";
			while ((source = in.readLine()) != null) {
				if (source.indexOf("Rejected") > 0) {
					String[] parts = source.split(" ");
					String ip = parts[2];
					ip = ip.substring(0, ip.indexOf(":"));
					if (!found(ip)) {
						Runtime rt = Runtime.getRuntime();
						Process proc = rt
								.exec("/bin/sh /var/www/bin/ban " + ip);
						System.out.println("Banning host " + ip);
						ips.add(ip);
					}
				}
			}
			return pageSource;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
