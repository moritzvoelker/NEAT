/*

MIT License

Copyright (c) 2020 Moritz Völker & Emil Baerens

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

import neat.Connection;
import neat.Node;
import neat.NodePurpose;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NodeTest {

    Node testSubject;

    @Mock
    Connection firstConnection;
    @Mock
    Connection secondConnection;
    @Mock
    Node dependentOnNode;

    private final double returnValue = 42.0;

    @BeforeEach
    public void init() {
        testSubject = new Node(NodePurpose.Hidden, 0) {
            @Override
            protected double calculateValue(List<Connection> connections) {
                return returnValue;
            }
        };
    }

    @Test
    public void testIsDependentOnSameNode() {
        when(dependentOnNode.getInnovationNumber()).thenReturn(0);

        boolean dependence = testSubject.isDependentOn(dependentOnNode);

        assertTrue(dependence);
    }

    @Test
    public void testIsDependentOnOtherNode() {
        when(dependentOnNode.getInnovationNumber()).thenReturn(1);
        when(firstConnection.isDependentOn(any(Node.class))).thenReturn(false);
        when(secondConnection.isDependentOn(any(Node.class))).thenReturn(true);

        testSubject.addInput(firstConnection);
        testSubject.addInput(secondConnection);

        boolean dependence = testSubject.isDependentOn(dependentOnNode);

        assertTrue(dependence);
        verify(secondConnection).isDependentOn(any(Node.class));
    }

    @Test
    public void testIsDependentOnNoNode() {
        when(dependentOnNode.getInnovationNumber()).thenReturn(1);
        when(firstConnection.isDependentOn(any(Node.class))).thenReturn(false);
        when(secondConnection.isDependentOn(any(Node.class))).thenReturn(false);

        testSubject.addInput(firstConnection);
        testSubject.addInput(secondConnection);

        boolean dependence = testSubject.isDependentOn(dependentOnNode);

        assertFalse(dependence);
        verify(secondConnection).isDependentOn(any(Node.class));
    }

    @Test
    public void testGetValue() throws NoSuchFieldException, IllegalAccessException {
        double value = testSubject.getValue();

        assertEquals(returnValue, value);
        Field calculated = Node.class.getDeclaredField("calculated");
        calculated.setAccessible(true);
        assertTrue(calculated.getBoolean(testSubject));
    }

    @Test
    public void testEqualsTrue() {
        when(dependentOnNode.getInnovationNumber()).thenReturn(0);
        boolean equals = testSubject.equals(dependentOnNode);

        assertTrue(equals);
    }

    @Test
    public void testEqualsFalse() {
        when(dependentOnNode.getInnovationNumber()).thenReturn(1);
        boolean equals = testSubject.equals(dependentOnNode);

        assertFalse(equals);
    }
}
