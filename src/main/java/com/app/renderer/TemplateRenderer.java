package com.app.renderer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import gg.jte.CodeResolver;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import gg.jte.resolve.DirectoryCodeResolver;

public class TemplateRenderer {
  private final TemplateEngine templateEngine;

  public TemplateRenderer() {
    Path templateDir = Path.of("src/main/jte");

    if (Files.exists(templateDir)) {
      CodeResolver codeResolver = new DirectoryCodeResolver(templateDir);
      this.templateEngine = TemplateEngine.create(
          codeResolver,
          Path.of("jte-classes"),
          ContentType.Html,
          TemplateRenderer.class.getClassLoader()
      );
    } else {
      this.templateEngine = TemplateEngine.createPrecompiled(ContentType.Html);
    }
  }

  public String render(String template, Map<String, Object> params) {
    StringOutput output = new StringOutput();
    templateEngine.render(template, params, output);
    
    return output.toString();
  }
}
