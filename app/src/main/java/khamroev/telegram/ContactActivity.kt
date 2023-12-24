package khamroev.telegram

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import khamroev.telegram.ui.theme.TelegramTheme
import khamroev.telegram.utils.SharedPrefHelper

@OptIn(ExperimentalMaterial3Api::class)
class ContactActivity : ComponentActivity() {
    lateinit var searchQuery:MutableState<String>
    lateinit var userList:MutableList<UserData>
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        var sharedPrefHelper=SharedPrefHelper.getInstance(this)
        super.onCreate(savedInstanceState)
        setContent {
            TelegramTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    searchQuery= remember {
                        mutableStateOf("")
                    }

                    userList = remember {
                        mutableStateListOf(UserData())
                    }

                    var user= remember {
                        mutableStateOf(sharedPrefHelper.getUser())
                    }

                    val reference = Firebase.database.reference.child("contact")
                    reference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val u = snapshot.children
                            userList.clear()
                            u.forEach{
                                val userData = it.getValue(UserData::class.java)
                                if (userData != null && user.value?.uid != userData.uid) {
                                    userList.add(userData)
                                    Log.d("NAME", userData.name.toString())
                                } else{
                                    user.value= userData!!
                                    Log.d("USER", userData.toString())
                                    Log.d("USER", user.toString())
                                }
                            }

                        }
                        override fun onCancelled(error: DatabaseError) {
                            Log.d("TAG", "onCancelled: ${error.message}")
                        }

                    })

                    Column(Modifier.fillMaxSize()) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(Color.LightGray)
                                .height(50.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.back),
                                contentDescription = null,
                                Modifier
                                    .size(40.dp)
                                    .padding(horizontal = 6.dp)
                                    .align(Alignment.CenterVertically)
                                    .clickable {
                                        onBackPressed()
                                    }
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(user.value?.photo)
                                    .crossfade(true)
                                    .build(),
                                placeholder = painterResource(R.drawable.logo),
                                contentDescription = ("no image"),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(35.dp)
                                    .clickable {
                                        var intent =
                                            Intent(this@ContactActivity, ProfileActivity::class.java)
                                        intent.putExtra("user", user.value)
                                        startActivity(intent)
                                    }
                                    .align(Alignment.CenterVertically) // Align vertically to the center
                            )

                            Text(
                                text = user.value?.name ?: "asdasdasd",
                                Modifier
                                    .padding(start = 12.dp)
                                    .align(Alignment.CenterVertically), // Align vertically to the center
                                fontSize = 22.sp
                            )
                        }

                        OutlinedTextField(
                            value = searchQuery.value,
                            onValueChange = { searchQuery.value = it },
                            label = { Text("Search") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search Icon"
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .heightIn(min = 56.dp) // Ensure a minimum height
                                .background(MaterialTheme.colorScheme.background)
                                .border(1.dp, MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(16.dp)), // Rounded corners
                            textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground)
                        )

                        LazyColumn() {
                        items(getFilteredUserList()) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .clickable {
                                        val i = Intent(
                                            this@ContactActivity,
                                            MessageActivity::class.java
                                        )
                                        i.putExtra("uid", user.value?.uid)
                                        i.putExtra("useruid", it.uid)
                                        i.putExtra("user", it)
                                        startActivity(i)
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(it.photo)
                                        .crossfade(true)
                                        .build(),
                                    placeholder = painterResource(R.drawable.logo),
                                    contentDescription = ("no image"),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(30.dp)
                                )
                                Text(
                                    text = it.name ?: "",
                                    Modifier.padding(start = 12.dp),
                                    fontSize = 22.sp
                                )
                            }
                        }
                    }
                    }
                }
                }
            }
        }
     fun getFilteredUserList(): MutableList<UserData> {
        return userList.filter { user ->
            // Filter users whose name contains the search query (case-insensitive)
            user.name?.contains(searchQuery.value, ignoreCase = true) == true
        }.sortedBy { it.name }.toMutableList() // Sort filtered users by name
    }

    }