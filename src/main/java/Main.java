import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.glClearColor;

public class Main {
    public static void main(String[] args) {
        DisplayManager.createDisplay();
        GL.createCapabilities();

        while (!DisplayManager.isCloseRequested()) {
            glClearColor(1.0f, 0.0f, 0f, 0f);
            DisplayManager.updateDisplay();
        }

        DisplayManager.destoryDisplay();
    }
}
