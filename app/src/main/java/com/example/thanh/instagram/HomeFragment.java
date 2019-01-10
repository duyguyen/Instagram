package com.example.thanh.instagram;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.thanh.instagram.models.Story;
import com.example.thanh.instagram.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    ListView feed_lv;
    ArrayList<Story> arrayListStories;
    StoryListAdapter storyListAdapter;
    ProgressDialog mProgressDialog;
    JSONArray jsonArrayIds;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        feed_lv = view.findViewById(R.id.feed_lv);
        arrayListStories = new ArrayList<>();
        storyListAdapter = new StoryListAdapter(getContext(), R.layout.feed_single_item, arrayListStories);

        feed_lv.setAdapter(storyListAdapter);

//        mProgressDialog = new ProgressDialog(getContext());
//        mProgressDialog.setTitle("News feed");
//        mProgressDialog.setMessage("Updating new feed ...");

        // call server and get data
        getFollowingIds();

//        Toast.makeText(getContext(), "HomeFragment", Toast.LENGTH_LONG).show();

        return view;
    }

    private void getFollowingIds() {

//        mProgressDialog.show();

        User user = SharePreferenceManager.getInstance(getContext()).getUserData();
        final int user_id = user.getId();


        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                URLS.get_following_ids + user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean("error")) {

                                JSONArray jsonArrayIds = jsonObject.getJSONArray("ids");

                                // remove brackets
                                String ids = jsonArrayIds.toString();
                                ids = ids.replace("[", "");
                                ids = ids.replace("]", "");

                                // after having ids, we make another request
                                // to get stories of those ids inside the database
                                getLatestNewFeed(ids);

                            } else {
                                Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                mProgressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                    }
                }
        );// end of the stringRequest

        VolleyHandler.getInstance(getContext().getApplicationContext()).addRequestToQueued(stringRequest);
    }

    private void getLatestNewFeed(String ids) {



        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                URLS.latest_news_feed + ids,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean("error")) {
//                                mProgressDialog.dismiss();
                                JSONArray jsonObjectStories = jsonObject.getJSONArray("stories");

                                for (int i = 0; i < jsonObjectStories.length(); i++) {
                                    JSONObject jsonObjectSingleStory = jsonObjectStories.getJSONObject(i);
                                    Story story = new Story(
                                            jsonObjectSingleStory.getInt("id"),
                                            jsonObjectSingleStory.getInt("user_id"),
                                            jsonObjectSingleStory.getInt("num_of_like"),
                                            jsonObjectSingleStory.getString("image_url"),
                                            jsonObjectSingleStory.getString("title"),
                                            jsonObjectSingleStory.getString("time"),
                                            jsonObjectSingleStory.getString("profile_image"),
                                            jsonObjectSingleStory.getString("username")

                                    );
                                    arrayListStories.add(story);
                                }

                                // tell arrayAdapter that data has been added
                                storyListAdapter.notifyDataSetChanged();


                            } else {
                                Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
//                                mProgressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
//                        mProgressDialog.dismiss();
                    }
                }
        ); // end of the stringRequest

        VolleyHandler.getInstance(getContext().getApplicationContext()).addRequestToQueued(stringRequest);
    }

}
