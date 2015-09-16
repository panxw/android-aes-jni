package com.panxw.aes.jni.test;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.os.Bundle;

import com.panxw.aes.jni.R;
import com.panxw.aes.jni.SecurityUtils;

public class MainActivity extends Activity {
	public static final String TESTDATA="kLNyk5O9jj0kG/lqskHCLs7HQttQjqMwNToSXGVs7WraXf0bVpBA1vaE+30Mz2wu/6dmmU6mHOVAye+w9zrgZswPAjqEtU8nAa7+z5RDeil/5kBEEnV/IVO+Xry6YO4AL6xuHm/6k32zn6C8R2ZCvNL/vvUbk49YH5MEyCU/9See8Y8hqM9jPTmGc9+izcIjZtkMnC1PfShwvgdtE5gkkBqVJx20bnjyzEEPIb3dxt/DlhmbnpBeC6GWzCHjzvdLcC3mfYHoP6+A1r+oXjDxGFfKIDgtwaUZfzAKhlpsx9gOn7e2CaC85Nyu2Xy1vjTBlJiwN1EPvI87nQrWWqOBDyRRzhlbc+f2pEfZ6yIQKXnR7QKLKptxnD3jcKuH5r2l82b1Q3OSFTCYRCzYtA/CYbdJq4gRxx8bFwSeqmxtYy0=kLNyk5O9jj0kG/lqskHCLs7HQttQjqMwNToSXGVs7WraXf0bVpBA1vaE+30Mz2wu/6dmmU6mHOVAye+w9zrgZswPAjqEtU8nAa7+z5RDeil/5kBEEnV/IVO+Xry6YO4AL6xuHm/6k32zn6C8R2ZCvNL/vvUbk49YH5MEyCU/9See8Y8hqM9jPTmGc9+izcIjZtkMnC1PfShwvgdtE5gkkBqVJx20bnjyzEEPIb3dxt/DlhmbnpBeC6GWzCHjzvdLcC3mfYHoP6+A1r+oXjDxGFfKIDgtwaUZfzAKhlpsx9gOn7e2CaC85Nyu2Xy1vjTBlJiwN1EPvI87nQrWWqOBDyRRzhlbc+f2pEfZ6yIQKXnR7QKLKptxnD3jcKuH5r2l82b1Q3OSFTCYRCzYtA/CYbdJq4gRxx8bFwSeqmxtYy0=";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		try {
			byte[] data = TESTDATA.getBytes("UTF-8");
			data=SecurityUtils.crypt(data, System.currentTimeMillis(), 0);
			String hexStr = SecurityUtils.bytes2HexStr(data);
			System.out.println("encrypt:"+hexStr);
			
			data = SecurityUtils.hexStr2Bytes(hexStr);
			data=SecurityUtils.crypt(data, System.currentTimeMillis(), 1);
			System.out.println("decrypt:"+new String(data,"UTF-8"));
			
			data = SecurityUtils.read("/mnt/sdcard/test.txt", System.currentTimeMillis());
			System.out.println("read:"+new String(data,"UTF-8"));
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
