/*

MIT License

Copyright (c) 2020 Moritz Völker & Emil Baerens

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

import neat.Connection;
import neat.InputNode;
import neat.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConnectionTest {

    Connection testSubject;

    @Mock
    Node inNode;
    @Mock
    Node outNode;

    @BeforeEach
    public void init() {
        testSubject = new Connection(inNode, outNode, -0.5);
    }

    @Test
    public void testIsDependentOnTrue() {
        when(inNode.isDependentOn(any(Node.class))).thenReturn(true);

        boolean dependence = testSubject.isDependentOn(inNode);

        assertTrue(dependence);
    }

    @Test
    public void testIsDependentOnFalse() {
        when(inNode.isDependentOn(any(Node.class))).thenReturn(false);

        boolean dependence = testSubject.isDependentOn(inNode);

        assertFalse(dependence);
    }

    @Test
    public void testSetInnovationNumberAlreadyExisting() {
        List<Connection> currentMutations = new ArrayList<>(2);
        Connection mutatedConnection = new Connection(new InputNode(0), new InputNode(1), 0.0);
        mutatedConnection.setInnovationNumber(2);
        currentMutations.add(mutatedConnection);
        testSubject = new Connection(new InputNode(0), new InputNode(1), -0.5);

        int oldInnovationNumber = 3;
        int newInnovationNumber = testSubject.setInnovationNumber(oldInnovationNumber, currentMutations);

        assertEquals(oldInnovationNumber, newInnovationNumber);
        assertEquals(mutatedConnection.getInnovationNumber(), testSubject.getInnovationNumber());
    }

    @Test
    public void testSetInnovationNumberNew() {
        List<Connection> currentMutations = new ArrayList<>(2);
        Connection mutatedConnection = new Connection(new InputNode(3), new InputNode(4), 0.0);
        mutatedConnection.setInnovationNumber(2);
        currentMutations.add(mutatedConnection);
        testSubject = new Connection(new InputNode(0), new InputNode(1), -0.5);

        int oldInnovationNumber = 5;
        int newInnovationNumber = testSubject.setInnovationNumber(oldInnovationNumber, currentMutations);

        assertEquals(oldInnovationNumber + 1, newInnovationNumber);
        assertEquals(oldInnovationNumber, testSubject.getInnovationNumber());
    }

    @Test
    public void testEqualsTrue() {
        testSubject = new Connection(new InputNode(0), new InputNode(1), -0.5);

        Connection equalConnection = new Connection(new InputNode(0), new InputNode(1), 0.0);

        boolean equals = testSubject.equals(equalConnection);

        assertTrue(equals);
    }

    @Test
    public void testEqualsFalse() {
        testSubject = new Connection(new InputNode(0), new InputNode(1), -0.5);

        Connection unequalConnection = new Connection(new InputNode(1), null, 0.0);

        boolean equals = testSubject.equals(unequalConnection);

        assertFalse(equals);
    }

    @Test
    public void testGetValue() {
        when(inNode.getValue()).thenReturn(3.0);

        double value = testSubject.getValue();

        assertEquals(-1.5, value);
    }
}
