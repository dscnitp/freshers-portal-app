package com.dscnitp.freshersportal.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dscnitp.freshersportal.Common.Node;
import com.dscnitp.freshersportal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private TextInputEditText Name, Branch, RollNo, Year;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseStorage mStorage;
    private DatabaseReference databaseReferenceUsers;
    private String PhotoUrl;
    private Uri ServerFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");



        update = (Button) findViewById(R.id.update);
        update.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        Name = (TextInputEditText) findViewById(R.id.name);
        RollNo = (TextInputEditText) findViewById(R.id.roll);
        Branch = (TextInputEditText) findViewById(R.id.branch);
        Year = (TextInputEditText) findViewById(R.id.year);
        ImageView ivProfile = findViewById(R.id.profileimage);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child(Node.Users);
            databaseReferenceUsers.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(Node.Name).getValue() != null)
                        Name.setText(dataSnapshot.child(Node.Name).getValue().toString());

                    if (dataSnapshot.child(Node.ROLL_NO).getValue() != null)
                        RollNo.setText(dataSnapshot.child(Node.ROLL_NO).getValue().toString());

                    if (dataSnapshot.child(Node.Branch).getValue() != null)
                        Branch.setText(dataSnapshot.child(Node.Branch).getValue().toString());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            ServerFileUri = currentUser.getPhotoUrl();

            Glide.with(this)
                    .load(ServerFileUri)
                    .error(R.mipmap.ic_launcher_foreground)
                    .placeholder(R.mipmap.ic_launcher_foreground)
                    .load(ivProfile);
        }
    }


                openActivity();
            }
        });
    }


    public void openActivity() {
        Intent intent = new Intent(EditProfileActivity.this, ProfileFragment.class);
        TextInputEditText name= (TextInputEditText) findViewById(R.id.name);
        String text = name.getText().toString();
        TextInputEditText roll= (TextInputEditText) findViewById(R.id.roll);
        String text1 = roll.getText().toString();
        TextInputEditText branch= (TextInputEditText) findViewById(R.id.branch);
        String text2 = branch.getText().toString();
        TextInputEditText year= (TextInputEditText) findViewById(R.id.year);
        String text3 = year.getText().toString();
        intent.putExtra("mytext", text);
        intent.putExtra("mytext1",text1);
        intent.putExtra("mytext2",text2);
        intent.putExtra("mytext3",text3);

        startActivity(intent);

    public void btnSave(View V) {
        if (Name.getText().toString().trim().equals("")) {
            Name.setError(getString(R.string.etName));
        }
        if (RollNo.getText().toString().trim().equals("")) {
            RollNo.setError(getString(R.string.etRoll));
        }
        if (Branch.getText().toString().trim().equals("")) {
            Branch.setError(getString(R.string.etBranch));
        }
        if (Year.getText().toString().trim().equals("")) {
            Year.setError(getString(R.string.etYear));
        } else {
            //            if(localFileUri!=null)
            //                updateNameAndPhoto();
            //            else
            updateNameOnly();
        }


    }
    public void updateNameOnly()
    {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().
                setDisplayName(Name.getText().toString().trim()).build();


        currentUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    String UserID=currentUser.getUid();
                    HashMap hashMap=new HashMap();
                    hashMap.put(Node.Name,Name.getText().toString().trim());
                    hashMap.put(Node.ROLL_NO,RollNo.getText().toString().trim());
                    hashMap.put(Node.Branch,Branch.getText().toString().trim());

                    databaseReferenceUsers.child(UserID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                finish();
                            }
                            else
                            {
                                Toast.makeText(EditProfileActivity.this,"User Not Created",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(EditProfileActivity.this,"Failed to Update : %1$s"+task.getException(),Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
