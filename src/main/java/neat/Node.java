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
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Node implements Serializable, Comparable<Node> {
    private int innovationNumber;
    private transient List<Connection> in;
    protected transient double value;
    private transient boolean calculated;
    private NodePurpose nodePurpose;

    public Node(NodePurpose nodePurpose, int innovationNumber) {
        this.in = new LinkedList<>();
        this.innovationNumber = innovationNumber;
        this.value = 0.0;
        this.calculated = false;
        this.nodePurpose = nodePurpose;
    }

    public boolean isDependentOn(Node node) {
        if (this.equals(node)) {
            return true;
        } else {
            for (Connection connection : in) {
                if (connection.isDependentOn(node)) {
                    return true;
                }
            }
            return false;
        }
    }

    protected abstract double calculateValue(List<Connection> connections);

    public double getValue() {
        if (!calculated) {
            value = calculateValue(getIn().stream().filter(Connection::isEnabled).collect(Collectors.toList()));
            calculated = true;
        }
        return value;
    }

    public void addInput(Connection input) {
        in.add(input);
    }

    public List<Connection> getIn() {
        return in;
    }

    public NodePurpose getNodePurpose() {
        return nodePurpose;
    }

    public void setNodePurpose(NodePurpose nodePurpose) {
        this.nodePurpose = nodePurpose;
    }

    public int getInnovationNumber() {
        return innovationNumber;
    }

    public void setInnovationNumber(int innovationNumber) {
        this.innovationNumber = innovationNumber;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Node && this.getInnovationNumber() == ((Node) obj).getInnovationNumber();
    }

    @Override
    public int compareTo(Node o) {
        return innovationNumber - o.getInnovationNumber();
    }

    public void resetCalculated() {
        calculated = false;
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        in = new LinkedList<>();
        objectInputStream.defaultReadObject();
        value = 0.0;
        calculated = false;
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
    }
}
