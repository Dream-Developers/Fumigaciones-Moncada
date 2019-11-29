package com.example.fumigacionesmoncada;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatsRecent implements Parcelable {

    private String uuid;
    private String username;
    private String lastMessage;
    private long timestamp;
    private String photoUrl;

    public ChatsRecent() {
    }

    protected ChatsRecent(Parcel in) {
        uuid = in.readString();
        username = in.readString();
        lastMessage = in.readString();
        timestamp = in.readLong();
        photoUrl = in.readString();
    }

    public static final Creator<ChatsRecent> CREATOR = new Creator<ChatsRecent>() {
        @Override
        public ChatsRecent createFromParcel(Parcel in) {
            return new ChatsRecent(in);
        }

        @Override
        public ChatsRecent[] newArray(int size) {
            return new ChatsRecent[size];
        }
    };

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uuid);
        parcel.writeString(username);
        parcel.writeString(lastMessage);
        parcel.writeLong(timestamp);
        parcel.writeString(photoUrl);
    }
}
