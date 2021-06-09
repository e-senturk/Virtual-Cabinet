package tr.edu.yildiz.virtualcabinet.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class ActivityModel implements Parcelable, Comparable<ActivityModel> {
    public static final Creator<ActivityModel> CREATOR = new Creator<ActivityModel>() {
        @Override
        public ActivityModel createFromParcel(Parcel in) {
            return new ActivityModel(in);
        }

        @Override
        public ActivityModel[] newArray(int size) {
            return new ActivityModel[size];
        }
    };
    private final String name;
    private final String type;
    private final String date;
    private final Double latitude;
    private final Double longitude;
    private final String address;
    private String combineName;

    public ActivityModel(String name, String type, String date, Double latitude, Double longitude, String address, String combineName) {
        this.name = name.replace("~", "").replace(":", "");
        this.type = type.replace("~", "").replace(":", "");
        this.date = date.replace("~", "").replace(":", "");
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address.replace("~", "").replace(":", "");
        this.combineName = combineName.replace("~", "").replace(":", "");
    }


    protected ActivityModel(Parcel in) {
        this.name = in.readString();
        this.type = in.readString();
        this.date = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.address = in.readString();
        this.combineName = in.readString();
    }

    public ActivityModel(String info) {
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
        this.type = infoList.get(1);
        this.date = infoList.get(2);
        this.latitude = Double.parseDouble(infoList.get(3));
        this.longitude = Double.parseDouble(infoList.get(4));
        this.address = infoList.get(5);
        this.combineName = infoList.get(6);
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getCombineName() {
        return combineName;
    }

    public void setCombineName(String combineName) {
        this.combineName = combineName;
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return "name:" + name + "~" +
                "type:" + type + "~" +
                "date:" + date + "~" +
                "latitude:" + latitude + "~" +
                "longitude:" + longitude + "~" +
                "address:" + address + "~" +
                "combineName:" + combineName + "~";
    }

    @Override
    public int compareTo(ActivityModel o) {
        return o.getName().compareTo(this.name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(date);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(address);
        dest.writeString(combineName);
    }
}
