package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    Context context;
    List<Tweet> tweets;


    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);

    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        Boolean retweeted = false;
        Boolean liked = false;

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        ImageView ivMedia;
        TextView tvRelativeTime;
        TextView tvLikeCount;
        TextView tvRetweetCount;
        ImageView ivLike;
        ImageView ivRetweet;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            tvRelativeTime = itemView.findViewById(R.id.tvRelativeTime);
            tvLikeCount = itemView.findViewById(R.id.tvLike);
            tvRetweetCount = itemView.findViewById(R.id.tvRetweet);
            ivLike = (ImageView) itemView.findViewById(R.id.ivLike);
            ivRetweet = (ImageView) itemView.findViewById(R.id.ivRetweet);

        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            tvRelativeTime.setText(tweet.createdAt);
            tvLikeCount.setText(tweet.likeCount);
            tvRetweetCount.setText(tweet.retweetCount);

            int radius = 50;
            //loading media image with glide into imageview
            Glide.with(context).load(tweet.user.profileImageUrl)
                    .transform(new RoundedCorners(radius)).into(ivProfileImage);

            Glide.with(context).load(tweet.media)
//                    .override(680, 454)
//                    .transform(new RoundedCorners(radius))
                    .into(ivMedia);

            //like imageView functionality
            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!liked){
                        int newCount = Integer.parseInt(tweet.likeCount) + 1;
                        tweet.likeCount = Integer.toString(newCount);
                        tvLikeCount.setText(Integer.toString(newCount));
                        view.setSelected(true);
                        liked = true;
                    }
                    else{
                        int newCount = Integer.parseInt(tweet.likeCount) - 1;
                        tweet.likeCount = Integer.toString(newCount);
                        tvLikeCount.setText(Integer.toString(newCount));
                        view.setSelected(false);
                        liked = false;
                    }

                    Log.i("Tweets Adapter", "in iv onclick listener");
                }
            });

            //retweet imageView functionality
            ivRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!retweeted){
                        int newCount = Integer.parseInt(tweet.retweetCount) + 1;
                        tweet.retweetCount = Integer.toString(newCount);
                        tvRetweetCount.setText(Integer.toString(newCount));
                        view.setSelected(true);
                        retweeted = true;
                    }
                    else{
                        int newCount = Integer.parseInt(tweet.retweetCount) - 1;
                        tweet.retweetCount = Integer.toString(newCount);
                        tvRetweetCount.setText(Integer.toString(newCount));
                        view.setSelected(false);
                        retweeted = false;
                    }

                    Log.i("Tweets Adapter", "in iv onclick listener");
                }
            });

        }
    }
}
