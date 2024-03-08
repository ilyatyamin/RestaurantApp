import enums.Type

fun main() {
    val console = ConsoleSystem()
    while (true) {
        when (console.userType) {
            Type.Admin -> {
                console.makeChoiceAdminMenu()
            }

            Type.Visitor -> {
                console.makeChoiceVisitorMenu()
            }

            Type.None -> {
                console.makeChoiceRegistrateMenu()
            }

            Type.Exit -> {
                break
            }
        }
    }
    console.final()
}