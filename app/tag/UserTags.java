package tag;

import groovy.lang.Closure;

import java.io.PrintWriter;
import java.util.Map;

import models.User;
import models.UserType;

import play.templates.FastTags;
import play.templates.GroovyTemplate.ExecutableTemplate;

@FastTags.Namespace("tc.user")
public class UserTags extends FastTags {
	public static void _name(Map<?, ?> args, Closure body, PrintWriter out,
			ExecutableTemplate template, int fromLine) {
		User user = (User) args.get("arg");
		if(user.type == UserType.NATIVE){
			if(user.fullName == null){
				out.println(user.email);
			}
			else{
				out.print(user.fullName);
			}
		}
		else if(user.type == UserType.TWITTER){
			if(user.fullName == null){
				out.println(user.screenName);
			}
			else{
				out.println(user.fullName);
			}
		}
		else{
			out.println(user.fullName);
		}
	}
}