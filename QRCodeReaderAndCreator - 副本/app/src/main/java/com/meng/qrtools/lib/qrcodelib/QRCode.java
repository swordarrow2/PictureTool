package com.meng.qrtools.lib.qrcodelib;

/**
 * *          _       _
 * *   __   _(_)_   _(_) __ _ _ __
 * *   \ \ / / \ \ / / |/ _` | '_ \
 * *    \ V /| |\ V /| | (_| | | | |
 * *     \_/ |_| \_/ |_|\__,_|_| |_|
 * <p>
 * Created by vivian on 2016/11/28.
 */

import android.graphics.*;
import com.google.zxing.*;
import com.google.zxing.common.*;
import com.google.zxing.qrcode.*;
import com.google.zxing.qrcode.decoder.*;
import java.io.*;
import java.util.*;

public class QRCode{
    private static int IMAGE_HALFWIDTH = 50;

    /**
     * 生成二维码，默认大小为500*500
     *
     * @param text 需要生成二维码的文字、网址等
     * @return bitmap
     */
    public static Bitmap createQRCode(String text){
        return createQRCode(text,0xff000000,0xffffffff,500);
    }

    /**
     * 生成二维码
     *
     * @param text 文字或网址
     * @param size 生成二维码的大小
     * @return bitmap
     */
    public static Bitmap createQRCode(String text,int true_dot_argb,int false_dot_argb,int size){
        try{
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET,"UTF-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(text,BarcodeFormat.QR_CODE,size,size,hints);
            int[] pixels = new int[bitMatrix.getWidth()*bitMatrix.getHeight()];
            for(int y = 0; y<size; y++){
                for(int x = 0; x<size; x++){
                    if(bitMatrix.get(x,y)){
                        pixels[y*size+x]=true_dot_argb;
                    }else{
                        pixels[y*size+x]=false_dot_argb;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(size,size,
												Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels,0,size,0,0,size,size);
            return bitmap;
        }catch(WriterException e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 生成带logo的二维码
     * @param text
     * @param size
     * @param mBitmap
     * @return
     */
    public static Bitmap createLogoQR(String text,int true_dot_argb,int false_dot_argb,int size,Bitmap mBitmap){
        try{
            IMAGE_HALFWIDTH=size/10;
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET,"utf-8");

            hints.put(EncodeHintType.ERROR_CORRECTION,ErrorCorrectionLevel.H);
            BitMatrix bitMatrix = new QRCodeWriter().encode(text,BarcodeFormat.QR_CODE,size,size,hints);
            //将logo图片按martix设置的信息缩放
            mBitmap=Bitmap.createScaledBitmap(mBitmap,size,size,false);

            int width = bitMatrix.getWidth();//矩阵高度
            int height = bitMatrix.getHeight();//矩阵宽度
            int halfW = width/2;
            int halfH = height/2;

            Matrix m = new Matrix();
            float sx = (float) 2*IMAGE_HALFWIDTH/mBitmap.getWidth();
            float sy = (float) 2*IMAGE_HALFWIDTH /mBitmap.getHeight();
            m.setScale(sx,sy);
            //设置缩放信息
            //将logo图片按martix设置的信息缩放
            mBitmap=Bitmap.createBitmap(mBitmap,0,0,
										mBitmap.getWidth(),mBitmap.getHeight(),m,false);

            int[] pixels = new int[size*size];
            for(int y = 0; y<size; y++){
                for(int x = 0; x<size; x++){
                    if(x>halfW-IMAGE_HALFWIDTH&&
                            x<halfW+IMAGE_HALFWIDTH&&
                            y>halfH-IMAGE_HALFWIDTH&&
                            y<halfH+IMAGE_HALFWIDTH){
                        //该位置用于存放图片信息
                        //记录图片每个像素信息
                        pixels[y*width+x]=mBitmap.getPixel(x-halfW
														   +IMAGE_HALFWIDTH,y-halfH+IMAGE_HALFWIDTH);
                    }else{
                        if(bitMatrix.get(x,y)){
                            pixels[y*size+x]=true_dot_argb;
                        }else{
                            pixels[y*size+x]=false_dot_argb;
                        }
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(size,size,
												Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels,0,size,0,0,size,size);
            return bitmap;
        }catch(WriterException e){
            e.printStackTrace();
            return null;
        }
    }

	/**
	 * 保存bitmap到SD卡
	 * @param bitName 保存的名字
	 * @param mBitmap 图片对像
	 * return 生成压缩图片后的图片路径
	 */

	public static String saveMyBitmap(String bitName,Bitmap mBitmap) throws IOException{
		File f = new File(bitName);
		if(!f.getParentFile().exists()){
			f.getParentFile().mkdirs();
		}
		f.createNewFile();	
		FileOutputStream fOut = null;
		fOut=new FileOutputStream(f);
		mBitmap.compress(Bitmap.CompressFormat.PNG,100,fOut);	
		fOut.flush();	
		fOut.close();
		return f.getAbsolutePath();
	}



}
