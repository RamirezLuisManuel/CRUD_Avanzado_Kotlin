package mx.edu.utng.crudvideojuegos

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class GameAdapter(private var gameList: List<EmpModelClass>) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvDesc: TextView = itemView.findViewById(R.id.tvDesc)
        val tvAnio: TextView = itemView.findViewById(R.id.tvAnio)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvQualifi: TextView = itemView.findViewById(R.id.tvQualifi)
        val ivGameImage: ImageView = itemView.findViewById(R.id.ivGameImage)

        init {
            // Manejar el clic en el item
            itemView.setOnClickListener {
                val game = gameList[adapterPosition]  // Obtener el juego en la posición seleccionada
                val context = itemView.context // Obtener el contexto desde itemView
                val intent = Intent(context, DetallActivity::class.java) // Nueva actividad de detalles
                intent.putExtra("game_id", game.id)  // Enviar el ID del juego (puedes enviar otros datos también)
                intent.putExtra("game_name", game.name)
                intent.putExtra("game_desc", game.desc)
                intent.putExtra("game_anio", game.anio)
                intent.putExtra("game_price", game.price)
                intent.putExtra("game_qualifi", game.qualifi)
                intent.putExtra("game_imgurl", game.imgUrl)

                context.startActivity(intent) // Iniciar la actividad de detalles
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false)
        return GameViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = gameList[position]
        holder.tvName.text = "Nombre: ${game.name}"
        holder.tvAnio.text = "Año: ${game.anio}"
        holder.tvPrice.text = "Precio: $${game.price}"
        holder.tvQualifi.text = "Calificación: ${game.qualifi}"

        // Cargar la imagen con Glide
        Glide.with(holder.itemView.context)
            .load(game.imgUrl)
            .placeholder(R.drawable.placeholder) // Imagen de carga
            .error(R.drawable.error) // Imagen en caso de error
            .into(holder.ivGameImage)
    }

    override fun getItemCount(): Int {
        return gameList.size
    }

    // Actualiza la lista del RecyclerView
    fun updateList(newList: List<EmpModelClass>) {
        gameList = newList.toMutableList()
        notifyDataSetChanged()
    }
}
