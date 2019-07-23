try {
	print("starting server...");
	wrapper.setToServer();
} catch(err) {
	print("Caught error");
	print(err.message);
}