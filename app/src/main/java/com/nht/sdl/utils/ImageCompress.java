package com.nht.sdl.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore.Images.ImageColumns;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageCompress {
    /**
     * 图像质量压缩
     *
     * @param image 输入图像
     * @return 输出图像
     */
    private static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);  //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            options -= 10;  //每次都减少10
            baos.reset();   //重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);  //这里压缩options%，把压缩后的数据存放到baos中
        }
        image.recycle();
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());   //把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(isBm, null, null);   //把ByteArrayInputStream数据生成图片
    }

    /**
     * 压缩图像到字节流中
     *
     * @param image 图像对象
     * @param size  压缩后图像大小（kb）
     * @return 字节数组
     */
    public static byte[] compressImage(Bitmap image, double size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);  //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        //循环判断如果压缩后图片是否大于size, 大于继续压缩
        while (baos.toByteArray().length / 1024 > size) {
            options -= 10;  //每次都减少10
            baos.reset();   //重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);  //这里压缩options%，把压缩后的数据存放到baos中
        }
        image.recycle();
        byte[] res = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            return null;
        }
        return res;
    }

    /**
     * 压缩图像到字节流中
     *
     * @param image 图像对象
     * @param size  压缩后图像大小（kb）
     * @param type  原图类型 jpg,png
     * @return
     */

    public static byte[] compressImage(Bitmap image, double size, String type) {
        if (null == image) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap.CompressFormat pic_type;
        if ("png".equalsIgnoreCase(type)) {
            pic_type = Bitmap.CompressFormat.PNG;
        } else {
            pic_type = Bitmap.CompressFormat.JPEG;
        }

        image.compress(pic_type, 90, baos);  //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        //循环判断如果压缩后图片是否大于size, 大于继续压缩
        while (baos.toByteArray().length / 1024 > size) {
            if ("png".equals(type)) {
                break;
            }

            options -= 10;  //每次都减少10
            if (options <= 0) {
                break;
            }
            baos.reset();   //重置baos即清空baos
            image.compress(pic_type, options, baos);  //这里压缩options%，把压缩后的数据存放到baos中
//            XLog.e("size", options + "  " + baos.toByteArray().length / 1024 + "");
        }
        image.recycle();
        byte[] res = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            return null;
        }
        return res;
    }


    /**
     * 读取原始图片
     *
     * @param srcPath 图片地址
     * @return 图片对象
     */
    public static Bitmap getOriginImage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        newOpts.inJustDecodeBounds = false;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(srcPath, newOpts);
    }

    /**
     * 读取本地图片
     *
     * @param srcPath 图片地址
     * @param height  显示图片的高度
     * @param width   显示图片的宽度
     * @return 图片对象
     */
    public static Bitmap getOriginImage(String srcPath, int width, int height) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;

        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        newOpts.inJustDecodeBounds = false;
        newOpts.inSampleSize = caculateScale(h, w, height, width);   //设置缩放比例
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
//        newOpts.inPreferredConfig = null;
//        newOpts.inInputShareable = true;
//        newOpts.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        if ("samsung".equals(Build.MANUFACTURER)) {
            int picRotate = getPicRotate(srcPath);
            if (picRotate>0) {
                bitmap = rotaingImageView(picRotate, bitmap);
            }
        }

        return bitmap;
    }

    /*
        * 旋转图片
        * @param angle
        * @param bitmap
        * @return Bitmap
        */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        ;
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    private static int getPicRotate(String srcPath) {
        int rotate = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(srcPath);
            int result = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            switch (result) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                default:
                    break;
            }
        } catch (IOException e) {

        }
        return rotate;
    }

    /**
     * 从文件读取图片，并进行压缩
     *
     * @param srcPath 文件地址
     * @return 图片对象
     */
    public static Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //  现在主流手机比较多是1280*960分辨率，所以高和宽我们设置为
        int hh = 1280;   //这里设置高度为1280f
        int ww = 960;    //这里设置宽度为960f
        int scale = caculateScale(h, w, hh, ww);
        newOpts.inSampleSize = scale;  //设置缩放比例
        newOpts.inJustDecodeBounds = false;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    /**
     * 获取真实文件路径
     *
     * @param context 上下文
     * @param uri     uri
     * @return 地址
     */
    public static String getRealFilePath(Context context, Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 计算图像压缩比
     *
     * @param h  原始图高度
     * @param w  原始图宽度
     * @param hh 压缩后高度
     * @param ww 压缩后宽度
     * @return 压缩率
     */
    public static int caculateScale(int h, int w, int hh, int ww) {
        //  重置宽高，使h>w
        if (w > h) {
            w = w ^ h;
            h = h ^ w;
            w = w ^ h;
        }
        //  计算缩放比，使用其中差别较大的比例
        int wScale = (int) (1.0 * w / ww);
        int hScale = (int) (1.0 * h / hh);
        int scale = wScale > hScale ? wScale : hScale;
        if (scale < 1)
            scale = 1;
        return scale;
    }
}
