package com.example.thanh.instagram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.thanh.instagram.models.Comment;
import com.example.thanh.instagram.models.User;
import com.example.thanh.instagram.utils.TimeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    // == constants ==
    private ImageView back_arrow;
    private ListView comment_lv;
    private EditText comment_et;
    private ImageButton comment_send_btn;
    private ArrayList<Comment> commentArrayList;
    private CommentListAdapter commentListAdapter;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        back_arrow = findViewById(R.id.back_arrow);
        comment_lv = findViewById(R.id.comment_lv);
        comment_et = findViewById(R.id.comment_et);
        comment_send_btn = findViewById(R.id.comment_send_btn);

        commentArrayList = new ArrayList<>();
        commentListAdapter = new CommentListAdapter(getApplicationContext(), R.layout.single_comment_item, commentArrayList);
        comment_lv.setAdapter(commentListAdapter);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("News feed");
        mProgressDialog.setMessage("Updating new feed ...");

        // get story_id from StoryListAdapter class
        final int story_id = getIntent().getIntExtra("story_id", 0);
        getAllComments(story_id);

        comment_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCommentToDatabase(story_id);
            }
        });

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackFeed();
            }
        });
    }
    private void getAllComments(int story_id) {

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                URLS.get_all_comments + story_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean("error")) {
                                mProgressDialog.dismiss();
                                JSONArray jsonObjectComments = jsonObject.getJSONArray("comments");

                                for (int i = 0; i < jsonObjectComments.length(); i++) {
                                    JSONObject jsonObjectSingleComment = jsonObjectComments.getJSONObject(i);
                                    Comment comment = new Comment(
                                            jsonObjectSingleComment.getInt("id"),
                                            jsonObjectSingleComment.getInt("user_id"),
                                            jsonObjectSingleComment.getInt("story_id"),
                                            jsonObjectSingleComment.getString("username"),
                                            jsonObjectSingleComment.getString("user_image"),
                                            jsonObjectSingleComment.getString("comment_text"),
                                            jsonObjectSingleComment.getString("time")
                                    );
                                    commentArrayList.add(comment);
                                }
                                // tell arrayAdapter that data has been added
                                commentListAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(CommentActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(CommentActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                    }
                }
        ); // end of the stringRequest

        VolleyHandler.getInstance(this).addRequestToQueued(stringRequest);
    }

    private void sendCommentToDatabase(final int story_id) {
        final String comment_text = comment_et.getText().toString();
        if (comment_text.equals("")) {
            return;
        }

        User user = SharePreferenceManager.getInstance(this).getUserData();
        final String username = user.getUsername();
        final int user_id = user.getId();
        final String profile_image = user.getImage();
        final String currentReadableTime = TimeUtils.currentReadableTime();


        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URLS.send_comments,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean("error")) {
                                mProgressDialog.dismiss();
                                JSONObject jsonObjectUser = jsonObject.getJSONObject("comment");

                                Comment comment = new Comment(
                                        jsonObjectUser.getInt("comment_id"),
                                        jsonObjectUser.getInt("user_id"),
                                        jsonObjectUser.getInt("story_id"),
                                        jsonObjectUser.getString("username"),
                                        jsonObjectUser.getString("profile_image"),
                                        jsonObjectUser.getString("comment_text"),
                                        jsonObjectUser.getString("time"));

                                commentArrayList.add(comment);
                                commentListAdapter.notifyDataSetChanged();
                                comment_et.setText("");
                            } else {
                                Toast.makeText(CommentActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CommentActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // map could hold any type of data
                // Volley doesn't allow to send an int.
                // We have to convert int to string where needed.
                Map commentData = new HashMap<>();
                commentData.put("story_id", String.valueOf(story_id));
                commentData.put("user_id", String.valueOf(user_id));
                commentData.put("username", username);
                commentData.put("user_image", profile_image);
                commentData.put("comment_text", comment_text);
                commentData.put("time", currentReadableTime);
                return commentData;
            }

        };// end of the stringRequest

        VolleyHandler.getInstance(this).addRequestToQueued(stringRequest);
    }

    private void goBackFeed() {
        startActivity(new Intent(this, MainActivity.class));
    }

}
