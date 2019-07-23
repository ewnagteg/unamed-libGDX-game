print("making world");
try {
	if (args.length == 3) {
		db.setWorld(args[1]);
		db.connect();
		db.cleanDB();
		db.conf();
		db.makeDB();
		db.addSeed(args[2]);
		db.close();
	} else {
		print("Error: Too many or too few arguments");
	}
} catch(err) {
	print("Caught error");
	print(err.message);
}
