package com.example.thanh.instagram;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.thanh.instagram.models.Comment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentListAdapter extends ArrayAdapter<Comment> {
    // == instants variables ==
    private Context mContext;
    private ArrayList<Comment> commentArrayList;

    public CommentListAdapter(Context context, int resource, ArrayList<Comment> list) {
        super(context, resource, list);
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Comment getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(Comment item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            // get a specific file
            LayoutInflater li = LayoutInflater.from(mContext);
            // choose the file that we want to add data to.
            // now view will be equal a single item.
            view = li.inflate(R.layout.single_comment_item, null);
        }

        Comment comment = getItem(position);
        if (comment != null) {
            CircleImageView profile_photo = view.findViewById(R.id.profile_photo);

            TextView user_name_tv = view.findViewById(R.id.username_tv);
            TextView comment_text = view.findViewById(R.id.comment_tv);
            TextView time = view.findViewById(R.id.time_tv);


            Picasso.get().load(comment.getProfile_image()).error(R.drawable.user).into(profile_photo);

            user_name_tv.setText(comment.getUser_name());
            comment_text.setText(comment.getComment_text());
            time.setText(comment.getTime());

        }
        return view;
    }
}
