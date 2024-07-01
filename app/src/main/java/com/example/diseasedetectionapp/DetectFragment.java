package com.example.diseasedetectionapp;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diseasedetectionapp.ml.DiseaseDetection;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.Permission;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetectFragment extends Fragment {
    final static int CAMERA_REQUEST_CODE = 11;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DetectFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetectFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetectFragment newInstance(String param1, String param2) {
        DetectFragment fragment = new DetectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        View view = inflater.inflate(R.layout.fragment_detect, container, false);
        Button cameraButton = view.findViewById(R.id.cameraButton);
        Button galleryButton = view.findViewById(R.id.galleryButton);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},  CAMERA_REQUEST_CODE);
                }



            }
        });
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 99);
                }

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), 2);


            }
        });

        return view;
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_detect, container, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1);
            } else if(!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("This feature is unavailable because this feature requires permission that you have denied. Please allow Camera Permission from settings to proceed further")
                        .setTitle("Permission Required")
                        .setCancelable(false)
                        .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()))
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);

                                dialog.dismiss();
                            }
                        });
                builder.show();
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA},  CAMERA_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap image = null;
        if (requestCode == 1 && resultCode == RESULT_OK) {
           image = (Bitmap) data.getExtras().get("data");

        }
        else if (requestCode == 2 && resultCode == RESULT_OK) {
            try {
                Uri uri = data.getData();
                InputStream imageStream = getActivity().getContentResolver().openInputStream(uri);
                image = BitmapFactory.decodeStream(imageStream);
            } catch (IOException e) {
//                Handle Exception
            }
        }

        if (resultCode == RESULT_OK) {
            ImageView imageView = getView().findViewById(R.id.imageView5);
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);
            classifyImage(image);
        }

    }

    private void classifyImage(Bitmap image) {
        try {
            int imageSize = 224;
            float threshold = 0.6f;
            DiseaseDetection model = DiseaseDetection.newInstance(getActivity().getApplicationContext());
            // Creates inputs for reference
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4*imageSize*imageSize*3);
            byteBuffer.order(ByteOrder.nativeOrder());

            // resize input image
            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, true);

            int [] intValues = new int[imageSize*imageSize];
            image.getPixels(intValues,0,image.getWidth(),0,0,image.getWidth(),image.getHeight());
            int pixel = 0;
            for(int i = 0; i < imageSize; i++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF)*(1.f/255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF)*(1.f/255.f));
                    byteBuffer.putFloat((val & 0xFF)*(1.f/255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            DiseaseDetection.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;
            for(int i = 0; i < confidences.length; i++){
                if(confidences[i] > maxConfidence){
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }

            // Define the classes
            String[] classes = {"Healthy Rice Crop", "Rice Bacterial Leaf Blight Disease", "Rice Leaf Brown Spot Disease", "Rice Leaf Scald Disease", "Rice Neck Blast Disease", "Rice Hispa Disease", "Rice Tungro Disease", "Rice False Smut Disease", "Rice Stem Rot", "Invalid"};

            // Display confidence level
            TextView confidenceHeader = getView().findViewById(R.id.textView4);
            confidenceHeader.setVisibility(View.VISIBLE);
            TextView confidenceTextView = getView().findViewById(R.id.confidenceLevelText);
            confidenceTextView.setVisibility(View.VISIBLE);
            String confidenceText = "Confidence Level: " + String.format("%.2f", maxConfidence * 100) + "%";
            confidenceTextView.setText(confidenceText);
            TextView result = getView().findViewById(R.id.resultText);
            TextView clickableText = getView().findViewById(R.id.clickableText);
            if (maxConfidence < threshold) {
                // If confidence is below threshold, display message
                result.setText(R.string.image_is_not_recognized);
            } else {
                // Check if predicted class index is within bounds
                if (maxPos >= 0 && maxPos < classes.length) {
                    // Display the predicted class
                    result.setText(classes[maxPos]);
                } else {
                    // If predicted class index is out of bounds, display unknown class
                    result.setText("Unknown");
                }
            }

            if (result.getText() != "Invalid") {

                clickableText.setVisibility(View.VISIBLE);
                clickableText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.google.com/search?q="+result.getText())));
                    }
                });
            }

            model.close();

        } catch (IOException e) {
//            catch error
        }
    }
}