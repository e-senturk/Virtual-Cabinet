package tr.edu.yildiz.virtualcabinet.service;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.content.FileProvider;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import tr.edu.yildiz.virtualcabinet.BuildConfig;
import tr.edu.yildiz.virtualcabinet.R;

public class Tools {
    /**
     * Auxiliary class for modifying media items
     */
    private Tools() {
    }

    // sql can't handle big images so this class resizes image
    public static Bitmap makeImageSmaller(Bitmap image, int maximumSize) {
        Bitmap mutable = image.copy(Bitmap.Config.RGBA_F16, true);
        Bitmap squared = createSquaredBitmap(mutable);
        mutable.recycle();
        Bitmap result = Bitmap.createScaledBitmap(squared, maximumSize, maximumSize, true);
        squared.recycle();
        return result;
    }


    private static Bitmap createSquaredBitmap(Bitmap srcBmp) {
        int dim = Math.max(srcBmp.getWidth(), srcBmp.getHeight());
        Bitmap dstBmp = Bitmap.createBitmap(dim, dim, srcBmp.getConfig());

        Canvas canvas = new Canvas(dstBmp);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(srcBmp, (float) (dim - srcBmp.getWidth()) / 2, (float) (dim - srcBmp.getHeight()) / 2, null);

        return dstBmp;
    }

    public static Bitmap initializeImageView(Context context, Uri imageUri, ImageView imageView) {
        try {
            Bitmap bitmap;
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            int size = Math.max(height, width) / 6;
            if (Build.VERSION.SDK_INT >= 28) {
                ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), imageUri);
                bitmap = Tools.makeImageSmaller(ImageDecoder.decodeBitmap(source), size);

            } else {
                bitmap = Tools.makeImageSmaller(MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri), size);
            }
            if (bitmap != null && imageView != null)
                imageView.setImageBitmap(bitmap);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap generateCombineBitmap(Context context, Uri imageUri) {
        if (imageUri == null) {
            return null;
        }
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            if (Build.VERSION.SDK_INT >= 28) {
                ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), imageUri);
                return ImageDecoder.decodeBitmap(source);
            } else {
                return MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            }
        } catch (IOException e) {
            System.out.println("Bitmap read failure");
            return null;
        }
    }

    // gets file's original name from uri
    private static String queryName(Context context, Uri uri) {
        Cursor returnCursor =
                context.getContentResolver().query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    // stores file and returns its new path and original name
    public static String storeAndGetPath(Context context, Uri uri, String fileName) {
        String realName = queryName(context, uri);
        String[] splitRealName = realName.split("\\.");
        if (fileName == null) {
            fileName = java.util.UUID.randomUUID().toString() + "." + splitRealName[splitRealName.length - 1];
        }
        String path = context.getFilesDir().getPath() + File.separatorChar + fileName;
        System.out.println(path);
        File destinationFilename = new File(path);
        try (InputStream ins = context.getContentResolver().openInputStream(uri)) {
            createFileFromStream(ins, destinationFilename);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return path;
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, java.util.UUID.randomUUID().toString(), null);
        return Uri.parse(path);
    }

    // creates an output stream and copies file to given destination
    public static void createFileFromStream(InputStream ins, File destination) {
        try (OutputStream outputStream = new FileOutputStream(destination)) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = ins.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Color getDominantColor(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        Bitmap mutable = newBitmap.copy(Bitmap.Config.RGBA_F16, true);
        Color color = Color.valueOf(mutable.getPixel(0, 0));
        newBitmap.recycle();
        mutable.recycle();
        return color;
    }

    public static Uri getUriFromStringPath(String path, Context context) {
        if (path == null || path.equals("")) return null;
        File file = new File(path);
        return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
    }

    public static Bitmap addBorder(Color color, Bitmap bmp, int borderSize) {
        Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + borderSize * 2, bmp.getHeight() + borderSize * 2, bmp.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(color.toArgb());
        canvas.drawBitmap(bmp, borderSize, borderSize, null);
        return bmpWithBorder;
    }

    public static void setMarginLeft(View v, int left) {
        ViewGroup.MarginLayoutParams params =
                (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        params.setMargins(left, params.topMargin,
                params.rightMargin, params.bottomMargin);
    }


    public static void showSnackBar(String text, ViewGroup layout, Context context, int length, View.OnClickListener action, String actionButtonText) {
        Snackbar snackbar = Snackbar.make(layout, text, length)
                .setActionTextColor(context.getColor(R.color.black))
                .setTextColor(context.getColor(R.color.white))
                .setBackgroundTint(context.getColor(R.color.orange));
        if (action != null)
            snackbar.setAction(actionButtonText, action);
        snackbar.show();
    }

    public static void showSnackBar(String text, ViewGroup layout, Context context, int length) {
        showSnackBar(text, layout, context, length, null, null);
    }

    public static void initializeImageView(Bitmap bitmap, ImageView imageView) {
        if (bitmap != null) imageView.setImageBitmap(bitmap);
    }

    // converts arraylists of string to blob
    public static <T> byte[] arrayListToBlob(ArrayList<T> arrayList) throws IOException {
        if (arrayList == null) {
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bos);

        for (T element : arrayList) {
            out.writeUTF(element.toString());
        }
        return bos.toByteArray();
    }

    // converts blob to arraylists of string
    public static ArrayList<String> blobToArrayList(byte[] blob) throws IOException {
        ArrayList<String> choicesList = new ArrayList<>();
        if (blob == null || blob.length == 0) {
            return choicesList;
        }
        ByteArrayInputStream bin = new ByteArrayInputStream(blob);
        DataInputStream in = new DataInputStream(bin);
        while (in.available() > 0) {
            String element = in.readUTF();
            choicesList.add(element);
        }
        return choicesList;
    }

    public static Bitmap generateMenuImage(Activity context, int id, int topBarSize) {
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), id);
        float width = bmp.getWidth();
        float height = bmp.getHeight();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = (displayMetrics.heightPixels - topBarSize) / 3;
        int cropWidth;
        int cropHeight;
        int x = 0;
        int y = 0;
        if (width / screenWidth > height / screenHeight) {
            cropHeight = (int) height;
            cropWidth = (int) height * screenWidth / screenHeight;
        } else {
            cropWidth = (int) width;
            cropHeight = (int) width * screenHeight / screenWidth;
        }
        return Bitmap.createBitmap(bmp, x, y, cropWidth, cropHeight);
    }
}
