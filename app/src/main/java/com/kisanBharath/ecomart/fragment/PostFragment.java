package com.kisanBharath.ecomart.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kisanBharath.ecomart.R;
import com.kisanBharath.ecomart.activity.MainActivity;
import com.kisanBharath.ecomart.models.ImageModel;
import com.kisanBharath.ecomart.models.Post;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;


public class PostFragment extends Fragment {

    //widgets
    private ImageView mPostImage;
    private EditText mTitle, mDescription, mPrice, mContactEmail;
    private Button btn_post;
    public static ProgressDialog progressDialog;
    private Spinner spinner_categories;
    private String selected_category;

    private static final String TAG = "PostFragment";
    //private AlertDialog progressDialog;
    private LinearLayout location_capture;
    private TextView address_tv, alert;

    private final int LOCATION_REQUEST_CODE = 1001;
    private final int PICK_IMAGE = 1002;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private double Lat, Lan;

    private final ArrayList<Uri> imageList = new ArrayList<>();
    private Uri imageUri;
    private int upload_count = 0;
    private String postId;
    private ArrayList<String> urlStrings = new ArrayList<>();
    private String address;

    private ImageModel imageModel;
    private Post post;

    DatabaseReference databaseReference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        mTitle = view.findViewById(R.id.input_title);
        mDescription = view.findViewById(R.id.input_description);
        mPrice = view.findViewById(R.id.input_price);
        mContactEmail = view.findViewById(R.id.input_email);

        spinner_categories = view.findViewById(R.id.spinner_categories);
        location_capture = view.findViewById(R.id.location_capture);
        address_tv = view.findViewById(R.id.address_tv);
        mPostImage = view.findViewById(R.id.post_image);
        alert = view.findViewById(R.id.alert);
        btn_post = view.findViewById(R.id.btn_post);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());


        postId = FirebaseDatabase.getInstance().getReference().push().getKey();

        imageModel = new ImageModel();
        post = new Post();

        ArrayList<String> categories_list = new ArrayList<String>();
        categories_list.add("ವಿಭಾಗವನ್ನು ಆಯ್ಕೆಮಾಡಿ");
        categories_list.add("ತರಕಾರಿಗಳು");
        categories_list.add("ಹೂಗಳು");
        categories_list.add("ಹಣ್ಣುಗಳು");
        categories_list.add("ಧಾನ್ಯ ಮತ್ತು ಮಸಾಲೆಗಳು");
        categories_list.add("ನರ್ಸರಿ ಸಸ್ಯಗಳು");
        categories_list.add("ಅಕ್ಕಿ");
        categories_list.add("ಗೋಧಿ");
        categories_list.add("ರಾಗಿ");
        categories_list.add("ತೈಲ");
        categories_list.add("ಇತರೆ");

        showProgressDialog();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Objects.requireNonNull(getActivity()),
                R.layout.simple_spinner_item, categories_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_categories.setAdapter(adapter);

        hideProgressDialog();

        init();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        return view;
    }

    private void init() {
        spinner_categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    selected_category = "ಇತರೆ";
                }

                if (position > 0) {
                    selected_category = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        location_capture.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                askLocationPermission();
            }

        });
        mPostImage.setOnClickListener(v -> {
            Log.d(TAG, "onClick: opening dialog to choose new photo");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, PICK_IMAGE);
        });
        btn_post.setOnClickListener(v -> {

            if (!mTitle.getText().toString().isEmpty()) {
                if (!mDescription.getText().toString().isEmpty()) {
                    if (!mPrice.getText().toString().isEmpty()) {
                        Log.d(TAG, "init: " + address);
                        if (address != null) {
                            if (!alert.getText().toString().equals("Take a Photo")) {
                                UploadPost();
                            } else
                                Toast.makeText(getContext(), "Please upload images...", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getContext(), "Please click on the location button", Toast.LENGTH_SHORT).show();
                    } else mPrice.setError("Enter Price");
                } else mDescription.setError("Enter Description");
            } else mTitle.setError("Enter Title");

            //UploadPost();
        });
    }

    private void UploadPost() {
        showProgressDialog();
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("posts/users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() +
                        "/" + postId + "/post_image");

        databaseReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.node_posts)).child(postId);
        post.setLocation(address);
        post.setContactEmail(mContactEmail.getText().toString());
        post.setDescription(mDescription.getText().toString());
        post.setPostId(postId);
        post.setPrice(mPrice.getText().toString());
        post.setTitle(mTitle.getText().toString());
        post.setMainCategories(selected_category);
        post.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        post.setDate(getDateTime());
        post.setPhoneNumber(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());


        //post.setTimestamp(ServerValue.TIMESTAMP);

        for (upload_count = 0; upload_count < imageList.size(); upload_count++) {
            Uri individualImage = imageList.get(upload_count);
            StorageReference imageName = storageReference.child("Image" + individualImage.getLastPathSegment());
            imageName.putFile(individualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageName.getDownloadUrl().addOnSuccessListener(uri -> {
                        urlStrings.add(String.valueOf(uri));
                        //String url = String.valueOf(uri);
                        // databaseReference.setValue(post);
                        if (urlStrings.size() == imageList.size()) {
                            StoreLink(urlStrings);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String getDateTime() {
        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading please wait...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    @SuppressLint("SetTextI18n")
    private void StoreLink(ArrayList<String> urlStrings) {

        HashMap<String, String> hashMap = new HashMap<>();

        HashMap<HashMap<String, String>, Object> hashMap1 = new HashMap<>();

        for (int i = 0; i < urlStrings.size(); i++) {
            imageModel.setImg(urlStrings.get(i));
            hashMap.put("ImgLink" + i, imageModel.getImg());
        }

       /* databaseReference.setValue(hashMap)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Successfully Uplosded", Toast.LENGTH_SHORT).show();
                                    post.setImages(hashMap);
                                    databaseReference.setValue(post);
                                   // startActivity(new Intent(getContext(), MainActivity.class));
                                }
                            }
                        }
                ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });*/

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Toast.makeText(getContext(), "Successfully Uplosded", Toast.LENGTH_SHORT).show();
                post.setImages(hashMap);
                databaseReference.setValue(post);
                startActivity(new Intent(getContext(), MainActivity.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        hideProgressDialog();
        alert.setText("Uploaded Successfully");
        //uploaderBtn.setVisibility(View.GONE);

        imageList.clear();
    }

    private void getLastLocation() {

        @SuppressLint("MissingPermission")
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();

        locationTask.addOnSuccessListener(location -> {

            if (location != null) {
                //we have the location
                Log.d(TAG, "onSuccess: " + location.toString());
                Log.d(TAG, "getLatitude: " + location.getLatitude());
                Log.d(TAG, "getLongitude: " + location.getLongitude());

                Lat = location.getLatitude();
                Lan = location.getLongitude();

                getAddressFromLatAndLang(Lat, Lan);

                location_capture.setVisibility(View.GONE);
            }

        }).addOnFailureListener(e -> {
            Log.e(TAG, "onFailure: " + e.getMessage());
            Toast.makeText(getContext(), "Please enable the location", Toast.LENGTH_SHORT).show();
        });

    }

    private void getAddressFromLatAndLang(double lat, double lan) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lan, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        address = addresses.get(0).getAddressLine(0);
        address_tv.setText(address);
        address_tv.setVisibility(View.VISIBLE);
        Log.d(TAG, "getAddressFromLatAndLang: " + addresses.get(0).getAddressLine(0));
    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(TAG, "getLastLocation: you should show an dialog...");

                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted
                getLastLocation();
            } else {
                //permission not granted

            }
        }
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {

                int countClipData = data.getClipData().getItemCount();

                int currentImageSelect = 0;
                while (currentImageSelect < countClipData) {
                    imageUri = data.getClipData().getItemAt(currentImageSelect).getUri();

                    if (imageList.size() < 3)
                        imageList.add(imageUri);
                    else
                        Toast.makeText(getContext(), "only 3 images can upload...", Toast.LENGTH_SHORT).show();

                    currentImageSelect = currentImageSelect + 1;
                }
                Picasso.get().load(imageList.get(0)).placeholder(R.drawable.add_photo).into(mPostImage);
                alert.setText("You have selected " + imageList.size() + " Images");
                alert.setTextColor(Color.parseColor("#33cc33"));


            } else {
                Toast.makeText(getContext(), "select images", Toast.LENGTH_SHORT).show();

                imageUri = data.getData();
                imageList.add(imageUri);

                Picasso.get().load(imageList.get(0)).placeholder(R.drawable.add_photo).into(mPostImage);

                alert.setText("You have selected " + imageList.size() + " Images");
                alert.setTextColor(Color.parseColor("#33cc33"));
            }
        }
    }
}