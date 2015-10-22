package com.airisith.modle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 将本地图片转为Bitmap，在转为二进制数组
 * @author Administrator
 *
 */
public class OpenImageFile {
	
	private static String filePath = null; 
	public OpenImageFile(String filePath){
		this.filePath = filePath;
	}
	
	public static byte[] getImageBytes(String filePath){

		byte[] imgBuf = null;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath);
		imgBuf = getBitmapByte(bitmap);
	
		return imgBuf;
	}
	
	public static byte[] getImageBytes(){
		byte[] imgBuf = null;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath);
		imgBuf = getBitmapByte(bitmap);
		return imgBuf;
	}
	
	public static byte[] getBitmapByte(Bitmap bitmap){  
	    ByteArrayOutputStream out = new ByteArrayOutputStream();  
	    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);  
	    try {  
	        out.flush();  
	        out.close();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  
	    return out.toByteArray();  
	}
}
