package controllers;

import play.i18n.Lang;
import play.mvc.Controller;

public class Langs extends Controller {

    public static void switchTo(String url, String lang) {
        Lang.change(lang);

        redirect(url);
    }
}
