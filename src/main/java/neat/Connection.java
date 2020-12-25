/*

MIT License

Copyright (c) 2020 Moritz Völker & Emil Baerens

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

package neat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

public class Connection implements Serializable, Comparable<Connection> {
    @SuppressWarnings("FieldMayBeFinal")
    private Node in;
    @SuppressWarnings("FieldMayBeFinal")
    private Node out;

    private int innovationNumber;
    private double weight;
    private boolean enabled;



    public Connection(Node in, Node out, double weight) {
        this.in = in;
        this.out = out;
        this.weight = weight;
        this.innovationNumber = -1;
        this.enabled = true;
    }

    public Connection(Connection connection, Node in, Node out) {
        this.in = in;
        this.out = out;
        this.weight = connection.weight;
        this.innovationNumber = connection.innovationNumber;
        this.enabled = connection.isEnabled();
    }

    public Connection(Connection connection) {
        this.in = connection.getIn();
        this.out = connection.getOut();
        this.weight = connection.weight;
        this.innovationNumber = connection.innovationNumber;
        this.enabled = connection.isEnabled();
    }

    public boolean isDependentOn(Node node) {
        return in.isDependentOn(node);
    }

    public double getValue() {
        return in.getValue() * weight;
    }

    public Node getIn() {
        return in;
    }

    public Node getOut() {
        return out;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getInnovationNumber() {
        return innovationNumber;
    }

    public void setInnovationNumber(int innovationNumber) {
        this.innovationNumber = innovationNumber;
    }

    public int setInnovationNumber(int currentInnovationNumber, List<Connection> currentMutations) {
        int i;
        for (i = 0; i < currentMutations.size(); i++) {
            if (equals(currentMutations.get(i))) {
                innovationNumber = currentMutations.get(i).getInnovationNumber();
                break;
            }
        }
        if (i == currentMutations.size()) {
            innovationNumber = currentInnovationNumber++;
            currentMutations.add(this);
        }
        return currentInnovationNumber;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Connection)) {
            return false;
        }
        if (!in.equals(((Connection) obj).getIn())) {
            return false;
        }
        if (!out.equals(((Connection) obj).getOut())) {
            return false;
        }
        return true;
        //return obj instanceof Connection && in.equals(((Connection) obj).getIn()) && out.equals(((Connection) obj).getOut());
    }

    @Override
    public int compareTo(Connection o) {
        return innovationNumber - o.getInnovationNumber();
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(Integer.toString(in.getInnovationNumber()) +  out.getInnovationNumber());
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
    }
}
