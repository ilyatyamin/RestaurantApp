package restaurant.usersystem

class Visitor(login: String, password: String) : User(login, password, UserRole.Visitor) {}