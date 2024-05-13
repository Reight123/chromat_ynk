package fr.cyu.chroma;

public class Cursor {
    private final int id;
    private int pos_x;
    private int pos_y;
    private int direction;
    private boolean is_shown;
    private int opacity;
    private int thick;

    public Cursor(int id, int pos_x, int pos_y, int direction, boolean is_shown, int opacity, int thick) {
        this.id = id;
        this.pos_x = pos_x;
        this.pos_y = pos_y;
        this.direction = direction;
        this.is_shown = is_shown;
        this.opacity = opacity;
        this.thick = thick;
    }

    public int getId() {
        return id;
    }

    public int getPos_x() {
        return pos_x;
    }

    public int getPos_y() {
        return pos_y;
    }

    public int getDirection() {
        return direction;
    }

    public boolean isIs_shown() {
        return is_shown;
    }

    public int getOpacity() {
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
}
