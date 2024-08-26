package uvg.edu.calculadoracompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Stack
import kotlin.math.sqrt

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


    // Estados para la operación y el resultado
    var operacionString by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("0") }
    var resultadoPressed by remember { mutableStateOf(false) }

    // Función para actualizar la operación
    val actualizarString: (String) -> Unit = { palabra ->
        if (resultadoPressed) {
            resultadoPressed = false
            operacionString = ""
        }
        operacionString += palabra
    }

    // Layout principal
    Column(
        modifier = Modifier
            .fillMaxSize(1f)
            .background(backgroundColor)
    ) {
        // Componente para el resultado y la operación
        ResultAndOperationDisplay(resultColor, operationColor, operacionString, resultado)

        // Componente para los botones
        CalculatorButtons(buttonColor, actualizarString, {
            val operacion = convertExpression(operacionString.replace('√', '$').replace("×", "*").replace("     0     ", "0"))
            if (expresionValida(operacion)) {
                val result = evaluatePostfix(infixToPostfix(operacion))
                resultado = result?.toString() ?: "División por 0"
            } else {
                resultado = "Error de Sintaxis"
            }
            resultadoPressed = true
        }, {
            if (operacionString.isNotEmpty()) {
                operacionString = operacionString.dropLast(1)
            }
        }, {
            operacionString = ""
            resultado = "0"
        })
    }
}

@Composable
fun ResultAndOperationDisplay(resultColor: Color, operationColor: Color, operacionString: String, resultado: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        // TextView para el resultado
        Text(
            text = resultado,
            fontSize = 64.sp,
            color = resultColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, top = 85.dp)
                .padding(horizontal = 10.dp),
            textAlign = TextAlign.End
        )

        // TextView para la operación
        Text(
            text = operacionString,
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
fun CalculatorButtons(
    buttonColor: Color,
    actualizarString: (String) -> Unit,
    onEqualPress: () -> Unit,
    onClearPress: () -> Unit,
    onAllClearPress: () -> Unit
) {
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
            CalculatorButtonRow(rowLabels, buttonColor, actualizarString, onEqualPress, onClearPress, onAllClearPress)
        }
    }
}

@Composable
fun CalculatorButtonRow(
    labels: List<String>,
    buttonColor: Color,
    actualizarString: (String) -> Unit,
    onEqualPress: () -> Unit,
    onClearPress: () -> Unit,
    onAllClearPress: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        labels.forEach { label ->
            CalculatorButton(label, buttonColor, actualizarString, onEqualPress, onClearPress, onAllClearPress)
        }
    }
}

@Composable
fun CalculatorButton(
    label: String,
    buttonColor: Color,
    actualizarString: (String) -> Unit,
    onEqualPress: () -> Unit,
    onClearPress: () -> Unit,
    onAllClearPress: () -> Unit
) {
    Button(
        onClick = {
            when (label) {
                "=" -> onEqualPress()
                "C" -> onClearPress()
                "AC" -> onAllClearPress()
                else -> actualizarString(label)
            }
        },
        modifier = Modifier
            .padding(4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(text = label, fontSize = 40.sp, color = Color.Black)
    }
}

val letterToNumberMap = mutableMapOf<Char, String>()

fun convertExpression(expression: String): String {
    val convertedExpression = StringBuilder()
    var currentLetter = 'a'

    // Separar la expresión por operadores y paréntesis
    val parts = expression.split("(?<=[-+*/^$()])|(?=[-+*/^$()])".toRegex())

    for (part in parts) {
        if (part.matches("\\d+(\\.\\d+)?".toRegex())) { // Si la parte es un número (entero o decimal)
            // Verificar si el número ya tiene una letra asignada
            if (!letterToNumberMap.containsValue(part)) {
                letterToNumberMap[currentLetter] = part
                currentLetter++
            }
            // Agregar la letra correspondiente al resultado
            for (key in letterToNumberMap.keys) {
                if (letterToNumberMap[key] == part) {
                    convertedExpression.append(key)
                    break
                }
            }
        } else {
            // Si no es un número, agregar el operador tal como está
            convertedExpression.append(part)
        }
    }

    return convertedExpression.toString()
}

// Función para retornar la precedencia de los operadores
fun prec(c: Char): Int {
    return when (c) {
        '$' -> 4 // Raíz cuadrada
        '^' -> 3 // Exponenciación
        '/', '*' -> 2 // Multiplicación y División
        '+', '-' -> 1 // Suma y Resta
        else -> -1
    }
}

// Función para retornar la asociatividad de los operadores
fun associativity(c: Char): Char {
    return if (c == '^') 'R' else 'L' // Right-associative para ^, Left-associative para los demás
}

// Función principal para convertir expresión infija a postfija
fun infixToPostfix(s: String): String {
    val result = StringBuilder()
    val stack = Stack<Char>()

    for (i in s.indices) {
        val c = s[i]

        // Si el carácter escaneado es un operando, agregarlo a la cadena de resultado.
        if (c.isLetterOrDigit()) {
            result.append(c)
        }
        // Si el carácter escaneado es un ‘(‘, empújalo a la pila.
        else if (c == '(') {
            stack.push(c)
        }
        // Si el carácter escaneado es un ‘)’, sacar de la pila hasta encontrar ‘(‘.
        else if (c == ')') {
            while (!stack.isEmpty() && stack.peek() != '(') {
                result.append(stack.pop())
            }
            stack.pop() // Sacar '('
        }
        // Si se escanea un operador
        else if (c == '$' || c == '+' || c == '-' || c == '*' || c == '/' || c == '^') {
            while (!stack.isEmpty() && prec(c) <= prec(stack.peek()) && associativity(c) == 'L') {
                result.append(stack.pop())
            }
            stack.push(c)
        }
    }

    // Sacar todos los elementos restantes de la pila
    while (!stack.isEmpty()) {
        result.append(stack.pop())
    }

    return result.toString()
}

// Método para evaluar el valor de una expresión postfija
fun evaluatePostfix(exp: String): Double? {
    val stack = Stack<Double>()

    // Escanear todos los caracteres uno por uno
    for (i in exp.indices) {
        val c = exp[i]

        // Si el carácter escaneado es un operando, empújalo a la pila.
        if (c.isLetterOrDigit()) {
            stack.push(letterToNumberMap[c]!!.toDouble())
        } else {
            if (c == '$') {
                // Si es el operador raíz cuadrada, saca un elemento y aplica la raíz cuadrada
                val val1 = stack.pop()
                stack.push(sqrt(val1))
            } else {
                // Para otros operadores
                val val1 = stack.pop()
                val val2 = stack.pop()

                when (c) {
                    '+' -> stack.push(val2 + val1)
                    '-' -> stack.push(val2 - val1)
                    '/' -> if (val1 != 0.0) stack.push(val2 / val1) else return null
                    '*' -> stack.push(val2 * val1)
                    '^' -> stack.push(Math.pow(val2, val1))
                }
            }
        }
    }
    return stack.pop()
}

fun expresionValida(expresion: String): Boolean {
    var parentesis = 0
    var operandos = 0
    var operadoresBinarios = 0
    var operadoresUnarios = 0
    var ultimoFueOperador = true // Para verificar si dos operadores están en fila

    for (c in expresion.toCharArray()) {
        when (c) {
            '(' -> parentesis++
            ')' -> {
                parentesis--
                if (parentesis < 0) return false // Verificar paréntesis de cierre inválido
            }
            '+', '-', '*', '/', '^' -> {
                if (ultimoFueOperador) return false // Dos operadores seguidos
                operadoresBinarios++
                ultimoFueOperador = true
            }
            '$' -> {
                if (!ultimoFueOperador) return false // El operador unario debe seguir a otro operador o estar al inicio
                operadoresUnarios++
                ultimoFueOperador = true
            }
            else -> if (c.isDigit() || c.isLetter()) {
                operandos++
                ultimoFueOperador = false
            } else {
                return false // Caracter inválido
            }
        }
    }

    // Verificar si la estructura es válida, los paréntesis están balanceados y la relación operadores-operandos es correcta
    val estructuraValida = parentesis == 0 && !ultimoFueOperador

    // Cuando hay operadores unarios, asegurar una relación 1:1 entre operadores totales y operandos
    return if (operadoresUnarios > 0) {
        estructuraValida && (operadoresBinarios + operadoresUnarios == operandos)
    } else {
        // De lo contrario, verificar la relación n:n+1 para operadores binarios y operandos
        estructuraValida && (operadoresBinarios == operandos - 1)
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorScreenPreview() {
    CalculatorScreen()
}