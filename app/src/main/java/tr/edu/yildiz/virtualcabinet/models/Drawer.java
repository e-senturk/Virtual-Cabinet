package tr.edu.yildiz.virtualcabinet.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Drawer implements Parcelable,Comparable<Drawer> {
    private String name;
    private String color;
    private ArrayList<Clothes> clothesList;

    public Drawer(String name,String color) {
        this.name = name;
        this.color = color;
        this.clothesList = new ArrayList<>();
    }

    public Drawer(String name,String color, ArrayList<Clothes> clothesList) {
        this.name = name;
        this.color = color;
        this.clothesList = clothesList;
    }

    public Drawer(Parcel in) {
        this.name = in.readString();
        this.color = in.readString();
        this.clothesList = in.readArrayList(Clothes.class.getClassLoader());
    }

    public void setName(String name) { this.name = name; }

    public String getName() {
        return name;
    }

    public ArrayList<Clothes> getClothesList() {
        return clothesList;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setClothesList(ArrayList<Clothes> clothesList) {
        this.clothesList = clothesList;
    }


    @Override
    public int compareTo(Drawer o) {
        return o.getName().compareTo(this.name);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(color);
        dest.writeList(clothesList);
    }

    public static final Creator<Drawer> CREATOR = new Creator<Drawer>() {
        @Override
        public Drawer createFromParcel(Parcel in) {
            return new Drawer(in);
        }

        @Override
        public Drawer[] newArray(int size) {
            return new Drawer[size];
        }
    };

}
