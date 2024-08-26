package uvg.edu.codelab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uvg.edu.codelab1.ui.theme.CodeLab1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodeLab1Theme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    val shouldShowOnboarding = rememberSaveable { mutableStateOf(true) }

    if(shouldShowOnboarding.value) {
        OnboardingScreen(onContinueClicked = { shouldShowOnboarding.value = false })
    } else {
        Greetings()
    }
}

@Composable
fun Greetings(names: List<String> = List(10) {"Composable  $it"}) {
    Surface (modifier = Modifier.padding(vertical = 4.dp)) {
        Column {
            LazyColumn {
                items(names) { name -> Greeting(name) }
            }
        }
    }
}

@Composable
fun OnboardingScreen(
    onContinueClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bienvenido, estás en la Página Inicial")
        Button(
            modifier = Modifier
                .padding(vertical = 24.dp),
            onClick = onContinueClicked
        ) {
            Text("Continuar")
        }
    }

}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun OnboardingPreview() {
    CodeLab1Theme {
        OnboardingScreen(onContinueClicked = {})
    }
}

@Composable
fun Greeting(name: String) {
    val expanded = remember { mutableStateOf(false) }
    val extraPadding = animateDpAsState(
        targetValue = if(expanded.value) 50.dp else 0.dp,
        animationSpec = tween(durationMillis = 1000)
    )
    Surface (color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
        Row (modifier = Modifier.padding(24.dp)) {
            Column (
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = extraPadding.value)
            ) {
                Text(text = "Hola,")
                Text(text = name)
            }
            OutlinedButton(
                onClick = { expanded.value = !expanded.value },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(if(expanded.value) "Ver menos" else "Ver más")
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingPreview() {
    CodeLab1Theme {
        Greetings()
    }
}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    CodeLab1Theme {
        MyApp()
    }
}