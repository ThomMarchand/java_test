@import gg.jte.Content

@param String title
@param Content content

<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>${title}</title>
</head>
<body>
  <nav>
    <a href="/">Accueil</a>
    <a href="/landing">Landing</a>
  </nav>
  <main>
    ${content}
  </main>
</body>
</html>
