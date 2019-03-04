package com.example.ian.ecommerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddProductActivity extends AppCompatActivity {
    private String categoryName, description, price, pname, saveCurrentDate, saveCurrentTime;
    private ImageView addNewImage;
    private EditText InputproductName,InputproductDescription,InputproductPrice;
    private Button addNewwProductBtn;
    private ProgressDialog loadingProgress;

    private static final int galleryPick = 1;
    private Uri ImageUri;
    private String productRandomKey, downloadImageUrl;

    private StorageReference ProductImageRef;
    private DatabaseReference productRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_product);

       // Toast.makeText(AdminAddProductActivity.this, categoryName, Toast.LENGTH_SHORT).show();

        categoryName = getIntent().getExtras().get("category").toString();
        ProductImageRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        addNewImage = (ImageView) findViewById(R.id.select_product_image);
        InputproductName = (EditText) findViewById(R.id.input_product_name);
        InputproductDescription = (EditText) findViewById(R.id.input_product_description);
        InputproductPrice = (EditText) findViewById(R.id.input_product_price);
        addNewwProductBtn = (Button) findViewById(R.id.add_product_btn);
        loadingProgress = new ProgressDialog(this);

        addNewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        addNewwProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });

    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE , true);
        startActivityForResult(galleryIntent, galleryPick);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data!=null ){
            if (data.getClipData() != null)
            {
                Toast.makeText(AdminAddProductActivity.this, "You selected multiple imagess", Toast.LENGTH_SHORT).show();
            }else if (data.getData() != null)
            {
                ImageUri = data.getData();
                addNewImage.setImageURI(ImageUri);
                Toast.makeText(AdminAddProductActivity.this, "You selected single images", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void ValidateProductData(){
        description = InputproductDescription.getText().toString();
        price = InputproductPrice.getText().toString();
        pname = InputproductName.getText().toString();

        if (ImageUri==null){
            Toast.makeText(AdminAddProductActivity.this, "Please add product Image", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(AdminAddProductActivity.this, "Please add product description", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(price)) {
            Toast.makeText(AdminAddProductActivity.this, "Please add product price", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pname)) {
            Toast.makeText(AdminAddProductActivity.this, "Please add product name", Toast.LENGTH_SHORT).show();
        }else{
            StoreProductInformation();
        }
    }

    private void StoreProductInformation() {

        loadingProgress.setTitle("Adding new Product");
        loadingProgress.setMessage("Please wait as we add the product to the database");
        loadingProgress.setCanceledOnTouchOutside(false);
        loadingProgress.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate= new SimpleDateFormat("MMM dd yyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime= new SimpleDateFormat("HHH: mm ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey =saveCurrentDate + saveCurrentTime;

        final StorageReference filepath = ProductImageRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");
        final UploadTask uploadTask = filepath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String message = e.toString();
                Toast.makeText(AdminAddProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingProgress.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddProductActivity.this, "Product Image Uploaded successfully: ", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){

                            throw task.getException();

                        }
                        downloadImageUrl = filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AdminAddProductActivity.this, "Got product url successfully", Toast.LENGTH_SHORT).show();

                            saveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }

    private void saveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", description);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", categoryName);
        productMap.put("price", price);
        productMap.put("pname", pname);

        productRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(AdminAddProductActivity.this, AdminCategoryActivity.class);
                            startActivity(intent);

                            loadingProgress.dismiss();
                            Toast.makeText(AdminAddProductActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            loadingProgress.dismiss();
                            String message= task.getException().toString();
                            Toast.makeText(AdminAddProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}

