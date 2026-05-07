@import java.util.List
@import com.app.model.User

@param List<User> users

@template.layout(title = "Utilisateurs", content = @`
  <h1>Liste des utilisateurs</h1>
  @template.atoms.text(label = "Voici la liste des utilisateurs enregistrés dans notre système.", variant = "body-regular")
  <ul>
    @for(User user : users)
      <li>${user.getName()} — ${user.getEmail()}</li>
    @endfor
  </ul>
`)
