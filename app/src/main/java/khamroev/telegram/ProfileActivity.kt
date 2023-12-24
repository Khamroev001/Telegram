package khamroev.telegram

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import khamroev.telegram.ui.theme.TelegramTheme
import khamroev.telegram.utils.SharedPrefHelper
@OptIn(ExperimentalMaterial3Api::class)
class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TelegramTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var sharedPrefHelper= SharedPrefHelper.getInstance(this)


                    var user= sharedPrefHelper.getUser()

                    var name by remember { mutableStateOf(user?.name) }
                    var email by remember { mutableStateOf(user?.email) }

                    val gso= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.client_id))
                        .requestEmail()
                        .build()

                    val gotData = remember { mutableStateOf(false) }


                    val userRef = Firebase.database.reference.child("contact").child(user?.uid!!)
                    userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val u = snapshot.getValue(UserData::class.java)
                            if (u != null){
                                if (!gotData.value){
                                    name = u.name!!
                                    user = u
                                    gotData.value = true
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })


                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        Arrangement.spacedBy(6.dp)
                    ) {
                        Image(painter = painterResource(id = R.drawable.back),
                            contentDescription = null,
                            Modifier
                                .size(40.dp)
                                .padding(horizontal = 6.dp)
                                .align(Alignment.Start)
                                .clickable{
                                    onBackPressed()
                                })
      Spacer(modifier = Modifier.height(120.dp))
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(user?.photo)
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(R.drawable.logo),
                            contentDescription = ("no image"),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(120.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        // Text Fields
                        OutlinedTextField(
                            value = name.toString(),
                            onValueChange = { name = it },
                            label = { Text("Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = email.toString(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Update Button
                        Button(
                            onClick = {


                                userRef.child("name").setValue(name)
                                gotData.value = false

                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(bottom = 8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Update")
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        // Logout Button
                        Button(
                            onClick = {
                                GoogleSignIn.getClient(this@ProfileActivity,gso).signOut()
                                sharedPrefHelper.logOut()
                                startActivity(Intent(this@ProfileActivity,MainActivity::class.java))

                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(bottom = 8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Logout")
                        }
            }
        }
    }
}
}}
