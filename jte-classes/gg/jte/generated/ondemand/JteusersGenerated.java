package gg.jte.generated.ondemand;
import java.util.List;
import com.app.model.User;
public final class JteusersGenerated {
	public static final String JTE_NAME = "users.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,3,3,3,11,11,11,12,12,12,12,12,12,13,13,16,16,16,3,3,3,3};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, List<User> users) {
		jteOutput.writeContent("\n<!DOCTYPE html>\n<html>\n<head><title>Users</title></head>\n<body>\n  <h1>Liste des utilisateurs</h1>\n  <ul>\n    ");
		for (User user : users) {
			jteOutput.writeContent("\n      <li>");
			jteOutput.setContext("li", null);
			jteOutput.writeUserContent(user.getName());
			jteOutput.writeContent(" — ");
			jteOutput.setContext("li", null);
			jteOutput.writeUserContent(user.getEmail());
			jteOutput.writeContent("</li>\n    ");
		}
		jteOutput.writeContent("\n  </ul>\n</body>\n</html>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		List<User> users = (List<User>)params.get("users");
		render(jteOutput, jteHtmlInterceptor, users);
	}
}
