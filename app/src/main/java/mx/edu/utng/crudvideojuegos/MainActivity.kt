package mx.edu.utng.crudvideojuegos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var dbHandler: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var gameAdapter: GameAdapter
    private var gameList = mutableListOf<EmpModelClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Inicializar la base de datos
        dbHandler = DatabaseHelper(this)

        // Cargar los juegos desde la base de datos
        gameList = dbHandler.viewGames().toMutableList()

        gameAdapter = GameAdapter(gameList)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Configurar el Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loadGames()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as androidx.appcompat.widget.SearchView

        searchView.queryHint = "Buscar videojuegos..."
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterGames(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterGames(newText)
                return true
            }
        })
        return true
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_agr -> {
                showAddGameDialog()
                true
            }
            R.id.action_acercade -> {
                // Muestra un Toast
                Toast.makeText(this, "Acerca de seleccionado", Toast.LENGTH_SHORT).show()

                // Inicia la nueva actividad
                val intent = Intent(this, AcercaDe::class.java) // Cambia AcercaDeActivity por el nombre de tu actividad
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadGames() {
        gameList = dbHandler.viewGames().toMutableList()
        gameAdapter = GameAdapter(gameList)
        recyclerView.adapter = gameAdapter
    }

    private fun showAddGameDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_game, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Agregar Videojuego")
            .setPositiveButton("Guardar") { dialog, _ ->
                val name = dialogView.findViewById<EditText>(R.id.etName).text.toString()
                val desc = dialogView.findViewById<EditText>(R.id.etDesc).text.toString()
                val anio = dialogView.findViewById<EditText>(R.id.etAnio).text.toString()
                val price = dialogView.findViewById<EditText>(R.id.etPrice).text.toString()
                val qualifi = dialogView.findViewById<EditText>(R.id.etQualifi).text.toString()
                val imgUrl = dialogView.findViewById<EditText>(R.id.etImgURL).text.toString()

                if (name.isNotEmpty() && desc.isNotEmpty()) {
                    val newGame = EmpModelClass(
                        id = 0, // El ID se autoincrementa
                        name = name,
                        desc = desc,
                        anio = anio,
                        price = price,
                        qualifi = qualifi,
                        imgUrl = imgUrl,
                        record_at = "" // Se asignará automáticamente por la base de datos
                    )
                    val result = dbHandler.addGame(newGame)
                    if (result > 0) {
                        Toast.makeText(this, "Juego agregado exitosamente", Toast.LENGTH_SHORT).show()
                        loadGames() // Esto recarga la lista de juegos
                    } else {
                        Toast.makeText(this, "Error al agregar juego", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Los campos Nombre y Descripción son obligatorios", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    // Filtrar juegos según el texto de búsqueda
    private fun filterGames(query: String?) {
        val filteredList = gameList.filter { game ->
            game.name.contains(query ?: "", ignoreCase = true) ||
                    game.anio.contains(query ?: "", ignoreCase = true) ||
                    game.price.contains(query ?: "", ignoreCase = true) ||
                    game.qualifi.contains(query ?: "", ignoreCase = true)
        }.toMutableList()

        gameAdapter.updateList(filteredList)
    }


    override fun onResume() {
        super.onResume()
        // Recargar los juegos desde la base de datos
        loadGames()
    }
}