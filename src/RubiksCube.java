
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
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
    public boolean flaga = true;


    private Group buildScene() {
        Box earth = new Box(1000,600,100);
        earth.setTranslateX(0);
        earth.setTranslateY(0);

        Cylinder cylinder = new Cylinder(100,100);
        cylinder.setTranslateX(400);
        cylinder.setTranslateY(200);
        cylinder.setTranslateZ(-50);
        cylinder.setRotationAxis(new Point3D(1,0,0));
        cylinder.setRotate(90);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(new Image(getClass().getResourceAsStream("metal-texture.jpg")));
        cylinder.setMaterial(material);


        return new Group(earth,cylinder);
    }
    @Override
    public void start(Stage primaryStage) {
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(50);
        light.setTranslateY(-300);
        light.setTranslateZ(-400);
        PointLight light2 = new PointLight(Color.color(0.6, 0.3, 0.4));
        light2.setTranslateX(400);
        light2.setTranslateY(0);
        light2.setTranslateZ(-400);


        Group group = buildScene();
        Scene scene = new Scene(
                new StackPane(group),
                650, 650,
                true,
                SceneAntialiasing.BALANCED
        );

        scene.setFill(Color.rgb(10, 10, 40));

        scene.setCamera(new PerspectiveCamera());
        primaryStage.setScene(scene);
        primaryStage.show();

        AmbientLight ambientLight = new AmbientLight(Color.color(0.2, 0.2, 0.2));


        scene.setOnMousePressed(me -> {
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });
        if (flaga == true)
        {
            Rotate rotateX = new Rotate(30, 0, 0, 0, Rotate.X_AXIS);
            Rotate rotateY = new Rotate(20, 0, 0, 0, Rotate.Y_AXIS);

            group.getTransforms().addAll(rotateX,rotateY);

            scene.setOnMouseDragged(me -> {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                rotateX.setAngle(rotateX.getAngle()-(mousePosY - mouseOldY));
                rotateY.setAngle(rotateY.getAngle()+(mousePosX - mouseOldX));

                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
            });
        }
        else
        {
            scene.setOnMouseDragged(mouseEvent ->{
                Rotate ro = new Rotate(30, 0, 0, 0, Rotate.X_AXIS);
                Rotate ro1 = new Rotate(30, 0, 0, 0, Rotate.Y_AXIS);

                group.getChildren().get(1).setRotationAxis(new Point3D(400,200,0));
                group.getChildren().get(1).getTransforms().addAll(ro,ro1);

                mousePosX = mouseEvent.getSceneX();
                mousePosY = mouseEvent.getSceneY();
                ro.setAngle(ro.getAngle()-(mousePosY - mouseOldY));
                ro1.setAngle(ro1.getAngle()+(mousePosX - mouseOldX));

                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
            });
        }


        scene.setOnKeyPressed(event ->{
                    KeyCode keyCode = event.getCode();

                    if(keyCode.equals(KeyCode.W))
                    {
                        flaga = true;
                        System.out.println("Flaga jest true");
                    }
                    if(keyCode.equals(KeyCode.A))
                    {
                        flaga = false;
                        System.out.println("Flaga jest false");
                    }
                }
        );

        primaryStage.setTitle("KcK");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}