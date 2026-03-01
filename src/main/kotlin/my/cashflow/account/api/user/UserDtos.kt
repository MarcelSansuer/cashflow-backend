package my.cashflow.account.api.user

data class CreateUserRequest(
    val userName: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val password: String,
){
    fun validate() {
        require(userName.isNotBlank()) { "Username must not be blank" }
        require(email.isNotBlank()) { "Email must not be blank" }
        require(firstName.isNotBlank()) { "First name must not be blank" }
        require(lastName.isNotBlank()) { "Last name must not be blank" }
        require(age > 0) { "Age must be greater than 0" }
        require(password.isNotBlank()) { "Password must not be blank" }
    }
}

data class CreateUserResponse(
    val id: String,
    val userName: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val role: String,
    val status: String,
    val createdAt: String,
)

data class GetUserResponse(
    val id: String,
    val userName: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val role: String,
    val status: String,
    val createdAt: String,
    val closedAt: String?,
    val changedAt: String?
)

