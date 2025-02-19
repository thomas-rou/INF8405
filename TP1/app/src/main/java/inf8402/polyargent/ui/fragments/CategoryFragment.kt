package inf8402.polyargent.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import inf8402.polyargent.R
import inf8402.polyargent.model.transaction.Category
import inf8402.polyargent.model.transaction.TransactionType
import inf8402.polyargent.model.transaction.TransactionDatabase
import inf8402.polyargent.ui.adapters.CategoryAdapter
import inf8402.polyargent.viewmodel.CategoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class CategoryFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var gridView: GridView
    private lateinit var fabAddCategory: FloatingActionButton
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_category, container, false)
        tabLayout = view.findViewById(R.id.tabsCategory)
        gridView = view.findViewById(R.id.gridViewCategories)
        fabAddCategory = view.findViewById(R.id.fabAddCategory)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ajout des onglets : "Dépenses" sélectionné par défaut
        tabLayout.addTab(tabLayout.newTab().setText("Dépenses"), true)
        tabLayout.addTab(tabLayout.newTab().setText("Revenus"))

        // Configuration de l'adapter avec une liste vide initialement
        categoryAdapter = CategoryAdapter(requireContext(), mutableListOf())
        gridView.adapter = categoryAdapter

        // Récupération du DAO via la base de données
        val database = TransactionDatabase.getDatabase(
            requireContext(),
            CoroutineScope(Dispatchers.IO)
        )
        // Création du ViewModel en utilisant la Factory avec le CategoryDao
        categoryViewModel = ViewModelProvider(
            this,
            CategoryViewModel.Factory(database.categoryDao())
        ).get(CategoryViewModel::class.java)

        categoryViewModel.getCategoriesByType(TransactionType.EXPENSE)
            .observe(viewLifecycleOwner) { categories ->
                categoryAdapter.updateCategories(categories)
            }

        // Lors de la sélection d'un onglet, on observe les catégories correspondantes
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val type = if (tab?.text == "Dépenses") {
                    TransactionType.EXPENSE
                } else {
                    TransactionType.INCOME
                }
                categoryViewModel.getCategoriesByType(type).observe(viewLifecycleOwner) { categories ->
                    categoryAdapter.updateCategories(categories)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Bouton "+" : ouvre un dialogue pour ajouter une catégorie
        fabAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    /**
     * Affiche un AlertDialog simple pour ajouter une nouvelle catégorie.
     * Vous pouvez bien entendu le remplacer par un fragment ou une activité dédiée.
     */
    private fun showAddCategoryDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_category, null)
        val etName = dialogView.findViewById<EditText>(R.id.editTextNewCategoryName)

        AlertDialog.Builder(requireContext())
            .setTitle("Ajouter une catégorie")
            .setView(dialogView)
            .setPositiveButton("Ajouter") { dialog, _ ->
                val categoryName = etName.text.toString().trim()
                if (categoryName.isNotEmpty()) {
                    // Ici, on définit des valeurs par défaut pour l'icône et la couleur.
                    val newCategory = Category(
                        categoryName = categoryName,
                        isDefault = false,
                        type = if (tabLayout.selectedTabPosition == 0)
                            TransactionType.EXPENSE else TransactionType.INCOME,
                        icon = "ic_default_icon",  // Assurez-vous que cette icône existe dans vos ressources
                        colorHex = "#CCCCCC"       // Couleur par défaut
                    )
                    categoryViewModel.addCategory(newCategory)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Annuler") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}
