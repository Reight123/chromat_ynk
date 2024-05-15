package fr.cyu.chroma;

import javafx.scene.canvas.GraphicsContext;

public class Cursor   {
    private final int id;
    private int pos_x= 0;
    private int pos_y= 0;
    private int direction=0;
    private boolean is_shown= false;
    private int opacity=100;
    private int thick=5;

    public Cursor(int id) {
        this.id = id;
        this.pos_x = 10;
        this.pos_y = 10;
    }

    public int getId() {
        return id;
    }

    public int getPos_x() { return  this.pos_x; }

    public int getPos_y() {
        return this.pos_y;
    }

    public int getDirection() {
        return direction;
    }

    public boolean isIs_shown() {
        return is_shown;
    }

    public int getCursorOpacity() {
        return opacity;
    }

    public int getThick() {
        return thick;
    }

    public void setPos_x(int pos_x) {
        this.pos_x = pos_x;
    }

    public void setPos_y(int pos_y) {
        this.pos_y = pos_y;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setIs_shown(boolean is_shown) {
        this.is_shown = is_shown;
    }

    public void setOpacity(int opacity) {
        this.opacity = opacity;
    }

    public void setThick(int thick) {
        this.thick = thick;
    }

    public  String toString(){
        return getId() + "(" + getPos_x() + "," + getPos_y() + ")";
    }
}
