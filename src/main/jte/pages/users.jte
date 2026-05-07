@import java.util.List
@import com.app.model.User
@import com.app.ui.TitleVariant
@import com.app.ui.TextVariant

@param List<User> users

@template.layout(title = "Utilisateurs", content = @`
  @template.atoms.title(label = "Whereas disregard", variant = TitleVariant.H1)
  @template.atoms.title(label = "Whereas disregard", variant = TitleVariant.H2)
  @template.atoms.title(label = "Whereas disregard", variant = TitleVariant.H3)

  @template.atoms.text(label = "Voici la liste des utilisateurs enregistrés dans notre système.", variant = TextVariant.BODY_REGULAR)
  @template.atoms.text(label = "Voici la liste des utilisateurs enregistrés dans notre système.", variant = TextVariant.BODY_MEDIUM)
  @template.atoms.text(label = "Voici la liste des utilisateurs enregistrés dans notre système.", variant = TextVariant.CAPTION)
  @template.atoms.text(label = "Voici la liste des utilisateurs enregistrés dans notre système.", variant = TextVariant.LABEL_UPPERCASE)

  <ul>
    @for(User user : users)
      <li>${user.getName()} — ${user.getEmail()}</li>
    @endfor
  </ul>
`)
