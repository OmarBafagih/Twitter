package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;



public class TimelineActivity extends AppCompatActivity{

    private SwipeRefreshLayout swipeContainer;


    private final int REQUEST_CODE = 20;

    private TwitterClient client;
    private RecyclerView rvTweets;
    private List<Tweet> tweets;
    private TweetsAdapter adapter;
    private ImageButton btnCompose;
    public static ImageView ivRetweet;

    public static final String TAG = "TimelineActivity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setTheme(R.style.darkTheme);
        setContentView(R.layout.activity_timeline);

        //Toolbar toolbar = (Toolbar) findViewById()
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);


        client = TwitterApp.getRestClient(this);//

        rvTweets = findViewById(R.id.rvTweets); // find recyclerview by id
        tweets = new ArrayList<>(); // initialize list of tweets
        adapter = new TweetsAdapter(this, tweets); //initializing tweets adapter

        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(rvTweets.getLayoutParams());
        marginLayoutParams.setMargins(0, 100, 0, 10);
        rvTweets.setLayoutParams(marginLayoutParams);
        rvTweets.requestLayout();

        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter); // using the tweetsAdapter within this recycler view

        btnCompose = (ImageButton) findViewById(R.id.btnCompose); // refers to the logout button within the view
        btnCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TimelineActivity.this, ComposeActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        populateHomeTimeline(); //populating timeline using this method

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //ivReply = findViewById(R.id.ivRetweet);
        /*ivRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/


    }


    public void reply(View view){

    }


    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        adapter.clear();
        populateHomeTimeline();
        swipeContainer.setRefreshing(false);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //add the menu items to the action bar/inflate the menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //FToolbar toolbar = findViewById(R.)
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         switch (item.getItemId()){
             case R.id.logOut:
                 //starts the activity in which we can compose a new tweet
                 //Intent intent = new Intent(this, ComposeActivity.class);
                 //startActivityForResult(intent, REQUEST_CODE);
                 finish();
                 client.clearAccessToken();
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


