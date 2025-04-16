
    package com.example.csc490group3.viewModels

    import androidx.compose.runtime.mutableStateOf
    import androidx.core.os.registerForAllProfilingResults
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import androidx.lifecycle.viewmodel.compose.viewModel
    import com.example.csc490group3.model.Event
    import com.example.csc490group3.model.User
    import com.example.csc490group3.supabase.DatabaseManagement.registerEvent
    import com.example.csc490group3.supabase.DatabaseManagement.simpleSearch
    import com.example.csc490group3.supabase.DatabaseManagement.userSearch
    import kotlinx.coroutines.launch

    class UserSeachViewModel: ViewModel() {
        var users = mutableStateOf<List<User>>(emptyList())
            private set

        var isLoading = mutableStateOf(true)
            private set

        var errorMessage = mutableStateOf<String?>(null)
            private set

        fun search(query: String) {
            viewModelScope.launch {
                try {
                    val result = userSearch(query)
                    if(result != null) {
                        users.value = result.toList()
                    }
                } catch (e: Exception) {
                    errorMessage.value = "Registration failed: ${e.localizedMessage}"
                }finally {
                    isLoading.value = false
                }
            }
        }


    }



