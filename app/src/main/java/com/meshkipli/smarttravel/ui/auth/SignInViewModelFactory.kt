import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.meshkipli.smarttravel.data.repository.AuthRepository // Assuming this is your AuthRepository path
import com.meshkipli.smarttravel.ui.auth.SignInViewModel

class SignInViewModelFactory(
    private val application: Application, // Add Application parameter
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Pass the application instance to SignInViewModel constructor
            return SignInViewModel(application, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
