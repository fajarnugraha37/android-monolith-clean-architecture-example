package com.example.githubapp.ui.home.favorite

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubapp.R
import com.example.githubapp.core.domain.model.User
import com.example.githubapp.core.ui.UserAdapter
import com.example.githubapp.core.utils.SortUtils
import com.example.githubapp.databinding.FragmentFavoriteBinding
import com.example.githubapp.ui.MainViewModel
import com.example.githubapp.utils.BaseFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.viewmodel.ext.android.viewModel
import androidx.lifecycle.Observer

@FlowPreview
@ExperimentalCoroutinesApi
class FavoriteFragment : BaseFragment<FragmentFavoriteBinding>() {
    private val mainViewModel: MainViewModel by viewModel()
    private val viewModel: FavoriteViewModel by viewModel()

    private lateinit var userAdapter: UserAdapter
    private var sort = SortUtils.USERNAME_ASC

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        userAdapter = UserAdapter()

        with(binding.rvUser) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = userAdapter
        }
        userAdapter.onItemClick = { selectedData ->
//            val intent = Intent(activity, DetailActivity::class.java)
//            intent.putExtra(DetailActivity.EXTRA_MOVIE, selectedData)
//            startActivity(intent)
//            val action = Directions.actionCategoryProductItems2ToProductItem(null, it)
            val navController = Navigation.findNavController(requireView())
            navController?.navigate(R.id.action_navigation_search_to_navigation_detail)
        }

        binding.fabNameAsc.setOnClickListener {
            binding.famSort.close(true)
            sort = SortUtils.USERNAME_ASC
            setList(sort)
        }
        binding.fabNameDesc.setOnClickListener {
            binding.famSort.close(true)
            sort = SortUtils.USERNAME_DESC
            setList(sort)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setList(sort)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_menu, menu)
        menu.findItem(R.id.action_search).isVisible = false
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            menu.findItem(R.id.action_light_mode).isVisible = true
        else
            menu.findItem(R.id.action_dark_mode).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_dark_mode -> mainViewModel.setThemeSetting(true)
            R.id.action_light_mode -> mainViewModel.setThemeSetting(false)
            R.id.action_about -> findNavController().navigate(R.id.action_navigation_favorite_to_navigation_developer)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setList(sort: String) {
        viewModel.getFavoriteUsers(sort).observe(viewLifecycleOwner, usersObserver)
    }

    private val usersObserver = Observer<List<User>> { users ->
        if (users.isNullOrEmpty()){
            binding.pbLoading.visibility = View.GONE
            binding.lavNotFound.visibility = View.VISIBLE
            binding.tvNotFound.visibility = View.VISIBLE
        } else {
            binding.pbLoading.visibility = View.GONE
            binding.lavNotFound.visibility = View.GONE
            binding.tvNotFound.visibility = View.GONE
        }
        userAdapter.setData(users)
    }

    companion object {
        fun instance() = FavoriteFragment()
    }
}