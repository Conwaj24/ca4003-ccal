class Utils {
		static boolean nonzero(String s) {
		try {
			return !s.isEmpty();
		} catch (java.lang.NullPointerException e) {
			return false;
		}
	}
	static String concatIfAllNonzero(String... args) {
		return concatIfAllNonzero(args, " ");
	}
	static String concatIfAllNonzero(String[] args, String separator) {
		if (args.length == 0)
			return "";

		String out = args[0];
		if (!nonzero(out))
			return "";

		for ( int i = 1; i < args.length; i++ ) {
			String s = args[i];
			if (!nonzero(s))
				return "";
			out = out.concat(separator);
			out = out.concat(s);
		}
		return out;
	}
}
