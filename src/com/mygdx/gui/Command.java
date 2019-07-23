package com.mygdx.gui;

public class Command {
	private String[] comms;
	private int i;
	private String options;

	public Command(String text) {
		comms = text.split("\\s+");
		i = 0;
	}

	public String next() {
		i++;
		if (comms[i - 1].charAt(0) == '-') {
			options = comms[i - 1];
			return next();
		}
		return comms[i - 1];
	}

	public boolean option(String opt) {
		return options.contains(opt);
	}

	public boolean hasOptions() {
		boolean r = false;
		for (String h : comms) {
			if (h.charAt(0) == '-') {
				r = true;
			}
		}
		return r;
	}
}
