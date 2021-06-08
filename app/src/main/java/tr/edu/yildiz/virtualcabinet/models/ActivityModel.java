package tr.edu.yildiz.virtualcabinet.models;

import android.os.Parcel;
import android.os.Parcelable;


public class ActivityModel implements Parcelable,Comparable<ActivityModel> {
    private final String name;
    private final String type;
    private final String date;
    private final Double latitude;
    private final Double longitude;
    private final String address;
    private String combineName;

    public ActivityModel(String name, String type, String date, Double latitude, Double longitude,String address, String combineName) {
        this.name = name;
        this.type = type;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.combineName = combineName;
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
}
