package com.example.db.ui.theme

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@SuppressLint("DefaultLocale")
@Composable
fun CajaProgreso(context: Context, habitId: Int) {
    // Recuperar datos guardados en SharedPreferences
    var nivel by remember { mutableIntStateOf(PreferencesManager.getInt(context, "nivel_habito_${habitId}", 0)) }
    var colorCaja by remember { mutableStateOf(Color.Magenta) }
    var contadorToques by remember { mutableIntStateOf(PreferencesManager.getInt(context, "contador_toques_${habitId}", 0)) }
    var ultimaInteraccion by remember {
        mutableStateOf(
            PreferencesManager.getString(context, "ultima_interaccion_${habitId}", null)?.let { LocalDateTime.parse(it) }
        )
    }
    var tiempoRestanteBloqueo by remember { mutableStateOf(Duration.ZERO) }
    var tiempoRestanteMensaje by remember { mutableStateOf(Duration.ZERO) }
    var mostrarDialogoAnimo by remember { mutableStateOf(false) }
    var mostrarDialogoFelicitacion by remember { mutableStateOf(false) }
    var mensajeAnimoMostrado by remember {
        mutableStateOf(PreferencesManager.getBoolean(context, "mensaje_animo_mostrado_${habitId}", false))
    }
    var isTouchable by remember { mutableStateOf(true) }
    var inicioBloqueo by remember {
        mutableStateOf(
            PreferencesManager.getString(context, "inicio_bloqueo_${habitId}", null)?.let { LocalDateTime.parse(it) }
        )
    }
    var inicioMensaje by remember {
        mutableStateOf(
            PreferencesManager.getString(context, "inicio_mensaje_${habitId}", null)?.let { LocalDateTime.parse(it) }
        )
    }
    val colores = listOf(Color.Magenta, Color.Yellow, Color.Red, Color.Green, Color.Blue)

    // Definición de los toques necesarios para cada nivel
    val toquesPorNivel = listOf(1, 2, 2, 3, 3, 3, 2, 2, 2, 1)

    // Lista de mensajes de ánimo
    val mensajesAnimo = listOf(
        "¡Sigue adelante, puedes lograrlo!",
        "Cada día es una nueva oportunidad.",
        "No te rindas, el éxito está cerca.",
        "Confía en ti mismo y en tus capacidades.",
        "Las grandes cosas toman tiempo.",
        "¡Eres más fuerte de lo que crees!"
    )

    // Lista de mensajes de felicitaciones
    val mensajesFelicitacion = listOf(
        "¡Felicidades por subir de nivel!",
        "¡Excelente trabajo, sigue así!",
        "¡Has alcanzado un nuevo nivel!",
        "¡Enhorabuena por tu logro!",
        "¡Tu esfuerzo está dando frutos!",
        "¡Bravo, estás progresando!"
    )

    var mensajeAnimoActual by remember { mutableStateOf("") }
    var mensajeFelicitacionActual by remember { mutableStateOf("") }

    // Actualizar el color cuando cambia el nivel
    LaunchedEffect(nivel) {
        colorCaja = colores[nivel % colores.size]
        // Guardar el nivel actualizado
        PreferencesManager.saveInt(context, "nivel_habito_${habitId}", nivel)
    }

    // Corrutina para manejar el bloqueo del recuadro
    LaunchedEffect(inicioBloqueo) {
        if (inicioBloqueo != null) {
            isTouchable = false
            while (true) {
                val ahora = LocalDateTime.now()
                val segundosTranscurridos = ChronoUnit.SECONDS.between(inicioBloqueo, ahora)
                val segundosEnUnDia = 5 // Tiempo de ejemplo
                val segundosRestantes = segundosEnUnDia - segundosTranscurridos

                if (segundosRestantes <= 0) {
                    isTouchable = true
                    inicioBloqueo = null
                    tiempoRestanteBloqueo = Duration.ZERO
                    // Eliminar inicioBloqueo de SharedPreferences
                    PreferencesManager.remove(context, "inicio_bloqueo_${habitId}")
                    break
                } else {
                    tiempoRestanteBloqueo = Duration.ofSeconds(segundosRestantes)
                }

                delay(1000L) // Actualizar cada segundo
            }
        } else {
            tiempoRestanteBloqueo = Duration.ZERO
            isTouchable = true
        }
    }

    // Corrutina para manejar el tiempo del mensaje
    LaunchedEffect(ultimaInteraccion, mensajeAnimoMostrado) {
        if (ultimaInteraccion != null && !mensajeAnimoMostrado) {
            while (true) {
                val ahora = LocalDateTime.now()
                val segundosTranscurridos = ChronoUnit.SECONDS.between(ultimaInteraccion, ahora)
                val segundosParaMensaje = 10 // Tiempo de ejemplo para mostrar mensaje
                val segundosRestantesMensaje = segundosParaMensaje - segundosTranscurridos

                if (segundosRestantesMensaje <= 0) {
                    mensajeAnimoActual = mensajesAnimo.random()
                    mostrarDialogoAnimo = true
                    mensajeAnimoMostrado = true

                    // Guardar inicio del mensaje
                    inicioMensaje = LocalDateTime.now()
                    PreferencesManager.saveString(context, "inicio_mensaje_${habitId}", inicioMensaje.toString())
                    PreferencesManager.saveBoolean(context, "mensaje_animo_mostrado_${habitId}", true)

                    tiempoRestanteMensaje = Duration.ZERO
                    break
                } else {
                    tiempoRestanteMensaje = Duration.ofSeconds(segundosRestantesMensaje)
                }

                delay(1000L) // Actualizar cada segundo
            }
        } else {
            tiempoRestanteMensaje = Duration.ZERO
        }
    }

    // Formatear el tiempo restante de bloqueo
    val totalSecondsBloqueo = tiempoRestanteBloqueo.seconds
    val horasBloqueo = totalSecondsBloqueo / 3600
    val minutosBloqueo = (totalSecondsBloqueo % 3600) / 60
    val segundosBloqueo = totalSecondsBloqueo % 60

    // Formatear el tiempo restante del mensaje
    val totalSecondsMensaje = tiempoRestanteMensaje.seconds
    val horasMensaje = totalSecondsMensaje / 3600
    val minutosMensaje = (totalSecondsMensaje % 3600) / 60
    val segundosMensaje = totalSecondsMensaje % 60

    // Mostrar la caja de progreso
    Box(
        modifier = Modifier
            .size(250.dp)
            .background(colorCaja)
            .clickable(enabled = isTouchable) {
                if (isTouchable) {
                    contadorToques++
                    ultimaInteraccion = LocalDateTime.now()
                    mensajeAnimoMostrado = false

                    // Guardar contador actualizado
                    PreferencesManager.saveInt(context, "contador_toques_${habitId}", contadorToques)
                    // Guardar última interacción
                    PreferencesManager.saveString(context, "ultima_interaccion_${habitId}", ultimaInteraccion.toString())
                    PreferencesManager.saveBoolean(context, "mensaje_animo_mostrado_${habitId}", false)

                    // Verificar si se cumplen los toques necesarios para el nivel actual
                    if (contadorToques >= toquesPorNivel.getOrElse(nivel) { 0 }) {
                        nivel++
                        contadorToques = 0

                        // Guardar el nivel y resetear los toques
                        PreferencesManager.saveInt(context, "nivel_habito_${habitId}", nivel)
                        PreferencesManager.saveInt(context, "contador_toques_${habitId}", 0)

                        if (nivel == 10) {
                            mensajeFelicitacionActual = "¡Felicidades! Hábito finalizado en el nivel 10."
                            mostrarDialogoFelicitacion = true
                        } else {
                            mensajeFelicitacionActual = mensajesFelicitacion.random()
                            mostrarDialogoFelicitacion = true
                        }
                    }

                    isTouchable = false
                    inicioBloqueo = LocalDateTime.now()

                    // Guardar el inicio del bloqueo
                    PreferencesManager.saveString(context, "inicio_bloqueo_${habitId}", inicioBloqueo.toString())
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Nivel: $nivel", color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Días: $contadorToques/${toquesPorNivel.getOrElse(nivel) { 0 }}",
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            if (!isTouchable) {
                Text(
                    text = String.format(
                        "Bloqueado por:\n%02d:%02d:%02d",
                        horasBloqueo, minutosBloqueo, segundosBloqueo
                    ),
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
            if (tiempoRestanteMensaje > Duration.ZERO) {
                Text(
                    text = String.format(
                        "Mensaje en:\n%02d:%02d:%02d",
                        horasMensaje, minutosMensaje, segundosMensaje
                    ),
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // Mostrar el diálogo de felicitación
    if (mostrarDialogoFelicitacion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoFelicitacion = false },
            title = { Text(text = "¡Felicitaciones!") },
            text = { Text(text = mensajeFelicitacionActual) },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoFelicitacion = false }) {
                    Text("Gracias")
                }
            }
        )
    }

    // Mostrar el diálogo de ánimo
    if (mostrarDialogoAnimo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoAnimo = false },
            title = { Text(text = "¡Ánimo!") },
            text = { Text(text = mensajeAnimoActual) },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoAnimo = false }) {
                    Text("Gracias")
                }
            }
        )
    }
}
