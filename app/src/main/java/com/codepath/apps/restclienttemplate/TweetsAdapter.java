package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

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

    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final int REQUEST_CODE = 21;

        private TwitterClient twitterClient;


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
        ImageView ivReply;

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
            ivReply = (ImageView) itemView.findViewById(R.id.ivReply);

            //a reference to the twitterClient to make the post request here instead
            twitterClient = TwitterApp.getRestClient(context);

            tvBody.setMovementMethod(LinkMovementMethod.getInstance());


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

            Glide.with(context).load(tweet.media).into(ivMedia);

            //like imageView functionality
            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!liked){
                        Toast.makeText(context, "Post Liked", Toast.LENGTH_SHORT).show();
                        int newCount = Integer.parseInt(tweet.likeCount) + 1;
                        tweet.likeCount = Integer.toString(newCount);
                        tvLikeCount.setText(Integer.toString(newCount));
                        view.setSelected(true);
                        liked = true;
                    }
                    else{
                        Toast.makeText(context, "Post Unliked", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(context, "You just retweeted this post, Nice!", Toast.LENGTH_LONG).show();
                        twitterClient.publishRetweet(tweet.body, tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i(context.toString(), "Post request (RETWEET) onSuccess");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(context.toString(), "Post request (RETWEET) onFailure", throwable);
                            }
                        });

                        // Intent intent = new Intent(context, RetweetActivity.class);
                       // intent.putExtra("tweet", Parcels.wrap(tweet));
                       // context.startActivity(intent);
                    }
                    else{
                        Toast.makeText(context, "You just un-retweeted this post", Toast.LENGTH_LONG).show();
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
