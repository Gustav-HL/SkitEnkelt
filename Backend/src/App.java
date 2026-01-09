import Resources.*;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.json.JavalinGson;

import java.awt.*;
import java.io.FileNotFoundException;
import java.net.URI;

public class App {
    public static void main(String[] args) throws FileNotFoundException {
        ToiletHandler toiletHandler = new ToiletHandler();

        Javalin.create(config -> {
                    config.jsonMapper(new JavalinGson());
                    config.bundledPlugins.enableCors(cors -> {
                        cors.addRule(it -> {
                            it.anyHost();
                        });
                    });
                    //Lägger till en path för att starta webbläsaren när backenden startar upp.
                    config.staticFiles.add(staticFiles -> {
                        staticFiles.directory = "Frontend";
                        staticFiles.location = Location.EXTERNAL;
                    });
                })
                .get("/toilets", ctx -> {
                    toiletHandler.getAllToilets(ctx);

                })
                //TODO- Vi hade kanske velat ha en scroll på hemsidan som ställer in range. Alternativt en egen flik, kanske hittar närmaste toaletterna till ens egen position.
                .post("/", ctx -> toiletHandler.addReview(ctx))
                .start(7070);


        //Startar webbläsaren när App.java exekverar, om möjligt.
        //Annars händer ingenting förutom en liten utskrift i terminalen
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI("http://localhost:7070/test.html"));
            }
        } catch (Exception e) {
            System.out.println("Starta html-filen manuellt.");
        }
    }


}