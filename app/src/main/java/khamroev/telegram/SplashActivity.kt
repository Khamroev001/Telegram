package khamroev.telegram

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import khamroev.telegram.ui.theme.TelegramTheme
import khamroev.telegram.utils.SharedPrefHelper
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    lateinit var sharedPrefHelper: SharedPrefHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TelegramTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    sharedPrefHelper=SharedPrefHelper.getInstance(this)

                         AppContent(context = this, sharedPrefHelper.getUser())
                }
            }
        }
    }
}

@Composable
fun AppContent(context: Context, userData: UserData?) {

    LaunchedEffect(true) {
        delay(2000)
        if (userData==null){
            gotoMain(context)
        }else{
            gotoContacts(context)
        }

    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Image(painter = painterResource(id = R.drawable.logo), contentDescription = null, modifier = Modifier.size(200.dp), contentScale = ContentScale.Crop)

}
}
fun gotoMain(context: Context){
    val i = Intent(context,MainActivity::class.java)
    startActivity(context,i,null)
}
fun gotoContacts(context: Context){
    val i = Intent(context,ContactActivity::class.java)
    startActivity(context,i,null)
}
