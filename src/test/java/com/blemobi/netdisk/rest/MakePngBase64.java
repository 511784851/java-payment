package com.blemobi.netdisk.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.blemobi.payment.util.Base64;

public class MakePngBase64 {

	public static void main(String[] args) throws IOException {
		File file = new File("E:/Temp/mini.png");
		byte[] data = new byte[(int) file.length()];
		FileInputStream fis = new FileInputStream(file);
		fis.read(data);
		String base64 = Base64.encode(data);
		System.out.println("["+base64+"]");
		File file2 = new File("E:/Temp/mini.base64");
		java.io.FileOutputStream fos = new FileOutputStream(file2);
		fos.write(base64.getBytes());
		System.out.println("finish!");
	}

}
