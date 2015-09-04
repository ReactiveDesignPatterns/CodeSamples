var PartSuccess = Java.type(
		'com.reactivedesignpatterns.chapter13.ComplexCommand.PartSuccess');

var process = function(input) {
	// 'input' is a Java 8 Stream
	var value = input.count();
	return new PartSuccess(value);
}