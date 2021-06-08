package tr.edu.yildiz.virtualcabinet.models;


import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

public class Clothes implements Parcelable,Comparable<Clothes> {
    private final String attachmentPath;
    private final String wearingLocation;
    private final String type;
    private final String color;
    private final String purchaseDate;
    private final String clothesPattern;
    private final Double price;

    public Clothes(String attachmentPath, String wearing_location, String type, String color, String purchaseDate, String clothesPattern, Double price) {
        this.attachmentPath = attachmentPath;
        this.wearingLocation = wearing_location;
        this.type = type;
        this.color = color;
        this.purchaseDate = purchaseDate;
        this.clothesPattern = clothesPattern;
        this.price = price;
    }

    protected Clothes(Parcel in) {
        this.attachmentPath = in.readString();
        this.wearingLocation = in.readString();
        this.type = in.readString();
        this.color = in.readString();
        this.purchaseDate = in.readString();
        this.clothesPattern = in.readString();
        this.price = in.readDouble();
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

    @Override
    public @NotNull String toString() {
        return "Clothes{" +
                "attachmentPath='" + attachmentPath + '\'' +
                ", wearing_location='" + wearingLocation + '\'' +
                ", type='" + type + '\'' +
                ", color='" + color + '\'' +
                ", purchaseDate='" + purchaseDate + '\'' +
                ", clothesPattern='" + clothesPattern + '\'' +
                ", price=" + price +
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
    }

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
}
