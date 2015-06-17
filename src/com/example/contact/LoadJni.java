package com.example.contact;


public class LoadJni {

	static 
	{
		System.loadLibrary("hellojni");
	}
	public static native String getResult();
}
