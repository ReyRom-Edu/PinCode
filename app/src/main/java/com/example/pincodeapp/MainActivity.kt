package com.example.pincodeapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MovableContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pincodeapp.ui.theme.PinCodeAppTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PinCodeAppTheme {
                val context = LocalContext.current
                var isAuthenticated by remember { mutableStateOf(false) }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(Modifier.fillMaxSize().padding(innerPadding)) {
                        if (isAuthenticated){
                            Text("Добро пожаловать")
                        }
                        else{
                            LoginScreen(context,
                                onLoginSuccess = {isAuthenticated = true},
                                onLoginFailed = {
                                    Toast.makeText(context, "Вход не выполнен", Toast.LENGTH_LONG)
                                        .show()
                                })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(context: Context, onLoginSuccess: ()->Unit, onLoginFailed: ()->Unit){
    var pinCode by remember { mutableStateOf("") }
    val authentificationHelper = remember {
        AuthentificationHelper(context, onLoginSuccess, onLoginFailed)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Введите пин-код", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            repeat(4){ index ->
                val filled = index < pinCode.length
                Box(
                    modifier = Modifier.size(16.dp)
                        .clip(CircleShape)
                        .background(if (filled) Color.Black else Color.Gray)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column {
            val numbers = listOf(
                1..3,
                4..6,
                7..9
            )

            numbers.forEach{ row ->
                Row(horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()) {
                    row.forEach { num ->
                        FilledIconButton(
                            onClick = {
                                pinCode = handlePinInput(pinCode, num.toString()){
                                    authentificationHelper.authentificate(it)
                                }
                            },
                            modifier = Modifier.size(70.dp)
                        ) {
                            Text(num.toString(), style = MaterialTheme.typography.displayMedium)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            Row(horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()) {

                IconButton(
                    onClick = {
                        if (pinCode.isNotEmpty()) pinCode = pinCode.dropLast(1)
                    },
                    modifier = Modifier.size(70.dp)
                ) {
                    Icon(painterResource(R.drawable.backspace_24),
                        "backspace",
                        modifier=Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.primary)
                }
                FilledIconButton(
                    onClick = {
                        pinCode = handlePinInput(pinCode, "0"){
                            authentificationHelper.authentificate(it)
                        }
                    },
                    modifier = Modifier.size(70.dp)
                ) {
                    Text("0", style = MaterialTheme.typography.displayMedium)
                }
                IconButton(
                    onClick = {
                        authentificationHelper.authentificateBiometric()
                    },
                    enabled = authentificationHelper.isBiometricAvailable(),
                    modifier = Modifier.size(70.dp)
                ) {
                    Icon(painterResource(R.drawable.fingerprint_24),
                        "fingerprint",
                        modifier=Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.primary)
                }
            }

        }
    }
}

private fun handlePinInput(
    pinCode: String,
    num: String,
    onAuth: (String)->Unit
) : String {
    var newPin = pinCode
    if (newPin.length < 4){
        newPin += num
        if (newPin.length == 4){
            onAuth(newPin)
            newPin = ""
        }
    }
    return newPin
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PinCodeAppTheme {
        LoginScreen(LocalContext.current, {}, {})
    }
}