package khamroev.telegram

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import khamroev.telegram.ui.theme.TelegramTheme

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    val database= Firebase.database
    val myRef=database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TelegramTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    auth=FirebaseAuth.getInstance()

                    val gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.client_id))
                        .requestEmail()
                        .build()

                    val mGoogleSignInClient= GoogleSignIn.getClient(this,gso)


                    Column {
                        Button(onClick =
                        { val signInIntent=mGoogleSignInClient.signInIntent
                            startActivityForResult(signInIntent,1)
                        }) {
                            Text(text = "Sign In Google")
                        }

                        Button(onClick =
                        { mGoogleSignInClient.signOut()
                        }) {
                            Text(text = "Sign Out")
                        }


                    }





                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==1){

            val task=GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account=task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken)
            }catch (e:ApiException){

                Log.d("TAG", "error  $e" )
            }

        }

    }


    private fun firebaseAuthWithGoogle(idToken:String?){
        val credential=GoogleAuthProvider.getCredential(idToken,null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this){ task->
                if (task.isSuccessful){
                    val user=auth.currentUser

                    val userData=UserData(user?.displayName, user?.uid, user?.photoUrl.toString())
                    myRef.child("contact").child(user?.uid ?:"")
                        .setValue(userData)
                        .addOnSuccessListener {
                            val i=Intent(this, ContactActivity::class.java)
                            i.putExtra("name",userData.name)
                            startActivity(i)
                        }

            }else{
                Log.d("TAG","error:Authenfication failed")
                }
            }


    }



}
