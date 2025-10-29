package ec.edu.uisek.githubclient.models

import com.google.gson.annotations.SerializedName


data class RepoOwner(
    val id: Long,
    val login: String,
    @SerializedName("avatar_url")
    val avatarUrl: String
)

// Now, let's create a function to use it...  <- ¡ERROR AQUÍ! Este texto no es una declaración válida.
    