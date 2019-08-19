package com.donteco.alarmClock.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.donteco.alarmClock.R;
import com.donteco.alarmClock.help.ApplicationStorage;
import com.donteco.alarmClock.help.ConstantsForApp;
import com.donteco.alarmClock.socialNetwork.SocialNetworkUser;
import com.facebook.AccessToken;
import com.squareup.picasso.Picasso;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.auth.VKAccessToken;

import java.util.List;

public class SocialNetworkAdapter extends RecyclerView.Adapter<SocialNetworkAdapter.SocialNetworkViewHolder>
{

    private final List<SocialNetworkUser> users;
    private SocialNetworkCallBack socialNetworkCallBack;

    public SocialNetworkAdapter(SocialNetworkCallBack socialNetworkCallBack)
    {
        this.socialNetworkCallBack = socialNetworkCallBack;
        users = ApplicationStorage.getSocialNetworkUsers();
    }

    public void updateUser(int position, SocialNetworkUser socialNetworkUser){
        users.set(position, socialNetworkUser);
        notifyItemChanged(position);
    }


    @NonNull
    @Override
    public SocialNetworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.social_media_recyclerview_element, parent, false);

        return new SocialNetworkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SocialNetworkViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class SocialNetworkViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView socialNetworkImage;
        private TextView socialNetworkName;
        private TextView socialNetworkUserName;
        private LinearLayout linearLayout;
        private ImageView socialNetworkExit;

        public SocialNetworkViewHolder(@NonNull View itemView)
        {
            super(itemView);

            socialNetworkImage = itemView.findViewById(R.id.social_media_iv_for_social_picture);
            socialNetworkName = itemView.findViewById(R.id.social_media_tv_for_name);
            socialNetworkUserName = itemView.findViewById(R.id.social_media_tv_for_user_info);
            linearLayout = itemView.findViewById(R.id.social_media_ll_for_user_info);
            socialNetworkExit = itemView.findViewById(R.id.social_media_iv_exit);
        }

        private void bind(SocialNetworkUser socialNetworkUser)
        {
            socialNetworkName.setText(socialNetworkUser.getSocialNetworkName());

            if(socialNetworkUser.getSurname() != null || socialNetworkUser.getName() != null)
            {
                String outputText = socialNetworkUser.getName() + " " + socialNetworkUser.getSurname();
                socialNetworkUserName.setText(outputText);
            }
            else
                socialNetworkUserName.setText(R.string.log_in_string);

            if(socialNetworkUser.getAvatar() != null)
                Picasso.get()
                .load(socialNetworkUser.getAvatar())
                .noFade()
                .into(socialNetworkImage);
            else
                socialNetworkImage.setImageDrawable(socialNetworkCallBack.onNoAvatarCondition(getAdapterPosition()));

            //User can press even icon or linear layout with data
            socialNetworkImage.setOnClickListener(view -> socialNetworkCallBack.onPressed(socialNetworkUser));
            linearLayout.setOnClickListener(view -> socialNetworkCallBack.onPressed(socialNetworkUser));

            socialNetworkExit.setOnClickListener(view -> socialNetworkCallBack.onExitPressed(getAdapterPosition()));

            exitIconLogic();
        }

        private void exitIconLogic()
        {
            switch (getAdapterPosition())
            {
                case 0:
                    VKAccessToken vkAccessToken = ApplicationStorage.getVkAccessToken();

                    if(VK.isLoggedIn() && vkAccessToken != null && vkAccessToken.isValid())
                        socialNetworkExit.setVisibility(View.VISIBLE);
                    else
                        socialNetworkExit.setVisibility(View.INVISIBLE);

                    break;

                case 1:
                    AccessToken fbAccessToken = AccessToken.getCurrentAccessToken();

                    if(fbAccessToken != null && !fbAccessToken.isExpired())
                        socialNetworkExit.setVisibility(View.VISIBLE);
                    else
                        socialNetworkExit.setVisibility(View.INVISIBLE);

                    break;
            }
        }
    }

    public interface SocialNetworkCallBack {
        void onPressed(SocialNetworkUser socialNetworkUser);
        void onExitPressed(int position);
        Drawable onNoAvatarCondition(int position);
    }
}
