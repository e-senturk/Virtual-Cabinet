package tr.edu.yildiz.virtualcabinet.models;


import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Clothes implements Parcelable, Comparable<Clothes> {
    public static final Creator<Clothes> CREATOR = new Creator<Clothes>() {
        @Override
        public Clothes createFromParcel(Parcel in) {
            return new Clothes(in);
        }

        @Override
        public Clothes[] newArray(int size) {
            return new Clothes[size];
        }
    };
    private final String attachmentPath;
    private final String wearingLocation;
    private final String type;
    private final String color;
    private final String purchaseDate;
    private final String clothesPattern;
    private final Double price;
    private String drawerName;

    public Clothes(String attachmentPath, String wearing_location, String type, String color, String purchaseDate, String clothesPattern, Double price, String drawerName) {
        this.attachmentPath = attachmentPath.replace("~", "").replace(":", "");
        this.wearingLocation = wearing_location.replace("~", "").replace(":", "");
        this.type = type.replace("~", "").replace(":", "");
        this.color = color.replace("~", "").replace(":", "");
        this.purchaseDate = purchaseDate.replace("~", "").replace(":", "");
        this.clothesPattern = clothesPattern.replace("~", "").replace(":", "");
        this.price = price;
        this.drawerName = drawerName;
    }

    protected Clothes(Parcel in) {
        this.attachmentPath = in.readString();
        this.wearingLocation = in.readString();
        this.type = in.readString();
        this.color = in.readString();
        this.purchaseDate = in.readString();
        this.clothesPattern = in.readString();
        this.price = in.readDouble();
        this.drawerName = in.readString();
    }

    public Clothes(String info) {
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
        this.attachmentPath = infoList.get(0);
        this.wearingLocation = infoList.get(1);
        this.type = infoList.get(2);
        this.color = infoList.get(3);
        this.purchaseDate = infoList.get(4);
        this.clothesPattern = infoList.get(5);
        this.price = Double.parseDouble(infoList.get(6));
        this.drawerName = infoList.get(7);
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public String getWearingLocation() {
        return wearingLocation;
    }

    public String getType() {
        return type;
    }

    public String getColor() {
        return color;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public String getClothesPattern() {
        return clothesPattern;
    }

    public Double getPrice() {
        return price;
    }

    public String getDrawerName() {
        return drawerName;
    }

    public void setDrawerName(String drawerName) {
        this.drawerName = drawerName;
    }

    @Override
    public @NotNull String toString() {
        return "Clothes{" +
                "attachmentPath:" + attachmentPath + '~' +
                "wearing_location:" + wearingLocation + '~' +
                "type:" + type + '~' +
                "color:" + color + '~' +
                "purchaseDate:" + purchaseDate + '~' +
                "clothesPattern:" + clothesPattern + '~' +
                "price:" + price + '~' +
                "drawer:" + drawerName + '~' +
                '}';
    }

    @Override
    public int compareTo(Clothes o) {
        return o.type.compareTo(this.type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(attachmentPath);
        dest.writeString(wearingLocation);
        dest.writeString(type);
        dest.writeString(color);
        dest.writeString(purchaseDate);
        dest.writeString(clothesPattern);
        dest.writeDouble(price);
        dest.writeString(drawerName);
    }
}
