package tr.edu.yildiz.virtualcabinet.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import tr.edu.yildiz.virtualcabinet.R;
import tr.edu.yildiz.virtualcabinet.service.Tools;

public class Combine implements Parcelable, Comparable<Combine> {
    public static final Creator<Combine> CREATOR = new Creator<Combine>() {
        @Override
        public Combine createFromParcel(Parcel in) {
            return new Combine(in);
        }

        @Override
        public Combine[] newArray(int size) {
            return new Combine[size];
        }
    };
    private final String topHeadPath;
    private final String facePath;
    private final String upperBodyPath;
    private final String lowerBodyPath;
    private final String feetPath;
    private String name;

    public Combine(String name, String topHeadPath, String facePath, String upperBodyPath, String lowerBodyPath, String feetPath) {
        this.name = name.replace("~", "").replace(":", "");
        this.topHeadPath = topHeadPath.replace("~", "").replace(":", "");
        this.facePath = facePath.replace("~", "").replace(":", "");
        this.upperBodyPath = upperBodyPath.replace("~", "").replace(":", "");
        this.lowerBodyPath = lowerBodyPath.replace("~", "").replace(":", "");
        this.feetPath = feetPath.replace("~", "").replace(":", "");
    }

    public Combine(String name, ArrayList<String> imagePaths) {
        this.name = name.replace("~", "").replace(":", "");
        this.topHeadPath = imagePaths.get(0).replace("~", "").replace(":", "");
        this.facePath = imagePaths.get(1).replace("~", "").replace(":", "");
        this.upperBodyPath = imagePaths.get(2).replace("~", "").replace(":", "");
        this.lowerBodyPath = imagePaths.get(3).replace("~", "").replace(":", "");
        this.feetPath = imagePaths.get(4).replace("~", "").replace(":", "");
    }

    protected Combine(Parcel in) {
        this.name = in.readString();
        this.topHeadPath = in.readString();
        this.facePath = in.readString();
        this.upperBodyPath = in.readString();
        this.lowerBodyPath = in.readString();
        this.feetPath = in.readString();
    }

    public Combine(String info) {
        ArrayList<String> infoList = new ArrayList<>();
        for (int i = 0; i < info.length(); i++) {
            if (info.charAt(i) == ':') {
                int j = i + 1;
                while (i < info.length() && info.charAt(i) != '~') {
                    i++;
                }
                infoList.add(info.substring(j, i));
            }
        }
        this.name = infoList.get(0);
        this.topHeadPath = infoList.get(1);
        this.facePath = infoList.get(2);
        this.upperBodyPath = infoList.get(3);
        this.lowerBodyPath = infoList.get(4);
        this.feetPath = infoList.get(5);
    }

    @Nonnull
    @Override
    public String toString() {
        return "name:" + name + "~" +
                "topHeadPath:" + topHeadPath + "~" +
                "facePath:" + facePath + "~" +
                "upperBodyPath:" + upperBodyPath + "~" +
                "lowerBodyPath:" + lowerBodyPath + "~" +
                "feetPath:" + feetPath + "~";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopHeadPath() {
        return topHeadPath;
    }

    public String getFacePath() {
        return facePath;
    }

    public String getUpperBodyPath() {
        return upperBodyPath;
    }

    public String getLowerBodyPath() {
        return lowerBodyPath;
    }

    public String getFeetPath() {
        return feetPath;
    }

    public Bitmap getTopHeadImage(Context context) {
        return createScaledImage(topHeadPath, context);
    }

    public Bitmap getFaceImage(Context context) {
        return createScaledImage(facePath, context);
    }

    public Bitmap getUpperBodyImage(Context context) {
        return createScaledImage(upperBodyPath, context);
    }

    public Bitmap getLowerBodyImage(Context context) {
        return createScaledImage(lowerBodyPath, context);
    }

    public Bitmap getFeetImage(Context context) {
        return createScaledImage(feetPath, context);
    }

    private Bitmap createScaledImage(String path, Context context) {
        Uri uri = Tools.getUriFromStringPath(path, context);
        return Tools.generateCombineBitmap(context, uri);
    }

    public ArrayList<String> generateArrayList() {
        ArrayList<String> items = new ArrayList<>();
        items.add(topHeadPath);
        items.add(facePath);
        items.add(upperBodyPath);
        items.add(lowerBodyPath);
        items.add(feetPath);
        return items;
    }

    private Bitmap mergeUris(Context context) {
        ArrayList<String> combinePathList = generateArrayList();
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        for (String path : combinePathList) {
            Uri uri = Tools.getUriFromStringPath(path, context);
            Bitmap bitmap = Tools.generateCombineBitmap(context, uri);
            if (bitmap != null) {
                bitmaps.add(bitmap);
            }
        }
        if (bitmaps.size() == 0)
            return null;
        else if (bitmaps.size() == 1) {
            return bitmaps.get(0);
        } else {
            Bitmap combined = bitmaps.get(0);
            combined = combined.copy(Bitmap.Config.RGBA_F16, true);
            for (int i = 1; i < bitmaps.size(); i++) {
                Bitmap temp = bitmaps.get(i);
                temp = temp.copy(Bitmap.Config.RGBA_F16, true);
                if (combined != null && temp != null) {
                    combined = addImageToBottom(combined, temp);
                }
            }
            return combined;
        }
    }

    private Bitmap addImageToBottom(Bitmap unMutable1, Bitmap unMutable2) {
        Bitmap bmp1 = unMutable1.copy(Bitmap.Config.RGBA_F16, true);
        Bitmap bmp2 = unMutable2.copy(Bitmap.Config.RGBA_F16, true);
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight() + bmp2.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, (float) (bmp1.getWidth() - bmp2.getWidth()) / 2, bmp1.getHeight(), null);
        bmp1.recycle();
        bmp2.recycle();
        return bmOverlay;
    }

    public Bitmap createMergedImage(Context context) {
        Bitmap combinedBitmap = mergeUris(context);
        if (combinedBitmap != null) {
            Bitmap framedBitmap = Tools.addBorder(Color.valueOf(context.getColor(R.color.white)), combinedBitmap, 1);
            return Tools.addBorder(Color.valueOf(context.getColor(R.color.black)), framedBitmap, 3);
        }
        return null;
    }

    @Override
    public int compareTo(Combine o) {
        return o.getName().compareTo(this.name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(topHeadPath);
        dest.writeString(facePath);
        dest.writeString(upperBodyPath);
        dest.writeString(lowerBodyPath);
        dest.writeString(feetPath);
    }
}
