package mx.edu.utng.crudvideojuegos

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale


class DetallActivity : AppCompatActivity() {

    private var gameId: Int = -1
    private lateinit var gameName: String
    private lateinit var gameDesc: String
    private lateinit var gameAnio: String
    private lateinit var gamePrice: String
    private lateinit var gameQualifi: String
    private lateinit var gameImgUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detall)

        // Obtener los datos enviados a través del Intent
        gameId = intent.getIntExtra("game_id", -1)
        gameName = intent.getStringExtra("game_name") ?: ""
        gameDesc = intent.getStringExtra("game_desc") ?: ""
        gameAnio = intent.getStringExtra("game_anio") ?: ""
        gamePrice = intent.getStringExtra("game_price") ?: ""
        gameQualifi = intent.getStringExtra("game_qualifi") ?: ""
        gameImgUrl = intent.getStringExtra("game_imgurl") ?: ""

        // Inicializar los TextViews y ImageView
        val GameId: TextView = findViewById(R.id.tvId) // ID del juego
        val GameName: TextView = findViewById(R.id.tvName)
        val GameDesc: TextView = findViewById(R.id.tvDesc)
        val GameAnio: TextView = findViewById(R.id.tvAnio)
        val GamePrice: TextView = findViewById(R.id.tvPrice)
        val GameQualifi: TextView = findViewById(R.id.tvQualifi)
        val GameImage: ImageView = findViewById(R.id.ivGameImage)
        val GameDate: TextView = findViewById(R.id.tvDateCreated) // Fecha de creación

        // Asignar los valores obtenidos a los TextViews
        GameId.text = "$gameId" // Mostrar el ID
        GameName.text = gameName
        GameDesc.text = gameDesc
        GameAnio.text = gameAnio
        GamePrice.text = gamePrice
        GameQualifi.text = gameQualifi
        GameDate.text = "${getCurrentDate()}" // Mostrar la fecha de creación

        // Cargar la imagen con Glide
        Glide.with(this)
            .load(gameImgUrl)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error)
            .into(GameImage)

        // Botón para eliminar el juego
        val btnDelete: Button = findViewById(R.id.btnDelete)
        btnDelete.setOnClickListener {
            val dbHelper = DatabaseHelper(this)
            val game = EmpModelClass(gameId, gameName, gameDesc, gameAnio, gamePrice, gameQualifi, gameImgUrl, "")
            val deleted = dbHelper.deleteGame(game)
            if (deleted > 0) {
                Toast.makeText(this, "Juego eliminado", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish() // Cerrar la actividad después de eliminar
            } else {
                Toast.makeText(this, "Error al eliminar el juego", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón para mostrar el modal de actualización
        val btnUpdate: Button = findViewById(R.id.btnUpdate)
        btnUpdate.setOnClickListener {
            showUpdateModal()
        }
    }

    // Función para mostrar el modal de actualización
    private fun showUpdateModal() {
        val dialogBuilder = AlertDialog.Builder(this)

        // Crear una vista personalizada para el diálogo
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_update_game, null)

        val editName: EditText = dialogView.findViewById(R.id.editGameName)
        val editDesc: EditText = dialogView.findViewById(R.id.editGameDesc)
        val editAnio: EditText = dialogView.findViewById(R.id.editGameAnio)
        val editPrice: EditText = dialogView.findViewById(R.id.editGamePrice)
        val editQualifi: EditText = dialogView.findViewById(R.id.editGameQualifi)

        // Rellenar los campos con los valores actuales
        editName.setText(gameName)
        editDesc.setText(gameDesc)
        editAnio.setText(gameAnio)
        editPrice.setText(gamePrice)
        editQualifi.setText(gameQualifi)

        // Configurar el diálogo
        dialogBuilder.setView(dialogView)
            .setCancelable(false)
            .setPositiveButton("Actualizar") { _, _ ->
                // Guardar los datos actualizados
                val updatedGame = EmpModelClass(gameId, editName.text.toString(), editDesc.text.toString(), editAnio.text.toString(), editPrice.text.toString(), editQualifi.text.toString(), gameImgUrl, "")
                val dbHelper = DatabaseHelper(this)
                val updated = dbHelper.updateGame(updatedGame)
                if (updated > 0) {
                    Toast.makeText(this, "Juego actualizado", Toast.LENGTH_SHORT).show()
                    // Actualizar la vista con los nuevos datos
                    gameName = editName.text.toString()
                    gameDesc = editDesc.text.toString()
                    gameAnio = editAnio.text.toString()
                    gamePrice = editPrice.text.toString()
                    gameQualifi = editQualifi.text.toString()
                    updateGameDetails()
                } else {
                    Toast.makeText(this, "Error al actualizar el juego", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

        // Mostrar el diálogo
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    // Método para actualizar la vista con los datos actualizados
    private fun updateGameDetails() {
        val GameName: TextView = findViewById(R.id.tvName)
        val GameDesc: TextView = findViewById(R.id.tvDesc)
        val GameAnio: TextView = findViewById(R.id.tvAnio)
        val GamePrice: TextView = findViewById(R.id.tvPrice)
        val GameQualifi: TextView = findViewById(R.id.tvQualifi)

        // Actualizar los TextViews con los nuevos valores
        GameName.text = gameName
        GameDesc.text = gameDesc
        GameAnio.text = gameAnio
        GamePrice.text = gamePrice
        GameQualifi.text = gameQualifi
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(Date()) // Fecha actual
    }
}
