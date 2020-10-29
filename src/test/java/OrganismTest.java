import neat.Connection;
import neat.NeatConfiguration;
import neat.organisms.Organism;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrganismTest {

    Organism testSubject;

    @Mock
    NeatConfiguration configuration;

    @BeforeEach
    public void init() {
        when(configuration.isBiasNodeEnabled()).thenReturn(true);
        testSubject = new Organism(configuration);
    }

    @Test
    public void testMutateAlways() {
        Organism testSubject = spy(this.testSubject);
        doNothing().when(testSubject).mutateWeights();
        doReturn(0).when(testSubject).mutateConnection(anyInt(), anyList());
        doReturn(0).when(testSubject).mutateNode(anyInt(), anyList());
        doNothing().when(testSubject).mutateEnablement();

        when(configuration.getMutateConnectionRate()).thenReturn(1.0);
        when(configuration.getMutateNodeRate()).thenReturn(1.0);
        when(configuration.getMutationRateEnablement()).thenReturn(1.0);


        testSubject.mutate(new ArrayList<>(0), 0);

        verify(testSubject).mutateWeights();
        verify(testSubject).mutateConnection(anyInt(), anyList());
        verify(testSubject).mutateNode(anyInt(), anyList());
        verify(testSubject).mutateEnablement();
    }

    @Test
    public void testMutateNever() {
        Organism testSubject = spy(this.testSubject);
        doNothing().when(testSubject).mutateWeights();

        when(configuration.getMutateConnectionRate()).thenReturn(0.0);
        when(configuration.getMutateNodeRate()).thenReturn(0.0);
        when(configuration.getMutationRateEnablement()).thenReturn(0.0);


        testSubject.mutate(new ArrayList<>(0), 0);

        verify(testSubject).mutateWeights();
        verify(testSubject, times(0)).mutateConnection(anyInt(), anyList());
        verify(testSubject, times(0)).mutateNode(anyInt(), anyList());
        verify(testSubject, times(0)).mutateEnablement();
    }

    @Test
    public void testMutateWeights() {
        when(configuration.getMutateWeightRate()).thenReturn(1.0);
        when(configuration.getPerturbRate()).thenReturn(1.0);
        when(configuration.getMaxConnectionAbsoluteValue()).thenReturn(0.5);
        when(configuration.getStepSize()).thenReturn(0.5);
        Connection connection = new Connection(null, null, 0.0);
        testSubject.getConnections().add(connection);

        testSubject.mutateWeights();

        double weight = connection.getWeight();
        assertTrue(weight > 0.0 && weight < configuration.getStepSize() || (weight < 0.0 && weight > -configuration.getStepSize()));
    }

    @Test
    public void testMutateConnection() {

    }
}
