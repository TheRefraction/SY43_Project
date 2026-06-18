package fr.utbm.sy43.pilulito.data.models

enum class AccountType {
    SENIOR,
    SUPERVISOR
}

data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val accountType: AccountType = AccountType.SENIOR,
    val linkCode: String = "",
    val linkedSeniorId: String? = null
)