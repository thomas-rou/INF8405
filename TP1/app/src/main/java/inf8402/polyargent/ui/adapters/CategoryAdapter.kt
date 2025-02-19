package inf8402.polyargent.ui.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import inf8402.polyargent.R
import inf8402.polyargent.model.transaction.Category

class CategoryAdapter(
    private val context: Context,
    private var categories: List<Category>
) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = categories.size

    override fun getItem(position: Int): Any = categories[position]

    override fun getItemId(position: Int): Long = categories[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.item_category_layout, parent, false)
        val imageView = view.findViewById<ImageView>(R.id.iconImageView)
        val textView = view.findViewById<TextView>(R.id.textCategory)

        val category = categories[position]
        textView.text = category.categoryName

        // Récupération de l'icône depuis les ressources (en se basant sur le nom stocké dans category.icon)
        val iconResId = context.resources.getIdentifier(category.icon, "drawable", context.packageName)
        imageView.setImageResource(iconResId)

        // Application dynamique de la couleur sur le fond circulaire de l'icône
        val backgroundDrawable = DrawableCompat.wrap(imageView.background)
        DrawableCompat.setTint(backgroundDrawable, Color.parseColor(category.colorHex))
        imageView.background = backgroundDrawable

        return view
    }

    fun updateCategories(newCategories: List<Category>) {
        this.categories = newCategories
        notifyDataSetChanged()
    }
}
