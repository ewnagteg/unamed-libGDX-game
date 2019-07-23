try {
	if (args.length == 3) {
		if (args[1] == "port") {
			print("setting port to: " + args[2]);
			wrapper.setPort(args[2]);
		} else if (args[1] == "world") {
			print("setting world to: " + args[2]);
			wrapper.setWorld(args[2]);
		}
	} else {
		print("Error: To few arguments");
	}
} catch(err) {
	print("Caught error");
	print(err.message);
}