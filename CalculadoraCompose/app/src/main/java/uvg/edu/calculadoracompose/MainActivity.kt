import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorScreen()
        }
    }
}
@Composable
fun CalculatorScreen() {
    // Definir colores
    val backgroundColor = Color(0xFF80A396)
    val buttonColor = Color(0xFFB0CDBC)
    val resultColor = Color(0xFF516F64)
    val operationColor = Color(0xFFD8E8DF)

    // Layout principal
    Column(
        modifier = Modifier
            .fillMaxSize(1f)
            .background(backgroundColor)
    ) {
        // Componente para el resultado y la operación
        ResultAndOperationDisplay(resultColor, operationColor)

        // Componente para los botones
        CalculatorButtons(buttonColor)
    }
}

@Composable
fun ResultAndOperationDisplay(resultColor: Color, operationColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        // TextView para el resultado
        Text(
            text = "0",
            fontSize = 64.sp,
            color = resultColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, top = 150.dp)
                .padding(horizontal = 10.dp),
            textAlign = TextAlign.End
        )

        // TextView para la operación
        Text(
            text = "0",
            fontSize = 32.sp,
            color = operationColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 20.dp)
                .padding(horizontal = 10.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun CalculatorButtons(buttonColor: Color) {
    // Definir los labels de los botones en una lista de filas
    val buttonRows = listOf(
        listOf("AC", "C", "(", ")"),
        listOf("√", "^", "%", "/"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "+"),
        listOf("     0     ", ".", "=")
    )

    Column(
        modifier = Modifier
            .background(Color(0xFFD8E8DF))
            .padding(8.dp)
    ) {
        buttonRows.forEach { rowLabels ->
            CalculatorButtonRow(rowLabels, buttonColor)
        }
    }
}

@Composable
fun CalculatorButtonRow(labels: List<String>, buttonColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        labels.forEach { label ->
            CalculatorButton(label, buttonColor)
        }
    }
}

@Composable
fun CalculatorButton(label: String, buttonColor: Color) {
    Button(
        onClick = { /* Acción del botón */ },
        modifier = Modifier
            .padding(4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(text = label, fontSize = 40.sp, color = Color.Black)
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorScreenPreview() {
    CalculatorScreen()
}