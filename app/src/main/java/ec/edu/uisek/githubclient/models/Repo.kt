package ec.edu.uisek.githubclient.models

data class Repo (
    val id: Long,
    val name: String,
    val description: String,
    val lenguage: String?,
    val owner: RepoOwner,


    )