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
        Connection mutatedConnection = new Connection(inNode, outNode, 0.0);
        mutatedConnection.setInnovationNumber(2);
        currentMutations.add(mutatedConnection);
        when(inNode.getInnovationNumber()).thenReturn(0);
        when(outNode.getInnovationNumber()).thenReturn(1);

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
        when(inNode.getInnovationNumber()).thenReturn(0);

        int oldInnovationNumber = 5;
        int newInnovationNumber = testSubject.setInnovationNumber(oldInnovationNumber, currentMutations);

        assertEquals(oldInnovationNumber + 1, newInnovationNumber);
        assertEquals(oldInnovationNumber, testSubject.getInnovationNumber());
    }

    @Test
    public void testEqualsTrue() {
        when(inNode.getInnovationNumber()).thenReturn(0);
        when(outNode.getInnovationNumber()).thenReturn(1);

        Connection equalConnection = new Connection(inNode, outNode, 0.0);

        boolean equals = testSubject.equals(equalConnection);

        assertTrue(equals);
    }

    @Test
    public void testEqualsFalse() {
        when(inNode.getInnovationNumber()).thenReturn(0);

        Connection unequalConnection = new Connection(new InputNode(2), new InputNode(3), 0.0);

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
