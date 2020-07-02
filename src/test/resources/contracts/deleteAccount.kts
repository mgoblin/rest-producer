import org.springframework.cloud.contract.spec.ContractDsl.Companion.contract

arrayOf(
        contract {
            name = "Should delete existing account"
            description = "Delete existing account"
            request {
                method = DELETE
                url = url("/account")
                headers {
                    header("Content-Type", "application/json")
                }
                body = body(mapOf(
                        "accountNumber" to "1"
                ))
            }
            response {
                status = OK
                headers {
                    header("Content-Type", "application/json")
                }
                body = body(mapOf(
                        "accountNumber" to "1",
                        "accountName" to "MikeAccount",
                        "accountBalance" to 1000,
                        "accountStatus" to "ACTIVE"
                ))
            }
        }
)