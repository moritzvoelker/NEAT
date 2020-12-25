/*

MIT License

Copyright (c) 2020 Moritz Völker & Emil Baerens

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

import neat.Connection;
import neat.NeatConfiguration;
import neat.Organism;
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
