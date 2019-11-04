
import java.awt.*;
import java.io.File;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Application;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.animation.RotateTransition;
import javafx.util.Duration;

import javax.sound.sampled.*;

/**
 * Simple implementation of the Rubik's cube using JavaFX 3D
 * http://stackoverflow.com/questions/34001900/how-to-render-3d-graphics-properly
 * @author JosePereda
 */
public class RubiksCube extends Application {

    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private int rozmiar_wyswietlacza=9;
    private int przekretlo_angle = 0;
    private boolean flaga = false;
    public Text text;
    public int aktualnyCzas=0;
    public Timer timer=new Timer();
    private boolean gotowe=false;

    @Override
    public void start(Stage primaryStage) {
        //Swiatlo na calosc
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(50);
        light.setTranslateY(-300);
        light.setTranslateZ(-400);

        //swiatlo w srodku mikrofali
        //TODO: to swiatlo jest teoretycznie w mikrofali i dziala, plan jet taki zeby sie wlaczalo podczas wlaczenie animacji
        //zeby ustawic najlatwiej wylaczyc swiatlo 'light' i zostawic tylko to
        PointLight mikrofalowe = new PointLight(Color.rgb(0,0,0));
        mikrofalowe.setTranslateX(350);
        mikrofalowe.setTranslateY(300);
        mikrofalowe.setTranslateZ(300);

        //calosc 1050 650 1000 0 0 500
        //Robienie klockow do mikrofali
        //top 1050 50 1000 spawn 0 0 500
        Box top = new Box(1050,50,1000);
        top.setTranslateX(0);
        top.setTranslateY(0);
        top.setTranslateZ(500);
        PhongMaterial phong = new PhongMaterial();
        phong.setDiffuseColor(Color.ORANGE);
        top.setMaterial(phong);

        //bot 1050 50 1000 spawn 0 600 500
        Box bot = new Box(1050,50,1000);
        bot.setTranslateX(0);
        bot.setTranslateY(600);
        bot.setTranslateZ(500);
        PhongMaterial phong2 = new PhongMaterial();
        phong2.setDiffuseColor(Color.WHEAT);
        bot.setMaterial(phong2);

        //left 650 50 1000 spawn 0 0
        Box left = new Box(50,650,1000);
        left.setTranslateX(-500);
        left.setTranslateY(300);
        left.setTranslateZ(500);
        PhongMaterial phong3 = new PhongMaterial();
        phong3.setDiffuseColor(Color.SKYBLUE);
        left.setMaterial(phong3);

        //right 650 50 1000 spawn 1000 0
        Box right = new Box(50,650,1000);
        right.setTranslateX(500);
        right.setTranslateY(300);
        right.setTranslateZ(500);
        PhongMaterial phong4 = new PhongMaterial();
        phong4.setDiffuseColor(Color.OLIVE);
        right.setMaterial(phong4);

        //back 1050 650 1000
        Box back = new Box(1050,650,50);
        back.setTranslateX(0);
        back.setTranslateY(300);
        back.setTranslateZ(1000);
        PhongMaterial phong5 = new PhongMaterial();
        phong5.setDiffuseColor(Color.BROWN);
        back.setMaterial(phong5);

        //front 1050 650 1000
        Box front = new Box(300,650,1025);
        front.setTranslateX(350);
        front.setTranslateY(300);
        front.setTranslateZ(500);
        PhongMaterial phong6 = new PhongMaterial();
        phong6.setDiffuseColor(Color.VIOLET);
        back.setMaterial(phong6);


        Box drzwiczki = new Box(750,650,5);
        drzwiczki.setTranslateX(-100);
        drzwiczki.setTranslateY(300);
        PhongMaterial phong8 = new PhongMaterial();
        //phong8.setDiffuseColor(Color.SILVER);
        phong8.setDiffuseColor(new Color(1,1,1,0.6));  // Note alpha of 0.6
        phong8.diffuseMapProperty();
        drzwiczki.setMaterial(phong8);

        //przkretlo
        Cylinder cylinder = new Cylinder(100,100);
        cylinder.setTranslateX(350);
        cylinder.setTranslateY(400);
        cylinder.setTranslateZ(-50);
        cylinder.setRotationAxis(new Point3D(1,0,0));
        cylinder.setRotate(90);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(new Image(getClass().getResourceAsStream("metal-texture.jpg")));
        cylinder.setMaterial(material);

        //krecacy sie spodek w mikrofali
        Cylinder spodek = new Cylinder(300,10);
        spodek.setTranslateX(-150);
        spodek.setTranslateY(550);
        spodek.setTranslateZ(500);
        spodek.setMaterial(material);


        //przycisk
        Cylinder button = new Cylinder(20,30);
        button.setTranslateX(360);
        button.setTranslateY(200);
        button.setTranslateZ(-50);
        button.setRotationAxis(new Point3D(1,0,0));
        button.setRotate(90);
        PhongMaterial phong7 = new PhongMaterial();
        phong7.setDiffuseColor(Color.LIMEGREEN);
        button.setMaterial(phong7);



        text = new Text();
        text.setText(" 0:00 ");
        text.setStyle("-fx-font-size: 60;");
        text.setCache(true);
        BorderPane borderPane = new BorderPane();
        borderPane.setTranslateZ(-50);
        borderPane.setTranslateX(290);
        borderPane.setStyle("-fx-border-color: black;-fx-background-color: #66CCFF;");
        borderPane.setTop(text);
        Sphere kulka = new Sphere(10,10);
        //gdybys cos dodawal to tutaj musisz wpisac wszystkie obiekty 3D + swiatla
//        Group group = new Group( spodek, drzwiczki, button , top, left, right, bot, back, front, cylinder, light, mikrofalowe);
       // Group group = new Group( borderPane,spodek,  button , top, left, right, bot, back, front, cylinder, light, mikrofalowe);
        Group group = new Group( borderPane,spodek, kulka, drzwiczki, button , top, left, right, bot, back, front, cylinder, light, mikrofalowe);


        Scene scene = new Scene(
                new StackPane(group),
                650, 650,
                true,
                SceneAntialiasing.BALANCED
        );

        scene.setFill(Color.rgb(10, 10, 40));
       // ((Pane) scene.getRoot()).getChildren().add(subScene.getRoot());
        //TODO: moze to kamera sie rusza podczas rotatetransition, jak cos to jest tutaj v

        PerspectiveCamera camera = new PerspectiveCamera();
        camera.setTranslateZ(-2000);
        scene.setCamera(camera);
        primaryStage.setScene(scene);
        primaryStage.show();


        Rotate rotateX = new Rotate(30, 0, 0, 0, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(20, 0, 0, 0, Rotate.Y_AXIS);
        //cylinder.setRotationAxis(new Point3D(0,0,1));

       group.getTransforms().addAll(rotateX,rotateY);


       scene.setOnMouseClicked(me -> {
                RotateTransition rt1 = new RotateTransition(Duration.millis(przekretlo_angle*1000),spodek);
                rt1.setAxis(new Point3D(0,1,0));
                rt1.setByAngle(przekretlo_angle*60);


                if (!flaga) //flaga sprawdza czy przycisk jest wcisniety
                {
                    //zmiana koloru
                    phong7.setDiffuseColor(Color.RED);
                    button.setMaterial(phong7);
                    flaga = true;
                    mikrofalowe.setColor(Color.SALMON);
                    //przygotowanie muzyki
                    String musicFile = "C:\\Users\\Adam\\IdeaProjects\\Projekt_Kck\\src\\mmm.mp3";
                    //Media sound = new Media(new File(musicFile).toURI().toString());
                  //  MediaPlayer mediaPlayer = new MediaPlayer(sound);
                    //mediaPlayer.play();


                    Thread runnable=new Thread() {
                        @Override
                        public void run() {
                            aktualnyCzas=przekretlo_angle;
                            TimerTask update = new TimerTask() {
                                @Override
                                public void run() {
                                    text.setText(print(aktualnyCzas));
                                }
                            };
                            TimerTask x=new TimerTask() {
                                @Override
                                public void run() {

                                    Platform.runLater(update);
                                    aktualnyCzas--;
                                }
                            };

                            timer.schedule(x,1000,1000);
                            long start=System.currentTimeMillis(), koniec=start+(1000*przekretlo_angle);

                            while (aktualnyCzas>0){
                                try{
                                Thread.sleep(50);
                                }catch (InterruptedException ie){
                                    ie.printStackTrace();
                                }

                            }
                            timer.cancel();
                            timer=new Timer();
                            text.setText(" 0:00 ");
                            gotowe=true;
                                phong7.setDiffuseColor(Color.LIMEGREEN);
                                button.setMaterial(phong7);
                                flaga = false;
                                przekretlo_angle=0;
                                rt1.pause();
                                gotowe=false;
                                mikrofalowe.setColor(Color.BLACK);
                        }
                    };
                    runnable.start();

                    //wlaczenie rotatetransition
                    rt1.play();
                   // MediaPlayer finalMediaPlayer = mediaPlayer;

                    //na koniec wykonywania rotate transition wroc przekretlo do stanu poczatkowego + wylacz muzyke
//                    rt1.setOnFinished(ae -> {
//                        for (int i = 0; i < przekretlo_angle && przekretlo_angle >= 0; i ++)
//                        {
//                            Rotate rt2 = new Rotate(-10, 0, 0, 0, Rotate.Y_AXIS);
//                            cylinder.getTransforms().add(rt2);
//                            przekretlo_angle = przekretlo_angle - 1;
//                        }
////                        //finalMediaPlayer.stop();
////                        phong7.setDiffuseColor(Color.LIMEGREEN);
////                        button.setMaterial(phong7);
//                    });

                    //TODO: odtworzyc dzwiek beep.mp3, problem z odtworzeniem calego
                }
//                if(gotowe){
//                    phong7.setDiffuseColor(Color.LIMEGREEN);
//                    button.setMaterial(phong7);
//                    flaga = false;
//                    przekretlo_angle=0;
//                    rt1.pause();
//                    gotowe=false;
//                }
           mouseOldY = me.getSceneY();
           mouseOldX = me.getSceneX();

       });

//        scene.setOnMousePressed(me -> {
//            if(mouseOldX == me.getSceneX() && mouseOldY == me.getSceneY())
//            {
//                //RotateTransition rt0 = new RotateTransition(Duration.millis(przekretlo_angle*1000),cylinder);
//                RotateTransition rt1 = new RotateTransition(Duration.millis(przekretlo_angle*1000),spodek);
//                //rt0.setAxis(new Point3D(0,0,1));
//                //rt0.setByAngle(przekretlo_angle);
//                rt1.setAxis(new Point3D(0,1,0));
//                rt1.setByAngle(przekretlo_angle*60);
//
//
//                if (!flaga) //flaga sprawdza czy przycisk jest wcisniety
//                {
//                    //zmiana koloru
//                    phong7.setDiffuseColor(Color.RED);
//                    button.setMaterial(phong7);
//                    flaga = true;
//
//                    //przygotowanie muzyki
//                    String musicFile = "C:\\Users\\Adam\\IdeaProjects\\Projekt_Kck\\src\\mmm.mp3";
//                    Media sound = new Media(new File(musicFile).toURI().toString());
//                    MediaPlayer mediaPlayer = new MediaPlayer(sound);
//                    mediaPlayer.play();
//
//                    //wlaczenie rotatetransition
//                    rt1.play();
//                    MediaPlayer finalMediaPlayer = mediaPlayer;
//
//                    //na koniec wykonywania rotate transition wroc przekretlo do stanu poczatkowego + wylacz muzyke
//                    rt1.setOnFinished(ae -> {
//                        for (int i = 0; i < przekretlo_angle && przekretlo_angle >= 0; i ++)
//                        {
//                            Rotate rt0 = new Rotate(-10, 0, 0, 0, Rotate.Y_AXIS);
//                            cylinder.getTransforms().add(rt0);
//                            przekretlo_angle = przekretlo_angle - 1;
//                        }
//                        finalMediaPlayer.stop();
//                        phong7.setDiffuseColor(Color.LIMEGREEN);
//                        button.setMaterial(phong7);
//                    });
//
//                    //TODO: odtworzyc dzwiek beep.mp3, problem z odtworzeniem calego
//
//                }
//                else
//                {
//                    phong7.setDiffuseColor(Color.LIMEGREEN);
//                    button.setMaterial(phong7);
//                    flaga = false;
//                    rt1.pause();
//                }
//            }
//
//            mouseOldY = me.getSceneY();
//            mouseOldX = me.getSceneX();
//
//        });

        //stare obracanie z rubikscube
//        scene.setOnMouseDragged(me -> {
//            mousePosX = me.getSceneX();
//            mousePosY = me.getSceneY();
//            rotateX.setAngle(rotateX.getAngle()-(mousePosY - mouseOldY));
//            rotateY.setAngle(rotateY.getAngle()+(mousePosX - mouseOldX));
//            mouseOldX = mousePosX;
//            mouseOldY = mousePosY;
//        });

        //press W nakreca pokretlo, press Q odkreca (o 10 stopni)
        scene.setOnMouseDragged(me -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            rotateX.setAngle(rotateX.getAngle()-(mousePosY - mouseOldY));
            rotateY.setAngle(rotateY.getAngle()+(mousePosX - mouseOldX));
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
        });
        scene.setOnKeyPressed(event ->{
                    KeyCode keyCode = event.getCode();

                    if(keyCode.equals(KeyCode.W)&&!flaga)
                    {
                        Rotate rt0 = new Rotate(10, 0, 0, 0, Rotate.Y_AXIS);
                        cylinder.getTransforms().add(rt0);

                        if(przekretlo_angle<240) {
                            przekretlo_angle = przekretlo_angle + 1;
                        }
                        text.setText(print(przekretlo_angle));

                    }
                    if(keyCode.equals(KeyCode.Q)&&!flaga)
                    {
                        Rotate rt0 = new Rotate(-10, 0, 0, 0, Rotate.Y_AXIS);
                        cylinder.getTransforms().add(rt0);
                        if(przekretlo_angle>0)
                            przekretlo_angle = przekretlo_angle - 1;
                        text.setText(print(przekretlo_angle));
                    }
                }
        );

        primaryStage.setTitle("KcK");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void wyswietlanie(int time) {
        aktualnyCzas=time;
        TimerTask update = new TimerTask() {
            @Override
            public void run() {
                text.setText(print(aktualnyCzas));
            }
        };
        TimerTask x=new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(update);
                aktualnyCzas--;
            }
        };
        timer.schedule(x,1000,1000);
        long start=System.currentTimeMillis(), koniec=start+(1000*time);
        while (aktualnyCzas>0);
        timer.cancel();
        text.setText(" ".repeat(rozmiar_wyswietlacza));
    }
    public String print(int time){
        String zwrot=((Integer) time).toString();
        Integer minuty=time/60, sekundy=time%60;
        String z=" "+minuty.toString()+":";
        if(sekundy==0){
            z=z+"0";
        }
        else if(sekundy<10){
            z=z+"0";
        }
        z=z+sekundy;
        return z+" ";
    }
    public static void main(String[] args) {
        launch(args);
    }

}