package com.app.renderer;

import java.nio.file.Path;
import java.util.List;

import com.app.model.User;

import gg.jte.CodeResolver;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import gg.jte.resolve.DirectoryCodeResolver;

public class TemplateRenderer {
  private final TemplateEngine templateEngine;

  public TemplateRenderer() {
    CodeResolver codeResolver = new DirectoryCodeResolver(Path.of("src/main/jte"));

    this.templateEngine = TemplateEngine.create(
        codeResolver,
        Path.of("jte-classes"),
        ContentType.Html,
        TemplateRenderer.class.getClassLoader()
    );
  }

  public String renderUsers(List<User> users) {
    StringOutput output = new StringOutput();

    templateEngine.render("users.jte", users, output);

    return output.toString();
  }
}
