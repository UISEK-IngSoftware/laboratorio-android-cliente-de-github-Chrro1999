package ec.edu.uisek.githubclient

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityRepoFormBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.RepoRequest
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepoForm : AppCompatActivity() {

    private lateinit var binding: ActivityRepoFormBinding
    private var isEditMode = false
    private var repoOwner: String = ""
    private var originalRepoName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRepoFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verificar si estamos en modo edición
        isEditMode = intent.getBooleanExtra("IS_EDIT", false)

        if (isEditMode) {
            setupEditMode()
        } else {
            setupCreateMode()
        }

        binding.cancelButton.setOnClickListener { finish() }
        binding.saveButton.setOnClickListener {
            if (isEditMode) {
                updateRepo()
            } else {
                createRepo()
            }
        }
    }

    private fun setupEditMode() {
        binding.saveButton.text = "Actualizar"

        originalRepoName = intent.getStringExtra("REPO_NAME") ?: ""
        repoOwner = intent.getStringExtra("REPO_OWNER") ?: ""

        binding.repoNameInput.setText(originalRepoName)
        binding.repoDescriptionInput.setText(intent.getStringExtra("REPO_DESCRIPTION") ?: "")

        // En modo edición, el nombre no se puede cambiar (según la API de GitHub)
        binding.repoNameInput.isEnabled = false
    }

    private fun setupCreateMode() {
        binding.saveButton.text = "Crear"
        binding.repoNameInput.isEnabled = true
    }

    private fun validateForm(): Boolean {
        val repoName = binding.repoNameInput.text.toString()

        if (repoName.isBlank()) {
            binding.repoNameInput.error = "El nombre del repositorio es requerido"
            return false
        }

        if (repoName.contains(" ")) {
            binding.repoNameInput.error = "El nombre del repositorio no puede contener espacios"
            return false
        }

        binding.repoNameInput.error = null
        return true
    }

    private fun createRepo() {
        if (!validateForm()) {
            return
        }
        val repoName = binding.repoNameInput.text.toString().trim()
        val repoDescription = binding.repoDescriptionInput.text.toString().trim()

        val repoRequest = RepoRequest(repoName, repoDescription)
        val apiService = RetrofitClient.gitHubApiService
        val call = apiService.addRepo(repoRequest)

        call.enqueue(object : Callback<Repo> {
            override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio creado exitosamente")
                    finish()
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "Error: No autorizado."
                        403 -> "Prohibido"
                        404 -> "No encontrado."
                        422 -> "El repositorio ya existe o nombre inválido"
                        else -> "Error ${response.code()}"
                    }
                    showMessage(errorMessage)
                }
            }
            override fun onFailure(call: Call<Repo>, t: Throwable) {
                val errorMsg = "Error al crear el repositorio: ${t.message}"
                Log.e("RepoForm", errorMsg, t)
                showMessage(errorMsg)
            }
        })
    }

    private fun updateRepo() {
        if (!validateForm()) {
            return
        }
        val repoName = binding.repoNameInput.text.toString().trim()
        val repoDescription = binding.repoDescriptionInput.text.toString().trim()

        val repoRequest = RepoRequest(repoName, repoDescription)
        val apiService = RetrofitClient.gitHubApiService
        val call = apiService.updateRepo(repoOwner, originalRepoName, repoRequest)

        call.enqueue(object : Callback<Repo> {
            override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio actualizado exitosamente")
                    finish()
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "Error: No autorizado."
                        403 -> "Prohibido"
                        404 -> "Repositorio no encontrado."
                        else -> "Error ${response.code()}"
                    }
                    showMessage(errorMessage)
                }
            }
            override fun onFailure(call: Call<Repo>, t: Throwable) {
                val errorMsg = "Error al actualizar el repositorio: ${t.message}"
                Log.e("RepoForm", errorMsg, t)
                showMessage(errorMsg)
            }
        })
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}