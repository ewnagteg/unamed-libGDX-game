try {
	print("running startup script..");
	if (args.length == 2) {
		// args is binded from "String[] args" in java main method
		wrapper.setWorld(args[0]);
		print("set world to: " + args[0]);
		wrapper.setPort(args[1]);
		print("set port to: " + args[1]);
	} else if (args.length == 1) {
		wrapper.setWorld(args[0]);
		print("set world to: " + args[0]);
	}
} catch(err) {
	print("Caught error");
	print(err.message);
}