package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final String TAG = "Compose Activity";
    private static final int MAX_TWEET_LENGTH = 140;
    private static EditText etCompose;
    private static Button btnTweet;
    private TwitterClient twitterClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        //initializing reference to twitterClient
        twitterClient = TwitterApp.getRestClient(this);

        //initializing edit text and button
        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);

        //on click listener for tweet button, this should make a twitter API call so that we can actually post the tweet
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                //tweet is empty
                if(tweetContent.isEmpty()){
                    Toast.makeText(ComposeActivity.this, "Your tweet cannot be empty, try again", Toast.LENGTH_LONG).show();
                    return;
                }
                //tweet is too long
                else if(tweetContent.length() > MAX_TWEET_LENGTH){
                    Toast.makeText(ComposeActivity.this, "Your tweet is too long, try again", Toast.LENGTH_LONG).show();
                    return;
                }
                //tweet is valid, so we can make the Post request
                else{
                    twitterClient.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "Post request onFailure");
                            try {
                                //aquired the tweet object

                                Tweet tweet = Tweet.fromJson(json.jsonObject);
                                Intent intent = new Intent();
                                //passing in data to go with our intent back to the Timeline activity
                                intent.putExtra("tweet", Parcels.wrap(tweet));
                                //set the result code as ResultOK
                                setResult(RESULT_OK, intent);
                                //close this activity and move the user back to the Timeline activity
                                finish();
                                Log.i(TAG, "published tweet says: " + tweet);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "Post request onFailure", throwable);
                        }
                    });
                }
            }
        });

    }
}