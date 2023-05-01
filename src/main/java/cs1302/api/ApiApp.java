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

    Stage stage;
    Scene scene;
    VBox vbox;
    HBox top;
    HBox mid;
    HBox bottom;
    Label plot;
    ComboBox<String> yearOptions;
    ComboBox<String> genreOptions;
    ImageView frame;
    Image poster;
    Button button;

    private URI imdb;
    private String url;
    private String[] loadingImgs;
    int resultIndex;

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
        plot = new Label("Blah blah blah blah");      //replace with ALOTO info
        plot.setFont(new Font("Open Sans", 14));
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
        resultIndex = 0;
    } // ApiApp

    /** {@inheritDoc} */
    @Override
    public void init() {
        HBox.setHgrow(top, Priority.ALWAYS);
        HBox.setHgrow(mid, Priority.ALWAYS);
        HBox.setHgrow(bottom, Priority.ALWAYS);
        HBox.setHgrow(genreOptions, Priority.ALWAYS);
        HBox.setHgrow(yearOptions, Priority.ALWAYS);
        top.getChildren().addAll(genreOptions, yearOptions);
        mid.getChildren().addAll(frame, plot);
        bottom.getChildren().addAll(button);
        vbox.getChildren().addAll(top, mid, bottom);

        Runnable t = () -> {
            System.out.println("starting");
            button.setDisable(true);
            generateMovie();
            button.setDisable(false);
            System.out.println("done");
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
            //String year = URLEncoder
            //.encode(setYear(yearOptions.getValue()), StandardCharsets.UTF_8);
            //String genre = URLEncoder
            //.encode(setGenre(genreOptions.getValue()), StandardCharsets.UTF_8);
            String year = setYear(yearOptions.getValue());
            String genre = setYear(genreOptions.getValue());
            String urlIMDb = "https://imdb-api.com/API/AdvancedSearch/k_hcvcf6rk?title_type=feature";
            String queryIMDb = urlIMDb + year + genre
                + "&certificates=us:G,us:PG,us:PG-13&count=100";
            System.out.println(queryIMDb);
            HttpRequest requestIMDb = HttpRequest.newBuilder()
                .uri(URI.create(urlIMDb))
                .build();
            HttpResponse<String> responseIMDb = HTTP_CLIENT
                .send(requestIMDb, BodyHandlers.ofString());
            String jsonStringIMDb = responseIMDb.body();
            IMDBResponse imdbResponse = GSON
                .fromJson(jsonStringIMDb, IMDBResponse.class);
            IMDBResult imdbRes = imdbResponse.results[resultIndex];
            System.out.println();
            System.out.println(jsonStringIMDb);
            System.out.println();

            String movieTitle = imdbRes.title.replaceAll(" ", "%20");
            System.out.println(movieTitle);
            //movieTitle = movieTitle.replaceAll("?", "%3F");
            String urlR = "https://movie-database-alternative.p.rapidapi.com/?s=";
            urlR += movieTitle + "&r=json&page=1";
            HttpRequest posterRequest = HttpRequest.newBuilder()
                .uri(URI.create(urlR))
                .header("X-RapidAPI-Key", "7178cc256bmsh31c7770921fb3ddp1e3ff2jsn946da8ba257d")
                .header("X-RapidAPI-Host", "movie-database-alternative.p.rapidapi.com")
                //.method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
            HttpResponse<String> posterResponse = HttpClient.newHttpClient()
                .send(posterRequest, BodyHandlers.ofString());
            //System.out.println(posterResponse.body());
            String jsonStringPoster = posterResponse.body();
            System.out.println();
            System.out.println(jsonStringPoster);
            System.out.println();
            PosterResponse posterRes = GSON
                .fromJson(jsonStringPoster, PosterResponse.class);

            poster = new Image(posterRes.search[0].poster);
            frame.setImage(poster);
            if (resultIndex >= 100) {
                resultIndex = 0;
            } else {
                resultIndex++;
            }
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
        if (value.equalsIgnoreCase("Any decade")) {
            y = "";
        } else if (value.equalsIgnoreCase("1950s")) {
            y = "&release_date=1950-01-01,1959-12-31";
        } else if (value.equalsIgnoreCase("1960s")) {
            y = "&release_date=1960-01-01,1969-12-31";
        } else if (value.equalsIgnoreCase("1970s")) {
            y = "&release_date=1970-01-01,1979-12-31";
        } else if (value.equalsIgnoreCase("1980s")) {
            y = "&release_date=1980-01-01,1989-12-31";
        } else if (value.equalsIgnoreCase("1990s")) {
            y = "&release_date=1990-01-01,1999-12-31";
        } else if (value.equalsIgnoreCase("2000s")) {
            y = "&release_date=2000-01-01,2009-12-31";
        } else if (value.equalsIgnoreCase("2010s")) {
            y = "&release_date=2010-01-01,2019-12-31";
        } //else

        return y;
    } //setYear

    /**
     * This method sets the genre for the HttpRequest.
     *
     * @param value the value to compare
     * @return the value to use in the HttpRequest
     */
    private String setGenre(String value) {
        String g = "";
        if (value.equalsIgnoreCase("Any genre")) {
            g = "";
        } else if (value.equalsIgnoreCase("Action")) {
            g = "&genres=action";
        } else if (value.equalsIgnoreCase("Comedy")) {
            g = "&genres=comedy";
        } else if (value.equalsIgnoreCase("Family")) {
            g = "&genres=family";
        } else if (value.equalsIgnoreCase("Mystery")) {
            g = "&genres=mystery";
        } else if (value.equalsIgnoreCase("Sci-Fi")) {
            g = "&genres=sci_fi";
        } else if (value.equalsIgnoreCase("Adventure")) {
            g = "&genres=adventure";
        } else if (value.equalsIgnoreCase("Fantasy")) {
            g = "&genres=fantasy";
        } else if (value.equalsIgnoreCase("Horror")) {
            g = "&genres=horror";
        } else if (value.equalsIgnoreCase("Animation")) {
            g = "&genres=animation";
        } else if (value.equalsIgnoreCase("Drama")) {
            g = "&genres=drama";
        } else if (value.equalsIgnoreCase("Musical")) {
            g = "&genres=musical";
        } else if (value.equalsIgnoreCase("Romance")) {
            g = "&genres=romance";
        } else if (value.equalsIgnoreCase("Thriller")) {
            g = "&genres=thriller";
        } //else

        return g;
    } //setGenre





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
     * Creates a ComboBox with the decades to choose from.
     *
     * @return the desired ComboBox
     */
    private ComboBox<String> createYearOptions() {
        //ObservableList<String> years = new ObservableList<>();
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
