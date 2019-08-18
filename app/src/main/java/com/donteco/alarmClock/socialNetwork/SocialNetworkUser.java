package com.donteco.alarmClock.socialNetwork;

import android.graphics.drawable.Drawable;

import com.donteco.alarmClock.help.ConstantsForApp;

import org.json.JSONException;
import org.json.JSONObject;

public class SocialNetworkUser
{
    private int id;
    private String name;
    private String surname;
    private String avatar;
    private String socialNetworkName;

    private boolean isAuthorized;

    public SocialNetworkUser(int id){
        this.id = id;
    }

    public SocialNetworkUser(String socialNetworkName) {
        this.socialNetworkName = socialNetworkName;
    }

    public SocialNetworkUser(int id, String name, String surname, String avatar, String socialNetworkName) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.avatar = avatar;
        this.socialNetworkName = socialNetworkName;
        isAuthorized = true;
    }

    public static SocialNetworkUser parseVK(JSONObject jsonObject)
    {
        try
        {
            System.out.println("JsonObject " + jsonObject);
            int id = jsonObject.getInt("id");
            String name = jsonObject.getString("first_name");
            String surname = jsonObject.getString("last_name");
            String avatar = jsonObject.getString("photo_100");

            return new SocialNetworkUser(id, name, surname, avatar, ConstantsForApp.VK_NAME);
        }
        catch (JSONException e) {
            return null;
        }
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public String getSocialNetworkName() {
        return socialNetworkName;
    }

    public void setSocialNetworkName(String socialNetworkName) {
        this.socialNetworkName = socialNetworkName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "VKUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
