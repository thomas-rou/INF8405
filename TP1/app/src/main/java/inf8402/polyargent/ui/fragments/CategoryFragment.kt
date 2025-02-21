package inf8402.polyargent.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import inf8402.polyargent.R
import inf8402.polyargent.model.transaction.Category
import inf8402.polyargent.model.transaction.TransactionType
import inf8402.polyargent.model.transaction.TransactionDatabase
import inf8402.polyargent.ui.adapters.CategoryAdapter
import inf8402.polyargent.ui.dialogs.CreateCategoryDialogFragment
import inf8402.polyargent.ui.dialogs.DeleteCategoryDialogFragment
import inf8402.polyargent.viewmodel.CategoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class CategoryFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var gridView: GridView
    private lateinit var fabAddCategory: FloatingActionButton
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var categoryAdapter: CategoryAdapter
    private var currentCategoriesLiveData: LiveData<List<Category>>? = null

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
            CategoryViewModel.Factory(database.categoryDao(), database.transactionDao())
        ).get(CategoryViewModel::class.java)

        loadCategories(TransactionType.EXPENSE)

        // Lors de la sélection d'un onglet, on observe les catégories correspondantes
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val type = if (tab?.text == "Dépenses") {
                    TransactionType.EXPENSE
                } else {
                    TransactionType.INCOME
                }
                loadCategories(type)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Bouton "+" : ouvre un dialogue pour ajouter une catégorie
        fabAddCategory.setOnClickListener {
            createCategoryDialog()
        }

        categoryAdapter.categoryLongClickListener = { category ->
            deleteConfirmationDialog(category)
        }

        observeErrorMessage()
    }

    private fun loadCategories(transactionType: TransactionType) {
        currentCategoriesLiveData?.removeObservers(viewLifecycleOwner)
        currentCategoriesLiveData = categoryViewModel.getCategoriesByType(transactionType)
        currentCategoriesLiveData?.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.updateCategories(categories)
        }
    }


    private fun createCategoryDialog() {
        val addCategoryDialog = CreateCategoryDialogFragment()
        addCategoryDialog.listener = object : CreateCategoryDialogFragment.OnCategoryCreatedListener {
            override fun onCategoryCreated(category: Category) {
                updateCategoryTab(category)
                categoryViewModel.insertCategory(category)
            }
        }
        addCategoryDialog.show(childFragmentManager, "CreateCategoryDialog")
    }

    private fun deleteConfirmationDialog(category: Category) {
        val deleteCategoryDialog = DeleteCategoryDialogFragment.newInstance(category.id)
        deleteCategoryDialog.listener = object : DeleteCategoryDialogFragment.OnCategoryDeletedListener {
            override fun onCategoryDeleted(categoryId: Int) {
                if (category.id == categoryId) {
                    updateCategoryTab(category)
                    categoryViewModel.deleteCategory(category)
                } else {
                    Toast.makeText(requireContext(), "Erreur lors de la suppression de la catégorie", Toast.LENGTH_SHORT).show()
                }
            }
        }
        deleteCategoryDialog.show(childFragmentManager, "DeleteCategoryDialog")
    }

    private fun updateCategoryTab(category: Category) {
        val currentTab = tabLayout.getTabAt(tabLayout.selectedTabPosition)?.text
        val newType = category.type
        if (newType == TransactionType.EXPENSE && currentTab != "Dépenses") {
            tabLayout.getTabAt(0)?.select()
        } else if (newType == TransactionType.INCOME && currentTab != "Revenus") {
            tabLayout.getTabAt(1)?.select()
        }
    }

    private fun observeErrorMessage() {
        categoryViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
