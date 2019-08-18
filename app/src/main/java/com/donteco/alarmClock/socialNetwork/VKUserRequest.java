package com.donteco.alarmClock.socialNetwork;

import com.vk.api.sdk.requests.VKRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VKUserRequest extends VKRequest<List<SocialNetworkUser>>
{

    //In fact i got only one user
    public VKUserRequest(@NotNull String method, List<SocialNetworkUser> users)
    {
        super(method);

        addParam("user_ids", users.get(0).getId());
        addParam("fields", "id");
        addParam("fields", "first_name");
        addParam("fields", "last_name");
        addParam("fields", "photo_100");
    }

    @Override
    public List<SocialNetworkUser> parse(@NotNull JSONObject r) throws Exception {
        JSONArray users = r.getJSONArray("response");
        List<SocialNetworkUser> result= new ArrayList<>();

        for (int i = 0; i < users.length(); i++)
            result.add(SocialNetworkUser.parseVK(users.getJSONObject(i)));


        return result;
    }
}
