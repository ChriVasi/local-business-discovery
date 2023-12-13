package local.business.discovery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private ImageView showPasswordImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        showPasswordImageView = findViewById(R.id.showPasswordImageView);

        // Set click listener for the showPasswordImageView
        showPasswordImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

        Button loginButton = findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredEmail = emailEditText.getText().toString().trim();
                String enteredPassword = passwordEditText.getText().toString().trim();

                if (isValidEmail(enteredEmail) && isValidPassword(enteredPassword)) {
                    saveCredentials(enteredEmail, enteredPassword);
                    startMainActivity();
                } else {
                    showToast("Invalid email or password");
                    emailEditText.setText("");
                    passwordEditText.setText("");
                }
            }
        });
    }

    private void togglePasswordVisibility() {
        // Toggle the password visibility based on the current state
        if (passwordEditText.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
            passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            showPasswordImageView.setImageResource(R.drawable.ic_eye_open);
        } else {
            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            showPasswordImageView.setImageResource(R.drawable.ic_eye_closed);
        }

        // Move the cursor to the end of the text
        passwordEditText.setSelection(passwordEditText.getText().length());
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    private void saveCredentials(String email, String password) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.key_email), email);
        editor.putString(getString(R.string.key_password), password);
        editor.apply();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
