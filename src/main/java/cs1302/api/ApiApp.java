package cs1302.api;

//import http-related components
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpResponse.BodyHandlers;
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
    //Label instructions;
    Label plot;
    ComboBox<String> yearOptions;
    ComboBox<String> genreOptions;
    ImageView frame;
    Image poster;
    Button button;

    private URI imdb;
    private String url;

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
        yearOptions.setMinWidth(150);
        yearOptions.setStyle("-fx-font: 14px \"Open Sans\";");
        genreOptions = createGenreOptions();
        genreOptions.setMinWidth(300);
        genreOptions.setStyle("-fx-font: 14px \"Open Sans\";");
        button = new Button("Generate a random movie!");
        button.setMaxWidth(400);
        button.setTextAlignment(TextAlignment.CENTER);
        button.setFont(new Font("Open Sans Italic", 25));
        frame = new ImageView(DEF_BG);
        frame.setFitWidth(344);
        frame.setPreserveRatio(true);
        poster = new Image(DEF_BG);
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
        button.setDisable(true);



        //System.out.println(Font.getFamilies());
        //System.out.println("\n\n");
        //System.out.println(Font.getFontNames("Open Sans"));






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
