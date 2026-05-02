@import java.util.List
@import com.app.model.User

@param List<User> users

<!DOCTYPE html>
<html>
<head><title>Users</title></head>
<body>
  <h1>Liste des utilisateurs</h1>
  <ul>
    @for(User user : users)
      <li>${user.getName()} — ${user.getEmail()}</li>
    @endfor
  </ul>
</body>
</html>