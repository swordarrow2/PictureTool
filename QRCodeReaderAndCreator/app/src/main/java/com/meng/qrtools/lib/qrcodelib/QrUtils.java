package com.meng.qrtools.lib.qrcodelib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.aztec.encoder.Encoder;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by xingli on 12/25/15.
 * https://github.com/iluhcm/QrCodeScanner/blob/master/app/src/main/java/com/kaola/qrcodescanner/qrcode/utils/QrUtils.java
 * 二维码相关功能类
 */
public class QrUtils{
    private static byte[] yuvs;
    private static int IMAGE_HALFWIDTH=50;

    /**
     * YUV420sp
     *
     * @param inputWidth
     * @param inputHeight
     * @param scaled
     * @return
     */
    public static byte[] getYUV420sp(int inputWidth,int inputHeight,Bitmap scaled){
        int[] argb=new int[inputWidth*inputHeight];

        scaled.getPixels(argb,0,inputWidth,0,0,inputWidth,inputHeight);

        /**
         * 需要转换成偶数的像素点，否则编码YUV420的时候有可能导致分配的空间大小不够而溢出。
         */
        int requiredWidth=inputWidth%2==0?inputWidth:inputWidth+1;
        int requiredHeight=inputHeight%2==0?inputHeight:inputHeight+1;

        int byteLength=requiredWidth*requiredHeight*3/2;
        if(yuvs==null||yuvs.length<byteLength){
            yuvs=new byte[byteLength];
        }else{
            Arrays.fill(yuvs,(byte)0);
        }
        encodeYUV420SP(yuvs,argb,inputWidth,inputHeight);
        scaled.recycle();
        return yuvs;
    }

    /**
     * RGB转YUV420sp
     *
     * @param yuv420sp inputWidth * inputHeight * 3 / 2
     * @param argb     inputWidth * inputHeight
     * @param width
     * @param height
     */
    private static void encodeYUV420SP(byte[] yuv420sp,int[] argb,int width,int height){
        // 帧图片的像素大小
        final int frameSize=width*height;
        // ---YUV数据---
        int Y, U, V;
        // Y的index从0开始
        int yIndex=0;
        // UV的index从frameSize开始
        int uvIndex=frameSize;

        // ---颜色数据---
        // int a, R, G, B;
        int R, G, B;
        //
        int argbIndex=0;
        //

        // ---循环所有像素点，RGB转YUV---
        for(int j=0;j<height;j++){
            for(int i=0;i<width;i++){

                // a is not used obviously
                // a = (argb[argbIndex] & 0xff000000) >> 24;
                R=(argb[argbIndex]&0xff0000)>>16;
                G=(argb[argbIndex]&0xff00)>>8;
                B=(argb[argbIndex]&0xff);
                //
                argbIndex++;

                // well known RGB to YUV algorithm
                Y=((66*R+129*G+25*B+128)>>8)+16;
                U=((-38*R-74*G+112*B+128)>>8)+128;
                V=((112*R-94*G-18*B+128)>>8)+128;

                //
                Y=Math.max(0,Math.min(Y,255));
                U=Math.max(0,Math.min(U,255));
                V=Math.max(0,Math.min(V,255));

                // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
                // meaning for every 4 Y pixels there are 1 V and 1 U. Note the sampling is every other
                // pixel AND every other scanline.
                // ---Y---
                yuv420sp[yIndex++]=(byte)Y;
                // ---UV---
                if((j%2==0)&&(i%2==0)){
                    //
                    yuv420sp[uvIndex++]=(byte)V;
                    //
                    yuv420sp[uvIndex++]=(byte)U;
                }
            }
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth,int reqHeight){
        // Raw height and width of image
        final int height=options.outHeight;
        final int width=options.outWidth;
        int inSampleSize=1;

        if(height>reqHeight||width>reqWidth){

            final int halfHeight=height/2;
            final int halfWidth=width/2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while((halfHeight/inSampleSize)>reqHeight&&(halfWidth/inSampleSize)>reqWidth){
                inSampleSize*=2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromFile(String imgPath,int reqWidth,int reqHeight){

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(imgPath,options);

        // Calculate inSampleSize
        options.inSampleSize=calculateInSampleSize(options,reqWidth,reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds=false;
        return BitmapFactory.decodeFile(imgPath,options);
    }


    /**
     * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency, reuse the same reader
     * objects from one decode to the next.
     */
   /* public static Result decodeImage(byte[] data, int width, int height) {
        // 处理
        Result result = null;
        try {
            Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
            hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
       //     hints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);
            PlanarYUVLuminanceSource source =
                    new PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false);
            /**
             * HybridBinarizer算法使用了更高级的算法，但使用GlobalHistogramBinarizer识别效率确实比HybridBinarizer要高一些。
             *
             * GlobalHistogram算法：（http://kuangjianwei.blog.163.com/blog/static/190088953201361015055110/）
             *
             * 二值化的关键就是定义出黑白的界限，我们的图像已经转化为了灰度图像，每个点都是由一个灰度值来表示，就需要定义出一个灰度值，大于这个值就为白（0），低于这个值就为黑（1）。
             * 在GlobalHistogramBinarizer中，是从图像中均匀取5行（覆盖整个图像高度），每行取中间五分之四作为样本；以灰度值为X轴，每个灰度值的像素个数为Y轴建立一个直方图，
             * 从直方图中取点数最多的一个灰度值，然后再去给其他的灰度值进行分数计算，按照点数乘以与最多点数灰度值的距离的平方来进行打分，选分数最高的一个灰度值。接下来在这两个灰度值中间选取一个区分界限，
             * 取的原则是尽量靠近中间并且要点数越少越好。界限有了以后就容易了，与整幅图像的每个点进行比较，如果灰度值比界限小的就是黑，在新的矩阵中将该点置1，其余的就是白，为0。
             *
            BinaryBitmap bitmap1 = new BinaryBitmap(new GlobalHistogramBinarizer(source));
            // BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
            QRCodeReader reader2 = new QRCodeReader();
            result = reader2.decode(bitmap1, hints);
        } catch (ReaderException e) {
        }
        return result;
    }
*/
    public static Result decodeImage(final String path){
        Bitmap bitmap=QrUtils.decodeSampledBitmapFromFile(path,256,256);
        // Google Photo 相册中选取云照片是会出现 Bitmap == null
        if(bitmap==null) return null;
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        int[] pixels=new int[width*height];
        bitmap.getPixels(pixels,0,width,0,0,width,height);
//                RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        PlanarYUVLuminanceSource source1=new PlanarYUVLuminanceSource(getYUV420sp(width,height,bitmap),width,height,0,0,width,height,false);
        BinaryBitmap binaryBitmap=new BinaryBitmap(new HybridBinarizer(source1));
//                BinaryBitmap binaryBitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source1));
        HashMap<DecodeHintType,Object> hints=new HashMap<>();

        hints.put(DecodeHintType.TRY_HARDER,Boolean.TRUE);
        hints.put(DecodeHintType.CHARACTER_SET,"UTF-8");

        try{
            return new MultiFormatReader().decode(binaryBitmap,hints);
        }catch(NotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存bitmap到SD卡
     *
     * @param bitName 保存的名字
     * @param mBitmap 图片对像
     *                return 生成压缩图片后的图片路径
     */
    public static String saveMyBitmap(String bitName,Bitmap mBitmap) throws IOException{
        File f=new File(bitName);
        if(!f.getParentFile().exists()){
            f.getParentFile().mkdirs();
        }
        f.createNewFile();
        FileOutputStream fOut=null;
        fOut=new FileOutputStream(f);
        mBitmap.compress(Bitmap.CompressFormat.PNG,100,fOut);
        fOut.flush();
        fOut.close();
        return f.getAbsolutePath();
    }

    /**
     * 生成二维码，默认大小为500*500
     *
     * @param text 需要生成二维码的文字、网址等
     * @return bitmap
     */
    public static Bitmap createQRCode(String text){
        return createBarcode(text,BarcodeFormat.QR_CODE,0xff000000,0xffffffff,500,null);
    }

    /**
     * 生成二维码
     *
     * @param text 文字或网址
     * @param size 生成二维码的大小
     * @return bitmap
     */
    public static Bitmap createBarcode(String text,BarcodeFormat format,int true_dot_argb,int false_dot_argb,int size,Bitmap b){
        if(b!=null&&format.equals(BarcodeFormat.QR_CODE)){
            return createLogoQR(text,true_dot_argb,false_dot_argb,size,b);
        }else{
            try{
                Hashtable<EncodeHintType,Object> hints=new Hashtable<>();
                hints.put(EncodeHintType.CHARACTER_SET,"UTF-8");
                if (format == BarcodeFormat.AZTEC) {//错误校正词的最小百分比
                    hints.put(EncodeHintType.ERROR_CORRECTION, Encoder.DEFAULT_AZTEC_LAYERS);//默认，可以不设
                } else if (format == BarcodeFormat.PDF_417) {
                    hints.put(EncodeHintType.ERROR_CORRECTION, 2);//纠错级别，允许为0到8。默认2，可以不设
                } else {
                    hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
                }
                BitMatrix bitMatrix=new MultiFormatWriter().encode(text,format,size,size,hints);
                int H=bitMatrix.getHeight();
                int W=bitMatrix.getWidth();
                int[] pixels=new int[H*W];
                for(int y=0;y<H;y++){
                    for(int x=0;x<W;x++){
                        if(bitMatrix.get(x,y)){
                            pixels[y*W+x]=true_dot_argb;
                        }else{
                            pixels[y*W+x]=false_dot_argb;
                        }
                    }
                }
                Bitmap bitmap=Bitmap.createBitmap(W,H,Bitmap.Config.ARGB_8888);
                bitmap.setPixels(pixels,0,W,0,0,W,H);
                return bitmap;
            }catch(WriterException e){
                e.printStackTrace();
                return null;
            }
        }

    }

    /**
     * 生成带logo的二维码
     *
     * @param text
     * @param size
     * @param mBitmap
     * @return
     */
    public static Bitmap createLogoQR(String text,int true_dot_argb,int false_dot_argb,int size,Bitmap mBitmap){
        try{
            IMAGE_HALFWIDTH=size/10;
            Hashtable<EncodeHintType,Object> hints=new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET,"utf-8");

            hints.put(EncodeHintType.ERROR_CORRECTION,ErrorCorrectionLevel.H);
            BitMatrix bitMatrix=new QRCodeWriter().encode(text,BarcodeFormat.QR_CODE,size,size,hints);
            //将logo图片按martix设置的信息缩放
            mBitmap=Bitmap.createScaledBitmap(mBitmap,size,size,false);

            int width=bitMatrix.getWidth();//矩阵高度
            int height=bitMatrix.getHeight();//矩阵宽度
            int halfW=width/2;
            int halfH=height/2;

            Matrix m=new Matrix();
            float sx=(float)2*IMAGE_HALFWIDTH/mBitmap.getWidth();
            float sy=(float)2*IMAGE_HALFWIDTH/mBitmap.getHeight();
            m.setScale(sx,sy);
            //设置缩放信息
            //将logo图片按martix设置的信息缩放
            mBitmap=Bitmap.createBitmap(mBitmap,0,0,
                    mBitmap.getWidth(),mBitmap.getHeight(),m,false);

            int[] pixels=new int[size*size];
            for(int y=0;y<size;y++){
                for(int x=0;x<size;x++){
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
            Bitmap bitmap=Bitmap.createBitmap(size,size,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels,0,size,0,0,size,size);
            return bitmap;
        }catch(WriterException e){
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap scaleBitmap(Bitmap origin,float ratio){
        if(origin==null){
            return null;
        }
        int width=origin.getWidth();
        int height=origin.getHeight();
        Matrix matrix=new Matrix();
        matrix.preScale(ratio,ratio);
        Bitmap newBM=Bitmap.createBitmap(origin,0,0,width,height,matrix,false);
        return newBM;
    }

    public static Bitmap generate(String contents,
                                  float dotScale,
                                  int colorDark,
                                  int colorLight,
                                  boolean autoColor,
                                  int cutX,
                                  int cutY,
                                  int qrSize,
                                  Bitmap background){
        //int cutX=between(mv.getSelectLeft()/mv.getXishu(),0,finallyBmp.getWidth()-qrSize);
        //int cutY=between(mv.getSelectTop()/mv.getXishu(),0,finallyBmp.getHeight()-qrSize);
        Bitmap bmpQRcode=AwesomeQRCode.create(
                contents,
                qrSize,
                0,
                dotScale,
                colorDark,
                colorLight,
                Bitmap.createBitmap(
                        background,
                        cutX,
                        cutY,
                        qrSize,
                        qrSize),
                false,
                autoColor,
                false,
                128);
        Bitmap finallyBmp=background.copy(Bitmap.Config.ARGB_8888,true);
        Canvas c=new Canvas(finallyBmp);
        c.drawBitmap(bmpQRcode,cutX,cutY,new Paint());
        // qrCodeImageView.setImageBitmap(QrUtils.scaleBitmap(finallyBmp,mv.getXishu()));
        // ViewGroup.LayoutParams para=qrCodeImageView.getLayoutParams();
        // para.height=(int)(screenW/finallyBmp.getWidth()*finallyBmp.getHeight());
        //  qrCodeImageView.setLayoutParams(para);
        return finallyBmp;
    }
    public static Bitmap flex(Bitmap bitmap, int dstWidth) {
        float wScale = (float) dstWidth / bitmap.getWidth();
        float hScale = wScale;
        return flex(bitmap, wScale, hScale);
    }

    public static Bitmap flex(Bitmap bitmap, float wScale, float hScale) {
        if (wScale <= 0 || hScale <= 0){
            return null;
        }
        float ii = 1 / wScale;    //采样的行间距
        float jj = 1 / hScale; //采样的列间距

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int dstWidth = (int) (wScale * width);
        int dstHeight = (int) (hScale * height);

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int[] dstPixels = new int[dstWidth * dstHeight];

        for (int j = 0; j < dstHeight; j++) {
            for (int i = 0; i < dstWidth; i++) {
                dstPixels[j * dstWidth + i] = pixels[(int) (jj * j) * width + (int) (ii * i)];
            }
        }
        System.out.println((int) ((dstWidth - 1) * ii));

        Bitmap outBitmap = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888);
        outBitmap.setPixels(dstPixels, 0, dstWidth, 0, 0, dstWidth, dstHeight);

        return outBitmap;
    }
}
