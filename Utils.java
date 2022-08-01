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
			out = out.concat(separator + s);
		}
		return out;
	}

	static String concatLeadingNonzeroes(String[] args, String separator) {
		if (args.length == 0)
			return "";

		String out = args[0];
		if (!nonzero(out))
			return "";

		for ( int i = 1; i < args.length; i++ ) {
			String s = args[i];
			if (!nonzero(s))
				return out;
			out = out.concat(separator + s);
		}
		return out;
	}
	static String concatLeadingNonzeroes(String... args) {
		return concatLeadingNonzeroes(args, " ");
	}

	static String concatTralingNonzeroes(String[] args, String separator) {
		if (args.length == 0)
			return "";

		String out = args[args.length - 1];
		if (!nonzero(out))
			return "";

		for ( int i = args.length-2; i >= 0; i-- ) {
			String s = args[i];
			if (!nonzero(s))
				return out;
			out = s.concat(separator + out);
		}
		return out;
	}
	static String concatTrailingNonzeroes(String... args) {
		return concatTralingNonzeroes(args, ".");
	}
	static String die(Throwable deathRattle) {
		System.err.println(deathRattle);
		System.exit(1);
		return null;
	}
}
