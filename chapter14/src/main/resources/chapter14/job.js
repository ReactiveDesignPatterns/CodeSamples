/*
 * Copyright (c) 2018 https://www.reactivedesignpatterns.com/
 * Copyright (c) 2018 https:rdp.reactiveplatform.xyz/
 *
 */
var PartSuccess = Java.type(
		'chapter14.ComplexCommand.PartSuccess');

var process = function(input) {
	// 'input' is a Java 8 Stream
	var value = input.count();
	return new PartSuccess(value);
}