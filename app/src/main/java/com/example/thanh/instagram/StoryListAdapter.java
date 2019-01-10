package com.example.thanh.instagram;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.thanh.instagram.models.Story;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryListAdapter extends ArrayAdapter<Story> {

    // == instants variables ==
    private Context mContext;
    ArrayList<Story> storyArrayList;

    public StoryListAdapter(Context context, int resource, ArrayList list) {
        super(context, resource, list);
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Story getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(Story item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    /*
     * each time data is added to array then we will call getView methods
     * it will instantiate view variable, create a new feed_single_item
     * then it will getItem at a specific position in our array list
     * then it will store it in the new object story.
     * */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            // get a specific file
            LayoutInflater li = LayoutInflater.from(mContext);
            // choose the file that we want to add data to.
            // now view will be equal a single item.
            view = li.inflate(R.layout.feed_single_item, null);
        }

        Story story = getItem(position);
        if (story != null) {

            CircleImageView profile_photo = view.findViewById(R.id.profile_photo);
            SquareImageView story_image = view.findViewById(R.id.story_image);

            TextView user_name_tv = view.findViewById(R.id.user_name_tv);
            TextView num_of_likes = view.findViewById(R.id.num_of_likes);
            TextView image_tag = view.findViewById(R.id.image_tag);
            TextView image_time = view.findViewById(R.id.image_time);
            TextView view_all_comments = view.findViewById(R.id.view_all_comments);

//            profile_photo.setImageURI(Uri.parse(story.getProfile_image())); // convert image to Uri, and add data to feed_single_item
//            story_image.setImageURI(Uri.parse(story.getStory_image()));

            Picasso.get().load(story.getProfile_image()).error(R.drawable.user).into(profile_photo);
            Picasso.get().load(story.getStory_image()).error(R.drawable.user).into(story_image);

            user_name_tv.setText(story.getUsername());
            num_of_likes.setText(story.getLike() + " likes");
            image_tag.setText(story.getTitle());
            image_time.setText(story.getTime());

            // view all comments
            viewAllComments(view_all_comments, story.getId());
        }
        return view;
    }

    private void viewAllComments(TextView view_all_comments, final int id){
        view_all_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewAllCommentsIntent = new Intent(getContext(), CommentActivity.class);
                viewAllCommentsIntent.putExtra("story_id", id); // pass story_id to CommentActivity
                getContext().startActivity(viewAllCommentsIntent);
            }
        });
    }
}
