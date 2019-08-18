package com.donteco.alarmClock.socialNetwork;

import com.vk.api.sdk.requests.VKRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class VkWallRequest extends VKRequest<Integer>
{
    public VkWallRequest(@NotNull String method, int id, String curTime) {
        super(method);

        if(id != 0)
            addParam("owner_id", id);

        addParam("message", curTime);
        //addParam("attachments", );
    }

    @Override
    public Integer parse(@NotNull JSONObject r) throws Exception {
        System.out.println("JsonObject " + r);
        JSONObject jsonObject = r.getJSONObject("response");
        return jsonObject.getInt("post_id");
    }
}
