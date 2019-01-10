package com.example.thanh.instagram;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.thanh.instagram.models.User;
import com.example.thanh.instagram.utils.ImageUtil;
import com.example.thanh.instagram.utils.TimeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class CameraFragment extends Fragment {

    Button upload_bnt;
    Button capture_bnt;
    ImageView capture_iv;
    Uri mImageUri;
    final int CAPTURE_IMAGE = 1, GALLERY_PICK = 2;
    Bitmap bitmap;
    String mStoryTitle, imageToString, mProfileImage;
    boolean okToUpload;

    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*
     * view will store fragment_camera.
     * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        upload_bnt = view.findViewById(R.id.upload_btn);
        capture_bnt = view.findViewById(R.id.capture_btn);
        capture_iv = view.findViewById(R.id.capture_iv);
        okToUpload = false; // no image to upload.

        return view;
    }


    /*
     * ACTION_GET_CONTENT will open the folder
     * */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // have to call this first to store image.
        getProfileImage();

        capture_bnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] options = {"Choose From Gallery", "Take Photo"};
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Choose Image");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            // choose from gallery
                            case 0:
                                Intent galleryIntent = new Intent();
                                galleryIntent.setType("image/*");
                                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLERY_PICK);
                                break;

                            // take a photo using camera
                            case 1:
                                capturePhoto();
                                break;
                        }
                    }
                });
                builder.show();
            }
        });

        upload_bnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storyAndImageTitle();
            }
        });


    }

    private void capturePhoto() {

        /*
         *
         * @param cameraIntent takes us to the camera.
         * @param mImageUri stores image in external directory.
         * start the camera.
         * put output to the mImageUri.
         **/

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String imageName = "image.jpg"; // temporary name
        mImageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), imageName));
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(cameraIntent, CAPTURE_IMAGE);


    }

    /*
     * mImageUri has already set in capturePhoto().
     * set bitmap by mImageUri.
     * fi bitmap is not null -> set okToUpload.
     * display image bitmap to capture_iv which is an imageView.
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (mImageUri != null) {

                    // convert uri to bit map.
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), mImageUri);

                        if (bitmap != null) {
                            okToUpload = true; // user has already chosen image to upload.
                            capture_iv.setImageBitmap(bitmap);
                            Log.i("bitmap", bitmap.toString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        if (requestCode == GALLERY_PICK) {
            if (resultCode == RESULT_OK) {
                okToUpload = true;
                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                    Toast.makeText(getContext(), "Now Click on Upload Button to Upload image", Toast.LENGTH_LONG).show();
//                    Log.i("Check: ", bitmap.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                capture_iv.setImageBitmap(bitmap);
            }
        }
    }

    /*
     * when click OK : set mStoryTitle.
     * set imageToString by convertImageToString() because bitmap was already set.
     * uploadStory() after we have all the info.
     * */
    private void storyAndImageTitle() {
        final EditText editText = new EditText(getContext());
        editText.setTextColor(Color.BLACK);
        editText.setHint("Set Title/Tag for story");
        editText.setHintTextColor(Color.GRAY);

        // create a dialog that the user can type text inside
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Story title");
        builder.setCancelable(false); // if the user can cancel when they click outside the dialog.
        builder.setView(editText);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (okToUpload) {
                    mStoryTitle = editText.getText().toString();
                    // convert image to string
                    imageToString = convertImageToString();
                    uploadStory();
                } else {
                    Toast.makeText(
                            getContext(),
                            "Please upload the photo first",
                            Toast.LENGTH_LONG
                    ).show();

                }


            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // cancel the dialog.
            }
        });

        builder.show(); // shows the dialog.
    }

    /*
     * need to check whether user upload image yet. Unless the app's gonna crash.
     * dateOfImage will be set by dateOfImage() but not readable.
     * currentTime will be set by currentReadableTime(). This will be readable.
     * get user's info from SharePreferenceManager.
     * set username && user_id.
     *
     * for uploaded image the user might change a bunch of time.
     * so we need to make a request to store image in server, couldn't use share preference.
     * */
    private void uploadStory() {

        // check if the user upload image.
        if (!okToUpload) {
            Toast.makeText(getContext(), "There is no image to upload", Toast.LENGTH_LONG).show();
            return;
        }

        final String dateOfImage = TimeUtils.dateOfImage();
        final String currentTime = TimeUtils.currentReadableTime();
        User user = SharePreferenceManager.getInstance(getContext()).getUserData();
        final String userName = user.getUsername();
        final int user_id = user.getId();

        final String profile_image = mProfileImage;



        final ProgressDialog mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle("Uploading Story");
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URLS.upload_story_image,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean("error")) {
                                mProgressDialog.dismiss();

                                Toast.makeText(getContext(),"story uploaded successfully", Toast.LENGTH_LONG).show();

                                // get back to home fragment
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.main_fragment_content, new HomeFragment());
                                ft.commit();
                            } else {
                                Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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
        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> imageMap = new HashMap<>();
                imageMap.put("image_name", dateOfImage); // should be unique.
                imageMap.put("image_encoded", imageToString); // string form of the image.
                imageMap.put("title", mStoryTitle);
                imageMap.put("time", currentTime);
                imageMap.put("username", userName);
                imageMap.put("user_id", String.valueOf(user_id));
                imageMap.put("profile_image", profile_image);
                return imageMap;
            }

        };// end of the stringRequest

        VolleyHandler.getInstance(getContext().getApplicationContext()).addRequestToQueued(stringRequest);
        okToUpload = false; // make the user can't upload the same image again.
    }

    /*
     * user ByteArrayOutputStream class to hold the compress of bit map image.
     * convert compress to byte array.
     * Base64 will convert byte array to string object.
     * */
    private String convertImageToString() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageByteArray = baos.toByteArray();
        String result = Base64.encodeToString(imageByteArray, Base64.DEFAULT);

        return result;
    }

    /*private String dateOfImage() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis()); // get the specific time. (EX: 87546987) not readable.
        return timestamp.toString();
    }

    private String currentReadableTime() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Date date = new Date(timestamp.getTime()); // readable (EX: 4/2/2018)
        return date.toString();
    }*/

    /*
     * user will store user's information by connecting to share preferences storage.
     * stringRequest object will be used for volley. This will be running
     * the network operations, reading from and writing to the cache, and
     * parsing responses.
     *
     * URLS.get_user_data + user_id which is the second param of stringRequest object
     * will be a URL + user_id.
     * */
    private void getProfileImage() {

        User user = SharePreferenceManager.getInstance(getContext()).getUserData();
//        int user_id = 3;
        int user_id = user.getId();

//        Log.i("Check: ", user_id+"");

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                URLS.get_user_data + user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (!jsonObject.getBoolean("error")) {

                                JSONObject jsonObjectUser = jsonObject.getJSONObject("user");

                                mProfileImage = jsonObjectUser.getString("image");
//                                Log.i("Check: ", mProfileImage+"");

                            } else {
                                Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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

                    }
                }
        );
        VolleyHandler.getInstance(getContext().getApplicationContext()).addRequestToQueued(stringRequest);
    }
}
