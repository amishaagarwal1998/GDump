package g_dump.g_dump;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class Signup extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private EditText editTextPhone,editTextVerification;
    private CheckBox checkBox;
    private Button buttonSignup,buttonVerification;
    private TextView tnc;
    private ImageView google,facebook;
    private ProgressDialog progressDialog;
    private String mVerificationID;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            //that means user is already logged in
            //so close this activity
            finish();

            //and open profile activity
            startActivity(new Intent(getApplicationContext(), Database.class));
            finish();
        }
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        buttonSignup = (Button) findViewById(R.id.buttonSignup);
        progressDialog = new ProgressDialog(this);
        checkBox=(CheckBox)findViewById(R.id.checkBox);
        tnc=(TextView)findViewById(R.id.textView8) ;
        google=(ImageView)findViewById(R.id.imageView) ;
        facebook=(ImageView)findViewById(R.id.imageView2);

        tnc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(Signup.this,tnc.class));

            }
        });




        //attaching listener to button
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });


    }

    private void registerUser(){

        //getting email and password from edit texts
        String phone = editTextPhone.getText().toString().trim();




        if(!Patterns.PHONE.matcher(phone).matches()){
            Toast.makeText(this,"Please enter a valid phone number",Toast.LENGTH_LONG).show();
            editTextPhone.requestFocus();
            return;
        }
        if(!checkBox.isChecked()){
            Toast.makeText(this, "Check the box to accept terms and condition", Toast.LENGTH_SHORT).show();
            checkBox.requestFocus();
            return;
        }



        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();



        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                signInWithPhoneAuthCredential(credential);
                progressDialog.show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(Signup.this,"Verification code is invalid",Toast.LENGTH_SHORT).show();
                    progressDialog.hide();
                    // ...
                } else {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(Signup.this,"An error occured",Toast.LENGTH_SHORT).show();
                    progressDialog.hide();
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                // Save verification ID and resending token so we can use them later
                setContentView(R.layout.verification);
                progressDialog.hide();
                mVerificationID=verificationId;
                mResendToken=token;
                editTextVerification=(EditText)findViewById(R.id.editTextVerification);
                buttonVerification=(Button)findViewById(R.id.buttonVerification);
                buttonVerification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String code=editTextVerification.getText().toString();
                        progressDialog.show();
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationID, code);
                        signInWithPhoneAuthCredential(credential);

                    }
                });


                // ...
            }
        };
        //creating a new user
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone,60,TimeUnit.SECONDS,Signup.this,mCallbacks);

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(Signup.this,"Registration successful",Toast.LENGTH_SHORT).show();
                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference userNameRef = rootRef.child("Users").child(firebaseAuth.getCurrentUser().getPhoneNumber());

                            ValueEventListener eventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists())
                                    {
                                        startActivity(new Intent(Signup.this,Profile.class));
                                        finish();


                                    }
                                    else{
                                        progressDialog.hide();
                                        Intent i=new Intent(Signup.this,Database.class);
                                        startActivity(i);


                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {



                                }
                            };
                            userNameRef.addListenerForSingleValueEvent(eventListener);




                        } else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(Signup.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            progressDialog.hide();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(Signup.this,"Please Enter a Valid verification code",Toast.LENGTH_SHORT).show();
                                progressDialog.hide();// The verification code entered was invalid
                            }
                        }
                    }
                });
    }



}
