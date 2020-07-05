package networkdisplay;

import neat.Node;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Layer {
    List<Blop> nodes;

    public Layer() {
        this.nodes = new LinkedList<>();
    }
}
