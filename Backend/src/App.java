import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;
import io.javalin.http.Context;

public class App {
    public static void main(String[] args) {
        App runner = new App();
        Javalin app = Javalin.create( )
                .post("/", ctx -> runner.updateMap(ctx))
                .start(7070);
    }


    public void updateMap(Context ctx){
        System.out.println(ctx.body());
    }

}