interface AccountServiceInterface {

    fun createAccount(name: String): String

    fun getAccountBalance(accountId: String): Double

    fun deposit(accountId: String, amount: Double)

    fun withdraw(accountId: String, amount: Double)

}