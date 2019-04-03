package controllers;

import play.i18n.Lang;
import play.mvc.Controller;
import play.mvc.With;

@With(Tracker.class)
public class Langs extends Controller {

    public static void switchTo(String url, String lang) {
        Lang.change(lang);

        redirect(url);
    }
}
