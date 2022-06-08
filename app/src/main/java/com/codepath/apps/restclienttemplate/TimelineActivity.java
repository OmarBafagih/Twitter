package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    private final int REQUEST_CODE = 20;

    private TwitterClient client;
    private RecyclerView rvTweets;
    private List<Tweet> tweets;
    private TweetsAdapter adapter;
    private Button btnLogout;

    public static final String TAG = "TimelineActivity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient(this);//

        rvTweets = findViewById(R.id.rvTweets); // find recyclerview by id
        tweets = new ArrayList<>(); // initialize list of tweets
        adapter = new TweetsAdapter(this, tweets); //initializing tweets adapter

        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter); // using the tweetsAdapter within this recycler view

        btnLogout = findViewById(R.id.btnLogout); // refers to the logout button within the view
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                client.clearAccessToken();
            }
        });

        populateHomeTimeline(); //populating timeline using this method

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //add the menu items to the action bar/inflate the menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         switch (item.getItemId()){
             case R.id.compose:
                 //starts the activity in which we can compose a new tweet
                 Intent intent = new Intent(this, ComposeActivity.class);
                 startActivityForResult(intent, REQUEST_CODE);
                 return true;
             default:
                 break;
         }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //if the request code is the same as the originally used request code for this request
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            //unwrapping Parcel sent with intent from ComposeActivity closure
                //this is the Tweet object obtained from the parcel
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
                //adding the tweet to the tweets arrayList
            tweets.add(0, tweet);
                //notifying the adapter that an item has been inserted (this will actually make the
                //adapter check the array for new items and reflect that change on the recycler view
            adapter.notifyItemInserted(0);
                //making the recycler view scroll up to the top
                //since a new tweet has been added to the top of the list (position 0)
            rvTweets.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess");
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "Json Exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "OnFailure", throwable);
            }
        });
    }
}
