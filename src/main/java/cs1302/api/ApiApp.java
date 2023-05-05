package cs1302.api;

//import http-related components
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//import scene graph components
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.Font;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import java.lang.Math;
import java.lang.StringBuilder;
import java.time.LocalTime;

//import exceptions
import java.io.IOException;
import java.lang.InterruptedException;

/**
 * Represents an application that generates a random movie with its plot and other information.
 */
public class ApiApp extends Application {

    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build();

    public static Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .create();

    public static final String DEF_BG = "file:resources/defaultbg.jpg";

    public static final String DEF_TEXT = "During World War II when all the"
        + " men are fighting the war, most of the jobs that were left vacant"
        + " because of their absence were filled in by women. The owners of"
        + " the baseball teams, not wanting baseball to be dormant indefinitely,"
        + " decide to form teams with women. So scouts are sent all over the"
        + " country to find women players. One of the scouts, passes through"
        + " Oregon and finds a woman named Dottie Hinson, who is incredible."
        + " He approaches her and asks her to try out but she's not interested."
        + " However, her sister, Kit who wants to get out of Oregon, offers to"
        + " go. But he agrees only if she can get her sister to go. When they"
        + " try out, they're chosen and are on the same team. Jimmy Dugan,"
        + " a former player, who's now a drunk, is the team manager."
        + " But he doesn't feel as if it's a real job so he drinks and is not"
        + " exactly doing his job. So Dottie steps up. After a few months when"
        + " it appears the girls are not garnering any attention, the league is"
        + " facing closure till Dottie does something that grabs attention."
        + " And it isn't long Dottie is the star of the team and Kit feels"
        + " like she's living in her shadow.";

    Stage stage;
    Scene scene;
    VBox vbox;
    HBox top;
    HBox mid;
    HBox bottom;
    Label plotLabel;
    ComboBox<String> yearOptions;
    ComboBox<String> genreOptions;
    ImageView frame;
    Image poster;
    Button button;

    private URI imdb;
    private String url;
    private String plot;
    private String[] loadingImgs;

    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        this.stage = null;
        this.scene = null;
        vbox = new VBox();
        top = new HBox(8);
        top.setAlignment(Pos.BOTTOM_CENTER);
        mid = new HBox(8);
        mid.setMinWidth(640);
        bottom = new HBox(8);
        bottom.setAlignment(Pos.BOTTOM_CENTER);
        plotLabel = new Label(formatString(DEF_TEXT));
        plotLabel.setFont(new Font("Nimbus Sans", 12));
        yearOptions = createYearOptions();
        yearOptions.setMinWidth(175);
        yearOptions.setStyle("-fx-font: 14px \"Open Sans\";");
        genreOptions = createGenreOptions();
        genreOptions.setMinWidth(400);
        genreOptions.setStyle("-fx-font: 14px \"Open Sans\";");
        button = new Button("Generate a random movie!");
        button.setMaxWidth(400);
        button.setTextAlignment(TextAlignment.CENTER);
        button.setFont(new Font("Open Sans Italic", 25));
        frame = new ImageView(DEF_BG);
        frame.setFitWidth(344);
        frame.setPreserveRatio(true);
        poster = new Image(DEF_BG);
        loadingImgs = setLoadingImgs();
    } // ApiApp

    /** {@inheritDoc} */
    @Override
    public void init() {
        VBox.setVgrow(vbox, Priority.ALWAYS);
        HBox.setHgrow(top, Priority.ALWAYS);
        HBox.setHgrow(mid, Priority.ALWAYS);
        HBox.setHgrow(bottom, Priority.ALWAYS);
        HBox.setHgrow(genreOptions, Priority.ALWAYS);
        HBox.setHgrow(yearOptions, Priority.ALWAYS);
        top.getChildren().addAll(genreOptions, yearOptions);
        mid.getChildren().addAll(frame, plotLabel);
        bottom.getChildren().addAll(button);
        vbox.getChildren().addAll(top, mid, bottom);

        //System.out.println(Font.getFamilies());
        //System.out.println(Font.getFontNames("Nimbus Sans"));
        //System.out.println("\n\n" + Font.getFontNames("Open Sans Light"));
        //System.out.println("\n\n" + Font.getFontNames("SansSerif"));


        Runnable t = () -> {
            try {
                //System.out.println("starting");
                button.setDisable(true);
                for (int i = 0; i < 6; i++) {
                    loadingScreen(i);
                    Thread.sleep(1000);
                } //for
                generateMovie();
                Platform.runLater(() -> plotLabel.setText(plot));
                button.setDisable(false);
                //System.out.println("done");
            } catch (Throwable e) {
                System.out.println(e.toString());
            } //catch
        }; //t

        button.setOnAction(event -> {
            runNow(t);
        });





    } //init

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.scene = new Scene(this.vbox);

        // demonstrate how to load local asset using "file:resources/"

        // some labels to display information

        // setup scene

        // setup stage
        stage.setTitle("ApiApp!");
        stage.setScene(this.scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();
        instructionsAlert();
        Platform.runLater(() -> this.stage.setResizable(false));
    } // start


    /** {@inheritDoc} */
    @Override
    public void stop() {
        //stop
    } //stop




    //private methods here

    /**
     * This method generates a random movie based on the user's choices.
     */
    private void generateMovie() {
        try {
            String year = setYear(yearOptions.getValue());
            System.out.println("year: " + year);
            String genre = setGenre(genreOptions.getValue());
            System.out.println("genre: " + genre);
            String urlIMDb = "https://imdb-api.com/API/"
                + "AdvancedSearch/k_hcvcf6rk?title_type=feature";
            String queryIMDb = urlIMDb + year + genre
                + "&certificates=us:G,us:PG,us:PG-13&count=100";
            System.out.println(queryIMDb);
            HttpRequest requestIMDb = HttpRequest.newBuilder()
                .uri(URI.create(queryIMDb))
                .build();
            HttpResponse<String> responseIMDb = HTTP_CLIENT
                .send(requestIMDb, BodyHandlers.ofString());
            String jsonStringIMDb = responseIMDb.body();
            IMDBResponse imdbResponse = GSON
                .fromJson(jsonStringIMDb, IMDBResponse.class);
            IMDBResult imdbRes = imdbResponse.results[0];

            //System.out.println("\n\n" + jsonStringIMDb + "\n\n");

            //second request
            String urlO = "http://www.omdbapi.com/?i=";
            urlO += imdbResponse.results[0].id;
            urlO += "&apikey=3015adf0&plot=full";
            HttpRequest requestO = HttpRequest.newBuilder()
                .uri(URI.create(urlO))
                .build();
            HttpResponse<String> responseO = HTTP_CLIENT
                .send(requestO, BodyHandlers.ofString());
            String jsonStringO = responseO.body();
            //System.out.println("\n\n" + jsonStringO + "\n\n");
            OMDBResponse oResponse = GSON
                .fromJson(jsonStringO, OMDBResponse.class);

            poster = new Image(imdbRes.image);
            frame.setImage(poster);

            plot = formatString(oResponse.plot);
            //plot.setText(oPlot);
            //System.out.println("\n\n" + plot + "\n\n");
        } catch (IOException | InterruptedException ioe) {
            System.out.println("exception thrown: ioe or ie");
        } //catch
    } //generateMovie


    /**
     * This method sets the year for the movie generator.
     *
     * @param value the value to compare
     * @return the year for the HttpRequest
     */
    private String setYear(String value) {
        String y = "";
        int yearRandom = 0;
        if (value.equalsIgnoreCase("Any decade")) {
            yearRandom = (int) (Math.random() * 72) + 1950;
        } else if (value.equalsIgnoreCase("1950s")) {
            yearRandom = (int) (Math.random() * 10) + 1950;
        } else if (value.equalsIgnoreCase("1960s")) {
            yearRandom = (int) (Math.random() * 10) + 1960;
        } else if (value.equalsIgnoreCase("1970s")) {
            yearRandom = (int) (Math.random() * 10) + 1970;
        } else if (value.equalsIgnoreCase("1980s")) {
            yearRandom = (int) (Math.random() * 10) + 1980;
        } else if (value.equalsIgnoreCase("1990s")) {
            yearRandom = (int) (Math.random() * 10) + 1990;
        } else if (value.equalsIgnoreCase("2000s")) {
            yearRandom = (int) (Math.random() * 10) + 2000;
        } else if (value.equalsIgnoreCase("2010s")) {
            yearRandom = (int) (Math.random() * 10) + 2010;
        } //else
        y = "&release_date=" + yearRandom + "-01-01," + yearRandom + "-12-31";
        return y;
    } //setYear

    /**
     * This method sets the genre for the HttpRequest.
     *
     * @param value the value to compare
     * @return the value to use in the HttpRequest
     */
    private String setGenre(String value) {
        value = genreOptions.getValue();
        String g = "";
        if (value.equals("Any genre")) {
            g = "";
        } else {
            g = "&genres=" + value.trim().toLowerCase();
            g = g.replace(" ", "-");
        } //else

        return g;
    } //setGenre



    /**
     * Returns a formatted String for the plot section.
     *
     * @param orig the String to be formatted
     * @return the formatted String
     */
    private String formatString(String orig) {
        String[] works = orig.split(" ");
        StringBuilder line = new StringBuilder();
        StringBuilder result = new StringBuilder();
        for (String work : works) {
            if (line.length() + work.length() > 60) {
                result.append(line).append("\n");
                line = new StringBuilder();
            }
            line.append(work).append(" ");
        }
        result.append(line);

        //System.out.println(result.toString());
        return result.toString();
    } //formatString



    /**
     * This method creates the String array with the loading image links.
     *
     * @return the new String array
     */
    private String[] setLoadingImgs() {
        String[] s = new String[6];
        s[0] = "file:resources/spiderman2.jpg";
        s[1] = "file:resources/shawshank.jpg";
        s[2] = "file:resources/the_craft.jpg";
        s[3] = "file:resources/10_things.jpg";
        s[4] = "file:resources/castaway.jpg";
        s[5] = "file:resources/the_apartment.jpg";

        return s;
    } //setLoadingImgs



    /**
     * This method cycles through the loading images.
     * @param i the image index to set
     */
    private void loadingScreen(int i) {
        poster = new Image(loadingImgs[i]);
        frame.setImage(poster);
    } //loadingScreen


    /**
     * Creates a ComboBox with the decades to choose from.
     *
     * @return the desired ComboBox
     */
    private ComboBox<String> createYearOptions() {
        ComboBox<String> op = new ComboBox<>();
        op.getItems().addAll(
            new String("Any decade"),
            new String("1950s"),
            new String("1960s"),
            new String("1970s"),
            new String("1980s"),
            new String("1990s"),
            new String("2000s"),
            new String("2010s")
        );
        op.setValue("Any decade");
        return op;
    } //yearOptions



    /**
     * This method creates the ComboBox for the genres.
     *
     * @return the desired ComboBox
     */
    private ComboBox<String> createGenreOptions() {
        ComboBox<String> op = new ComboBox<>();
        op.getItems().addAll(
            new String("Any genre"),
            new String("Action"),
            new String("Comedy"),
            new String("Family"),
            new String("Mystery"),
            new String("Sci-Fi"),
            new String("Adventure"),
            new String("Fantasy"),
            new String("Horror"),
            new String("Animation"),
            new String("Drama"),
            new String("Musical"),
            new String("Romance"),
            new String("Thriller")
        );
        op.setValue("Any genre");

        return op;
    } //genreOptions



    /**
     * Creates and immediately starts a new daemon thread that executes
     * {@code target.run()}. This method, which may be called from any thread,
     * will return immediately its caller.
     * @param target the object whose {@code run} method is invoked when this
     * thread is started.
     */
    public static void runNow(Runnable target) {
        Thread t = new Thread(target);
        t.setDaemon(true);
        t.start();
    } //runNow



    /**
     * This method creates an instructions alert window.
     */
    public void instructionsAlert() {
        Platform.runLater(() -> {
            String s = "Welcome to the random movie generator!\n\n";
            s += "Just select a decade and/or a genre from the menu and click the\n";
            s += "button to get started. If you don't choose a decade or a genre,\n";
            s += "the search will be completely random.\n\n";
            s += "For the sake of keeping this SFW, movies rated R or\n";
            s += "higher have been excluded from the results.\n\n";
            s += "So enjoy and happy clicking!";
            TextArea text = new TextArea(s);
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.getDialogPane().setContent(text);
            alert.setHeight(350.0);
            alert.setWidth(475.0);
            alert.setResizable(false);
            alert.showAndWait();
        });


    } //instructionsAlert














} // ApiApp
