package com.example.mistareas_0
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// ---------- MODELO DE DATOS ----------

data class Tarea(
    val titulo: String,
    val creacion: Long,   // hora de creación, en milisegundos
    val deadline: Long    // fecha y hora límite, en milisegundos
) : Serializable

// ---------- FUNCIONES AUXILIARES ----------

fun formatearFecha(milisegundos: Long): String {
    val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return formato.format(Date(milisegundos))
}

fun elegirFechaYHora(contexto: Context, alElegir: (Long) -> Unit) {
    val ahora = Calendar.getInstance()
    DatePickerDialog(
        contexto,
        { _, anio, mes, dia ->
            TimePickerDialog(
                contexto,
                { _, hora, minuto ->
                    val elegida = Calendar.getInstance()
                    elegida.set(anio, mes, dia, hora, minuto, 0)
                    alElegir(elegida.timeInMillis)
                },
                12, 0, true
            ).show()
        },
        ahora.get(Calendar.YEAR),
        ahora.get(Calendar.MONTH),
        ahora.get(Calendar.DAY_OF_MONTH)
    ).show()
}

// ---------- ACTIVITY Y NAVEGACIÓN ----------

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.safeDrawingPadding()) {
                        var pantallaNueva by rememberSaveable { mutableStateOf(false) }

                        var tareas by rememberSaveable {
                            val ahora = System.currentTimeMillis()
                            mutableStateOf(
                                listOf(
                                    Tarea("Comprar pan", ahora, ahora + 3_600_000L),
                                    Tarea("Estudiar Android", ahora, ahora + 86_400_000L),
                                    Tarea("Preparar examen", ahora, ahora + 172_800_000L)
                                )
                            )
                        }

                        if (pantallaNueva) {
                            PantallaNuevaTarea(
                                onVolver = { pantallaNueva = false },
                                onGuardar = { titulo, deadline ->
                                    val nueva = Tarea(
                                        titulo = titulo,
                                        creacion = System.currentTimeMillis(),
                                        deadline = deadline
                                    )
                                    tareas = tareas + nueva
                                    pantallaNueva = false
                                }
                            )
                        } else {
                            PantallaTareas(
                                tareas = tareas,
                                onNuevaTarea = { pantallaNueva = true }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------- PANTALLA 1: LISTA ----------

@Composable
fun PantallaTareas(
    tareas: List<Tarea>,
    onNuevaTarea: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Mis Tareas",
                style = MaterialTheme.typography.headlineMedium
            )
            Button(onClick = onNuevaTarea) {
                Text("+")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(tareas) { tarea ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = tarea.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "Creada: ${formatearFecha(tarea.creacion)}")
                    Text(text = "Deadline: ${formatearFecha(tarea.deadline)}")
                }
            }
        }
    }
}

// ---------- PANTALLA 2: NUEVA TAREA ----------

@Composable
fun PantallaNuevaTarea(
    onVolver: () -> Unit,
    onGuardar: (String, Long) -> Unit
) {
    var texto by rememberSaveable { mutableStateOf("") }
    var deadline by rememberSaveable { mutableStateOf<Long?>(null) }
    val contexto = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = onVolver) {
            Text("← Volver")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Nueva tarea",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = texto,
            onValueChange = { texto = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                elegirFechaYHora(contexto) { milisegundos ->
                    deadline = milisegundos
                }
            }
        ) {
            Text("Elegir fecha y hora límite")
        }
        Spacer(modifier = Modifier.height(8.dp))

        val limite = deadline
        Text(
            text = if (limite != null) {
                "Deadline: ${formatearFecha(limite)}"
            } else {
                "Deadline: sin elegir"
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (texto.isNotBlank() && limite != null) {
                    onGuardar(texto, limite)
                }
            },
            enabled = texto.isNotBlank() && limite != null
        ) {
            Text("Guardar")
        }
    }
}


