package com.donteco.alarmClock.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.donteco.alarmClock.R;
import com.donteco.alarmClock.socialNetwork.SocialNetworkUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SocialNetworkAdapter extends RecyclerView.Adapter<SocialNetworkAdapter.SocialNetworkViewHolder>
{

    private final List<SocialNetworkUser> users;
    private SocialNetworkCallBack socialNetworkCallBack;

    public SocialNetworkAdapter(SocialNetworkCallBack socialNetworkCallBack, List<SocialNetworkUser> users)
    {
        this.socialNetworkCallBack = socialNetworkCallBack;
        this.users = users;
    }

    public void updateUser(int position, SocialNetworkUser socialNetworkUser){
        users.set(position, socialNetworkUser);
        notifyItemChanged(position);
    }
    @NonNull
    @Override
    public SocialNetworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        System.out.println("Social media recyclerview element " + R.layout.social_media_recyclerview_element);
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
        private FrameLayout frameLayout;

        public SocialNetworkViewHolder(@NonNull View itemView)
        {
            super(itemView);

            socialNetworkImage = itemView.findViewById(R.id.social_media_iv_for_social_picture);
            socialNetworkName = itemView.findViewById(R.id.social_media_tv_for_name);
            socialNetworkUserName = itemView.findViewById(R.id.social_media_tv_for_user_info);
            frameLayout = itemView.findViewById(R.id.social_media_fl_for_all_info);
        }

        private void bind(SocialNetworkUser socialNetworkUser)
        {
            socialNetworkImage.setBackground(socialNetworkUser.getSocialNetworkIcon());
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
                socialNetworkImage.setImageDrawable(socialNetworkUser.getSocialNetworkIcon());

            frameLayout.setOnClickListener(view -> socialNetworkCallBack.onPressed(socialNetworkUser));
        }
    }

    public interface SocialNetworkCallBack {
        void onPressed(SocialNetworkUser socialNetworkUser);
    }
}
