/*

MIT License

Copyright (c) 2020 Moritz Völker & Emil Baerens

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

import neat.Node;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import util.SortedList;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SortedListTest {
    SortedList<Node> testSubject;

    @Mock
    Node one;
    @Mock
    Node two;
    @Mock
    Node three;

    private boolean isSorted() {
        for (int i = 0; i < testSubject.size() - 1; i++) {
            if (testSubject.get(i).compareTo(testSubject.get(i+1)) > 0) {
                return false;
            }
        }
        return true;
    }

    @Test
    public void testAddSmallest() {
        testSubject = new SortedList<>();
        when(one.compareTo(two)).thenReturn(-1);

        testSubject.add(two);
        testSubject.add(one);

        assertTrue(isSorted());
    }

    @Test
    public void testAddBiggest() {
        testSubject = new SortedList<>();
        when(one.compareTo(two)).thenReturn(-1);
        when(two.compareTo(one)).thenReturn(1);

        testSubject.add(one);
        testSubject.add(two);

        assertTrue(isSorted());
    }

    @Test
    public void testAddInMiddle() {
        testSubject = new SortedList<>();
        when(one.compareTo(two)).thenReturn(-1);
        when(two.compareTo(one)).thenReturn(1);
        when(two.compareTo(three)).thenReturn(-1);

        testSubject.add(one);
        testSubject.add(three);
        testSubject.add(two);

        assertTrue(isSorted());
    }

    @Test
    public void testAddAll() {
        testSubject = spy(new SortedList<>());
        List<Node> toAdd = new ArrayList<>(3);
        toAdd.add(one);
        toAdd.add(two);
        toAdd.add(three);

        testSubject.addAll(toAdd);

        verify(testSubject, times(3)).add(any());
    }
}
